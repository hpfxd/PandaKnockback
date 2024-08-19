package com.hpfxd.pandaknockback.profile.value;

import java.util.Random;

public class UniformRandomValue implements ValueSource {
    private final Random random;
    private final double min;
    private final double max;

    public UniformRandomValue(double min, double max) {
        this(new Random(), min, max);
    }

    public UniformRandomValue(Random random, double min, double max) {
        this.random = random;
        this.min = min;
        this.max = max;
    }

    @Override
    public double getAsDouble() {
        return this.random.nextDouble() * (this.max - this.min) + this.min;
    }

    @Override
    public String toString() {
        return "[" + this.min + "-" + this.max + "]";
    }
}
