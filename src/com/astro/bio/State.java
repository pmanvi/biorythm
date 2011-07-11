package com.astro.bio;

public enum State {

    Excellent, Good, Averege, Poor, NA;

    public static State state(double p) {
        if (p > 70) {
            return Excellent;
        }
        if (p > 60) {
            return Good;
        }
        if (p > 50) {
            return Averege;
        }
        if (p < 50) {
            return Poor;
        }
        return NA;
    }
}
