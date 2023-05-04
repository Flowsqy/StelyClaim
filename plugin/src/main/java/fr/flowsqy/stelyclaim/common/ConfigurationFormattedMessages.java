package fr.flowsqy.stelyclaim.common;

import fr.flowsqy.stelyclaim.io.ConfigurationCachedMessages;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Map;

public class ConfigurationFormattedMessages extends SimpleFormattedMessages {

    private final ConfigurationCachedMessages configurationCachedMessages;

    public ConfigurationFormattedMessages(char colorChar, Map<String, String> defaultPlaceholders, YamlConfiguration yamlConfiguration) {
        super(defaultPlaceholders);
        this.configurationCachedMessages = new ConfigurationCachedMessages(colorChar, yamlConfiguration);
    }

    @Override
    public String getMessage(String identifier) {
        return configurationCachedMessages.getMessage(identifier);
    }
}
