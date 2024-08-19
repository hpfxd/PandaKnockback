package com.hpfxd.pandaknockback.profile;

import org.bukkit.entity.Entity;

public interface KnockbackProfile {
    KnockbackSettings getSettings(Entity attacker);
}
