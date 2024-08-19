package com.hpfxd.pandaknockback.command;

import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

public abstract class Subcommand implements TabExecutor {
    private final List<String> aliases;
    private final String permission;
    private final String description;
    private final String usage;

    public Subcommand(List<String> aliases, String permission, String description, String usage) {
        this.aliases = aliases;
        this.permission = permission;
        this.description = description;
        this.usage = usage;
    }

    public String getName() {
        return this.aliases.get(0);
    }

    public List<String> getAliases() {
        return this.aliases;
    }

    public String getPermission() {
        return this.permission;
    }

    public boolean canUse(CommandSender sender) {
        return sender.hasPermission(this.getPermission());
    }

    public String getDescription() {
        return this.description;
    }

    public String getUsage(String label) {
        return this.usage.replace("<command>", label);
    }
}
