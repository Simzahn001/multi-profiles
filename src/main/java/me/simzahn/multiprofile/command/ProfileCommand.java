package me.simzahn.multiprofile.command;

import me.simzahn.multiprofile.Profile;
import me.simzahn.multiprofile.ProfileManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
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

                player.sendMessage(Component.text("Your profiles:", TextColor.color(255, 171, 0), TextDecoration.BOLD));

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
                    ).clickEvent(ClickEvent.callback(
                            audience -> {
                                new Profile(player, strings[1]);
                                player.sendMessage(Component.text(
                                        "Your profile has been created successfully!",
                                        TextColor.color(0, 255, 0),
                                        TextDecoration.BOLD
                                ));
                                player.sendMessage(Component.text(
                                                "You can now switch to it with /switch " + strings[1] + "!",
                                                TextColor.color(0, 170, 196)
                                ));
                            }
                    )));
                }
                return true;
            }

        }

        return false;
    }

    private void syncAndSaveCurrentProfile(Player player) {

        ProfileManager profileManager = new ProfileManager(player);

        Optional<Profile> profile = profileManager.getActiveProfile();

        if (profile.isPresent()) {

            System.out.println("current is present");
            profile.get().setData(player);
            if (profile.get().save()) {
                System.out.println("current profile saved");
            } else {
                System.out.println("current profile not saved");
            }

        } else {

            Profile newProfile = new Profile(player, "default");
            newProfile.setData(player);
            if (newProfile.save()) {
                System.out.println("new profile saved");
            } else {
                System.out.println("new profile not saved");
            }


        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

            if (args.length == 1) {

                List<String> list = new ArrayList<>();
                list.add("switch");
                list.add("list");
                return list;

            }else if (args.length == 2) {

                if (args[0].equals("switch")) {

                    if (!(sender instanceof Player)) {
                        return null;
                    }
                    Player player = (Player) sender;
                    ProfileManager profileManager = new ProfileManager(player);

                    return profileManager.getProfiles();

                }

            }

            return null;
    }
}
