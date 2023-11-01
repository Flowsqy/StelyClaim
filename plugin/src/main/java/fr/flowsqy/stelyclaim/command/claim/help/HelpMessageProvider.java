package fr.flowsqy.stelyclaim.command.claim.help;

import fr.flowsqy.stelyclaim.api.command.CommandContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface HelpMessageProvider {

    @Nullable
    String get(@NotNull CommandContext context);

}
