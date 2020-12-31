package com.i54m.vorplextownycore;

import com.alonsoaliaga.alonsoleaderboards.api.LeaderboardExpansion;
import com.alonsoaliaga.alonsoleaderboards.enums.OrderType;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.*;

public class RichNationKing extends LeaderboardExpansion {

    RichNationKing() {
        super(Main.getInstance(), "corelb_nation_k", OrderType.DESCENDING, 3, 10);
        register();
    }

    @Override
    public void playEffect(Location location) {
        location.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, location, 25);
        location.getWorld().playSound(location, Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 75, 1);
    }

    @Override
    public void reloadMessages() {}

    @Override
    public TreeMap<Integer, Map.Entry<String, String>> getLeaderboardsStringData() {
        try {
            TreeMap<Integer, Map.Entry<String, String>> nations = new TreeMap<>();
            for (Nation nation : TownyAPI.getInstance().getDataSource().getNations()) {
                nations.put((int) nation.getAccount().getHoldingBalance(), new ScoreEntry<>(nation.getCapital().getMayor().getName(), nation.getAccount().getHoldingFormattedBalance()));
            }
            return nations;
        } catch (Exception e) {
            return null;
        }
    }
}
