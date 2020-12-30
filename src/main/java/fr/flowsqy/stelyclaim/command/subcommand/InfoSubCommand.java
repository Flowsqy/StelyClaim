package fr.flowsqy.stelyclaim.command.subcommand;

import org.bukkit.command.CommandSender;

import java.util.List;

public class InfoSubCommand extends SubCommand {
    public InfoSubCommand(String name, String alias, String permission, boolean stats, boolean console) {
        super(name, alias, permission, stats, console);
    }

    @Override
    public void execute(CommandSender sender, List<String> args) {

    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args) {
        return null;
    }
}
