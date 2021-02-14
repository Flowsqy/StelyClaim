package fr.flowsqy.stelyclaim;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import fr.flowsqy.stelyclaim.command.CommandManager;
import fr.flowsqy.stelyclaim.io.BedrockManager;
import fr.flowsqy.stelyclaim.io.Messages;
import fr.flowsqy.stelyclaim.util.TeleportSync;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StelyClaimPlugin extends JavaPlugin {

    private static StelyClaimPlugin instance;
    private Messages messages;
    private BedrockManager breakManager;
    private RegionContainer regionContainer;
    private SessionManager sessionManager;
    private TeleportSync teleportSync;

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

        this.messages = new Messages(initMessages(dataFolder));
        this.breakManager = new BedrockManager(getDataFolder());
        this.regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        this.sessionManager = WorldEdit.getInstance().getSessionManager();
        this.teleportSync = new TeleportSync(this);

        new CommandManager(this, messages);

    }

    private boolean checkDataFolder(File dataFolder) {
        if (dataFolder.exists())
            return dataFolder.canWrite();
        return dataFolder.mkdirs();
    }

    private YamlConfiguration initMessages(File dataFolder) {
        final File messagesFile = new File(dataFolder, "messages.yml");
        if (!messagesFile.exists()) {
            try {
                Files.copy(Objects.requireNonNull(getResource("messages.yml")), messagesFile.toPath());
            } catch (IOException ignored) {
            }
        }

        return YamlConfiguration.loadConfiguration(messagesFile);
    }

    public Messages getMessages() {
        return messages;
    }

    public BedrockManager getBreakManager() {
        return breakManager;
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
}
