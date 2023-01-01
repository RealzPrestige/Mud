package dev.zprestige.mud.util.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtil {

    public static float gaussian(float x, float s) {
        double output = 1.0 / Math.sqrt(2.0 * Math.PI * (s * s));
        return (float) (output * Math.exp(-(x * x) / (2.0 * (s * s))));
    }

    public static float clamp(float num, float min, float max) {
        return num < min ? min : Math.min(num, max);
    }

    public static float lerp(float current, float target, float lerp) {
        return current - ((current - target) * clamp(lerp, 0, 1));
    }


    public static float roundNumber(float value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();
        BigDecimal decimal = BigDecimal.valueOf(value);
        decimal = decimal.setScale(places, RoundingMode.FLOOR);
        return decimal.floatValue();
    }

}
