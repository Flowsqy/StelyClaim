package fr.flowsqy.stelyclaim.command.subcommand;

import org.bukkit.command.CommandSender;

import java.util.List;

public class HelpSubCommand extends SubCommand {

    private final List<SubCommand> subCommands;

    public HelpSubCommand(String name, String alias, String permission, boolean stats, boolean console, List<SubCommand> subCommands) {
        super(name, alias, permission, stats, console);
        this.subCommands = subCommands;
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {

    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args) {
        return null;
    }

}
