package me.simzahn.multiprofile;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import me.simzahn.multiprofile.utils.ConfigUtils;
import me.simzahn.multiprofile.utils.Serializer;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        //player.teleport(location);

        Main.getPlugin().getConfig().set("active-profile." + player.getUniqueId().toString(), name);
        List<String> list = Main.getPlugin().getConfig().getStringList("active-profiles");
        if (!list.contains(player.getUniqueId().toString())) {
            list.add(player.getUniqueId().toString());
        }

        PlayerProfile profile = player.getPlayerProfile();
        profile.setName(this.name);
        player.setPlayerProfile(profile);

        Main.getPlugin().saveConfig();

    }

    public void saveToConfig() {

        Gson gson =  new Gson();
        YamlConfiguration file = ConfigUtils.loadNewConfig(getConfigPath().toPath());

        file.set("uuid", playerUUID);
        file.set("health", health);
        file.set("inventory", Serializer.itemStackArrayToBase64(this.inventory));
        /*file.set("location", Serializer.serialize(location));*/

        System.out.println(ConfigUtils.saveConfig(file, getConfigPath()));
    }

    public void fetchData(Player player) {
        this.health = player.getHealth();
        this.inventory = player.getInventory().getContents();
        this.location = player.getLocation();
    }

    public File getConfigPath() {
        return getPathToProfile(this.playerUUID, this.name);
    }

    public static Optional<Profile> fromConfig(Path path) {

        if (!ConfigUtils.doesConfigExist(path)) {
            return Optional.empty();
        }

        YamlConfiguration file = ConfigUtils.loadNewConfig(path);


        Gson gson = new Gson();

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
        } catch (JsonSyntaxException e) {
            return Optional.empty();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Location location = null;
        /*try {
            location = Serializer.itemStackArrayFromBase64(file.getString("location"), Location.class);
        } catch (JsonSyntaxException e) {
            return Optional.empty();
        }*/


        return Optional.of(new Profile(
                path.getFileName().toString().replace(".profile", ""),
                uuid,
                health,
                inventory,
                location
        ));

    }

    public static Optional<Profile> getCurrentProfile(Player player) {
        String name = Main.getPlugin().getConfig().getString("active-profile." + player.getUniqueId().toString());
        if (name == null) {
            return Optional.empty();
        }

        return loadProfile(player.getUniqueId().toString(), name);
    }

    public static Optional<Profile> loadProfile(String palyerUUID, String name) {

        File path = getPathToProfile(palyerUUID, name);

        return fromConfig(path.toPath());

    }

    public static List<String> getAllProfiles(Player player) {
        File file = new File(Main.getPlugin().getDataFolder().getPath() + "/" + player.getUniqueId().toString());

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

    public static File getPathToProfile(String playerUUID, String profileName) {

        return new File(Main.getPlugin().getDataFolder().getPath() + "/" + playerUUID + "/" + profileName + ".profile");

    }

    public static Optional<String> getActiveProfile(Player player) {

        if (Main.getPlugin().getConfig().getStringList("active-profile").contains(player.getUniqueId().toString())) {
            return Optional.empty();
        }

        return Optional.ofNullable(Main.getPlugin().getConfig().getString("active-profile." + player.getUniqueId().toString()));
    }

}
