package fr.flowsqy.stelyclaim.api.command;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CommandTabCompleter {

    List<String> tabComplete(@NotNull CommandContext context);

}
