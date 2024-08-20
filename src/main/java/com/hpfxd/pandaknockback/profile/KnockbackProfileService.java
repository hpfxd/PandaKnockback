package com.hpfxd.pandaknockback.profile;

import org.bukkit.entity.Player;

/**
 * Responsible for retrieving and setting the knockback profile of players.
 */
public interface KnockbackProfileService {
    KnockbackProfile getProfile(Player player);

    void setProfile(Player player, KnockbackProfile profile);
}
