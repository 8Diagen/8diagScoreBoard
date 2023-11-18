package diag.playmine.com;

import diag.playmine.acolor.Color.Color;
import diag.playmine.com.Events.OnJoin;
import diag.playmine.com.PlayerScoreBoard.ScoreBoard;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class AScoreBoard extends JavaPlugin {
    public static AScoreBoard plugin;

    public AScoreBoard() {
    }

    public static AScoreBoard getPlugin() {
        return plugin;
    }

    public void onEnable() {
        plugin = this;
        System.out.println("aScoreBoard plugin has been successfully loaded");
        saveDefaultConfig();
        ScoreBoard.setColorConfig();
        initialize();
    }

    public void onDisable() {
        System.out.println("aScoreBoard plugin has been successfully completed");
    }

    public void initialize() {
        this.getServer().getPluginManager().registerEvents(new OnJoin(), this);
    }
}