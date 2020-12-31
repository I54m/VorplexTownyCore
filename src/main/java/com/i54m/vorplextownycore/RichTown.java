package com.i54m.vorplextownycore;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import me.robin.leaderheads.api.LeaderHeadsAPI;
import me.robin.leaderheads.datacollectors.DataCollector;
import me.robin.leaderheads.objects.BoardType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RichTown extends DataCollector {
    RichTown() {
        super("towny-richest", "Towny", BoardType.MONEY, "&bRichest Towns", "richtowns", Arrays.asList(null, null, "{amount}", null), true, String.class);
    }

    @Override
    public List<Map.Entry<?, Double>> requestAll() {
        try {
            HashMap<String, Double> towns = new HashMap<>();
            for (Town town : TownyAPI.getInstance().getDataSource().getTowns()) {
                towns.put(town.getName(), town.getAccount().getHoldingBalance());
            }
            return LeaderHeadsAPI.sortMap(towns);
        } catch (Exception e) {
            return null;
        }
    }
}
