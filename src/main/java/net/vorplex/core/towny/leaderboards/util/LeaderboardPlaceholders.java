package net.vorplex.core.towny.leaderboards.util;

import net.vorplex.core.towny.VorplexTownyCore;
import net.vorplex.core.towny.leaderboards.nations.NationBank;
import net.vorplex.core.towny.leaderboards.nations.NationResidents;
import net.vorplex.core.towny.leaderboards.towns.TownBank;
import net.vorplex.core.towny.leaderboards.towns.TownLandClaimed;
import net.vorplex.core.towny.leaderboards.towns.TownResidents;
import net.vorplex.core.towny.records.LeaderboardInfo;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.TreeMap;

public class LeaderboardPlaceholders {
    //vorplextowny_lb_townbank_1_town
    //vorplextowny_lb_townbank_1_score
    //vorplextowny_lb_townbank_1_leader
    //vorplextowny_lb_townbank_1_leader_skin
    public static String Parse(String[] paramsList) {
        int position;
        if (paramsList.length < 3) return null;
        if (NumberUtils.isParsable(paramsList[1])) position = Integer.parseInt(paramsList[1]);
        else return null;
        if (position > 10 || position <= 0) return null;

        TreeMap<Integer, LeaderboardInfo> scoresCopy = switch (paramsList[0].toLowerCase()) {
            case "townbank" -> TownBank.scores;
            case "townlandclaimed" -> TownLandClaimed.scores;
            case "townresidents" -> TownResidents.scores;
            case "nationresidents" -> NationResidents.scores;
            case "nationbank" -> NationBank.scores;
            default -> null;
        };
        if (scoresCopy == null) return null;

        if (paramsList.length != 4 && (scoresCopy.isEmpty() || scoresCopy.get(position) == null))
            return "???";
        if (paramsList[2].equalsIgnoreCase("town")) return scoresCopy.get(position).town();
        else if (paramsList[2].equalsIgnoreCase("score")) return scoresCopy.get(position).score();
        else if (paramsList[2].equalsIgnoreCase("leader"))
            if (scoresCopy.get(position) == null) return "Superman00800"; //default question mark skin
            else if (paramsList.length == 4 && paramsList[3] != null && paramsList[3].equalsIgnoreCase("skin"))
                    return scoresCopy.get(position).leader();
            else return scoresCopy.get(position).leader();

        return null;
    }
}
