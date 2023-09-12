package me.simzahn.multiprofile.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigUtils {

    /**
     * Load a new config from the given path. If the config does not exist, it will be created.
     * @param path The path to the config file.
     * @return The loaded config.
     */
    public static YamlConfiguration loadNewConfig(@NotNull Path path) {

        File customConfigFile = path.toFile();

        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            try {
                customConfigFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return YamlConfiguration.loadConfiguration(customConfigFile);
    }

    /**
     * Check if the given config exists.
     * @param path The path to the config file.
     * @return True if the config exists.
     */
    public static boolean doesConfigExist(@NotNull Path path) {
        return path.toFile().exists();
    }

    /**
     * Save the given config.
     * @param config The config to save.
     * @return True if the config was saved successfully.
     */
    public static boolean saveConfig(YamlConfiguration config, File file) {

        try {
            config.save(file);
        } catch (IOException e) {
            return false;
        }

        return true;

    }

}
