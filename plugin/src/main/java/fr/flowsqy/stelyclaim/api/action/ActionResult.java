package fr.flowsqy.stelyclaim.api.action;

import java.util.concurrent.atomic.AtomicInteger;

public record ActionResult(int code, boolean success, int codeModifier) {

    public ActionResult(int code, boolean success) {
        this(code, success, 0);
    }

    public boolean isModifier(int modifier) {
        return (codeModifier & modifier) != 0;
    }

    private final static AtomicInteger actionResultCounter;

    static {
        actionResultCounter = new AtomicInteger();
    }

    public static int registerResultCode() {
        return actionResultCounter.getAndIncrement();
    }

    public static int getModifier(int... modifiers) {
        int out = 0;
        for (int mod : modifiers) {
            out |= mod;
        }
        return out;
    }

}