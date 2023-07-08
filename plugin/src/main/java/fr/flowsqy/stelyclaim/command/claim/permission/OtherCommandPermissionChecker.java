package fr.flowsqy.stelyclaim.command.claim.permission;

import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import org.jetbrains.annotations.NotNull;

public interface OtherCommandPermissionChecker extends CommandPermissionChecker {

    boolean checkOther(@NotNull CommandContext<ClaimContext> context);

}
