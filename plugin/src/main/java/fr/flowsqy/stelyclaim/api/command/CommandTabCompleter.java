package fr.flowsqy.stelyclaim.api.command;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CommandTabCompleter {

    @Nullable
    List<String> tabComplete(@NotNull CommandContext context);

}
