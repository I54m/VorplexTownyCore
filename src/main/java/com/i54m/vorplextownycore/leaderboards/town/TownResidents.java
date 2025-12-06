package com.i54m.vorplextownycore.leaderboards.town;

import com.alonsoaliaga.alonsoleaderboards.api.LeaderboardExpansion;
import com.i54m.vorplextownycore.Main;
import org.bukkit.Effect;
import org.bukkit.Location;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class TownResidents extends LeaderboardExpansion {

    public static TreeMap<Integer, Map.Entry<String, String>> scores = new TreeMap<>();

    public TownResidents() {
        super(Main.getInstance(), "town-res",
                scores,
                Arrays.asList("§c--§e #{RANKING} §c--","§f{PLAYER}","§b{SCORE}","§c-------------"),
                Arrays.asList("§c--§e #{RANKING} §c--","§cNo Town","§bUnknown","§c-------------"),
                "§e{RANKING}. &§{PLAYER} §7- §e{SCORE}",
                "§7No data available",
                "§7Unknown",
                "§7Unknown",
                2,
                1);
        register();
    }

    @Override
    public void playEffect(Location location) {
        location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 10);
    }

    @Override
    public void reloadMessages() {}
}
