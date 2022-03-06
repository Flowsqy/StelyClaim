package fr.flowsqy.stelyclaim;

import com.earth2me.essentials.Essentials;
import fr.flowsqy.stelyclaim.api.ProtocolManager;
import fr.flowsqy.stelyclaim.command.CommandManager;
import fr.flowsqy.stelyclaim.internal.PlayerHandler;
import fr.flowsqy.stelyclaim.io.BedrockManager;
import fr.flowsqy.stelyclaim.io.Messages;
import fr.flowsqy.stelyclaim.io.StatisticManager;
import fr.flowsqy.stelyclaim.util.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StelyClaimPlugin extends JavaPlugin {

    private final Map<String, PillarData> pillarData = new HashMap<>();
    private YamlConfiguration configuration;
    private Messages messages;
    private BedrockManager breakManager;
    private StatisticManager statisticManager;
    private TeleportSync teleportSync;
    private EssentialsManager essentialsManager;
    private MailManager mailManager;
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
        this.messages = new Messages(initFile(dataFolder, "messages.yml"), "&7[&5StelyClaim&7]&f");
        this.breakManager = new BedrockManager(dataFolder);
        this.statisticManager = new StatisticManager(this, dataFolder);
        this.teleportSync = new TeleportSync(this);
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("Essentials");
        this.essentialsManager = plugin instanceof Essentials ? new EssentialsManager.EssentialsManagerImpl((Essentials) plugin) : EssentialsManager.NULL;
        this.mailManager = new MailManager(messages, configuration, essentialsManager);

        new DisconnectListener(this);

        this.protocolManager = new ProtocolManager(this);

        // Register internal ClaimHandler
        protocolManager.registerHandler(new PlayerHandler(this));

        this.commandManager = new CommandManager(this);

        this.statisticManager.initData();
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

    public Messages getMessages() {
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

    public EssentialsManager getEssentialsManager() {
        return essentialsManager;
    }

    public MailManager getMailManager() {
        return mailManager;
    }

    public Map<String, PillarData> getPillarData() {
        return pillarData;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
