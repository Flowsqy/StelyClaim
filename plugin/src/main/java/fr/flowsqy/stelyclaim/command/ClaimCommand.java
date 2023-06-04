package fr.flowsqy.stelyclaim.command;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.command.claim.*;
import fr.flowsqy.stelyclaim.command.claim.domain.AddMemberSubCommand;
import fr.flowsqy.stelyclaim.command.claim.domain.AddOwnerSubCommand;
import fr.flowsqy.stelyclaim.command.claim.domain.RemoveMemberSubCommand;
import fr.flowsqy.stelyclaim.command.claim.domain.RemoveOwnerSubCommand;
import fr.flowsqy.stelyclaim.command.claim.interact.InfoSubCommand;
import fr.flowsqy.stelyclaim.command.claim.interact.RemoveSubCommand;
import fr.flowsqy.stelyclaim.command.claim.interact.TeleportSubCommand;
import fr.flowsqy.stelyclaim.command.claim.selection.DefineSubCommand;
import fr.flowsqy.stelyclaim.command.claim.selection.RedefineSubCommand;
import fr.flowsqy.stelyclaim.command.claim.statistics.StatsSubCommand;
import fr.flowsqy.stelyclaim.command.sender.BlockCommandSender;
import fr.flowsqy.stelyclaim.command.sender.ConsoleCommandSender;
import fr.flowsqy.stelyclaim.command.sender.EntityCommandSender;
import fr.flowsqy.stelyclaim.command.struct.CommandContext;
import fr.flowsqy.stelyclaim.command.struct.CommandExecutor;
import fr.flowsqy.stelyclaim.command.struct.CommandTabExecutor;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import fr.flowsqy.stelyclaim.io.StatisticManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Entity;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ClaimCommand implements TabExecutor {

    private final ConfigurationFormattedMessages messages;
    private final StatisticManager statisticManager;
    private final List<SubCommand> subCommands;
    private final SubCommand helpSubCommand;
    private int tabLimit;
    private final CommandTabExecutor<ClaimContextData> commandTabExecutor;

    public ClaimCommand(StelyClaimPlugin plugin) {
        this.messages = plugin.getMessages();
        this.statisticManager = plugin.getStatisticManager();
        subCommands = new ArrayList<>();
        initCommands(plugin);
        this.statisticManager.initSubCommands(subCommands);
        helpSubCommand = subCommands.get(0);
        commandTabExecutor = new CommandTabExecutor<>() {
            @Override
            public void execute(@NotNull CommandContext<ClaimContextData> context) {

            }

            @Override
            public List<String> tabComplete(@NotNull CommandContext<ClaimContextData> context) {
                return null;
            }
        };
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        final fr.flowsqy.stelyclaim.command.sender.CommandSender commandSender = getSender(sender);
        final CommandContext<ClaimContextData> context = new CommandContext<>(commandSender, args, new ClaimContextData(), 0);

        // TODO Check for statistics
        commandTabExecutor.execute(context);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        final fr.flowsqy.stelyclaim.command.sender.CommandSender commandSender = getSender(sender);
        final CommandContext<ClaimContextData> context = new CommandContext<>(commandSender, args, new ClaimContextData(), 0);
        return commandTabExecutor.tabComplete(context);
    }

    @NotNull
    private fr.flowsqy.stelyclaim.command.sender.CommandSender getSender(@NotNull CommandSender sender) {
        if (sender instanceof Entity entity) {
            return new EntityCommandSender<>(entity);
        }
        if (sender instanceof org.bukkit.command.ConsoleCommandSender console) {
            return new ConsoleCommandSender(console);
        }
        if (sender instanceof org.bukkit.command.BlockCommandSender block) {
            return new BlockCommandSender(block);
        }
        throw new UnsupportedOperationException("Unsupported command sender type: " + sender.getClass());
    }

    /**
     * Initialize all internal sub-commands and fill subCommands array
     * The {@link HelpSubCommand} is the first in the list
     * Initialize the tab limit property
     *
     * @param plugin The {@link StelyClaimPlugin} instance to create all sub-commands
     */
    private void initCommands(StelyClaimPlugin plugin) {
        final Configuration config = plugin.getConfiguration();
        final HelpSubCommand helpSubCommand = new HelpSubCommand(
                plugin,
                "help",
                "h",
                Permissions.HELP,
                true,
                config.getStringList("worlds.help"),
                config.getBoolean("statistic.help"),
                this
        );
        subCommands.add(helpSubCommand);
        subCommands.add(new DefineSubCommand(
                plugin,
                "define",
                "d",
                Permissions.DEFINE,
                false,
                config.getStringList("worlds.define"),
                config.getBoolean("statistic.define")
        ));
        subCommands.add(new RedefineSubCommand(
                plugin,
                "redefine",
                "rd",
                Permissions.REDEFINE,
                false,
                config.getStringList("worlds.redefine"),
                config.getBoolean("statistic.redefine")
        ));
        subCommands.add(new AddMemberSubCommand(
                plugin,
                "addmember",
                "am",
                Permissions.ADDMEMBER,
                false,
                config.getStringList("worlds.addmember"),
                config.getBoolean("statistic.addmember")
        ));
        subCommands.add(new RemoveMemberSubCommand(
                plugin,
                "removemember",
                "rm",
                Permissions.REMOVEMEMBER,
                false,
                config.getStringList("worlds.removemember"),
                config.getBoolean("statistic.removemember")
        ));
        subCommands.add(new AddOwnerSubCommand(
                plugin,
                "addowner",
                "ao",
                Permissions.ADDOWNER,
                false,
                config.getStringList("worlds.addowner"),
                config.getBoolean("statistic.addowner")
        ));
        subCommands.add(new RemoveOwnerSubCommand(
                plugin,
                "removeowner",
                "ro",
                Permissions.REMOVEOWNER,
                false,
                config.getStringList("worlds.removeowner"),
                config.getBoolean("statistic.removeowner")
        ));
        subCommands.add(new RemoveSubCommand(
                plugin,
                "remove",
                "r",
                Permissions.REMOVE,
                false,
                config.getStringList("worlds.remove"),
                config.getBoolean("statistic.remove")
        ));
        subCommands.add(new InfoSubCommand(
                plugin,
                "info",
                "i",
                Permissions.INFO,
                false,
                config.getStringList("worlds.info"),
                config.getBoolean("statistic.info")
        ));
        subCommands.add(new TeleportSubCommand(
                plugin,
                "teleport",
                "tp",
                Permissions.TELEPORT,
                false,
                config.getStringList("worlds.teleport"),
                config.getBoolean("statistic.teleport")
        ));
        subCommands.add(new HereSubCommand(
                plugin,
                "here",
                "hr",
                Permissions.HERE,
                false,
                config.getStringList("worlds.here"),
                config.getBoolean("statistic.here")
        ));
        subCommands.add(new NearSubCommand(
                plugin,
                "near",
                "n",
                Permissions.NEAR,
                false,
                config.getStringList("worlds.near"),
                config.getBoolean("statistic.near")
        ));
        subCommands.add(new ListAddSubCommand(
                plugin,
                "listadd",
                "la",
                Permissions.LISTADD,
                false,
                config.getStringList("worlds.listadd"),
                config.getBoolean("statistic.listadd")
        ));
        final StatsSubCommand statsSubCommand = new StatsSubCommand(
                plugin,
                "stats",
                "s",
                Permissions.STATS,
                true,
                config.getBoolean("statistic.stats"),
                statisticManager
        );
        subCommands.add(statsSubCommand);
        tabLimit = subCommands.size();
        // End of tab commands
        subCommands.add(new PillarSubCommand(
                plugin,
                "pillar",
                "p",
                Permissions.PILLAR,
                false,
                config.getStringList("worlds.pillar"),
                config.getBoolean("statistic.pillar"),
                helpSubCommand
        ));
        // Load statistics tabs (should be at the end to count every sub-commands)
        statsSubCommand.initSubCommands(subCommands);
    }

    @SuppressWarnings("unused") // API
    public void registerCommand(SubCommand subCommand, boolean canTabComplete, Function<Permission, Permission> createPermissionFunction) {
        if (subCommands.stream().anyMatch(subCmd -> (
                subCmd.getName().equalsIgnoreCase(subCommand.getName()) ||
                        subCmd.getName().equalsIgnoreCase(subCommand.getAlias()) ||
                        subCmd.getAlias().equalsIgnoreCase(subCommand.getName()) ||
                        subCmd.getAlias().equalsIgnoreCase(subCommand.getAlias())
        ))) {
            throw new IllegalArgumentException("Can not register this subCommand, the name or the alias is already taken");
        }

        Permissions.registerPerm(subCommand, createPermissionFunction);

        if (canTabComplete) {
            subCommands.add(tabLimit, subCommand);
            tabLimit++;
        } else {
            subCommands.add(subCommand);
        }
    }

    @SuppressWarnings("unused") // API
    public void unregisterCommand(SubCommand subCommand) {
        int foundIndex = -1;
        for (int index = 0; index < subCommands.size(); index++) {
            if (subCommands.get(index) == subCommand) {
                foundIndex = index;
                break;
            }
        }
        if (foundIndex > -1) {
            if (foundIndex < tabLimit) {
                tabLimit--;
            }
            subCommands.remove(foundIndex);
        }
    }

    public static class Permissions {

        public static final String HELP = "stelyclaim.claim.help";
        public static final String DEFINE = "stelyclaim.claim.define";
        public static final String REDEFINE = "stelyclaim.claim.redefine";
        public static final String ADDMEMBER = "stelyclaim.claim.addmember";
        public static final String REMOVEMEMBER = "stelyclaim.claim.removemember";
        public static final String ADDOWNER = "stelyclaim.claim.addowner";
        public static final String REMOVEOWNER = "stelyclaim.claim.removeowner";
        public static final String REMOVE = "stelyclaim.claim.remove";
        public static final String INFO = "stelyclaim.claim.info";
        public static final String TELEPORT = "stelyclaim.claim.teleport";
        public static final String HERE = "stelyclaim.claim.here";
        public static final String NEAR = "stelyclaim.claim.near";
        public static final String LISTADD = "stelyclaim.claim.listadd";
        public static final String STATS = "stelyclaim.claim.stats";
        public static final String PILLAR = "stelyclaim.claim.pillar";

        public static void registerPerm(SubCommand subCommand, Function<Permission, Permission> createPermissionFunction) {
            // Get base and global perm
            final Permission globalPerm = Bukkit.getPluginManager().getPermission("stelyclaim.claim.*");
            final Permission basePerm = Bukkit.getPluginManager().getPermission("stelyclaim.claim");
            if (globalPerm == null || basePerm == null) {
                throw new RuntimeException(
                        "Can not register '"
                                + subCommand.getName()
                                + "' subcommand because global perm or base perm are not registered"
                );
            }
            // Create permission
            final Permission commandPerm = new Permission(subCommand.getPermission());
            // Link the command permission to the base permission
            basePerm.addParent(commandPerm, true);

            // Create custom permissions
            final Permission topPermission = createPermissionFunction.apply(commandPerm);
            // Link the top permission to the global permission
            topPermission.addParent(globalPerm, true);
        }

        public static String getOtherPerm(String permission) {
            return permission + "-other";
        }

    }

}
