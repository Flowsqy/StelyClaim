package fr.flowsqy.stelyclaim.command.claim.statistics;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.command.claim.help.HelpMessage;
import fr.flowsqy.stelyclaim.command.claim.permission.OtherCommandPermissionChecker;

public class ResetStatsSubCommand extends SubStatsSubCommand {

    public ResetStatsSubCommand(@NotNull UUID id, @NotNull String name, @NotNull String[] triggers,
            @NotNull StelyClaimPlugin plugin, @NotNull OtherCommandPermissionChecker permChecker,
            @NotNull HelpMessage helpMessage) {
        super(id, name, triggers, plugin, permChecker, helpMessage);
    }

    @Override
    protected boolean process(@NotNull CommandContext context, boolean own, @Nullable String command,
            @NotNull OfflinePlayer target) {
        final String other = own ? "" : "-other";
        final CommandSender sender = context.getActor().getBukkit();
        final UUID targetId = target.getUniqueId();
        final String targetName = target.getName();
        if (command == null) {
            if (!statisticManager.remove(targetId)) {
                messages.sendMessage(sender, "claim.stats.nodata-all" + other, "%target%", targetName);
                return false;
            }
            messages.sendMessage(sender, "claim.stats.reset-all" + other, "%target%", targetName);
            statisticManager.saveTask();
            return true;
        }
        if (!statisticManager.removeStat(targetId, command)) {
            messages.sendMessage(sender, "claim.stats.nodata" + other, "%target%", "%command%", targetName, command);
            return false;
        }
        messages.sendMessage(sender, "claim.stats.reset" + other, "%target%", "%command%", targetName, command);
        statisticManager.saveTask();
        return true;
    }

}
