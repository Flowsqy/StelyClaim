package fr.flowsqy.stelyclaim.api.command;

import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public record ResolveResult(@NotNull Optional<CommandNode> node, boolean found) {
}

