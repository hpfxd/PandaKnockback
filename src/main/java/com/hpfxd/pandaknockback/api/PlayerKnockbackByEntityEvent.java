package com.hpfxd.pandaknockback.api;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerKnockbackByEntityEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Entity attacker;

    private double baseHorizontal;
    private double baseVertical;

    private double bonusHorizontal;
    private double bonusVertical;

    private boolean cancelled;

    public PlayerKnockbackByEntityEvent(Player victim, Entity attacker) {
        super(victim);
        this.attacker = attacker;
    }

    public Entity getAttacker() {
        return this.attacker;
    }

    public double getBaseHorizontal() {
        return this.baseHorizontal;
    }

    public void setBaseHorizontal(double baseHorizontal) {
        this.baseHorizontal = baseHorizontal;
    }

    public double getBaseVertical() {
        return this.baseVertical;
    }

    public void setBaseVertical(double baseVertical) {
        this.baseVertical = baseVertical;
    }

    public double getBonusHorizontal() {
        return this.bonusHorizontal;
    }

    public void setBonusHorizontal(double bonusHorizontal) {
        this.bonusHorizontal = bonusHorizontal;
    }

    public double getBonusVertical() {
        return this.bonusVertical;
    }

    public void setBonusVertical(double bonusVertical) {
        this.bonusVertical = bonusVertical;
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
