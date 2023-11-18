package diag.playmine.com.PlayerScoreBoard;

import diag.playmine.acolor.Color.Color;
import diag.playmine.com.AScoreBoard;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class ScoreBoard {

    public static Score level;
    public static Score exp;
    public static Score balance;
    public static Objective objective;
    public static Scoreboard board;

    public static List<Score> scores() {
        return List.of(balance, exp, level);
    }
    public static String getPath(Plugin plugin, String uuid) {
        return plugin.getDataFolder().toString().substring(0, 8) + "Players/" + uuid + ".json";
    }

    public static void setColorConfig() {
        Color.config = (YamlConfiguration) Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("aScoreBoard")).getConfig();
    }

    public static Scoreboard newScoreBoard(String path) throws JSONException, IOException {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        assert scoreboardManager != null;
        board = scoreboardManager.getNewScoreboard();
        objective = board.registerNewObjective("Statistics", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("Статистика");
        updateScoreBoardData(path);
        for (int i = scores().size(); i > 0; i--){
            scores().get(i - 1).setScore(i);
        }
        return board;
    }

    public static void updateScoreBoardData(String path) throws IOException, JSONException {
        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);
        JSONObject jsonObject = new JSONObject(br.readLine());
        level = objective.getScore(Color.message("scoreboard.playerLevel", new String[]{convert(jsonObject.getInt("playerLevel") + "")}));
        exp = objective.getScore(Color.message("scoreboard.currentExp", new String[]{convert(jsonObject.getDouble("currentExp") + "")}));
        balance = objective.getScore(Color.message("scoreboard.balance", new String[]{convert(jsonObject.getDouble("balance") + "")}));
    }

    public static double getBalance(String path) throws IOException, JSONException {
        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);
        JSONObject jsonObject = new JSONObject(br.readLine());
        return jsonObject.getDouble("balance");
    }

    public static void setBalance(String path, double value) throws IOException, JSONException {
        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);
        JSONObject jsonObject = new JSONObject(br.readLine());
        double bal = jsonObject.getDouble("balance");
        bal -= value;
        jsonObject.put("balance", bal);
        FileWriter fw = new FileWriter(path);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(jsonObject.toString());
        bw.close();
        Objects.requireNonNull(Bukkit.getPlayer(jsonObject.getString("playerName"))).setScoreboard(newScoreBoard(path));
    }

    public static Map<Integer, Character> symbols = Map.of(3, 'K',6, 'M',9, 'B');

    public static String convert(String num) {
        if (num.contains(".")) num = num.substring(0, num.indexOf("."));
        int numLength = num.length();
        for (int i = 1; i < 4; i++) {
            Object[] ints = symbols.keySet().toArray();
            Arrays.sort(ints);
            int index = (Integer) ints[ints.length - i];
            if (numLength > index) {
                num = num.substring(0, num.length() - index) + "." + num.charAt(num.length() - index) + symbols.get(index);
                if (num.charAt(num.indexOf('.') + 1) == '0') num = num.replace(".0", "");
                break;
            }
        }
        return num;
    }

    public static class sendActionBarMessage implements Runnable {

        public Player p;

        public sendActionBarMessage(Player p) {
            this.p = p;
            BukkitScheduler scheduler = AScoreBoard.getPlugin().getServer().getScheduler();
            scheduler.scheduleSyncRepeatingTask(AScoreBoard.getPlugin(), this, 0, 100000);
        }

        @Override
        public void run() {
            p.sendTitle("\uE001", "", 0, 100000, 0);
        }
    }
}
