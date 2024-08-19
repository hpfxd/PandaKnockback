package com.hpfxd.pandaknockback.command;

import com.hpfxd.pandaknockback.KnockbackPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandExecutionHandler implements TabExecutor {
    private final KnockbackPlugin plugin;
    private final Collection<Subcommand> subcommands;
    private final Map<String, Subcommand> aliasSubcommandMap;

    public CommandExecutionHandler(KnockbackPlugin plugin, Collection<Subcommand> subcommands) {
        this.plugin = plugin;
        this.subcommands = subcommands;

        this.aliasSubcommandMap = new HashMap<>();
        for (final Subcommand subcommand : subcommands) {
            for (final String alias : subcommand.getAliases()) {
                this.aliasSubcommandMap.put(alias, subcommand);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            final PluginDescriptionFile description = this.plugin.getDescription();
            sender.sendMessage(ChatColor.GOLD + description.getFullName() + ChatColor.GREEN + " by " + String.join(", ", description.getAuthors()));

            for (final Subcommand subcommand : this.subcommands) {
                if (subcommand.canUse(sender)) {
                    sender.sendMessage(ChatColor.RED + "/" + label + " " + subcommand.getUsage(subcommand.getName()) + ChatColor.GRAY + " - " + subcommand.getDescription());
                }
            }

            return true;
        } else {
            final String subcommandAlias = args[0];
            final Subcommand subcommand = this.aliasSubcommandMap.get(subcommandAlias);

            if (subcommand == null) {
                sender.sendMessage(ChatColor.RED + "Unknown subcommand");
                return true;
            } else if (!subcommand.canUse(sender)) {
                sender.sendMessage(ChatColor.RED + "You cannot use this subcommand");
                return true;
            } else {
                final boolean success = subcommand.onCommand(sender, command, label, args);
                if (!success) {
                    sender.sendMessage(ChatColor.RED + "Usage: /" + label + " " + subcommand.getUsage(subcommandAlias));
                }

                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            final List<String> subcommands = new ArrayList<>();

            for (final Subcommand subcommand : this.subcommands) {
                if (subcommand.canUse(sender)) {
                    subcommands.addAll(subcommand.getAliases());
                }
            }

            return subcommands;
        } else if (args.length >= 2) {
            final Subcommand subcommand = this.aliasSubcommandMap.get(args[0]);

            if (subcommand != null && subcommand.canUse(sender)) {
                return subcommand.onTabComplete(sender, command, alias, args);
            }
        }

        return Collections.emptyList();
    }
}
