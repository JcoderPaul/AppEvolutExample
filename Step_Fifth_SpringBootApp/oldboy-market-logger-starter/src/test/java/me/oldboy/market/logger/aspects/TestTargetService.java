package me.oldboy.market.logger.aspects;

import me.oldboy.market.logger.annotation.EnableLog;

public class TestTargetService {

    @EnableLog
    public int performOperation(int value) throws InterruptedException {
        Thread.sleep(50);
        return value * 2;
    }

    public int unLoggedOperation(int value) {
        return value + 1;
    }
}
