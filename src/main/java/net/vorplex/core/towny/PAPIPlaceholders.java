package net.vorplex.core.towny;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.vorplex.core.towny.leaderboards.nations.NationBank;
import net.vorplex.core.towny.leaderboards.nations.NationResidents;
import net.vorplex.core.towny.leaderboards.towns.TownBank;
import net.vorplex.core.towny.leaderboards.towns.TownLandClaimed;
import net.vorplex.core.towny.leaderboards.towns.TownResidents;
import net.vorplex.core.towny.leaderboards.util.LeaderboardPlaceholders;
import net.vorplex.core.towny.records.LeaderboardInfo;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

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

        return null;
    }
}
