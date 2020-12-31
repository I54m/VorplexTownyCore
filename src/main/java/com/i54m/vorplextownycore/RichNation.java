package com.i54m.vorplextownycore;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import me.robin.leaderheads.api.LeaderHeadsAPI;
import me.robin.leaderheads.datacollectors.DataCollector;
import me.robin.leaderheads.objects.BoardType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RichNation extends DataCollector {
    RichNation() {
        super("nation-richest", "Towny", BoardType.MONEY, "&bRichest Nations", "richnations", Arrays.asList(null, null, "&6$ {amount}", null), true, String.class);
    }

    @Override
    public List<Map.Entry<?, Double>> requestAll() {
        try {
            HashMap<String, Double> nations = new HashMap<>();
            for (Nation nation : TownyAPI.getInstance().getDataSource().getNations()) {
                nations.put(nation.getCapital().getName(), nation.getAccount().getHoldingBalance());
            }
            return LeaderHeadsAPI.sortMap(nations);
        } catch (Exception e) {
            return null;
        }
    }
}
