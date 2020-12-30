package fr.flowsqy.stelyclaim.command.subcommand;

import org.bukkit.command.CommandSender;

import java.util.List;

public class HelpSubCommand extends SubCommand {

    public HelpSubCommand(String name, String alias, String permission, boolean other, boolean stats, boolean console) {
        super(name, alias, permission, other, stats, console);
    }

    @Override
    public void execute(CommandSender sender, String arg, List<String> args) {

    }

    @Override
    public List<String> tab(CommandSender sender, String arg, List<String> args) {
        return null;
    }

}
