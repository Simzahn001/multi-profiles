package me.simzahn.multiprofile.command;

import me.simzahn.multiprofile.PathCreator;
import me.simzahn.multiprofile.Profile;
import me.simzahn.multiprofile.ProfileManager;
import me.simzahn.multiprofile.utils.ConfigUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.UUID;

public class CommandCallback implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

        if (!event.getMessage().startsWith("/profile create")) return;

        event.setCancelled(true);

        String profileName = event.getMessage().split(" ")[2];
        String uuid = event.getMessage().split(" ")[3];

        Player player = Bukkit.getPlayer(UUID.fromString(uuid));


        if (player.hasPermission("multiprofile.commands.bypassProfileLimit")) {
            player.sendMessage(Component.text(
                    "You don't have the permission to create that many profiles!",
                    TextColor.color(255, 0, 0),
                    TextDecoration.BOLD
            ));
            return;
        }

        int maxProfiles = ConfigUtils.loadNewConfig(new PathCreator().getPathToDefaultConfig()).getInt("limit");
        int currentProfiles = new ProfileManager(player).getProfiles().size();

        if (currentProfiles >= maxProfiles) {
            player.sendMessage(Component.text(
                    "You can't create more than " + maxProfiles + " profiles!",
                    TextColor.color(255, 0, 0),
                    TextDecoration.BOLD
            ));
            return;
        }

        new Profile(player, profileName);

        player.sendMessage(Component.text(
                "Your profile has been created successfully!",
                TextColor.color(0, 255, 0),
                TextDecoration.BOLD
        ));
        player.sendMessage(Component.text(
                "You can now switch to it with /switch " + profileName + "!",
                TextColor.color(0, 170, 196)
        ));

    }
}
