package fr.flowsqy.stelyclaim.command.claim.help;

import org.jetbrains.annotations.NotNull;

import fr.flowsqy.stelyclaim.api.command.CommandContext;

public interface HelpMessageProvider {

    String get(@NotNull CommandContext context);

}
