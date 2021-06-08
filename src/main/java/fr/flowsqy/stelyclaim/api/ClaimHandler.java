package fr.flowsqy.stelyclaim.api;

public interface ClaimHandler<T extends ClaimOwner> {

    String getId();

    T getOwner(String claimIdentifier);

    String getIdentifier(T owner);

    RegionModifier<T> getDefineModifier();

    RegionModifier<T> getRedefineModifier();

    ClaimMessage getMessages();

}
