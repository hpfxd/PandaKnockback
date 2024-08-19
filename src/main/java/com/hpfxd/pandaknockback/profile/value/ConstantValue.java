package com.hpfxd.pandaknockback.profile.value;

public class ConstantValue implements ValueSource {
    private final double constant;

    public ConstantValue(double constant) {
        this.constant = constant;
    }

    @Override
    public double getAsDouble() {
        return this.constant;
    }

    @Override
    public String toString() {
        return String.valueOf(this.constant);
    }
}
