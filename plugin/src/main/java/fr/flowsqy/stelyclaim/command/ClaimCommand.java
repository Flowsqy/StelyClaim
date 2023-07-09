package fr.flowsqy.stelyclaim.command;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import fr.flowsqy.stelyclaim.api.actor.BlockActor;
import fr.flowsqy.stelyclaim.api.actor.ConsoleActor;
import fr.flowsqy.stelyclaim.api.actor.EntityActor;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.command.claim.*;
import fr.flowsqy.stelyclaim.command.claim.domain.AddMemberSubCommand;
import fr.flowsqy.stelyclaim.command.claim.domain.AddOwnerSubCommand;
import fr.flowsqy.stelyclaim.command.claim.domain.RemoveMemberSubCommand;
import fr.flowsqy.stelyclaim.command.claim.domain.RemoveOwnerSubCommand;
import fr.flowsqy.stelyclaim.command.claim.help.BasicHelpMessageProvider;
import fr.flowsqy.stelyclaim.command.claim.help.HelpData;
import fr.flowsqy.stelyclaim.command.claim.help.HelpMessage;
import fr.flowsqy.stelyclaim.command.claim.help.OtherHelpMessageProvider;
import fr.flowsqy.stelyclaim.command.claim.interact.InfoSubCommand;
import fr.flowsqy.stelyclaim.command.claim.interact.RemoveSubCommand;
import fr.flowsqy.stelyclaim.command.claim.interact.TeleportSubCommand;
import fr.flowsqy.stelyclaim.command.claim.permission.*;
import fr.flowsqy.stelyclaim.command.claim.selection.DefineSubCommand;
import fr.flowsqy.stelyclaim.command.claim.selection.RedefineSubCommand;
import fr.flowsqy.stelyclaim.command.claim.statistics.ResetStatsSubCommand;
import fr.flowsqy.stelyclaim.command.claim.statistics.ShowStatsSubCommand;
import fr.flowsqy.stelyclaim.command.claim.statistics.StatsSubCommand;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import fr.flowsqy.stelyclaim.io.StatisticManager;
import fr.flowsqy.stelyclaim.protocol.ClaimContext;
import org.bukkit.command.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ClaimCommand implements TabExecutor {

    private final StatisticManager statisticManager;
    private final ClaimHandler<?> defaultHandler;
    private final HelpMessage helpMessage;
    private final String basePermission;
    private final ClaimSubCommandManager subCommandManager;
    private final ClaimRootCommand rootCommand;

    public ClaimCommand(@NotNull StelyClaimPlugin plugin, @NotNull String basePermission) {
        statisticManager = plugin.getStatisticManager();
        defaultHandler = plugin.getHandlerRegistry().getHandler("player");
        helpMessage = new HelpMessage();
        this.basePermission = basePermission;
        subCommandManager = new ClaimSubCommandManager();
        rootCommand = new ClaimRootCommand(subCommandManager, helpMessage);

        initInternalCommands(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        final Actor actor = getActor(sender);
        final CommandContext<ClaimContext> context = new CommandContext<>(actor, args, new ClaimContext(defaultHandler), 0);
        rootCommand.execute(context);
        /* TODO Implement stats again, but well do we really need it ? :D
        final String statistic = contextual.getData().getStatistic();
        if (statistic != null && statisticManager.allowStats(statistic) && actor.isPlayer()) {
            statisticManager.increment(actor.getPlayer().getUniqueId(), statistic);
            statisticManager.saveTask();
        }*/
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        final Actor actor = getActor(sender);
        final CommandContext<ClaimContext> context = new CommandContext<>(actor, args, new ClaimContext(defaultHandler), 0);
        return rootCommand.tabComplete(context);
    }

    @NotNull
    private Actor getActor(@NotNull CommandSender sender) {
        if (sender instanceof Entity entity) {
            return new EntityActor<>(entity);
        }
        if (sender instanceof ConsoleCommandSender console) {
            return new ConsoleActor(console);
        }
        if (sender instanceof BlockCommandSender block) {
            return new BlockActor(block);
        }
        throw new UnsupportedOperationException("Unsupported command sender type: " + sender.getClass());
    }

    /**
     * Initialize all internal sub commands
     *
     * @param plugin The {@link StelyClaimPlugin} instance
     */
    private void initInternalCommands(@NotNull StelyClaimPlugin plugin) {
        final Configuration config = plugin.getConfiguration();
        final ConfigurationFormattedMessages messages = plugin.getMessages();

        // Help
        final String helpName = "help";
        final CommandPermissionChecker helpPermChecker = new BasicCPC(basePermission + "." + helpName);
        subCommandManager.register(
                new HelpSubCommand(helpName, new String[]{helpName, "h"}, helpPermChecker, subCommandManager, helpMessage),
                false
        );
        if (config.getBoolean("statistic.help")) {
            statisticManager.allowStats(helpName);
        }
        final String helpHelpMessage = messages.getFormattedMessage("help." + helpName);
        final HelpData helpHelpData = new HelpData(helpName, false, new BasicHelpMessageProvider(helpPermChecker, helpHelpMessage));
        helpMessage.registerCommand(helpHelpData);

        // Define
        final String defineName = "define";
        final OtherCommandPermissionChecker definePermChecker = new OtherContextCPC(basePermission + ".", "." + defineName);
        subCommandManager.register(
                new DefineSubCommand(
                        defineName,
                        new String[]{defineName, "d"},
                        plugin,
                        config.getStringList("worlds.define"),
                        definePermChecker,
                        helpMessage
                ),
                true
        );
        if (config.getBoolean("statistic.define")) {
            statisticManager.addCommand(defineName);
        }
        final String defineHelpMessage = messages.getFormattedMessage("help." + defineName);
        final String defineOtherHelpMessage = messages.getFormattedMessage("help." + defineName + "-other");
        final OtherHelpMessageProvider defineHMP = new OtherHelpMessageProvider(definePermChecker, defineHelpMessage, defineOtherHelpMessage);
        final HelpData defineHelpData = new HelpData(defineName, true, defineHMP);
        helpMessage.registerCommand(defineHelpData);

        // Redefine
        final String redefineName = "redefine";
        final OtherCommandPermissionChecker redefinePermChecker = new OtherContextCPC(basePermission + ".", "." + redefineName);
        subCommandManager.register(
                new RedefineSubCommand(
                        redefineName,
                        new String[]{redefineName, "rd"},
                        plugin,
                        config.getStringList("worlds.redefine"),
                        redefinePermChecker,
                        helpMessage
                ),
                true
        );
        if (config.getBoolean("statistic.redefine")) {
            statisticManager.addCommand(redefineName);
        }
        final String redefineHelpMessage = messages.getFormattedMessage("help." + redefineName);
        final String redefineOtherHelpMessage = messages.getFormattedMessage("help." + redefineName + "-other");
        final OtherHelpMessageProvider redefineHMP = new OtherHelpMessageProvider(redefinePermChecker, redefineHelpMessage, redefineOtherHelpMessage);
        final HelpData redefineHelpData = new HelpData(redefineName, true, redefineHMP);
        helpMessage.registerCommand(redefineHelpData);

        // AddMember
        final String addmemberName = "addmember";
        final OtherCommandPermissionChecker addmemberPermChecker = new OtherContextCPC(basePermission + ".", "." + addmemberName);
        subCommandManager.register(
                new AddMemberSubCommand(
                        addmemberName,
                        new String[]{addmemberName, "am"},
                        plugin,
                        config.getStringList("worlds.addmember"),
                        addmemberPermChecker,
                        helpMessage
                ),
                true
        );
        if (config.getBoolean("statistic.addmember")) {
            statisticManager.addCommand(addmemberName);
        }
        final String addmemberHelpMessage = messages.getFormattedMessage("help." + addmemberName);
        final String addmemberOtherHelpMessage = messages.getFormattedMessage("help." + addmemberName + "-other");
        final OtherHelpMessageProvider addMemberHMP = new OtherHelpMessageProvider(addmemberPermChecker, addmemberHelpMessage, addmemberOtherHelpMessage);
        final HelpData addMemberHelpData = new HelpData(addmemberName, true, addMemberHMP);
        helpMessage.registerCommand(addMemberHelpData);

        // RemoveMember
        final String removememberName = "removemember";
        final OtherCommandPermissionChecker removememberPermChecker = new OtherContextCPC(basePermission + ".", "." + removememberName);
        subCommandManager.register(
                new RemoveMemberSubCommand(
                        removememberName,
                        new String[]{removememberName, "rm"},
                        plugin,
                        config.getStringList("worlds.removemember"),
                        removememberPermChecker,
                        helpMessage
                ),
                true
        );
        if (config.getBoolean("statistic.removemember")) {
            statisticManager.addCommand(removememberName);
        }
        final String removememberHelpMessage = messages.getFormattedMessage("help." + removememberName);
        final String removeMemberOtherHelpMessage = messages.getFormattedMessage("help." + removememberName + "-other");
        final OtherHelpMessageProvider removeMemberHMP = new OtherHelpMessageProvider(removememberPermChecker, removememberHelpMessage, removeMemberOtherHelpMessage);
        final HelpData removeMemberHelpData = new HelpData(removememberName, true, removeMemberHMP);
        helpMessage.registerCommand(removeMemberHelpData);

        // AddOwner
        final String addownerName = "addowner";
        final OtherCommandPermissionChecker addownerPermChecker = new OtherContextCPC(basePermission + ".", "." + addownerName);
        subCommandManager.register(
                new AddOwnerSubCommand(
                        addownerName,
                        new String[]{addownerName, "ao"},
                        plugin,
                        config.getStringList("worlds.addowner"),
                        addownerPermChecker,
                        helpMessage
                ),
                true
        );
        if (config.getBoolean("statistic.addowner")) {
            statisticManager.addCommand(addownerName);
        }
        final String addOwnerHelpMessage = messages.getFormattedMessage("help." + addownerName);
        final String addOwnerOtherHelpMessage = messages.getFormattedMessage("help." + addownerName + "-other");
        final OtherHelpMessageProvider addOwnerHMP = new OtherHelpMessageProvider(addownerPermChecker, addOwnerHelpMessage, addOwnerOtherHelpMessage);
        final HelpData addOwnerHelpData = new HelpData(addownerName, true, addOwnerHMP);
        helpMessage.registerCommand(addOwnerHelpData);

        // RemoveOwner
        final String removeOwnerName = "removeowner";
        final OtherCommandPermissionChecker removeOwnerPermChecker = new OtherContextCPC(basePermission + ".", "." + removeOwnerName);
        subCommandManager.register(
                new RemoveOwnerSubCommand(
                        removeOwnerName,
                        new String[]{removeOwnerName, "ro"},
                        plugin,
                        config.getStringList("worlds.removeowner"),
                        removeOwnerPermChecker,
                        helpMessage
                ),
                true
        );
        if (config.getBoolean("statistic.removeowner")) {
            statisticManager.addCommand(removeOwnerName);
        }
        final String removeOwnerHelpMessage = messages.getFormattedMessage("help." + removeOwnerName);
        final String removeOwnerOtherHelpMessage = messages.getFormattedMessage("help." + removeOwnerName + "-other");
        final OtherHelpMessageProvider removeOwnerHMP = new OtherHelpMessageProvider(removeOwnerPermChecker, removeOwnerHelpMessage, removeOwnerOtherHelpMessage);
        final HelpData removeOwnerHelpData = new HelpData(removeOwnerName, true, removeOwnerHMP);
        helpMessage.registerCommand(removeOwnerHelpData);

        // Remove
        final String removeName = "remove";
        final OtherCommandPermissionChecker removePermChecker = new OtherContextCPC(basePermission + ".", "." + removeName);
        registerCommand(
                new RemoveSubCommand(
                        removeName,
                        new String[]{removeName, "r"},
                        plugin,
                        config.getStringList("worlds.remove"),
                        removePermChecker,
                        helpMessage
                ),
                removePermChecker
        );
        if (config.getBoolean("statistic.remove")) {
            statisticManager.addCommand(removeName);
        }
        final String removeHelpMessage = messages.getFormattedMessage("help." + removeName);
        helpMessage.registerCommand(new HelpMessage.HelpData(removeName, removePermChecker, id -> removeHelpMessage));

        // Info
        final String infoName = "info";
        final OtherCommandPermissionChecker infoPermChecker = new OtherContextCPC(basePermission + ".", "." + infoName);
        registerCommand(
                new InfoSubCommand(
                        infoName,
                        new String[]{infoName, "i"},
                        plugin,
                        config.getStringList("worlds.info"),
                        infoPermChecker,
                        helpMessage
                ),
                infoPermChecker
        );
        if (config.getBoolean("statistic.info")) {
            statisticManager.addCommand(infoName);
        }
        final String infoHelpMessage = messages.getFormattedMessage("help." + infoName);
        helpMessage.registerCommand(new HelpMessage.HelpData(infoName, infoPermChecker, id -> infoHelpMessage));

        // Teleport
        final String teleportName = "teleport";
        final OtherCommandPermissionChecker teleportPermChecker = new OtherContextCPC(basePermission + ".", "." + teleportName);
        registerCommand(
                new TeleportSubCommand(
                        teleportName,
                        new String[]{teleportName, "tp"},
                        plugin,
                        config.getStringList("worlds.teleport"),
                        teleportPermChecker,
                        helpMessage
                ),
                teleportPermChecker
        );
        if (config.getBoolean("statistic.teleport")) {
            statisticManager.addCommand(teleportName);
        }
        final String teleportHelpMessage = messages.getFormattedMessage("help." + teleportName);
        helpMessage.registerCommand(new HelpMessage.HelpData(teleportName, teleportPermChecker, id -> teleportHelpMessage));

        // Here
        final String hereName = "here";
        final OtherCommandPermissionChecker herePermChecker = new OtherBasicCPC(basePermission + "." + hereName);
        registerCommand(
                new HereSubCommand(
                        hereName,
                        new String[]{hereName, "hr"},
                        plugin,
                        config.getStringList("worlds.here"),
                        herePermChecker,
                        helpMessage
                ),
                herePermChecker
        );
        if (config.getBoolean("statistic.here")) {
            statisticManager.addCommand(hereName);
        }
        final String hereHelpMessage = messages.getFormattedMessage("help." + hereName);
        helpMessage.registerCommand(new HelpMessage.HelpData(hereName, herePermChecker, id -> hereHelpMessage));

        // Near
        final String nearName = "near";
        final NearCommandPermissionChecker nearPermChecker = new NearCommandPermissionChecker(basePermission + "." + nearName);
        registerCommand(
                new NearSubCommand(
                        nearName,
                        new String[]{nearName, "n"},
                        plugin,
                        config.getStringList("worlds.near"),
                        nearPermChecker,
                        helpMessage
                ),
                nearPermChecker
        );
        if (config.getBoolean("statistic.near")) {
            statisticManager.addCommand(nearName);
        }
        final String nearHelpMessage = messages.getFormattedMessage("help." + nearName);
        helpMessage.registerCommand(new HelpMessage.HelpData(nearName, nearPermChecker, id -> nearHelpMessage));

        // ListAdd
        final String listaddName = "listadd";
        final OtherCommandPermissionChecker listaddPermChecker = new OtherBasicCPC(basePermission + "." + listaddName);
        registerCommand(
                new ListAddSubCommand(
                        listaddName,
                        new String[]{listaddName, "la"},
                        plugin,
                        config.getStringList("worlds.listadd"),
                        listaddPermChecker,
                        helpMessage
                ),
                listaddPermChecker
        );
        if (config.getBoolean("statistic.listadd")) {
            statisticManager.addCommand(listaddName);
        }
        final String listaddHelpMessage = messages.getFormattedMessage("help." + listaddName);
        helpMessage.registerCommand(new HelpMessage.HelpData(listaddName, listaddPermChecker, id -> listaddHelpMessage));

        // Pillar
        final String pillarName = "pillar";
        //final CommandPermissionChecker pillarData = new CommandPermissionChecker(pillarName, basePermission, false);
        registerCommand(
                new PillarSubCommand(
                        pillarName,
                        new String[]{pillarName},
                        plugin,
                        helpMessage
                ),
                pillarData
        );
        if (config.getBoolean("statistic.pillar")) {
            statisticManager.addCommand(pillarName);
        }
        final String pillarHelpMessage = messages.getFormattedMessage("help." + pillarName);
        helpMessage.registerCommand(new HelpMessage.HelpData(pillarName, pillarData, id -> pillarHelpMessage));

        // Player
        final String playerName = "player";
        final CommandPermissionChecker playerPermChecker = new ContextCPC(basePermission + ".contextual.", "");
        registerCommand(
                new ContextSubCommand(
                        playerName,
                        new String[]{playerName, "p"},
                        defaultHandler,
                        subCommandManager,
                        playerPermChecker,
                        helpMessage
                ),
                playerPermChecker
        );
        if (config.getBoolean("statistic.player")) {
            statisticManager.addCommand(playerName);
        }
        final String playerHelpMessage = messages.getFormattedMessage("help." + playerName);
        helpMessage.registerCommand(new HelpMessage.HelpData(playerName, playerPermChecker, id -> playerHelpMessage));

        // Stats
        final String statsName = "stats";

        final String showStatsName = "show";
        final OtherCommandPermissionChecker showStatsPermChecker = new OtherBasicCPC(basePermission + "." + statsName + "." + showStatsName);
        final String showStatsHelpName = statsName + "_" + showStatsName;
        final ShowStatsSubCommand showStatsSubCommand = new ShowStatsSubCommand(
                showStatsName,
                new String[]{showStatsName, "s"},
                plugin,
                showStatsPermChecker,
                showStatsHelpName,
                helpMessage
        );
        final String showStatsHelpMessage = messages.getFormattedMessage("help." + showStatsHelpName);
        helpMessage.registerCommand(new HelpMessage.HelpData(showStatsHelpName, showStatsPermChecker, id -> showStatsHelpMessage));

        final String resetStatsName = "reset";
        final OtherCommandPermissionChecker resetStatsPermChecker = new OtherBasicCPC(basePermission + "." + statsName + "." + resetStatsName);
        final String resetStatsHelpName = statsName + "_" + resetStatsName;
        final ResetStatsSubCommand resetStatsSubCommand = new ResetStatsSubCommand(
                resetStatsName,
                new String[]{resetStatsName, "r"},
                plugin,
                resetStatsPermChecker,
                resetStatsHelpName,
                helpMessage
        );
        final String resetStatsHelpMessage = messages.getFormattedMessage("help." + resetStatsHelpName);
        helpMessage.registerCommand(new HelpMessage.HelpData(resetStatsHelpName, resetStatsPermChecker, id -> resetStatsHelpMessage));


        final CommandPermissionChecker statsData = new BasicCPC(basePermission + "." + statsName);
        registerCommand(
                new StatsSubCommand(
                        statsName,
                        new String[]{statsName, "s"},
                        statsData,
                        helpMessage,
                        Arrays.asList(showStatsSubCommand, resetStatsSubCommand)
                ),
                statsData
        );
        if (config.getBoolean("statistic.stats")) {
            statisticManager.addCommand(statsName);
        }
        final String statsHelpMessage = messages.getFormattedMessage("help." + statsName);
        helpMessage.registerCommand(new HelpMessage.HelpData(statsName, statsData, id -> statsHelpMessage));
    }

    private void registerPerm(@NotNull CommandPermissionChecker data, @NotNull String[] modifiers) {
        // TODO Update that :clown:
        /*
        final PluginManager pluginManager = Bukkit.getPluginManager();
        // Get base and global perm
        final Permission globalPerm = pluginManager.getPermission(basePermission + ".*");
        final Permission basePerm = pluginManager.getPermission(basePermission);
        if (globalPerm == null || basePerm == null) {
            throw new RuntimeException(
                    "Can not register the subcommand because global perm or base perm are not registered"
            );
        }
        // Create permission
        final Permission commandPerm = new Permission(data.getBasePerm());
        // Link the command permission to the base permission
        basePerm.addParent(commandPerm, true);

        // Create custom permissions
        final Permission topPermission = createPermissionFunction.apply(commandPerm);
        // Link the top permission to the global permission
        topPermission.addParent(globalPerm, true);
        */
    }

    public HelpMessage getHelpMessage() {
        return helpMessage;
    }

    public String getBasePermission() {
        return basePermission;
    }

}
