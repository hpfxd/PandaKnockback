package com.hpfxd.pandaknockback.internal;

import com.google.common.collect.ImmutableList;
import com.hpfxd.pandaknockback.profile.KnockbackSettings;
import com.hpfxd.pandaknockback.profile.KnockbackProfileStorage;
import com.hpfxd.pandaknockback.profile.value.ConstantValue;
import com.hpfxd.pandaknockback.profile.value.UniformRandomValue;
import com.hpfxd.pandaknockback.profile.value.ValueSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class PersistedProfileStorage implements KnockbackProfileStorage<PersistedProfile> {
    private final FileConfiguration source;

    public PersistedProfileStorage(FileConfiguration source) {
        this.source = source;
    }

    @Override
    public StorageLoadResult<PersistedProfile, IllegalSettingException> loadProfiles() {
        final ImmutableList.Builder<PersistedProfile> successful = ImmutableList.builder();
        final ImmutableList.Builder<IllegalSettingException> failed = ImmutableList.builder();

        for (final String id : this.source.getKeys(false)) {
            final PersistedProfile profile;
            try {
                profile = this.deserialize(this.source.getConfigurationSection(id));
            } catch (IllegalSettingException e) {
                failed.add(e);
                continue;
            }

            successful.add(profile);
        }

        return new StorageLoadResult<>(successful.build(), failed.build());
    }

    public PersistedProfile deserialize(ConfigurationSection config) throws IllegalSettingException {
        final String name = config.getName();
        final KnockbackSettings baseSettings = this.deserializeSettings(config, null);
        final Map<EntityType, KnockbackSettings> entityLayers = new EnumMap<>(EntityType.class);

        for (final EntityType entityType : EntityType.values()) {
            final String configKey = entityType.name().toLowerCase(Locale.ROOT);
            final ConfigurationSection section = config.getConfigurationSection(configKey);

            if (section != null) {
                entityLayers.put(entityType, this.deserializeSettings(section, config));
            }
        }

        return new PersistedProfile(name, baseSettings, entityLayers);
    }

    private KnockbackSettings deserializeSettings(ConfigurationSection config, ConfigurationSection defaults) throws IllegalSettingException {
        return new KnockbackSettings(
                this.getValue(config, defaults, "base.horizontal"),
                this.getValue(config, defaults, "base.vertical"),
                this.getValue(config, defaults, "sprint-bonus.horizontal"),
                this.getValue(config, defaults, "sprint-bonus.vertical"),
                this.getValue(config, defaults, "knockback-enchantment-bonus.horizontal"),
                this.getValue(config, defaults, "knockback-enchantment-bonus.vertical"),
                this.getValue(config, defaults, "pre-multiplier.horizontal"),
                this.getValue(config, defaults, "pre-multiplier.vertical"),
                this.getValue(config, defaults, "limit.vertical")
        );
    }

    private ValueSource getValue(ConfigurationSection config, ConfigurationSection defaults, String path) throws IllegalSettingException {
        Object value = config.get(path);
        if (value == null && defaults != null) {
            value = defaults.get(path);
        }

        try {
            if (value == null) {
                throw new NullPointerException("Required setting does not exist");
            }

            return this.deserializeValue(value);
        } catch (Exception e) {
            throw new IllegalSettingException(config.getCurrentPath(), path, e);
        }
    }

    private ValueSource deserializeValue(Object input) {
        if (input instanceof Number) {
            return new ConstantValue(((Number) input).doubleValue());
        } else if (input instanceof String) {
            final String s = (String) input;

            final int sepIdx = s.indexOf('-');
            if (sepIdx > 1) {
                final double min = Double.parseDouble(s.substring(0, sepIdx));
                final double max = Double.parseDouble(s.substring(sepIdx + 1));

                return new UniformRandomValue(min, max);
            } else {
                final double value = Double.parseDouble(s);

                return new ConstantValue(value);
            }
        } else if (input instanceof ConfigurationSection) {
            final ConfigurationSection section = (ConfigurationSection) input;
            final String type = Objects.requireNonNull(section.getString("type"), "type");

            switch (type) {
                case "constant":
                    final double value = section.getDouble("value");
                    return new ConstantValue(value);
                case "uniform":
                    final double min = section.getDouble("min");
                    final double max = section.getDouble("max");
                    return new UniformRandomValue(min, max);
                default:
                    throw new IllegalArgumentException("Unknown number provider type: " + type);
            }
        } else {
            throw new IllegalArgumentException("Don't know how to turn " + input.getClass().getName() + " into a number");
        }
    }

    public FileConfiguration getSource() {
        return this.source;
    }

    public static class IllegalSettingException extends IllegalArgumentException {
        private final String sectionPath;
        private final String settingKey;

        private IllegalSettingException(String sectionPath, String settingKey, Throwable cause) {
            super("\"" + sectionPath + "\": Setting \"" + settingKey + "\" is illegal", cause);

            this.sectionPath = sectionPath;
            this.settingKey = settingKey;
        }

        public String getSectionPath() {
            return this.sectionPath;
        }

        public String getSettingKey() {
            return this.settingKey;
        }
    }
}
