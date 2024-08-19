package com.hpfxd.pandaknockback.profile;

import org.bukkit.entity.Player;

public interface KnockbackProfileService {
    KnockbackProfile getProfile(Player player);

    void setProfile(Player player, KnockbackProfile profile);
}
