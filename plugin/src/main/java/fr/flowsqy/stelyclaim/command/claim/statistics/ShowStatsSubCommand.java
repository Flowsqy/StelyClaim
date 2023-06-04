package fr.flowsqy.stelyclaim.command.claim.statistics;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.command.claim.ClaimContextData;
import fr.flowsqy.stelyclaim.command.struct.CommandContext;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ShowStatsSubCommand extends SubStatsSubCommand {

    private final static String NAME = "show";
    private final static String[] TRIGGERS = new String[]{NAME, "s"};

    public ShowStatsSubCommand(@NotNull StelyClaimPlugin plugin) {
        super(NAME, TRIGGERS, plugin);
    }

    @Override
    protected boolean process(@NotNull CommandContext<ClaimContextData> context, boolean own, @Nullable String command, @NotNull OfflinePlayer target) {
        final CommandSender sender = context.getSender().getBukkit();
        final String targetName = target.getName();
        final UUID targetId = target.getUniqueId();
        final String other = (own ? "" : "-other");
        if (command != null) {
            messages.sendMessage(
                    sender,
                    "claim.stats.show" + other,
                    "%command%", "%target%", "%stat%",
                    command, targetName, String.valueOf(statisticManager.get(targetId, command))
            );
        }
        final String path = "claim.stats.show" + other;
        for (String statCommand : statisticManager.getCommands()) {
            messages.sendMessage(
                    sender,
                    path,
                    "%command%", "%target%", "%stat%",
                    statCommand, targetName, String.valueOf(statisticManager.get(targetId, statCommand))
            );
        }
        messages.sendMessage(
                sender,
                "claim.stats.show-total" + other,
                "%target%", "%stat%",
                targetName, String.valueOf(statisticManager.getTotal(targetId))
        );
        return true;
    }
}
