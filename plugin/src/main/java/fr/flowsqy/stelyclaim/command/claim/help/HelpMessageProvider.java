package fr.flowsqy.stelyclaim.command.claim.help;

import fr.flowsqy.stelyclaim.api.command.CommandContext;
import org.jetbrains.annotations.NotNull;

public interface HelpMessageProvider {

    String get(@NotNull CommandContext context);

}
