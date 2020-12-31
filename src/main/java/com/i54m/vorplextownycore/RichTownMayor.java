package com.i54m.vorplextownycore;

import com.alonsoaliaga.alonsoleaderboards.api.LeaderboardExpansion;
import com.alonsoaliaga.alonsoleaderboards.enums.OrderType;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;

import java.util.*;

public class RichTownMayor extends LeaderboardExpansion {

    RichTownMayor() {
        super(Main.getInstance(), "corelb_town_m", OrderType.DESCENDING, 3, 10);
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
            TreeMap<Integer, Map.Entry<String, String>> towns = new TreeMap<>();
            for (Town town : TownyAPI.getInstance().getDataSource().getTowns()) {
                towns.put((int) town.getAccount().getHoldingBalance(), new ScoreEntry<>(town.getMayor().getName(), town.getAccount().getHoldingFormattedBalance()));
            }
            return towns;
        } catch (Exception e) {
            return null;
        }
    }
}
