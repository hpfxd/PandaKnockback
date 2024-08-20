package com.hpfxd.pandaknockback.command;

import com.hpfxd.pandaknockback.internal.PersistedProfile;
import com.hpfxd.pandaknockback.internal.PersistedProfileService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApplyProfileSubcommand extends Subcommand {
    private final PersistedProfileService profileService;

    public ApplyProfileSubcommand(PersistedProfileService profileService) {
        super(
                Collections.singletonList("applyprofile"),
                "pandaknockback.applyprofile",
                "Apply a saved profile to a player",
                "<command> <player> <profile>"
        );
        this.profileService = profileService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 3) {
            return false;
        }

        final Player player = Bukkit.getPlayer(args[1]);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Player not found");
            return true;
        }

        final PersistedProfile profile = this.profileService.getLoadedProfile(args[2]);
        if (profile == null) {
            sender.sendMessage(ChatColor.RED + "Profile not found");
            return true;
        }

        this.profileService.setProfile(player, profile);
        sender.sendMessage(ChatColor.GREEN + "Applied profile " + ChatColor.YELLOW + profile.getName());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        final List<String> completions = new ArrayList<>();
        if (args.length == 2) {
            for (final Player player : Bukkit.getOnlinePlayers()) {
                if (!(sender instanceof Player) || ((Player) sender).canSee(player)) {
                    completions.add(player.getName());
                }
            }
        } else if (args.length == 3) {
            for (final PersistedProfile profile : this.profileService.getLoadedProfiles()) {
                completions.add(profile.getName());
            }
        }
        return completions;
    }
}
