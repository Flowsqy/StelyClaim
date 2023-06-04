package fr.flowsqy.stelyclaim.command.struct;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CommandTabCompleter<T> {

    List<String> tabComplete(@NotNull CommandContext<T> context);

}
