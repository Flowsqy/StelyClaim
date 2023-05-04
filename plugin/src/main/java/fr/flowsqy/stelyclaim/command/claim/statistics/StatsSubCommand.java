package fr.flowsqy.stelyclaim.command.claim.statistics;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.command.claim.OtherSubCommand;
import fr.flowsqy.stelyclaim.command.claim.SubCommand;
import fr.flowsqy.stelyclaim.io.StatisticManager;
import org.bukkit.command.CommandSender;

import java.util.*;

public class StatsSubCommand extends OtherSubCommand {

    private final ResetStatsSubCommand resetStatsSubCommand;
    private final ShowStatsSubCommand showStatsSubCommand;

    public StatsSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, boolean statistic, StatisticManager statisticManager) {
        super(
                plugin.getMessages(),
                name,
                alias,
                permission,
                console,
                getAllWorlds(
                        plugin.getConfiguration().getStringList("worlds.stats.reset"),
                        plugin.getConfiguration().getStringList("worlds.stats.show")
                ),
                statistic
        );
        this.resetStatsSubCommand = new ResetStatsSubCommand(
                plugin,
                "reset",
                "r",
                permission + ".reset",
                console,
                plugin.getConfiguration().getStringList("worlds.stats.reset"),
                statistic,
                statisticManager
        );
        this.showStatsSubCommand = new ShowStatsSubCommand(
                plugin,
                "show",
                "s",
                permission + ".show",
                console,
                plugin.getConfiguration().getStringList("worlds.stats.show"),
                statistic,
                statisticManager
        );
    }

    private static List<String> getAllWorlds(List<String> first, List<String> second) {
        if (first.isEmpty() || second.isEmpty())
            return new ArrayList<>();
        final Set<String> worlds = new HashSet<>(first);
        worlds.addAll(second);
        return new ArrayList<>(worlds);
    }

    public void initSubCommands(List<SubCommand> subCommands) {
        resetStatsSubCommand.initSubCommands(subCommands);
        showStatsSubCommand.initSubCommands(subCommands);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args, int size, boolean isPlayer) {
        if (size > 1) {
            final SubStatsSubCommand subCommand = getSubCommand(args.get(1));
            if (subCommand != null && sender.hasPermission(subCommand.getPermission()))
                return subCommand.execute(sender, args, size, isPlayer);
        }
        messages.sendMessage(sender, "help." + getName() + (sender.hasPermission(getPermission() + "-other") ? "-other" : ""));
        return false;
    }

    private boolean matchSubCommand(SubStatsSubCommand subCommand, String arg) {
        return subCommand.getName().equalsIgnoreCase(arg) || subCommand.getAlias().equalsIgnoreCase(arg);
    }

    private SubStatsSubCommand getSubCommand(String arg) {
        return
                matchSubCommand(resetStatsSubCommand, arg) ?
                        resetStatsSubCommand : (
                        matchSubCommand(showStatsSubCommand, arg) ?
                                showStatsSubCommand : null
                );
    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        if (args.size() > 1) {
            final String arg = args.get(1);
            final SubStatsSubCommand subCommand = getSubCommand(arg);
            if (subCommand != null && args.size() > 2 && sender.hasPermission(subCommand.getPermission())) {
                return subCommand.tab(sender, args, isPlayer);
            }
            final List<String> completions = new ArrayList<>();
            fillList(sender, completions, arg, resetStatsSubCommand);
            fillList(sender, completions, arg, showStatsSubCommand);
            return completions;
        }
        return Collections.emptyList();
    }

    private void fillList(CommandSender sender, List<String> completions, String arg, SubStatsSubCommand subCommand) {
        if (subCommand.getName().startsWith(arg.toLowerCase(Locale.ROOT)) && sender.hasPermission(subCommand.getPermission()))
            completions.add(subCommand.getName());
    }

}
