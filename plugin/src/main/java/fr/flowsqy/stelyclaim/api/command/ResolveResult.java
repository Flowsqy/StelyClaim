package fr.flowsqy.stelyclaim.api.command;

import org.jetbrains.annotations.NotNull;

public record ResolveResult(@NotNull CommandNode node, boolean found, boolean success) {
}

