package fr.flowsqy.stelyclaim.api.action;

import fr.flowsqy.stelyclaim.api.LockableCounter;

public record ActionResult(int code, boolean success, int codeModifier) {

    public final static LockableCounter REGISTER = new LockableCounter();

    public ActionResult(int code, boolean success) {
        this(code, success, 0);
    }

    public static int getModifier(int... modifiers) {
        int out = 0;
        for (int mod : modifiers) {
            out |= mod;
        }
        return out;
    }

    public boolean isModifier(int modifier) {
        return (codeModifier & modifier) != 0;
    }

}
