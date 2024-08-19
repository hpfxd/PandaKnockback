package com.hpfxd.pandaknockback.api;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.util.Vector;

public class PlayerKnockbackByEntityApplyEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Entity attacker;

    private Vector playerVelocity;
    private Vector baseVelocity;
    private Vector bonusVelocity;

    private boolean cancelled;

    public PlayerKnockbackByEntityApplyEvent(Player victim, Entity attacker, Vector playerVelocity, Vector baseVelocity, Vector bonusVelocity) {
        super(victim);
        this.playerVelocity = playerVelocity;
        this.attacker = attacker;
        this.baseVelocity = baseVelocity;
        this.bonusVelocity = bonusVelocity;
    }

    public Entity getAttacker() {
        return this.attacker;
    }

    public Vector getPlayerVelocity() {
        return this.playerVelocity.clone();
    }

    public void setPlayerVelocity(Vector playerVelocity) {
        this.playerVelocity = playerVelocity.clone();
    }

    public Vector getBaseVelocity() {
        return this.baseVelocity.clone();
    }

    public void setBaseVelocity(Vector baseVelocity) {
        this.baseVelocity = baseVelocity.clone();
    }

    public Vector getBonusVelocity() {
        return this.bonusVelocity.clone();
    }

    public void setBonusVelocity(Vector bonusVelocity) {
        this.bonusVelocity = bonusVelocity.clone();
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
