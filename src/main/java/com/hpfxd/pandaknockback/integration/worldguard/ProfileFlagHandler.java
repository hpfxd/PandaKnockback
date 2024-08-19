package com.hpfxd.pandaknockback.integration.worldguard;

import com.hpfxd.pandaknockback.internal.PersistedProfile;
import com.hpfxd.pandaknockback.internal.ProfileService;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.session.MoveType;
import com.sk89q.worldguard.session.Session;
import com.sk89q.worldguard.session.handler.Handler;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Set;

public class ProfileFlagHandler extends Handler {
    private final ProfileService profileService;

    @Override
    public void initialize(Player player, Location current, ApplicableRegionSet set) {
        final PersistedProfile profile = this.getProfile(player, set);

        this.profileService.setProfile(player, profile);
    }

    @Override
    public boolean onCrossBoundary(Player player, Location from, Location to, ApplicableRegionSet toSet, Set<ProtectedRegion> entered, Set<ProtectedRegion> exited, MoveType moveType) {
        final PersistedProfile profile = this.getProfile(player, toSet);

        this.profileService.setProfile(player, profile);
        return true;
    }

    private PersistedProfile getProfile(Player player, ApplicableRegionSet set) {
        final String profileName = set.queryValue(WGBukkit.getPlugin().wrapPlayer(player), WorldGuardIntegration.FLAG);

        if (profileName == null) {
            return null;
        } else {
            return this.profileService.getLoadedProfile(profileName);
        }
    }

    public ProfileFlagHandler(ProfileService profileService, Session session) {
        super(session);
        this.profileService = profileService;
    }

    public static class Factory extends Handler.Factory<ProfileFlagHandler> {
        private final ProfileService profileService;

        public Factory(ProfileService profileService) {
            this.profileService = profileService;
        }

        @Override
        public ProfileFlagHandler create(Session session) {
            return new ProfileFlagHandler(this.profileService, session);
        }
    }
}
