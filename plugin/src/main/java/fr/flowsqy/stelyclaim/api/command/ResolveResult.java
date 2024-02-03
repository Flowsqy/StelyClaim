package fr.flowsqy.stelyclaim.api.command;

import org.jetbrains.annotations.NotNull;

public record ResolveResult(@NotNull CommandTree tree, boolean found, boolean success) {
}

