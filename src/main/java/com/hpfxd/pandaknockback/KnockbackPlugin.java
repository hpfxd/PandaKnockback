package com.hpfxd.pandaknockback;

import com.hpfxd.pandaknockback.command.ApplyProfileSubcommand;
import com.hpfxd.pandaknockback.command.CommandExecutionHandler;
import com.hpfxd.pandaknockback.command.EditProfileSubcommand;
import com.hpfxd.pandaknockback.command.ReloadSubcommand;
import com.hpfxd.pandaknockback.command.Subcommand;
import com.hpfxd.pandaknockback.integration.worldguard.WorldGuardIntegration;
import com.hpfxd.pandaknockback.internal.PersistedProfileService;
import com.hpfxd.pandaknockback.profile.KnockbackProfileService;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class KnockbackPlugin extends JavaPlugin {
    private static final int METRICS_ID = 23110;

    @Override
    public void onLoad() {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            WorldGuardIntegration.registerFlag();
        }
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.reloadConfig();

        this.setupAttackListener();
        this.setupProfileService();
        this.registerCommand();

        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            WorldGuardIntegration.registerHandler();
        }

        this.setupMetrics();
    }

    private void setupAttackListener() {
        final AttackListener attackListener = new AttackListener();

        Bukkit.getPluginManager().registerEvents(attackListener, this);
        Bukkit.getScheduler().runTaskTimer(this, attackListener, 0, 0);
    }

    private void setupProfileService() {
        final PersistedProfileService profileService = new PersistedProfileService(this);
        Bukkit.getPluginManager().registerEvents(profileService, this);
        Bukkit.getServicesManager().register(KnockbackProfileService.class, profileService, this, ServicePriority.Lowest);

        final ProfileApplicationHandler applicationHandler = new ProfileApplicationHandler(Bukkit.getServicesManager().load(KnockbackProfileService.class));
        Bukkit.getPluginManager().registerEvents(applicationHandler, this);
    }

    private void registerCommand() {
        final List<Subcommand> subcommands = new ArrayList<>();

        final KnockbackProfileService registeredService = Bukkit.getServicesManager().load(KnockbackProfileService.class);
        if (registeredService instanceof PersistedProfileService) {
            final PersistedProfileService profileService = (PersistedProfileService) registeredService;

            // these subcommands require the built-in ProfileService to be used

            subcommands.add(new ApplyProfileSubcommand(profileService));
            subcommands.add(new EditProfileSubcommand(profileService, YamlConfiguration.loadConfiguration(this.getTextResource("profiles.yml")).getConfigurationSection("default")));
            subcommands.add(new ReloadSubcommand(profileService));
        }

        this.getCommand("pandaknockback").setExecutor(new CommandExecutionHandler(this, subcommands));
    }

    private void setupMetrics() {
        new Metrics(this, METRICS_ID);
    }
}
