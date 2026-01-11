package net.vorplex.core.towny.leaderboards.objects;

import com.palmergames.bukkit.towny.object.Nation;

public class NationLeaderboardInfo extends LeaderboardInfo {

    public NationLeaderboardInfo(Nation nation, String score, String leader){
        super(nation, score, leader);
    }

}