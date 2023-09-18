package me.simzahn.multiprofile.command;

import me.simzahn.multiprofile.PathCreator;
import me.simzahn.multiprofile.Profile;
import me.simzahn.multiprofile.ProfileManager;
import me.simzahn.multiprofile.utils.ConfigUtils;
import me.simzahn.multiprofile.utils.Serializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProfileCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Component.text("This command must be executed by a player!", TextColor.color(110, 0, 15), TextDecoration.BOLD));
            return true;
        }

        Player player = (Player) commandSender;

        if (strings.length == 1) {

            if (strings[0].equalsIgnoreCase("list")) {

                ProfileManager profileManager = new ProfileManager(player);

                int maxProfiles = ConfigUtils.loadNewConfig(new PathCreator().getPathToDefaultConfig()).getInt("limit");
                int currentProfiles = new ProfileManager(player).getProfiles().size();

                player.sendMessage(Component.text("Your profiles:", TextColor.color(255, 171, 0), TextDecoration.BOLD)
                        .append(Component.text(" (" + currentProfiles + "/" + maxProfiles + ")", TextColor.color(255, 171, 0))));

                profileManager.getProfiles().forEach(profileName -> {

                    Component component = Component.text(
                            " - " + profileName,
                            TextColor.color(150, 0, 120)
                    );
                    player.sendMessage(component);

                });

                return true;

            }

            return false;

        }else if (strings.length == 2) {

            if (strings[0].equals("switch")) {

                ProfileManager profileManager = new ProfileManager(player);

                if (profileManager.hasProfile(strings[1])) {

                    profileManager.getProfile(strings[1]).ifPresent(profile -> {

                        if (profile.getName().equalsIgnoreCase(strings[1])) {
                            player.sendMessage(Component.text(
                                    "You are already using this profile!",
                                    TextColor.color(255, 0, 0),
                                    TextDecoration.BOLD
                            ));
                            return;
                        }

                        syncAndSaveCurrentProfile(player);

                        profile.apply(player);
                        player.sendMessage(Component.text(
                                "Your profile has been switched to " + strings[1] + "!",
                                TextColor.color(0, 255, 0)
                        ));

                    });
                } else {

                    player.sendMessage(Component.text(
                            "This profile does not exist!",
                            TextColor.color(255, 0, 0),
                            TextDecoration.BOLD
                    ));
                    player.sendMessage(Component.text(
                            "If you wish to create a new profile with this name, click the button below!",
                            TextColor.color(0, 170, 196)
                    ));
                    player.sendMessage(Component.text(
                            "[Create new profile with name " + strings[1] + "]",
                            TextColor.color(114, 255, 58),
                            TextDecoration.BOLD
                    ).clickEvent(ClickEvent.runCommand("/profile create " + strings[1] + " " + player.getUniqueId().toString())));
                }
                return true;
            } else if (strings[0].equals("defaults")) {

                if (!commandSender.hasPermission("multiprofile.commands.configureDefaults")) {
                    commandSender.sendMessage(Component.text(
                            "You do not have permission to use this command!",
                            TextColor.color(255, 0, 0)
                    ));
                }

                YamlConfiguration config = ConfigUtils.loadNewConfig(new PathCreator().getPathToDefaultConfig());

                if (strings[1].equals("inventory")) {

                    config.set("defaults.inventory", Serializer.itemStackArrayToBase64(player.getInventory().getContents()));
                    commandSender.sendMessage(Component.text(
                            "Your inventory has been saved as the default inventory!",
                            TextColor.color(0, 255, 0)
                    ));

                } else if (strings[1].equals("location")) {

                    config.set("defaults.location", Serializer.locationToBase64(player.getLocation()));
                    commandSender.sendMessage(Component.text(
                            "Your location has been saved as the default location!",
                            TextColor.color(0, 255, 0)
                    ));

                }

                ConfigUtils.saveConfig(config, new PathCreator().getPathToDefaultConfig().toFile());
                return true;
            } else if (strings[0].equals("limit")) {

                if (!commandSender.hasPermission("multiprofile.commands.limit")) {
                    commandSender.sendMessage(Component.text(
                            "You do not have permission to use this command!",
                            TextColor.color(255, 0, 0)
                    ));
                }

                YamlConfiguration config = ConfigUtils.loadNewConfig(new PathCreator().getPathToDefaultConfig());

                if (strings[1].equals("none")) {
                    config.set("limit", -1);
                    commandSender.sendMessage(Component.text(
                            "The profile limit has been set to none!",
                            TextColor.color(0, 255, 0)
                    ));
                } else {
                    try {
                        int limit = Integer.parseInt(strings[1]);
                        config.set("limit", limit);
                        commandSender.sendMessage(Component.text(
                                "The profile limit has been set to " + limit + "!",
                                TextColor.color(0, 255, 0)
                        ));
                    } catch (NumberFormatException e) {
                        commandSender.sendMessage(Component.text(
                                "The limit must be a number!",
                                TextColor.color(255, 0, 0)
                        ));
                        return true;
                    }
                }

                ConfigUtils.saveConfig(config, new PathCreator().getPathToDefaultConfig().toFile());
                return true;

            }

        }

        return false;
    }

    private void syncAndSaveCurrentProfile(Player player) {

        ProfileManager profileManager = new ProfileManager(player);

        Optional<Profile> profile = profileManager.getActiveProfile();

        if (profile.isPresent()) {

            profile.get().setData(player);
            profile.get().save();

        } else {

            Profile newProfile = new Profile(player, "default");
            newProfile.setData(player);
            newProfile.save();


        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

            if (args.length == 1) {

                List<String> list = new ArrayList<>();
                list.add("switch");
                list.add("list");

                if (sender.hasPermission("multiprofile.commands.configureDefaults")) {
                    list.add("defaults");
                }
                if (sender.hasPermission("multiprofile.commands.configureProfileLimit")) {
                    list.add("limit");
                }

                return list;

            }else if (args.length == 2) {

                if (args[0].equals("switch")) {

                    if (!(sender instanceof Player)) {
                        return null;
                    }
                    Player player = (Player) sender;
                    ProfileManager profileManager = new ProfileManager(player);

                    return profileManager.getProfiles();

                } else if (args[0].equals("defaults")) {

                    List<String> list = new ArrayList<>();
                    list.add("inventory");
                    list.add("location");
                    return list;

                } else if (args[0].equals("limit")) {

                    List<String> list = new ArrayList<>();
                    for (int i = 1; i <= 4; i++) {
                        list.add(String.valueOf(i));
                    }
                    list.add("none");
                    return list;

                }

            }

            return null;
    }
}
