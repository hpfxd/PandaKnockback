package com.hpfxd.pandaknockback.internal;

import com.hpfxd.pandaknockback.profile.KnockbackProfile;
import com.hpfxd.pandaknockback.profile.KnockbackSettings;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Map;

public class PersistedProfile implements KnockbackProfile {
    private final String name;
    private final KnockbackSettings base;
    private final Map<EntityType, KnockbackSettings> entityLayers;

    public PersistedProfile(String name, KnockbackSettings base, Map<EntityType, KnockbackSettings> entityLayers) {
        this.name = name;
        this.base = base;
        this.entityLayers = entityLayers;
    }

    public String getName() {
        return this.name;
    }

    public KnockbackSettings getBase() {
        return this.base;
    }

    public KnockbackSettings getLayer(EntityType entityType) {
        return this.entityLayers.get(entityType);
    }

    @Override
    public KnockbackSettings getSettings(Entity attacker) {
        return this.entityLayers.getOrDefault(attacker.getType(), this.base);
    }
}
