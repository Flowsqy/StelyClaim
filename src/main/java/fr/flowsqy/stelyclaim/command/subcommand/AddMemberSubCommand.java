package fr.flowsqy.stelyclaim.command.subcommand;

import org.bukkit.command.CommandSender;

import java.util.List;

public class AddMemberSubCommand extends SubCommand {
    public AddMemberSubCommand(String name, String alias, String permission, boolean stats, boolean console) {
        super(name, alias, permission, stats, console);
    }

    @Override
    public void execute(CommandSender sender, List<String> args, boolean isPlayer) {

    }

    @Override
    public List<String> tab(CommandSender sender, List<String> args, boolean isPlayer) {
        return null;
    }
}
