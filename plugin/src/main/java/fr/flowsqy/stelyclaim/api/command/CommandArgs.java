package fr.flowsqy.stelyclaim.api.command;

import org.jetbrains.annotations.NotNull;

public record CommandArgs(@NotNull String[] path, @NotNull String[] args) {
}
