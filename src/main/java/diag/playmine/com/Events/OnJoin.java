package diag.playmine.com.Events;

import java.io.IOException;

import diag.playmine.com.AScoreBoard;
import diag.playmine.com.PlayerScoreBoard.ScoreBoard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.json.JSONException;

public class OnJoin implements Listener {
    public OnJoin() {
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws JSONException, IOException {
        Player p = e.getPlayer();
        new ScoreBoard.sendActionBarMessage(p);
        Plugin plugin = Bukkit.getPluginManager().getPlugin("aScoreBoard");
        assert plugin != null;
        p.setScoreboard(ScoreBoard.newScoreBoard(ScoreBoard.getPath(plugin, String.valueOf(p.getUniqueId()))));
    }
}
