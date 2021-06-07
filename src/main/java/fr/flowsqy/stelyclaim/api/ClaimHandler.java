package fr.flowsqy.stelyclaim.api;

public interface ClaimHandler {

    String getOwner(String claim);

    RegionModifier getDefineModifier();

    RegionModifier getRedefineModifier();

}
