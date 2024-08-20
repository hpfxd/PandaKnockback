package com.hpfxd.pandaknockback.command;

import com.hpfxd.pandaknockback.internal.PersistedProfile;
import com.hpfxd.pandaknockback.internal.PersistedProfileStorage;
import com.hpfxd.pandaknockback.internal.PersistedProfileService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EditProfileSubcommand extends Subcommand {
    private static final Pattern SETTING_KEY_PATTERN = Pattern.compile("[a-z.-]+");

    private final PersistedProfileService profileService;
    private final ConfigurationSection defaultProfileSection;

    private final List<String> completionKeys;

    public EditProfileSubcommand(PersistedProfileService profileService, ConfigurationSection defaultProfileSection) {
        super(
                Collections.singletonList("editprofile"),
                "pandaknockback.editprofile",
                "Modify and read a profile's settings",
                "<command> <profile> <setting> [value]"
        );
        this.profileService = profileService;
        this.defaultProfileSection = defaultProfileSection;

        this.completionKeys = this.defaultProfileSection.getKeys(true).stream()
                .filter(key -> !this.defaultProfileSection.isConfigurationSection(key))
                .collect(Collectors.toList());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 3 && args.length != 4) {
            return false;
        }

        final PersistedProfile profile = this.profileService.getLoadedProfile(args[1]);
        if (profile == null) {
            sender.sendMessage(ChatColor.RED + "Profile not found");
            return true;
        }

        final String settingKey = args[2];
        if (!SETTING_KEY_PATTERN.matcher(settingKey).matches()) {
            sender.sendMessage(ChatColor.RED + "Illegal setting key");
            return true;
        }

        final FileConfiguration source = this.profileService.getStorage().getSource();
        final ConfigurationSection sourceSection = source.getConfigurationSection(profile.getName());

        if (args.length == 3) {
            final Object value = sourceSection.get(settingKey);

            if (value == null) {
                sender.sendMessage(ChatColor.RED + "Setting key does not exist in " + profile.getName());
            } else {
                sender.sendMessage(ChatColor.GOLD + profile.getName() + "." + settingKey + ChatColor.YELLOW + ": " + value);
            }
        } else {
            final Configuration memoryConfiguration = new MemoryConfiguration();
            final ConfigurationSection memorySection = memoryConfiguration.createSection(sourceSection.getName());

            // Copy all values from sourceSection to memorySection
            for (final String key : sourceSection.getKeys(true)) {
                memorySection.set(key, sourceSection.get(key));
            }

            final String value = args[3];
            memorySection.set(settingKey, value);

            try {
                this.profileService.getStorage().deserialize(memorySection);
            } catch (PersistedProfileStorage.IllegalSettingException e) {
                sender.sendMessage(ChatColor.RED + e.getMessage() + ChatColor.GRAY + ": " + e.getCause());
                return true;
            }

            // The value didn't cause the profile to become invalid; so we can now set it in the source section
            sourceSection.set(settingKey, value);

            try {
                source.save(this.profileService.getProfilesFile());
            } catch (IOException e) {
                sender.sendMessage(ChatColor.RED + "An error occurred while trying to save the updated profiles file");
            }

            this.profileService.readProfiles();

            sender.sendMessage(ChatColor.GREEN + "Set " + ChatColor.YELLOW + settingKey + ChatColor.GREEN + " to " + ChatColor.YELLOW + value + ChatColor.GREEN + " in " + ChatColor.YELLOW + profile.getName());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final List<String> completions = new ArrayList<>();
        if (args.length == 2) {
            for (final PersistedProfile profile : this.profileService.getLoadedProfiles()) {
                completions.add(profile.getName());
            }
        } else if (args.length == 3) {
            completions.addAll(this.completionKeys);
        }
        return completions;
    }
}
