package com.hpfxd.pandaknockback;

import com.hpfxd.pandaknockback.api.PlayerKnockbackByEntityApplyEvent;
import com.hpfxd.pandaknockback.api.PlayerKnockbackByEntityEvent;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles firing {@link PlayerKnockbackByEntityEvent} and applying the results to the attack.
 * <p>
 * The server processes attacks as follows (the parts we care about):
 * <ul>
 *     <li>
 *         If the attacker is a player:
 *         <ol>
 *             <li>{@link PrePlayerAttackEntityEvent}</li>
 *             <li>{@link EntityDamageByEntityEvent}</li>
 *             <li>Server calculates knockback for the victim (but does not send it yet; we overwrite that velocity withour calculations in the velocity event)</li>
 *             <li>If the attacker was sprinting or has the knockback enchantment, server sets their sprinting state to {@code false}</li>
 *             <li>{@link PlayerVelocityEvent}</li>
 *             <li>Server immediately sends the entity velocity packet to the victim with the result</li>
 *         </ol>
 *     </li>
 *     <li>
 *         If the attacker is not a player:
 *         <ol>
 *             <li>{@link EntityDamageByEntityEvent}</li>
 *             <li>Server calculates knockback for the victim (but does not send it yet; we overwrite that velocity with our calculations in the velocity event)</li>
 *             <li>Later in the tick, {@link PlayerVelocityEvent} is fired during entity tracker updating, before velocity is sent to the victim</li>
 *         </ol>
 *     </li>
 * </ul>
 */
public class AttackListener implements Listener, Runnable {
    private final Map<Player, Vector> knockback = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            // Only handle damage events where the victim is a player
            return;
        }

        final Player victim = (Player) event.getEntity();
        final Entity attacker = event.getDamager();

        final Location victimLocation = victim.getLocation();
        final Location attackerLocation = attacker.getLocation();

        final PlayerKnockbackByEntityEvent knockbackEvent = new PlayerKnockbackByEntityEvent(victim, attacker);
        Bukkit.getPluginManager().callEvent(knockbackEvent);

        if (knockbackEvent.isCancelled()) {
            return;
        }

        double dirX = attackerLocation.getX() - victimLocation.getX();
        double dirZ = attackerLocation.getZ() - victimLocation.getZ();
        for (; dirX * dirX + dirZ * dirZ < 0.0001; dirZ = (Math.random() - Math.random()) * 0.01) {
            dirX = (Math.random() - Math.random()) * 0.01;
        }

        final double magnitude = Math.sqrt(dirX * dirX + dirZ * dirZ);
        final Vector baseVelocity = new Vector(
                -(dirX / magnitude * knockbackEvent.getBaseHorizontal()),
                knockbackEvent.getBaseVertical(),
                -(dirZ / magnitude * knockbackEvent.getBaseHorizontal())
        );

        final Vector bonusVelocity = new Vector(
                -Math.sin(attacker.getLocation().getYaw() * Math.PI / 180f) * knockbackEvent.getBonusHorizontal(),
                knockbackEvent.getBonusVertical(),
                Math.cos(attacker.getLocation().getYaw() * Math.PI / 180f) * knockbackEvent.getBonusHorizontal()
        );

        final PlayerKnockbackByEntityApplyEvent applyEvent = new PlayerKnockbackByEntityApplyEvent(victim, attacker, victim.getVelocity(), baseVelocity, bonusVelocity);
        Bukkit.getPluginManager().callEvent(applyEvent);

        if (applyEvent.isCancelled()) {
            return;
        }

        final Vector velocity = applyEvent.getPlayerVelocity()
                .add(applyEvent.getBaseVelocity())
                .add(applyEvent.getBonusVelocity());

        this.knockback.put(victim, velocity);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerVelocity(PlayerVelocityEvent event) {
        final Vector knockbackVector = this.knockback.remove(event.getPlayer());

        if (knockbackVector != null) {
            event.setVelocity(knockbackVector);
        }
    }

    @Override
    public void run() {
        this.knockback.clear();
    }
}
