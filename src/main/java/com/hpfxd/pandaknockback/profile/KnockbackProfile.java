package com.hpfxd.pandaknockback.profile;

import org.bukkit.entity.Entity;

@FunctionalInterface
public interface KnockbackProfile {
    static KnockbackProfile constant(KnockbackSettings settings) {
        return attacker -> settings;
    }

    KnockbackSettings getSettings(Entity attacker);
}
