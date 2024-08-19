package com.hpfxd.pandaknockback;

import com.hpfxd.pandaknockback.api.PlayerKnockbackByEntityApplyEvent;
import com.hpfxd.pandaknockback.api.PlayerKnockbackByEntityEvent;
import com.hpfxd.pandaknockback.profile.KnockbackProfile;
import com.hpfxd.pandaknockback.profile.KnockbackProfileService;
import com.hpfxd.pandaknockback.profile.KnockbackSettings;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ProfileApplicationHandler implements Listener {
    private final KnockbackProfileService profileService;

    public ProfileApplicationHandler(KnockbackProfileService profileService) {
        this.profileService = profileService;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onKnockback(PlayerKnockbackByEntityEvent event) {
        final Player player = event.getPlayer();
        final KnockbackProfile profile = this.profileService.getProfile(player);

        if (profile == null) {
            // No profile is applied, don't handle
            event.setCancelled(true);
            return;
        }

        final Entity attacker = event.getAttacker();
        final KnockbackSettings settings = profile.getSettings(attacker);

        event.setBaseHorizontal(settings.getBaseHorizontal().getAsDouble());
        event.setBaseVertical(settings.getBaseVertical().getAsDouble());

        double bonusHorizontal = 0;
        double bonusVertical = 0;

        if (attacker instanceof Player && ((Player) attacker).isSprinting()) {
            bonusHorizontal += settings.getSprintBonusHorizontal().getAsDouble();
            bonusVertical += settings.getSprintBonusVertical().getAsDouble();
        }

        if (attacker instanceof LivingEntity) {
            final EntityEquipment equipment = ((LivingEntity) attacker).getEquipment();
            final ItemStack itemInHand = equipment.getItemInHand();

            if (itemInHand != null) {
                final int level = itemInHand.getEnchantmentLevel(Enchantment.KNOCKBACK);

                bonusHorizontal += settings.getEnchantmentHorizontal().getAsDouble() * level;
                bonusVertical += settings.getEnchantmentVertical().getAsDouble() * level;
            }
        }

        event.setBonusHorizontal(bonusHorizontal);
        event.setBonusVertical(bonusVertical);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onApply(PlayerKnockbackByEntityApplyEvent event) {
        final Player player = event.getPlayer();
        final KnockbackProfile profile = this.profileService.getProfile(player);

        if (profile == null) {
            return;
        }

        final Entity attacker = event.getAttacker();
        final KnockbackSettings settings = profile.getSettings(attacker);

        final double preMulH = settings.getPreMultiplierHorizontal().getAsDouble();
        event.setPlayerVelocity(event.getPlayerVelocity().multiply(new Vector(preMulH, settings.getPreMultiplierVertical().getAsDouble(), preMulH)));

        final Vector playerAndBase = event.getPlayerVelocity().add(event.getBaseVelocity());
        final double limit = settings.getLimitVertical().getAsDouble();
        final double diff = playerAndBase.getY() - limit;

        if (diff > 0) {
            // above limit

            final Vector compensatedBase = event.getBaseVelocity();
            compensatedBase.setY(compensatedBase.getY() - diff);

            event.setBaseVelocity(compensatedBase);
        }
    }
}
