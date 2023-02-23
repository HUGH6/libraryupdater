package util;

import java.util.Random;

public class RandomManager {

    private static Random randomNumberGenerator = null;

    static {
        randomNumberGenerator = new Random(0);
    }

    public static Integer nextInt(int bound) {
        return randomNumberGenerator.nextInt(bound);
    }

    public static Double nextDouble() {
        return randomNumberGenerator.nextDouble();
    }
}

