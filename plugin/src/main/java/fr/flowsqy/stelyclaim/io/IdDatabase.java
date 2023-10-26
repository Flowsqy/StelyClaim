package fr.flowsqy.stelyclaim.io;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class IdDatabase {

    @NotNull
    public Set<UUID> load(@NotNull File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            final Set<UUID> ids = new HashSet<>();
            String line;
            while ((line = reader.readLine()) != null) {
                ids.add(UUID.fromString(line));
            }
            return ids;
        } catch (FileNotFoundException e) {
            return new HashSet<>();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(@NotNull File file, @NotNull Set<UUID> ids) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (UUID id : ids) {
                writer.write(id.toString());
                writer.newLine();
            }
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
