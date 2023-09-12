package me.simzahn.multiprofile.command;

import me.simzahn.multiprofile.Profile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SwitchCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(Component.text("This command must be executed by a player!", TextColor.color(110, 0, 15), TextDecoration.BOLD));
            return true;
        }

        if (strings.length != 1) {
            return false;
        }

        Player player = (Player) commandSender;

        // check if the profile exits
        if (!Profile.getAllProfiles(player).contains(strings[0])) {
            player.sendMessage(
                    Component.text("This profile does not exist!", TextColor.color(110, 0, 15), TextDecoration.BOLD)
            );
            player.sendMessage(
                    Component.text("[Click to create a new Profile with the name " + strings[0] + "]", TextColor.color(0, 140, 18), TextDecoration.BOLD)
                            .clickEvent(ClickEvent.callback(
                                    (clickEvent) -> {

                                        // save the current profile
                                        Optional<Profile> currentProfile = Profile.getCurrentProfile(player);
                                        if (currentProfile.isPresent()) {
                                            currentProfile.get().fetchData(player);
                                            currentProfile.get().saveToConfig();
                                        } else {
                                            Profile profile = new Profile(player, "default");
                                            profile.fetchData(player);
                                            profile.saveToConfig();
                                        }

                                        Profile profile = new Profile(player, strings[0]);
                                        profile.apply(player);
                                        player.sendMessage(
                                                Component.text("Profile " + strings[0] + " created!", TextColor.color(0, 140, 18), TextDecoration.BOLD)
                                        );
                                    }
                            ))
            );
            return true;
        }else {

            // save the current profile
            Optional<Profile> currentProfile = Profile.getCurrentProfile(player);
            if (currentProfile.isPresent()) {
                currentProfile.get().fetchData(player);
                currentProfile.get().saveToConfig();
            } else {
                Profile profile = new Profile(player, "default");
                profile.fetchData(player);
                profile.saveToConfig();
            }

            Optional<Profile> profile = Profile.loadProfile(player.getUniqueId().toString(), strings[0]);
            if (profile.isPresent()) {
                profile.get().apply(player);
            } else {
                player.sendMessage(
                        Component.text("This profile does not exist!", TextColor.color(110, 0, 15), TextDecoration.BOLD)
                );
            }
            return true;
        }


    }
}
