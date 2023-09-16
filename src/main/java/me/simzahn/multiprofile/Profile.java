package me.simzahn.multiprofile;

import com.destroystokyo.paper.profile.PlayerProfile;
import me.simzahn.multiprofile.utils.ConfigUtils;
import me.simzahn.multiprofile.utils.Serializer;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class Profile {

    private final String name;
    private final String playerUUID;

    private double health = 0;
    private ItemStack[] inventory = null;
    private Location location;

    public Profile(Player player, String name) {
        this.name = name;
        this.playerUUID = player.getUniqueId().toString();

        health = 20;
        inventory = new ItemStack[] {};
        location = player.getLocation();

        if (!save()) {
            throw new RuntimeException("Could not save profile");
        };
    }

    private Profile(String name, String playerUUID, double health, ItemStack[] inventory, Location location) {
        this.name = name;
        this.playerUUID = playerUUID;
        this.health = health;
        this.inventory = inventory;
        this.location = location;
    }

    public void apply(Player player) {
        player.setHealth(health);
        player.getInventory().setContents(inventory);
        player.teleport(location);

        YamlConfiguration config = ConfigUtils.loadNewConfig(new PathCreator().getPathToDefaultConfig());
        config.set("active-profile." + this.playerUUID.toString(), name);
        ConfigUtils.saveConfig(config, new PathCreator().getPathToDefaultConfig().toFile());

        PlayerProfile profile = player.getPlayerProfile();
        profile.setName(this.name);
        player.setPlayerProfile(profile);
    }

    public boolean save() {

        YamlConfiguration file = ConfigUtils.loadNewConfig(new PathCreator().getPath(this.playerUUID, this.name));

        file.set("name", name);
        file.set("uuid", playerUUID);
        file.set("health", health);
        file.set("inventory", Serializer.itemStackArrayToBase64(this.inventory));
        file.set("location", Serializer.locationToBase64(this.location));

        return ConfigUtils.saveConfig(file, new PathCreator().getPath(this.playerUUID, this.name).toFile());

    }

    public void setData(Player player) {
        this.health = player.getHealth();
        this.inventory = player.getInventory().getContents();
        this.location = player.getLocation();
    }

    public static Optional<Profile> loadProfile(File profileFile) {

        if (!profileFile.exists()) {
            return Optional.empty();
        } else {

            YamlConfiguration file = ConfigUtils.loadNewConfig(profileFile.toPath());

            String name = file.getString("name", "");
            if (name.equals("")) {
                return Optional.empty();
            }
            String uuid = file.getString("uuid", "");
            if (uuid.equals("")) {
                return Optional.empty();
            }
            int health = file.getInt("health", -1);
            if (health == -1) {
                return Optional.empty();
            }
            ItemStack[] inventory;
            try {
                inventory = Serializer.itemStackArrayFromBase64(file.getString("inventory"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Location location = null;
            try {
                location = Serializer.locationFromBase64(file.getString("location"));
            } catch (IOException e) {
                return Optional.empty();
            }

            return Optional.of(new Profile(
                    name,
                    uuid,
                    health,
                    inventory,
                    location
            ));

        }

    }


}
