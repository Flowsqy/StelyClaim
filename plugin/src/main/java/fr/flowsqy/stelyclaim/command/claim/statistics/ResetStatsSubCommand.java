package fr.flowsqy.stelyclaim.command.claim.statistics;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.command.claim.ClaimContextData;
import fr.flowsqy.stelyclaim.command.struct.CommandContext;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ResetStatsSubCommand extends SubStatsSubCommand {

    private final static String NAME = "reset";
    private final static String[] TRIGGERS = new String[]{NAME, "r"};

    public ResetStatsSubCommand(@NotNull StelyClaimPlugin plugin) {
        super(NAME, TRIGGERS, plugin);
    }

    @Override
    protected boolean process(@NotNull CommandContext<ClaimContextData> context, boolean own, @Nullable String command, @NotNull OfflinePlayer target) {
        final String other = own ? "" : "-other";
        final CommandSender sender = context.getSender().getBukkit();
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
