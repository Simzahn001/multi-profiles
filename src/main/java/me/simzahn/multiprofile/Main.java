package me.simzahn.multiprofile;

import me.simzahn.multiprofile.command.CommandCallback;
import me.simzahn.multiprofile.command.ProfileCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main plugin;

    @Override
    public void onEnable() {

        plugin = this;

        getCommand("profile").setExecutor(new ProfileCommand());

        getServer().getPluginManager().registerEvents(new CommandCallback(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Main getPlugin() {
        return plugin;
    }

}
