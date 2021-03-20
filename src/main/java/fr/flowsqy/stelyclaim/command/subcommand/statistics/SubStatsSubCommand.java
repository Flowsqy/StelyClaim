package fr.flowsqy.stelyclaim.command.subcommand.statistics;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.command.subcommand.SubCommand;
import fr.flowsqy.stelyclaim.io.StatisticManager;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class SubStatsSubCommand extends SubCommand {

    protected final StatisticManager statisticManager;
    private final Set<String> commandsName;

    public SubStatsSubCommand(StelyClaimPlugin plugin, String name, String alias, String permission, boolean console, List<String> allowedWorlds, boolean statistic, StatisticManager statisticManager) {
        super(plugin, name, alias, permission, console, allowedWorlds, statistic);
        this.statisticManager = statisticManager;
        this.commandsName = new HashSet<>();
    }

    public void initSubCommands(List<SubCommand> subCommands){
        this.commandsName.clear();
        subCommands.stream()
                .map(SubCommand::getName)
                .forEach(commandsName::add);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args, int size, boolean isPlayer) {
        final boolean own;
        final String command;
        final String target;
        switch (size){
            case 2:
                own = true;
                command = null;
                target = null;
                break;
            case 3:
                own = true;
                command = args.get(2);
                target = null;
                break;
            case 4:
                command = args.get(2);
                target = args.get(3);
                own = target.equals(sender.getName());
                break;
            default:
                messages.sendMessage(sender, "help."+getName()+(sender.hasPermission(getPermission()+"-other") ? "-other" : ""));
                return false;
        }
        if(!own && !sender.hasPermission(getPermission()+"-other")){
            messages.sendMessage(sender, "help."+getName());
            return false;
        }
        if(command != null){
            if(!commandsName.contains(command)){
                messages.sendMessage(sender, "claim.stats.commandnotexist", "%command%", command);
                return false;
            }
            if(!statisticManager.allowStats(command)){
                messages.sendMessage(sender, "claim.stats.commandnotstat", "%command%", command);
                return false;
            }
        }
        return executeSub(sender, own, command, target);
    }

    protected abstract boolean executeSub(CommandSender sender, boolean own, String command, String target);

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        return null;
    }
}
