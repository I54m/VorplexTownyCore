package net.vorplex.core.towny.leaderboards.objects;

import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import lombok.Getter;

public abstract class LeaderboardInfo {

    @Getter
    private final String nation;
    @Getter
    private final String town;
    @Getter
    private final String score;
    @Getter
    private final String leader;

    public LeaderboardInfo(Nation nation, String score, String leader) {
        this.town = "";
        this.nation = nation.getName();
        this.score = score;
        this.leader = leader;
    }

    public LeaderboardInfo(Town town, String score, String leader) {
        this.nation = "";
        this.town = town.getName();
        this.score = score;
        this.leader = leader;
    }
}
