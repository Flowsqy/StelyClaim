package fr.flowsqy.stelyclaim.command;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import fr.flowsqy.stelyclaim.api.actor.BlockActor;
import fr.flowsqy.stelyclaim.api.actor.ConsoleActor;
import fr.flowsqy.stelyclaim.api.actor.EntityActor;
import fr.flowsqy.stelyclaim.api.command.CommandContext;
import fr.flowsqy.stelyclaim.api.permission.*;
import fr.flowsqy.stelyclaim.command.claim.*;
import fr.flowsqy.stelyclaim.command.claim.domain.AddMemberSubCommand;
import fr.flowsqy.stelyclaim.command.claim.domain.AddOwnerSubCommand;
import fr.flowsqy.stelyclaim.command.claim.domain.RemoveMemberSubCommand;
import fr.flowsqy.stelyclaim.command.claim.domain.RemoveOwnerSubCommand;
import fr.flowsqy.stelyclaim.command.claim.help.*;
import fr.flowsqy.stelyclaim.command.claim.interact.InfoSubCommand;
import fr.flowsqy.stelyclaim.command.claim.interact.RemoveSubCommand;
import fr.flowsqy.stelyclaim.command.claim.interact.TeleportSubCommand;
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
import java.util.UUID;

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
        final CommandContext context = new CommandContext(actor, args, new ClaimContext(defaultHandler), 0);
        context.appendCommandName("claim");
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
        final CommandContext context = new CommandContext(actor, args, new ClaimContext(defaultHandler), 0);
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
        final UUID helpId = UUID.fromString("ac1851e4-dcb4-44d3-977d-f9f9f62f82a3");
        final String helpName = "help";
        final PermissionChecker helpPermChecker = new BasicPC(basePermission + "." + helpName);
        subCommandManager.register(
                new HelpSubCommand(helpId, helpName, new String[]{helpName, "h"}, helpPermChecker, rootCommand, helpMessage),
                false
        );
        if (config.getBoolean("statistic.help")) {
            statisticManager.allowStats(helpName);
        }
        final String helpHelpMessage = messages.getFormattedMessage("help." + helpName);
        final HelpMessageProvider helpHMP = new BasicHelpMessageProvider(helpPermChecker, helpHelpMessage);
        helpMessage.register(helpId, helpHMP);

        // Define
        final UUID defineId = UUID.fromString("8a387ffb-b204-409c-ab51-9a85c0c22915");
        final String defineName = "define";
        final OtherPermissionChecker definePermChecker = new OtherContextPC(basePermission + ".", "." + defineName);
        subCommandManager.register(
                new DefineSubCommand(
                        defineId,
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
        final HelpMessageProvider defineHMP = new OtherHelpMessageProvider(definePermChecker, defineHelpMessage, defineOtherHelpMessage);
        helpMessage.register(defineId, defineHMP);

        // Redefine
        final UUID redefineId = UUID.fromString("6e57c8c6-1237-4339-98fc-d20812bdeb42");
        final String redefineName = "redefine";
        final OtherPermissionChecker redefinePermChecker = new OtherContextPC(basePermission + ".", "." + redefineName);
        subCommandManager.register(
                new RedefineSubCommand(
                        redefineId,
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
        final HelpMessageProvider redefineHMP = new OtherHelpMessageProvider(redefinePermChecker, redefineHelpMessage, redefineOtherHelpMessage);
        helpMessage.register(redefineId, redefineHMP);

        // AddMember
        final UUID addMemberId = UUID.fromString("a417fbaa-0d93-4045-b26b-ae58c46ec5d9");
        final String addmemberName = "addmember";
        final OtherPermissionChecker addmemberPermChecker = new OtherContextPC(basePermission + ".", "." + addmemberName);
        subCommandManager.register(
                new AddMemberSubCommand(
                        addMemberId,
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
        final HelpMessageProvider addMemberHMP = new OtherHelpMessageProvider(addmemberPermChecker, addmemberHelpMessage, addmemberOtherHelpMessage);
        helpMessage.register(addMemberId, addMemberHMP);

        // RemoveMember
        final UUID removeMemberId = UUID.fromString("a41af9c0-419d-4b8e-9eb5-662d7551162f");
        final String removememberName = "removemember";
        final OtherPermissionChecker removememberPermChecker = new OtherContextPC(basePermission + ".", "." + removememberName);
        subCommandManager.register(
                new RemoveMemberSubCommand(
                        removeMemberId,
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
        final HelpMessageProvider removeMemberHMP = new OtherHelpMessageProvider(removememberPermChecker, removememberHelpMessage, removeMemberOtherHelpMessage);
        helpMessage.register(removeMemberId, removeMemberHMP);

        // AddOwner
        final UUID addOwnerId = UUID.fromString("109f5ea6-62d0-4e34-b6a7-4841fcc60d52");
        final String addownerName = "addowner";
        final OtherPermissionChecker addownerPermChecker = new OtherContextPC(basePermission + ".", "." + addownerName);
        subCommandManager.register(
                new AddOwnerSubCommand(
                        addOwnerId,
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
        final HelpMessageProvider addOwnerHMP = new OtherHelpMessageProvider(addownerPermChecker, addOwnerHelpMessage, addOwnerOtherHelpMessage);
        helpMessage.register(addOwnerId, addOwnerHMP);

        // RemoveOwner
        final UUID removeOwnerId = UUID.fromString("290aff1b-9f4d-4afc-bed7-4907c51fa7b0");
        final String removeOwnerName = "removeowner";
        final OtherPermissionChecker removeOwnerPermChecker = new OtherContextPC(basePermission + ".", "." + removeOwnerName);
        subCommandManager.register(
                new RemoveOwnerSubCommand(
                        removeOwnerId,
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
        final HelpMessageProvider removeOwnerHMP = new OtherHelpMessageProvider(removeOwnerPermChecker, removeOwnerHelpMessage, removeOwnerOtherHelpMessage);
        helpMessage.register(removeOwnerId, removeOwnerHMP);

        // Remove
        final UUID removeId = UUID.fromString("bd4b2455-9e23-4997-b983-f4d3be0c0d7a");
        final String removeName = "remove";
        final OtherPermissionChecker removePermChecker = new OtherContextPC(basePermission + ".", "." + removeName);
        subCommandManager.register(
                new RemoveSubCommand(
                        removeId,
                        removeName,
                        new String[]{removeName, "r"},
                        plugin,
                        config.getStringList("worlds.remove"),
                        removePermChecker,
                        helpMessage
                ),
                true
        );
        if (config.getBoolean("statistic.remove")) {
            statisticManager.addCommand(removeName);
        }
        final String removeHelpMessage = messages.getFormattedMessage("help." + removeName);
        final String removeOtherHelpMessage = messages.getFormattedMessage("help." + removeName + "-other");
        final HelpMessageProvider removeHMP = new OtherHelpMessageProvider(removePermChecker, removeHelpMessage, removeOtherHelpMessage);
        helpMessage.register(removeId, removeHMP);

        // Info
        final UUID infoId = UUID.fromString("5a5ca7ba-93f4-46ca-8b13-ca325ce286ff");
        final String infoName = "info";
        final OtherPermissionChecker infoPermChecker = new OtherContextPC(basePermission + ".", "." + infoName);
        subCommandManager.register(
                new InfoSubCommand(
                        infoId,
                        infoName,
                        new String[]{infoName, "i"},
                        plugin,
                        config.getStringList("worlds.info"),
                        infoPermChecker,
                        helpMessage
                ),
                true
        );
        if (config.getBoolean("statistic.info")) {
            statisticManager.addCommand(infoName);
        }
        final String infoHelpMessage = messages.getFormattedMessage("help." + infoName);
        final String infoOtherHelpMessage = messages.getFormattedMessage("help." + infoName + "-other");
        final HelpMessageProvider infoHMP = new OtherHelpMessageProvider(infoPermChecker, infoHelpMessage, infoOtherHelpMessage);
        helpMessage.register(infoId, infoHMP);

        // Teleport
        final UUID teleportId = UUID.fromString("36d52f0c-c8e3-41d8-ba57-cd9651f3c6b4");
        final String teleportName = "teleport";
        final OtherPermissionChecker teleportPermChecker = new OtherContextPC(basePermission + ".", "." + teleportName);
        subCommandManager.register(
                new TeleportSubCommand(
                        teleportId,
                        teleportName,
                        new String[]{teleportName, "tp"},
                        plugin,
                        config.getStringList("worlds.teleport"),
                        teleportPermChecker,
                        helpMessage
                ),
                true
        );
        if (config.getBoolean("statistic.teleport")) {
            statisticManager.addCommand(teleportName);
        }
        final String teleportHelpMessage = messages.getFormattedMessage("help." + teleportName);
        final String teleportOtherHelpMessage = messages.getFormattedMessage("help." + teleportName + "-other");
        final HelpMessageProvider teleportHMP = new OtherHelpMessageProvider(teleportPermChecker, teleportHelpMessage, teleportOtherHelpMessage);
        helpMessage.register(teleportId, teleportHMP);

        // Here
        final UUID hereId = UUID.fromString("e8d49bbb-83b0-4796-be2d-79f8487f0532");
        final String hereName = "here";
        final OtherPermissionChecker herePermChecker = new OtherBasicPC(basePermission + "." + hereName);
        subCommandManager.register(
                new HereSubCommand(
                        hereId,
                        hereName,
                        new String[]{hereName, "hr"},
                        plugin,
                        config.getStringList("worlds.here"),
                        herePermChecker,
                        infoPermChecker,
                        helpMessage
                ),
                false
        );
        if (config.getBoolean("statistic.here")) {
            statisticManager.addCommand(hereName);
        }
        final String hereHelpMessage = messages.getFormattedMessage("help." + hereName);
        final HelpMessageProvider hereHMP = new BasicHelpMessageProvider(herePermChecker, hereHelpMessage);
        helpMessage.register(hereId, hereHMP);

        // Near
        final UUID nearId = UUID.fromString("24341444-78c3-440e-a4ff-ebdd248f11ed");
        final String nearName = "near";
        final NearCommandPermissionChecker nearPermChecker = new NearCommandPermissionChecker(basePermission + "." + nearName);
        subCommandManager.register(
                new NearSubCommand(
                        nearId,
                        nearName,
                        new String[]{nearName, "n"},
                        plugin,
                        config.getStringList("worlds.near"),
                        nearPermChecker,
                        helpMessage
                ),
                false
        );
        if (config.getBoolean("statistic.near")) {
            statisticManager.addCommand(nearName);
        }
        final String nearHelpMessage = messages.getFormattedMessage("help." + nearName);
        final HelpMessageProvider nearHMP = new BasicHelpMessageProvider(nearPermChecker, nearHelpMessage);
        helpMessage.register(nearId, nearHMP);

        // ListAdd
        final UUID listAddId = UUID.fromString("9b4186c9-eb7f-48e4-8a46-3c173b28af2d");
        final String listaddName = "listadd";
        final OtherPermissionChecker listaddPermChecker = new OtherBasicPC(basePermission + "." + listaddName);
        subCommandManager.register(
                new ListAddSubCommand(
                        listAddId,
                        listaddName,
                        new String[]{listaddName, "la"},
                        plugin,
                        config.getStringList("worlds.listadd"),
                        listaddPermChecker,
                        helpMessage
                ),
                false
        );
        if (config.getBoolean("statistic.listadd")) {
            statisticManager.addCommand(listaddName);
        }
        final String listaddHelpMessage = messages.getFormattedMessage("help." + listaddName);
        final String listAddOtherHelpMessage = messages.getFormattedMessage("help." + listaddName + "-other");
        final HelpMessageProvider listAddHMP = new OtherHelpMessageProvider(listaddPermChecker, listaddHelpMessage, listAddOtherHelpMessage);
        helpMessage.register(listAddId, listAddHMP);

        // Pillar
        final String pillarName = "pillar";
        //final CommandPermissionChecker pillarData = new CommandPermissionChecker(pillarName, basePermission, false);
        subCommandManager.register(
                new PillarSubCommand(
                        new String[]{pillarName},
                        plugin,
                        helpMessage,
                        rootCommand
                ),
                false
        );
        if (config.getBoolean("statistic.pillar")) {
            statisticManager.addCommand(pillarName);
        }

        // Player
        final UUID playerId = UUID.fromString("e7c0094d-5c8d-4807-a428-6e5a739e11fc");
        final String playerName = "player";
        final PermissionChecker playerPermChecker = new ContextPC(basePermission + ".contextual.", "");
        subCommandManager.register(
                new ContextSubCommand(
                        playerId,
                        playerName,
                        new String[]{playerName, "p"},
                        defaultHandler,
                        subCommandManager,
                        playerPermChecker,
                        helpMessage
                ),
                false
        );
        if (config.getBoolean("statistic.player")) {
            statisticManager.addCommand(playerName);
        }
        final String playerHelpMessage = messages.getFormattedMessage("help." + playerName);
        final HelpMessageProvider playerHMP = new BasicHelpMessageProvider(playerPermChecker, playerHelpMessage);
        helpMessage.register(playerId, playerHMP);

        // Stats
        final String statsName = "stats";

        final UUID showStatsId = UUID.fromString("412ff688-7b16-4852-b305-7e5c19b89b98");
        final String showStatsName = "show";
        final OtherPermissionChecker showStatsPermChecker = new OtherBasicPC(basePermission + "." + statsName + "." + showStatsName);
        final ShowStatsSubCommand showStatsSubCommand = new ShowStatsSubCommand(
                showStatsId,
                showStatsName,
                new String[]{showStatsName, "s"},
                plugin,
                showStatsPermChecker,
                helpMessage
        );
        final String showStatsHelpMessage = messages.getFormattedMessage("help." + statsName + "-" + showStatsName);
        final String showStatOtherHelpMessage = messages.getFormattedMessage("help." + statsName + "-" + showStatsName + "-other");
        final HelpMessageProvider showStatsHMP = new OtherHelpMessageProvider(showStatsPermChecker, showStatsHelpMessage, showStatOtherHelpMessage);
        helpMessage.register(showStatsId, showStatsHMP);

        final UUID resetStatsId = UUID.fromString("893ac1a9-ced0-4511-a997-9eb2176e5f96");
        final String resetStatsName = "reset";
        final OtherPermissionChecker resetStatsPermChecker = new OtherBasicPC(basePermission + "." + statsName + "." + resetStatsName);
        final ResetStatsSubCommand resetStatsSubCommand = new ResetStatsSubCommand(
                resetStatsId,
                resetStatsName,
                new String[]{resetStatsName, "r"},
                plugin,
                resetStatsPermChecker,
                helpMessage
        );
        final String resetStatsHelpMessage = messages.getFormattedMessage("help." + statsName + "-" + resetStatsName);
        final String resetStatOtherHelpMessage = messages.getFormattedMessage("help." + statsName + "-" + resetStatsName + "-other");
        final HelpMessageProvider resetStatsHMP = new OtherHelpMessageProvider(resetStatsPermChecker, resetStatsHelpMessage, resetStatOtherHelpMessage);
        helpMessage.register(resetStatsId, resetStatsHMP);

        final UUID statsId = UUID.fromString("be98a9a6-a6f0-4bd9-aceb-ac47c40b94ba");
        final PermissionChecker statsPermChecker = new BasicPC(basePermission + "." + statsName);
        subCommandManager.register(
                new StatsSubCommand(
                        statsId,
                        statsName,
                        new String[]{statsName, "s"},
                        statsPermChecker,
                        helpMessage,
                        Arrays.asList(showStatsSubCommand, resetStatsSubCommand)
                ),
                false
        );
        if (config.getBoolean("statistic.stats")) {
            statisticManager.addCommand(statsName);
        }
        final String statsHelpMessage = messages.getFormattedMessage("help." + statsName);
        final HelpMessageProvider statsHMP = new BasicHelpMessageProvider(helpPermChecker, statsHelpMessage);
        helpMessage.register(statsId, statsHMP);
    }

    private void registerPerm(@NotNull PermissionChecker data, @NotNull String[] modifiers) {
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
