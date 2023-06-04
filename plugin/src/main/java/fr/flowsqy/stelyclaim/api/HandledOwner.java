package fr.flowsqy.stelyclaim.api;

import org.jetbrains.annotations.NotNull;

public record HandledOwner<T extends ClaimOwner>(@NotNull ClaimHandler<T> handler, T owner) {
}
