package me.simzahn.multiprofile;

import org.bukkit.entity.Player;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathCreator {

    private final Path ROOT = Main.getPlugin().getDataFolder().toPath();

    public Path getPath(Player player) {
        return Paths.get(ROOT.toString(), player.getUniqueId().toString());
    }

    public Path getPath(Player player, String profile) {
        return Paths.get(ROOT.toString(), player.getUniqueId().toString(), profile + ".profile");
    }

    public Path getPath(String uuid, String profile) {
        return Paths.get(ROOT.toString(), uuid, profile + ".profile");
    }

    public Path getPathToDefaultConfig() {
        return Paths.get(ROOT.toString(), "conifg.yml");
    }



}
