package me.simzahn.multiprofile;

import me.simzahn.multiprofile.utils.ConfigUtils;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProfileManager {

    private final Player player;

    public ProfileManager(Player player) {
        this.player = player;
    }

    public Optional<Profile> getProfile(String profile) {
        File profileFile = new PathCreator().getPath(this.player, profile).toFile();
        if (profileFile.exists()) {
            return Profile.loadProfile(profileFile);
        } else {
            return Optional.empty();
        }
    }

    public boolean hasProfile(String profile) {
        return new PathCreator().getPath(this.player, profile).toFile().exists();
    }

    public List<String> getProfiles() {

        File file = new PathCreator().getPath(this.player).toFile();

        File[] subFiles = file.listFiles();

        if (subFiles == null) {
            return new ArrayList<>();
        }

        return Arrays.stream(subFiles)
                .map(File::getName)
                .filter(name -> name.endsWith(".profile"))
                .map(name -> name.replace(".profile", ""))
                .collect(Collectors.toList());

    }

    public String getActiveProfileName() {
        return ConfigUtils.loadNewConfig(
                new PathCreator().getPathToDefaultConfig()
        ).getString("active-profile." + this.player.getUniqueId().toString());
    }

    public Optional<Profile> getActiveProfile() {
        return getProfile(getActiveProfileName());
    }

}
