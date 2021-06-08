package fr.flowsqy.stelyclaim.api;

import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface ClaimOwner {

    String getName();

    List<UUID> getOwners();

    List<UUID> getMembers();

    boolean own(Player player);

}
