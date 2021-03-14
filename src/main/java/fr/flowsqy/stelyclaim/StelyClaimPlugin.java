package fr.flowsqy.stelyclaim;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import fr.flowsqy.stelyclaim.command.CommandManager;
import fr.flowsqy.stelyclaim.io.BedrockManager;
import fr.flowsqy.stelyclaim.io.Messages;
import fr.flowsqy.stelyclaim.io.StatisticManager;
import fr.flowsqy.stelyclaim.util.DisconnectListener;
import fr.flowsqy.stelyclaim.util.MailManager;
import fr.flowsqy.stelyclaim.util.PillarData;
import fr.flowsqy.stelyclaim.util.TeleportSync;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
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

    private static StelyClaimPlugin instance;
    private YamlConfiguration configuration;
    private Messages messages;
    private BedrockManager breakManager;
    private StatisticManager statisticManager;
    private RegionContainer regionContainer;
    private SessionManager sessionManager;
    private TeleportSync teleportSync;
    private MailManager mailManager;
    private final Map<String, PillarData> pillarData = new HashMap<>();

    public static StelyClaimPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        final Logger logger = getLogger();
        final File dataFolder = getDataFolder();

        if (!checkDataFolder(dataFolder)) {
            logger.log(Level.WARNING, "Can not write in the directory : " + dataFolder.getAbsolutePath());
            logger.log(Level.WARNING, "Disable the plugin");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.configuration = initFile(dataFolder, "config.yml");
        this.messages = new Messages(initFile(dataFolder, "messages.yml"));
        this.breakManager = new BedrockManager(dataFolder);
        this.statisticManager = new StatisticManager(this, dataFolder);
        this.regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        this.sessionManager = WorldEdit.getInstance().getSessionManager();
        this.teleportSync = new TeleportSync(this);
        this.mailManager = new MailManager(messages, configuration);

        new DisconnectListener(this);

        new CommandManager(this);

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

    public RegionContainer getRegionContainer() {
        return regionContainer;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public TeleportSync getTeleportSync() {
        return teleportSync;
    }

    public MailManager getMailManager() {
        return mailManager;
    }

    public Map<String, PillarData> getPillarData() {
        return pillarData;
    }
}
