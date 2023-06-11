package fr.flowsqy.stelyclaim.command;

import fr.flowsqy.stelyclaim.StelyClaimPlugin;
import fr.flowsqy.stelyclaim.api.ClaimHandler;
import fr.flowsqy.stelyclaim.api.actor.Actor;
import fr.flowsqy.stelyclaim.api.actor.BlockActor;
import fr.flowsqy.stelyclaim.api.actor.ConsoleActor;
import fr.flowsqy.stelyclaim.api.actor.EntityActor;
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
import fr.flowsqy.stelyclaim.command.claim.statistics.ResetStatsSubCommand;
import fr.flowsqy.stelyclaim.command.claim.statistics.ShowStatsSubCommand;
import fr.flowsqy.stelyclaim.command.claim.statistics.StatsSubCommand;
import fr.flowsqy.stelyclaim.command.struct.CommandContext;
import fr.flowsqy.stelyclaim.command.struct.CommandNode;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import fr.flowsqy.stelyclaim.io.StatisticManager;
import org.bukkit.command.*;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class ClaimCommand implements TabExecutor {

    private final StatisticManager statisticManager;
    private final ClaimHandler<?> playerHandler;
    private final HelpMessage helpMessage;
    private final String basePermission;
    private final ClaimSubCommandManager subCommandManager;
    private final ClaimRootCommand rootCommand;

    public ClaimCommand(@NotNull StelyClaimPlugin plugin, @NotNull String basePermission) {
        statisticManager = plugin.getStatisticManager();
        playerHandler = plugin.getProtocolManager().getHandler("player");
        helpMessage = new HelpMessage();
        this.basePermission = basePermission;
        subCommandManager = new ClaimSubCommandManager();
        rootCommand = new ClaimRootCommand(subCommandManager, helpMessage);

        initInternalCommands(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        final Actor actor = getActor(sender);
        final CommandContext<ClaimContextData> context = new CommandContext<>(actor, args, new ClaimContextData(), 0);
        context.getData().setHandler(playerHandler);
        rootCommand.execute(context);
        final String statistic = context.getData().getStatistic();
        if (statistic != null && statisticManager.allowStats(statistic) && actor.isPlayer()) {
            statisticManager.increment(actor.getPlayer().getUniqueId(), statistic);
            statisticManager.saveTask();
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        final Actor actor = getActor(sender);
        final CommandContext<ClaimContextData> context = new CommandContext<>(actor, args, new ClaimContextData(), 0);
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
        final PermissionData helpData = new PermissionData(helpName, basePermission, false);
        registerCommand(
                new HelpSubCommand(helpName, new String[]{helpName, "h"}, helpData, subCommandManager, helpMessage),
                helpData
        );
        if (config.getBoolean("statistic.help")) {
            statisticManager.allowStats(helpName);
        }
        final String helpHelpMessage = messages.getFormattedMessage("help." + helpName);
        helpMessage.registerCommand(new HelpMessage.HelpData(helpName, helpData, id -> helpHelpMessage));

        // Define
        final String defineName = "define";
        final PermissionData defineData = new PermissionData(defineName, basePermission, true);
        registerCommand(
                new DefineSubCommand(
                        defineName,
                        new String[]{defineName, "d"},
                        plugin,
                        config.getStringList("worlds.define"),
                        defineData,
                        helpMessage
                ),
                defineData
        );
        if (config.getBoolean("statistic.define")) {
            statisticManager.addCommand(defineName);
        }
        final String defineHelpMessage = messages.getFormattedMessage("help." + defineName);
        helpMessage.registerCommand(new HelpMessage.HelpData(defineName, defineData, id -> defineHelpMessage));

        // Redefine
        final String redefineName = "redefine";
        final PermissionData redefineData = new PermissionData(redefineName, basePermission, true);
        registerCommand(
                new RedefineSubCommand(
                        redefineName,
                        new String[]{redefineName, "rd"},
                        plugin,
                        config.getStringList("worlds.redefine"),
                        redefineData,
                        helpMessage
                ),
                redefineData
        );
        if (config.getBoolean("statistic.redefine")) {
            statisticManager.addCommand(redefineName);
        }
        final String redefineHelpMessage = messages.getFormattedMessage("help." + redefineName);
        helpMessage.registerCommand(new HelpMessage.HelpData(redefineName, redefineData, id -> redefineHelpMessage));

        // AddMember
        final String addmemberName = "addmember";
        final PermissionData addmemberData = new PermissionData(addmemberName, basePermission, true);
        registerCommand(
                new AddMemberSubCommand(
                        addmemberName,
                        new String[]{addmemberName, "am"},
                        plugin,
                        config.getStringList("worlds.addmember"),
                        addmemberData,
                        helpMessage
                ),
                addmemberData
        );
        if (config.getBoolean("statistic.addmember")) {
            statisticManager.addCommand(addmemberName);
        }
        final String addmemberHelpMessage = messages.getFormattedMessage("help." + addmemberName);
        helpMessage.registerCommand(new HelpMessage.HelpData(addmemberName, addmemberData, id -> addmemberHelpMessage));

        // RemoveMember
        final String removememberName = "removemember";
        final PermissionData removememberData = new PermissionData(removememberName, basePermission, true);
        registerCommand(
                new RemoveMemberSubCommand(
                        removememberName,
                        new String[]{removememberName, "rm"},
                        plugin,
                        config.getStringList("worlds.removemember"),
                        removememberData,
                        helpMessage
                ),
                removememberData
        );
        if (config.getBoolean("statistic.removemember")) {
            statisticManager.addCommand(removememberName);
        }
        final String removememberHelpMessage = messages.getFormattedMessage("help." + removememberName);
        helpMessage.registerCommand(new HelpMessage.HelpData(removememberName, removememberData, id -> removememberHelpMessage));

        // AddOwner
        final String addownerName = "addowner";
        final PermissionData addownerData = new PermissionData(addownerName, basePermission, true);
        registerCommand(
                new AddOwnerSubCommand(
                        addownerName,
                        new String[]{addownerName, "ao"},
                        plugin,
                        config.getStringList("worlds.addowner"),
                        addownerData,
                        helpMessage
                ),
                addownerData
        );
        if (config.getBoolean("statistic.addowner")) {
            statisticManager.addCommand(addownerName);
        }
        final String addownerHelpMessage = messages.getFormattedMessage("help." + addownerName);
        helpMessage.registerCommand(new HelpMessage.HelpData(addownerName, addownerData, id -> addownerHelpMessage));

        // RemoveOwner
        final String removeownerName = "removeowner";
        final PermissionData removeownerData = new PermissionData(removeownerName, basePermission, true);
        registerCommand(
                new RemoveOwnerSubCommand(
                        removeownerName,
                        new String[]{removeownerName, "ro"},
                        plugin,
                        config.getStringList("worlds.removeowner"),
                        removeownerData,
                        helpMessage
                ),
                removeownerData
        );
        if (config.getBoolean("statistic.removeowner")) {
            statisticManager.addCommand(removeownerName);
        }
        final String removeownerHelpMessage = messages.getFormattedMessage("help." + removeownerName);
        helpMessage.registerCommand(new HelpMessage.HelpData(removeownerName, removeownerData, id -> removeownerHelpMessage));

        // Remove
        final String removeName = "remove";
        final PermissionData removeData = new PermissionData(removeName, basePermission, true);
        registerCommand(
                new RemoveSubCommand(
                        removeName,
                        new String[]{removeName, "r"},
                        plugin,
                        config.getStringList("worlds.remove"),
                        removeData,
                        helpMessage
                ),
                removeData
        );
        if (config.getBoolean("statistic.remove")) {
            statisticManager.addCommand(removeName);
        }
        final String removeHelpMessage = messages.getFormattedMessage("help." + removeName);
        helpMessage.registerCommand(new HelpMessage.HelpData(removeName, removeData, id -> removeHelpMessage));

        // Info
        final String infoName = "info";
        final PermissionData infoData = new PermissionData(infoName, basePermission, true);
        registerCommand(
                new InfoSubCommand(
                        infoName,
                        new String[]{infoName, "i"},
                        plugin,
                        config.getStringList("worlds.info"),
                        infoData,
                        helpMessage
                ),
                infoData
        );
        if (config.getBoolean("statistic.info")) {
            statisticManager.addCommand(infoName);
        }
        final String infoHelpMessage = messages.getFormattedMessage("help." + infoName);
        helpMessage.registerCommand(new HelpMessage.HelpData(infoName, infoData, id -> infoHelpMessage));

        // Teleport
        final String teleportName = "teleport";
        final PermissionData teleportData = new PermissionData(teleportName, basePermission, true);
        registerCommand(
                new TeleportSubCommand(
                        teleportName,
                        new String[]{teleportName, "tp"},
                        plugin,
                        config.getStringList("worlds.teleport"),
                        teleportData,
                        helpMessage
                ),
                teleportData
        );
        if (config.getBoolean("statistic.teleport")) {
            statisticManager.addCommand(teleportName);
        }
        final String teleportHelpMessage = messages.getFormattedMessage("help." + teleportName);
        helpMessage.registerCommand(new HelpMessage.HelpData(teleportName, teleportData, id -> teleportHelpMessage));

        // Here
        final String hereName = "here";
        final PermissionData hereData = new PermissionData(hereName, basePermission, false);
        registerCommand(
                new HereSubCommand(
                        hereName,
                        new String[]{hereName, "hr"},
                        plugin,
                        config.getStringList("worlds.here"),
                        hereData,
                        helpMessage
                ),
                hereData
        );
        if (config.getBoolean("statistic.here")) {
            statisticManager.addCommand(hereName);
        }
        final String hereHelpMessage = messages.getFormattedMessage("help." + hereName);
        helpMessage.registerCommand(new HelpMessage.HelpData(hereName, hereData, id -> hereHelpMessage));

        // Near
        final String nearName = "near";
        final PermissionData nearData = new PermissionData(nearName, basePermission, false);
        registerCommand(
                new NearSubCommand(
                        nearName,
                        new String[]{nearName, "n"},
                        plugin,
                        config.getStringList("worlds.near"),
                        nearData,
                        helpMessage
                ),
                nearData
        );
        if (config.getBoolean("statistic.near")) {
            statisticManager.addCommand(nearName);
        }
        final String nearHelpMessage = messages.getFormattedMessage("help." + nearName);
        helpMessage.registerCommand(new HelpMessage.HelpData(nearName, nearData, id -> nearHelpMessage));

        // ListAdd
        final String listaddName = "listadd";
        final PermissionData listaddData = new PermissionData(listaddName, basePermission, false);
        registerCommand(
                new ListAddSubCommand(
                        listaddName,
                        new String[]{listaddName, "la"},
                        plugin,
                        config.getStringList("worlds.listadd"),
                        listaddData,
                        helpMessage
                ),
                listaddData
        );
        if (config.getBoolean("statistic.listadd")) {
            statisticManager.addCommand(listaddName);
        }
        final String listaddHelpMessage = messages.getFormattedMessage("help." + listaddName);
        helpMessage.registerCommand(new HelpMessage.HelpData(listaddName, listaddData, id -> listaddHelpMessage));

        // Pillar
        final String pillarName = "pillar";
        final PermissionData pillarData = new PermissionData(pillarName, basePermission, false);
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
        final PermissionData playerData = new PermissionData(null, basePermission, false);
        registerCommand(
                new ContextSubCommand(
                        playerName,
                        new String[]{playerName, "p"},
                        playerHandler,
                        subCommandManager,
                        playerData,
                        helpMessage
                ),
                playerData
        );
        if (config.getBoolean("statistic.player")) {
            statisticManager.addCommand(playerName);
        }
        final String playerHelpMessage = messages.getFormattedMessage("help." + playerName);
        helpMessage.registerCommand(new HelpMessage.HelpData(playerName, playerData, id -> playerHelpMessage));

        // Stats
        final String statsName = "stats";

        final String showStatsName = "show";
        final PermissionData showStatsData = new PermissionData(statsName + "." + showStatsName, basePermission, false);
        final String showStatsHelpName = statsName + "_" + showStatsName;
        final ShowStatsSubCommand showStatsSubCommand = new ShowStatsSubCommand(
                showStatsName,
                new String[]{showStatsName, "s"},
                plugin,
                showStatsData,
                showStatsHelpName,
                helpMessage
        );
        final String showStatsHelpMessage = messages.getFormattedMessage("help." + showStatsHelpName);
        helpMessage.registerCommand(new HelpMessage.HelpData(showStatsHelpName, showStatsData, id -> showStatsHelpMessage));

        final String resetStatsName = "reset";
        final PermissionData resetStatsData = new PermissionData(statsName + "." + resetStatsName, basePermission, false);
        final String resetStatsHelpName = statsName + "_" + resetStatsName;
        final ResetStatsSubCommand resetStatsSubCommand = new ResetStatsSubCommand(
                resetStatsName,
                new String[]{resetStatsName, "r"},
                plugin,
                resetStatsData,
                resetStatsHelpName,
                helpMessage
        );
        final String resetStatsHelpMessage = messages.getFormattedMessage("help." + resetStatsHelpName);
        helpMessage.registerCommand(new HelpMessage.HelpData(resetStatsHelpName, resetStatsData, id -> resetStatsHelpMessage));


        final PermissionData statsData = new PermissionData(statsName, basePermission, true);
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

    private void registerCommand(@NotNull CommandNode<ClaimContextData> command, @NotNull PermissionData data) {
        // TODO Check for unsafe add
        subCommandManager.register(command, data.isContextSpecific());
    }

    private void registerPerm(@NotNull PermissionData data, @NotNull String[] modifiers) {
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
