package fr.flowsqy.stelyclaim;

import fr.flowsqy.stelyclaim.api.HandlerRegistry;
import fr.flowsqy.stelyclaim.api.ProtocolManager;
import fr.flowsqy.stelyclaim.command.CommandManager;
import fr.flowsqy.stelyclaim.common.ConfigurationFormattedMessages;
import fr.flowsqy.stelyclaim.common.PrefixedConfigurationFormattedMessages;
import fr.flowsqy.stelyclaim.external.ExternalManager;
import fr.flowsqy.stelyclaim.internal.PlayerHandler;
import fr.flowsqy.stelyclaim.api.BedrockManager;
import fr.flowsqy.stelyclaim.io.StatisticManager;
import fr.flowsqy.stelyclaim.pillar.PillarManager;
import fr.flowsqy.stelyclaim.protocol.RegionNameManager;
import fr.flowsqy.stelyclaim.util.TeleportSync;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StelyClaimPlugin extends JavaPlugin {

    private YamlConfiguration configuration;
    private ConfigurationFormattedMessages messages;
    private BedrockManager breakManager;
    private StatisticManager statisticManager;
    private PillarManager pillarManager;
    private TeleportSync teleportSync;
    private HandlerRegistry handlerRegistry;
    private ProtocolManager protocolManager;
    private CommandManager commandManager;

    @Override
    public void onEnable() {

        final Logger logger = getLogger();
        final File dataFolder = getDataFolder();

        if (!checkDataFolder(dataFolder)) {
            logger.log(Level.WARNING, "Can not write in the directory : " + dataFolder.getAbsolutePath());
            logger.log(Level.WARNING, "Disable the plugin");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.configuration = initFile(dataFolder, "config.yml");
        this.messages = PrefixedConfigurationFormattedMessages.create(
                initFile(dataFolder, "messages.yml"),
                ChatColor.GRAY + "[" + ChatColor.DARK_PURPLE + "StelyClaim" + ChatColor.GRAY + "]" + ChatColor.WHITE
        );

        RegionNameManager.setInternalMessages(messages);

        this.breakManager = new BedrockManager(dataFolder);
        breakManager.load();
        this.statisticManager = new StatisticManager(this, dataFolder);
        this.teleportSync = new TeleportSync(this);

        final ExternalManager externalManager = new ExternalManager();
        externalManager.load(this);

        pillarManager = new PillarManager();
        pillarManager.load(this);

        handlerRegistry = new HandlerRegistry();

        // Register internal ClaimHandler
        handlerRegistry.registerHandler(new PlayerHandler(this));

        this.protocolManager = null;//new ProtocolManager(this);

        this.commandManager = new CommandManager(this);

        this.statisticManager.initData();
    }

    @Override
    public void onDisable() {
        breakManager.save();
    }

    private boolean checkDataFolder(File dataFolder) {
        if (dataFolder.exists())
            return dataFolder.canWrite();
        return dataFolder.mkdirs();
    }

    private YamlConfiguration initFile(File dataFolder, String fileName) {
        final File file = new File(dataFolder, fileName);
        if (!file.exists()) {
            try {
                Files.copy(Objects.requireNonNull(getResource(fileName)), file.toPath());
            } catch (IOException ignored) {
            }
        }

        return YamlConfiguration.loadConfiguration(file);
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    @NotNull
    public ConfigurationFormattedMessages getMessages() {
        return messages;
    }

    public BedrockManager getBreakManager() {
        return breakManager;
    }

    public StatisticManager getStatisticManager() {
        return statisticManager;
    }

    public TeleportSync getTeleportSync() {
        return teleportSync;
    }

    public PillarManager getPillarManager() {
        return pillarManager;
    }

    public HandlerRegistry getHandlerRegistry() {
        return handlerRegistry;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    @SuppressWarnings("unused") // API
    public CommandManager getCommandManager() {
        return commandManager;
    }
}
