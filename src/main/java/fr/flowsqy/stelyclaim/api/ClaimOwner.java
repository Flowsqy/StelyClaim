package fr.flowsqy.stelyclaim.api;

import java.util.List;
import java.util.UUID;

public interface ClaimOwner {

    String getName();

    List<UUID> getOwners();

    List<UUID> getMembers();

}
