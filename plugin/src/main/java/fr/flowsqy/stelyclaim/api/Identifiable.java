package fr.flowsqy.stelyclaim.api;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface Identifiable {

    @NotNull UUID getId();

}
