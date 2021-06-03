package com.pnxtest.syncloud;

import java.util.concurrent.TimeUnit;

public class Threads {
    public static void sleepQuietly(long sleepFor, TimeUnit unit) {
        try {
            Thread.sleep(unit.toMillis(sleepFor));
        } catch (Throwable var4) {
        }

    }

    private Threads() {
    }
}
