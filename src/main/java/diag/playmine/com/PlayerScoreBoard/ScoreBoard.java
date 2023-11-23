package diag.playmine.com.PlayerScoreBoard;

import diag.playmine.com.AScoreBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;

public class ScoreBoard {
    public static Score level;
    public static Score balance;
    public static Score expNeedScore;
    public static Score expNeedProgress;
    public static Objective objective;
    public static String expNeed = "&aТребуется до следующего уровня:";
    public static String progressExp = "&e↳&f ▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰▰ &e⊶ 10.5%";
    public static Scoreboard board;

    public static String getPath(Plugin plugin, String uuid) {
        return plugin.getDataFolder().toString().substring(0, 8) + "Players/" + uuid + ".json";
    }

    public static Scoreboard newScoreBoard(String path) throws JSONException, IOException {
        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        assert scoreboardManager != null;
        board = scoreboardManager.getNewScoreboard();
        objective = board.registerNewObjective("Statistics", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("Статистика");
        updateScoreBoardData(path);
        newSpaceScore(1).setScore(6);
        level.setScore(5);
        expNeedScore.setScore(4);
        expNeedProgress.setScore(3);
        newSpaceScore(2).setScore(2);
        balance.setScore(1);
        return board;
    }

    public static Score newSpaceScore(int cnt) {
        return objective.getScore(" ".repeat(cnt));
    }

    public static void updateScoreBoardData(String path) throws IOException, JSONException {
        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);
        JSONObject jsonObject = new JSONObject(br.readLine());
        expNeedScore = objective.getScore(Color.c(expNeed));
        expNeedProgress = objective.getScore(Color.c(progressExp));
        level = objective.getScore(Color.message("scoreboard.playerLevel", new String[]{convert(jsonObject.getInt("playerLevel") + "")}));
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
        jsonObject.put("balance", value);
        FileWriter fw = new FileWriter(path);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(jsonObject.toString());
        bw.close();
        Objects.requireNonNull(Bukkit.getPlayer(jsonObject.getString("playerName"))).setScoreboard(newScoreBoard(path));
    }

    public static void setExpProgress(double currentExp, double nextLevelExp) {
        YamlConfiguration config = (YamlConfiguration) Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("aScoreBoard")).getConfig();
        double next = nextLevelExp / 100;
        double percent = currentExp / next;
        String strPercent = percent + "";
        strPercent = strPercent.substring(0, strPercent.indexOf(".") + 2);
        int greenCubesCount = Integer.parseInt(strPercent.substring(0, strPercent.indexOf("."))) / 5;
        String cubes = "▰".repeat(20);
        StringBuilder sb = new StringBuilder(cubes);
        sb.insert(0, config.getString("scoreboard.cubes1color"));
        sb.insert(greenCubesCount + 2, config.getString("scoreboard.cubes2color"));
        progressExp = Color.c(config.getString("scoreboard.progressBarExp")).replace("{cubes}", sb.toString()) .replace("{percent}", strPercent);;
    }

    public static Map<Integer, Character> symbols = Map.of(3, 'K',6, 'M',9, 'B');

    public static String convert(String num) {
        if (num.contains("E")) {
            num = num.replace(".", "");
            int iterations = Integer.parseInt(num.substring(num.indexOf("E") + 1));
            num = num.substring(0, num.indexOf("E"));
            if (num.length() - 1 != iterations){
                num += "0".repeat(iterations - num.length() + 1);
            }
        }
        else if (num.contains("."))
            num = num.substring(0, num.indexOf("."));
        int numLength = num.length();
        for (int i = 1; i < 4; i++) {
            Object[] ints = symbols.keySet().toArray();
            Arrays.sort(ints);
            int index = (Integer) ints[ints.length - i];
            if (numLength > index) {
                num = num.substring(0, num.length() - index) + "." + num.charAt(num.length() - index) + symbols.get(index);
                if (num.charAt(num.indexOf('.') + 1) == '0')
                    num = num.replace(".0", "");
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

    public static class Color {
        public static YamlConfiguration config = (YamlConfiguration) Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("aScoreBoard")).getConfig();

        public static String c(String text) {
            return ChatColor.translateAlternateColorCodes('&', text);
        }

        public static String message(String a, String[] args) {
            return MessageFormat.format(c(config.getString(a)), args);
        }
        public static String jsonMessage(String a, String[] args) {
            return String.format(c(config.getString(a)), args);
        }
    }

    public static class levelsInfo {
        public static Map<String, Integer> levels = new HashMap<>() {{
            put("level1", 100);
            put("level2", 120);
            put("level3", 144);
            put("level4", 173);
            put("level5", 208);
            put("level6", 250);
            put("level7", 300);
            put("level8", 360);
            put("level9", 432);
            put("level10", 518);
            put("level11", 622);
            put("level12", 746);
            put("level13", 895);
            put("level14", 1074);
            put("level15", 1289);
            put("level16", 1547);
            put("level17", 1856);
            put("level18", 2227);
            put("level19", 2672);
            put("level20", 3206);
            put("level21", 3847);
            put("level22", 4616);
            put("level23", 5539);
            put("level24", 6647);
            put("level25", 7976);
            put("level26", 9571);
            put("level27", 11485);
            put("level28", 13782);
            put("level29", 16538);
            put("level30", 19846);
            put("level31", 23815);
            put("level32", 28578);
            put("level33", 34294);
            put("level34", 41153);
            put("level35", 49384);
            put("level36", 59261);
            put("level37", 71113);
            put("level38", 85336);
            put("level39", 102403);
            put("level40", 122884);
            put("level41", 147461);
            put("level42", 176953);
            put("level43", 212344);
            put("level44", 254813);
            put("level45", 305776);
            put("level46", 366931);
            put("level47", 440317);
            put("level48", 528380);
            put("level49", 634056);
            put("level50", 760867);
            put("level51", 836954);
            put("level52", 920649);
            put("level53", 1012714);
            put("level54", 1113985);
            put("level55", 1225384);
            put("level56", 1347922);
            put("level57", 1482714);
            put("level58", 1630985);
            put("level59", 1794084);
            put("level60", 1973492);
            put("level61", 2170841);
            put("level62", 2387925);
            put("level63", 2626718);
            put("level64", 2889390);
            put("level65", 3178329);
            put("level66", 3496162);
            put("level67", 3845778);
            put("level68", 4230356);
            put("level69", 4653392);
            put("level70", 5118731);
            put("level71", 5630604);
            put("level72", 6193665);
            put("level73", 6813032);
            put("level74", 7494335);
            put("level75", 8243769);
            put("level76", 9068146);
            put("level77", 9974961);
            put("level78", 10972457);
            put("level79", 12069703);
            put("level80", 13276674);
            put("level81", 14604342);
            put("level82", 16064777);
            put("level83", 17671255);
            put("level84", 19438381);
            put("level85", 21382220);
            put("level86", 23520443);
            put("level87", 25872488);
            put("level88", 28459737);
            put("level89", 31305711);
            put("level90", 34436283);
            put("level91", 37879912);
            put("level92", 41667904);
            put("level93", 45834695);
            put("level94", 50418166);
            put("level95", 55459984);
            put("level96", 61005984);
            put("level97", 67106584);
            put("level98", 73817244);
            put("level99", 81198970);
            put("level100", 89318869);
        }};

        public static Map<EntityType, Integer> exp = new HashMap<>(){{
            put(EntityType.ZOMBIE, 1);
        }};
    }
}
