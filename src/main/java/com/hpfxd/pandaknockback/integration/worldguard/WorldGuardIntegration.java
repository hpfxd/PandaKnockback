package com.hpfxd.pandaknockback.integration.worldguard;

import com.hpfxd.pandaknockback.internal.PersistedProfileService;
import com.hpfxd.pandaknockback.profile.KnockbackProfileService;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import org.bukkit.Bukkit;

public class WorldGuardIntegration {
    public static final StringFlag FLAG = new StringFlag("knockback-profile");

    public static void registerFlag() {
        try {
            WGBukkit.getPlugin().getFlagRegistry().register(FLAG);
        } catch (FlagConflictException ignored) {
        }
    }

    public static void registerHandler() {
        final KnockbackProfileService registeredService = Bukkit.getServicesManager().load(KnockbackProfileService.class);

        if (registeredService instanceof PersistedProfileService) {
            final PersistedProfileService profileService = (PersistedProfileService) registeredService;

            WGBukkit.getPlugin().getSessionManager().registerHandler(new ProfileFlagHandler.Factory(profileService), null);
        }
    }
}
