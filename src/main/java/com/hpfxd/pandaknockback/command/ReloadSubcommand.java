package com.hpfxd.pandaknockback.command;

import com.hpfxd.pandaknockback.internal.ProfileService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ReloadSubcommand extends Subcommand {
    private final ProfileService profileService;

    public ReloadSubcommand(ProfileService profileService) {
        super(
                Collections.singletonList("reload"),
                "pandaknockback.reload",
                "Reload knockback profiles",
                "<command>"
        );
        this.profileService = profileService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            return false;
        }

        try {
            this.profileService.loadStorage();
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException("Failed to load profiles", e);
        }

        if (this.profileService.readProfiles()) {
            sender.sendMessage(ChatColor.GREEN + "All profiles were loaded successfully");
        } else {
            sender.sendMessage(ChatColor.RED + "One or more profiles failed to load. Check console for details");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
