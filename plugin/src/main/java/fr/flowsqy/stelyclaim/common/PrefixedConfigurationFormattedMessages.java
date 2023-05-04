package fr.flowsqy.stelyclaim.common;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Collections;

public class PrefixedConfigurationFormattedMessages {

    /**
     * Create a {@link ConfigurationFormattedMessages} that uses {@code '&'} character for color code prefixes
     * and the global placeholder '%prefix%' stored at the path 'prefix' in the configuration
     *
     * @param yamlConfiguration The configuration that stores the messages
     * @param defaultPrefix     The fallback prefix
     * @return A {@link ConfigurationFormattedMessages} as described above
     */
    public static ConfigurationFormattedMessages create(YamlConfiguration yamlConfiguration, String defaultPrefix) {
        final String originalPrefix = yamlConfiguration.getString("prefix", defaultPrefix);
        assert originalPrefix != null;
        final String prefix = ChatColor.translateAlternateColorCodes('&', originalPrefix);
        return new ConfigurationFormattedMessages('&', Collections.singletonMap("%prefix%", prefix), yamlConfiguration);
    }
}
