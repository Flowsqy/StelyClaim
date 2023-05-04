package fr.flowsqy.stelyclaim.io;

import fr.flowsqy.stelyclaim.api.CachedMessages;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationCachedMessages implements CachedMessages {

    private final Map<String, String> messageById;

    public ConfigurationCachedMessages(char colorChar, YamlConfiguration yamlConfiguration) {
        messageById = new HashMap<>();
        fillMessageById(colorChar, yamlConfiguration);
    }

    private void fillMessageById(char colorChar, YamlConfiguration yamlConfiguration) {
        for (Map.Entry<String, Object> entry : yamlConfiguration.getValues(true).entrySet()) {
            if (entry.getValue() instanceof String) {
                messageById.put(entry.getKey(), ChatColor.translateAlternateColorCodes(colorChar, (String) entry.getValue()));
            }
        }
    }

    @Override
    public String getMessage(String identifier) {
        return messageById.get(identifier);
    }
}
