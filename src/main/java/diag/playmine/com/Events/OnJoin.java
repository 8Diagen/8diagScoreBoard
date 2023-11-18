package diag.playmine.com.Events;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    public static class PlayerInfo {
        public String playerName;
        public Double balance;
        public Double currentExp = 0d;
        public Integer playerLevel = 0;

        public String id;

        public void setBalance(Double balance) {
            this.balance = balance;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setPlayerName(String playerName) {
            this.playerName = playerName;
        }
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws JSONException, IOException {
        Player p = e.getPlayer();
        String path = ScoreBoard.getPath(AScoreBoard.getPlugin(), p.getUniqueId() + "");
        File f = new File(path);
        if (!f.exists()){
            writeData(p, path);
        }
        new ScoreBoard.sendActionBarMessage(p);
        Plugin plugin = Bukkit.getPluginManager().getPlugin("aScoreBoard");
        assert plugin != null;
        p.setScoreboard(ScoreBoard.newScoreBoard(path));
    }

    public void writeData(Player player, String path) throws IOException {
        PlayerInfo playerInfo = new PlayerInfo();
        ObjectMapper objectMapper = new ObjectMapper();
        playerInfo.setBalance(Double.parseDouble(Objects.requireNonNull(AScoreBoard.getPlugin().getConfig().getString("startbalance"))));
        playerInfo.setPlayerName(Objects.requireNonNull(player.getPlayer()).getName());
        playerInfo.setId(player.getUniqueId().toString());
        String result = objectMapper.writeValueAsString(playerInfo);
        File file = new File(path);
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(result);
        fileWriter.flush();
        fileWriter.close();
    }
}
