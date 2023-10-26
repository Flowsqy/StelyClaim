package fr.flowsqy.stelyclaim.api.action;

import fr.flowsqy.stelyclaim.api.LockableCounter;

public record ActionResult(int code, boolean success) {

    public final static LockableCounter REGISTER = new LockableCounter();

}
