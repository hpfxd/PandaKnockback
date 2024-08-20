package com.hpfxd.pandaknockback.profile;

import com.hpfxd.pandaknockback.profile.value.ValueSource;

// oh how I hate Java 8 compatibility (this was a record)
public final class KnockbackSettings {
    private final ValueSource baseHorizontal;
    private final ValueSource baseVertical;
    private final ValueSource sprintBonusHorizontal;
    private final ValueSource sprintBonusVertical;
    private final ValueSource enchantmentHorizontal;
    private final ValueSource enchantmentVertical;
    private final ValueSource preMultiplierHorizontal;
    private final ValueSource preMultiplierVertical;
    private final ValueSource limitVertical;

    public KnockbackSettings(ValueSource baseHorizontal, ValueSource baseVertical, ValueSource sprintBonusHorizontal, ValueSource sprintBonusVertical, ValueSource enchantmentHorizontal, ValueSource enchantmentVertical, ValueSource preMultiplierHorizontal, ValueSource preMultiplierVertical, ValueSource limitVertical) {
        this.baseHorizontal = baseHorizontal;
        this.baseVertical = baseVertical;
        this.sprintBonusHorizontal = sprintBonusHorizontal;
        this.sprintBonusVertical = sprintBonusVertical;
        this.enchantmentHorizontal = enchantmentHorizontal;
        this.enchantmentVertical = enchantmentVertical;
        this.preMultiplierHorizontal = preMultiplierHorizontal;
        this.preMultiplierVertical = preMultiplierVertical;
        this.limitVertical = limitVertical;
    }

    public ValueSource getBaseHorizontal() {
        return this.baseHorizontal;
    }

    public ValueSource getBaseVertical() {
        return this.baseVertical;
    }

    public ValueSource getSprintBonusHorizontal() {
        return this.sprintBonusHorizontal;
    }

    public ValueSource getSprintBonusVertical() {
        return this.sprintBonusVertical;
    }

    public ValueSource getEnchantmentHorizontal() {
        return this.enchantmentHorizontal;
    }

    public ValueSource getEnchantmentVertical() {
        return this.enchantmentVertical;
    }

    public ValueSource getPreMultiplierHorizontal() {
        return this.preMultiplierHorizontal;
    }

    public ValueSource getPreMultiplierVertical() {
        return this.preMultiplierVertical;
    }

    public ValueSource getLimitVertical() {
        return this.limitVertical;
    }
}
