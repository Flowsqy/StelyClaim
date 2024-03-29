package fr.flowsqy.stelyclaim.command.claim.statistics;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.permission.OtherPermissionChecker;
import fr.flowsqy.stelyclaim.command.claim.help.HelpMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ShowStatsSubCommand extends SubStatsSubCommand {

    public ShowStatsSubCommand(@NotNull UUID id, @NotNull String name, @NotNull String[] triggers,
                               @NotNull StelyClaimPlugin plugin, @NotNull OtherPermissionChecker permChecker,
                               @NotNull HelpMessage helpMessage) {
        super(id, name, triggers, plugin, permChecker, helpMessage);
    }

    @Override
    protected boolean process(@NotNull CommandContext context, boolean own, @Nullable String command,
                              @NotNull OfflinePlayer target) {
        final CommandSender sender = context.getActor().getBukkit();
        final String targetName = target.getName();
        final UUID targetId = target.getUniqueId();
        final String other = (own ? "" : "-other");
        if (command != null) {
            messages.sendMessage(
                    sender,
                    "claim.stats.show" + other,
                    "%command%", "%target%", "%stat%",
                    command, targetName, String.valueOf(statisticManager.get(targetId, command)));
        }
        final String path = "claim.stats.show" + other;
        for (String statCommand : statisticManager.getCommands()) {
            messages.sendMessage(
                    sender,
                    path,
                    "%command%", "%target%", "%stat%",
                    statCommand, targetName, String.valueOf(statisticManager.get(targetId, statCommand)));
        }
        messages.sendMessage(
                sender,
                "claim.stats.show-total" + other,
                "%target%", "%stat%",
                targetName, String.valueOf(statisticManager.getTotal(targetId)));
        return true;
    }
}
