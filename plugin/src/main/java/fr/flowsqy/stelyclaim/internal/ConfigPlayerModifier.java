package fr.flowsqy.stelyclaim.internal;

import fr.flowsqy.stelyclaim.api.FormattedMessages;
import fr.flowsqy.stelyclaim.common.ConfigRegionModifier;
import org.bukkit.configuration.Configuration;

public class ConfigPlayerModifier extends ConfigRegionModifier<PlayerOwner> {

    public ConfigPlayerModifier(Configuration config, FormattedMessages messages, String category) {
        super(config, messages, category, owner -> owner.player().getUniqueId());
    }

}
