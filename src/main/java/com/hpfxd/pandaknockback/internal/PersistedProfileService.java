package com.hpfxd.pandaknockback.internal;

import com.hpfxd.pandaknockback.KnockbackPlugin;
import com.hpfxd.pandaknockback.internal.PersistedProfileStorage.IllegalSettingException;
import com.hpfxd.pandaknockback.profile.KnockbackProfile;
import com.hpfxd.pandaknockback.profile.KnockbackProfileService;
import com.hpfxd.pandaknockback.profile.KnockbackProfileStorage.StorageLoadResult;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PersistedProfileService implements KnockbackProfileService, Listener {
    private final KnockbackPlugin plugin;

    private final File profilesFile;
    private PersistedProfileStorage storage;

    private final Map<Player, KnockbackProfile> profileMap = new HashMap<>();
    private final Map<String, PersistedProfile> profiles = new HashMap<>();
    private KnockbackProfile defaultProfile;

    public PersistedProfileService(KnockbackPlugin plugin) {
        this.plugin = plugin;
        this.profilesFile = new File(this.plugin.getDataFolder(), "profiles.yml");

        try {
            this.loadStorage();
            this.readProfiles();
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException("Failed to load profiles", e);
        }
    }

    public void loadStorage() throws IOException, InvalidConfigurationException {
        final YamlConfiguration configuration = new YamlConfiguration();

        if (!this.profilesFile.exists()) {
            this.plugin.saveResource("profiles.yml", false);
        }

        configuration.load(this.profilesFile);

        this.storage = new PersistedProfileStorage(configuration);
    }

    public boolean readProfiles() {
        final StorageLoadResult<PersistedProfile, IllegalSettingException> loadResult = this.storage.loadProfiles();

        if (loadResult.getFailed().isEmpty()) {
            this.plugin.getSLF4JLogger().info("Loaded {} knockback profiles", loadResult.getSuccessful().size());
        } else {
            this.plugin.getSLF4JLogger().error("-----------------------------------------------");
            this.plugin.getSLF4JLogger().error("{} knockback profiles failed to load!", loadResult.getFailed().size());
            this.plugin.getSLF4JLogger().error("");
            this.plugin.getSLF4JLogger().error("This is usually due to some of your settings being formatted incorrectly. Please see the logs below for more information.");
            this.plugin.getSLF4JLogger().error("-----------------------------------------------");

            for (final IllegalSettingException exception : loadResult.getFailed()) {
                this.plugin.getSLF4JLogger().warn("Profile {}", exception.getMessage(), exception.getCause());
            }
        }

        this.profiles.clear();

        for (final PersistedProfile profile : loadResult.getSuccessful()) {
            this.profiles.put(profile.getName(), profile);
        }

        final String defaultProfileName = this.plugin.getConfig().getString("default-profile");
        if (defaultProfileName != null && !defaultProfileName.equalsIgnoreCase("none")) {
            this.defaultProfile = this.getLoadedProfile(defaultProfileName);

            if (this.defaultProfile == null) {
                this.plugin.getSLF4JLogger().warn("Default profile is set to \"{}\", but that profile is not loaded", defaultProfileName);
            }
        } else {
            this.defaultProfile = null;
        }

        // Check online players and replace with updated profiles
        for (final Iterator<Map.Entry<Player, KnockbackProfile>> it = this.profileMap.entrySet().iterator(); it.hasNext(); ) {
            final Map.Entry<Player, KnockbackProfile> entry = it.next();
            final KnockbackProfile profile = entry.getValue();

            if (profile instanceof PersistedProfile) {
                final PersistedProfile newProfile = this.getLoadedProfile(((PersistedProfile) profile).getName());

                if (newProfile == null) {
                    // Profile no longer exists
                    it.remove();
                } else {
                    // Profile still exists, update player's profile to the new one
                    entry.setValue(newProfile);
                }
            }
        }

        return loadResult.getFailed().isEmpty();
    }

    @Override
    public KnockbackProfile getProfile(Player player) {
        return this.profileMap.getOrDefault(player, this.defaultProfile);
    }

    @Override
    public void setProfile(Player player, KnockbackProfile profile) {
        if (profile != null) {
            this.profileMap.put(player, profile);
        } else {
            this.profileMap.remove(player);
        }
    }

    public PersistedProfile getLoadedProfile(String name) {
        return this.profiles.get(name);
    }

    public Collection<PersistedProfile> getLoadedProfiles() {
        return this.profiles.values();
    }

    public PersistedProfileStorage getStorage() {
        return this.storage;
    }

    public File getProfilesFile() {
        return this.profilesFile;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.profileMap.remove(event.getPlayer());
    }
}
