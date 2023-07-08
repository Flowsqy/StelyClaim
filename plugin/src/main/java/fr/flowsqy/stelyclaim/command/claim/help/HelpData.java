package fr.flowsqy.stelyclaim.command.claim.help;

import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public record HelpData(@NotNull String command, boolean contextual,
                       @NotNull Function<CommandContext<ClaimContext>, String> messageProvider) {
}
