package fr.flowsqy.stelyclaim.api;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

public interface Identifiable {
    
    @NotNull UUID getId();

}
