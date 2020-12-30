package fr.flowsqy.stelyclaim;

import fr.flowsqy.stelyclaim.command.CommandManager;
import fr.flowsqy.stelyclaim.io.BedrockManager;
import fr.flowsqy.stelyclaim.io.Messages;
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

}
