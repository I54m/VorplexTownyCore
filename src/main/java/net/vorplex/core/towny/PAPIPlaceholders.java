package net.vorplex.core.towny;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.vorplex.core.towny.leaderboards.util.LeaderboardPlaceholders;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class PAPIPlaceholders extends PlaceholderExpansion {

    private final VorplexTownyCore plugin;

    public PAPIPlaceholders(VorplexTownyCore plugin){
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "vorplextowny";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getPluginMeta().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    //vorplextowny_lb_<leaderboard>_<position>_<town|leader|score>[_skin]
    //vorplextowny_<town|nation>_name_formatted
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        String[] paramsList = params.split("_");

        if (paramsList[0].equalsIgnoreCase("lb"))
            return LeaderboardPlaceholders.Parse(Arrays.copyOfRange(paramsList, 1, paramsList.length));

        switch (paramsList[0].toLowerCase()) {
            case "town": {
                if (paramsList.length != 3
                        || !paramsList[1].equalsIgnoreCase("name")
                        || !paramsList[2].equalsIgnoreCase("formatted")) return null;
                Resident resident;
                Town town;
                try {
                    resident = TownyAPI.getInstance().getResident(player.getUniqueId());
                    if (resident == null) throw new Exception("Resident is null!");
                    town = resident.getTown();
                    return town.getName();
                } catch (Exception ignored) {
                    return "No Town";
                }
            }
            case "nation": {
                if (paramsList.length != 3
                        || !paramsList[1].equalsIgnoreCase("name")
                        || !paramsList[2].equalsIgnoreCase("formatted")) return null;
                Resident resident;
                Nation nation;
                try {
                    resident = TownyAPI.getInstance().getResident(player.getUniqueId());
                    if (resident == null) throw new Exception("Resident is null!");
                    nation = resident.getNation();
                    return nation.getName();
                } catch (Exception ignored) {
                    return "No Nation";
                }
            }
            default:
                return null;
        }
    }
}
