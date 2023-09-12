package me.simzahn.multiprofile;

import me.simzahn.multiprofile.command.SwitchCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

public final class Main extends JavaPlugin {

    private static Main plugin;

    @Override
    public void onEnable() {

        plugin = this;

        getCommand("switch").setExecutor(new SwitchCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Main getPlugin() {
        return plugin;
    }

}
