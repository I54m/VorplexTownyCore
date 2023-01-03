package com.i54m.vorplextownycore;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import com.i54m.vorplextownycore.leaderboards.nation.NationResidents;
import com.i54m.vorplextownycore.leaderboards.nation.RichNation;
import com.i54m.vorplextownycore.leaderboards.nation.king.NationResidentsKing;
import com.i54m.vorplextownycore.leaderboards.nation.king.RichNationKing;
import com.i54m.vorplextownycore.leaderboards.town.RichTown;
import com.i54m.vorplextownycore.leaderboards.town.TownLandClaimed;
import com.i54m.vorplextownycore.leaderboards.town.TownResidents;
import com.i54m.vorplextownycore.leaderboards.town.mayor.RichTownMayor;
import com.i54m.vorplextownycore.leaderboards.town.mayor.TownLandClaimedMayor;
import com.i54m.vorplextownycore.leaderboards.town.mayor.TownResidentsMayor;
import com.i54m.vorplextownycore.objects.ScoreEntry;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import us.myles.ViaVersion.api.Via;

import java.util.TreeMap;
import java.util.UUID;

public class Main extends JavaPlugin {

    public static Main instance;

    public static Main getInstance() {
        return instance;
    }

    public static void setInstance(Main main) {
        instance = main;
    }

    @Override
    public void onEnable() {
        setInstance(this);
        if (Bukkit.getPluginManager().getPlugin("AlonsoLeaderboards") == null) {
            getLogger().severe("AlonsoLeaderboards has not been detected!");
            getLogger().severe("This plugin requires AlonsoLeaderboards to work!");
            getLogger().severe("Plugin Disabled");
            this.setEnabled(false);
            return;
        }
        if (Bukkit.getPluginManager().getPlugin("Towny") == null) {
            getLogger().severe("Towny has not been detected!");
            getLogger().severe("This plugin requires Towny to work!");
            getLogger().severe("Plugin Disabled");
            this.setEnabled(false);
            return;
        }
        this.getCommand("giveplotvouchers").setExecutor(new GiveCommand());
        Bukkit.getPluginManager().registerEvents(new onInteract(), this);
        new RichTown();
        new RichTownMayor();

        new TownLandClaimed();
        new TownLandClaimedMayor();

        new TownResidents();
        new TownResidentsMayor();

        new RichNation();
        new RichNationKing();

        new NationResidents();
        new NationResidentsKing();


        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), this::updateScores), 20, 200);

        if (Bukkit.getPluginManager().getPlugin("MVdWPlaceholderAPI") != null) {
            PlaceholderAPI.registerPlaceholder(this, "player_has_town", (event) -> {
                if (!event.isOnline())
                    return String.valueOf(false);
                if (event.getPlayer() == null)
                    return String.valueOf(false);
                Player player = event.getPlayer();
                Resident resident;
                try {
                    resident = TownyAPI.getInstance().getDataSource().getResident(player.getName());
                } catch (NotRegisteredException nre) {
                    return String.valueOf(false);
                }
                if (resident.hasTown()) return String.valueOf(true);
                else return String.valueOf(false);
            });
            PlaceholderAPI.registerPlaceholder(this, "player_has_nation", (event) -> {
                if (!event.isOnline())
                    return String.valueOf(false);
                if (event.getPlayer() == null)
                    return String.valueOf(false);
                Player player = event.getPlayer();
                Resident resident;
                try {
                    resident = TownyAPI.getInstance().getDataSource().getResident(player.getName());
                } catch (NotRegisteredException nre) {
                    return String.valueOf(false);
                }
                if (resident.hasNation()) return String.valueOf(true);
                else return String.valueOf(false);
            });
            if (Bukkit.getPluginManager().isPluginEnabled("ViaVersion")) {
                PlaceholderAPI.registerPlaceholder(this, "vorplextowny_scoreboardversion", (event) -> {
                    StringBuilder returnString = new StringBuilder();
                    if (event.isOnline() && event.getPlayer() != null) {
                        Player player = event.getPlayer();
                        UUID uuid = player.getUniqueId();
                        if (Via.getAPI().getPlayerVersion(uuid) < 393) returnString.append("legacy_default");
                        else returnString.append("default");
                        Resident resident;
                        try {
                            resident = TownyAPI.getInstance().getDataSource().getResident(player.getName());
                            if (resident.hasTown()) returnString.append("_has_town");
                            else returnString.append("_no_town");
                        } catch (NotRegisteredException nre) {
                            returnString.append("_no_town");
                        }
                    } else returnString.append("legacy_default_no_town");

                    return returnString.toString();
                });
            } else if (!Bukkit.getPluginManager().isPluginEnabled("ViaVersion")) {
                getLogger().severe("ViaVersion not detected, unable to enable viaversion module!");
            }
            PlaceholderAPI.registerPlaceholder(this, "vorplex_town_mayor_title", (event) -> {
                String playerName;
                if (!event.isOnline())
                    playerName = event.getOfflinePlayer().getName();
                else playerName = event.getPlayer().getName();
                try {
                    Resident resident = TownyAPI.getInstance().getDataSource().getResident(playerName);
                    if (!resident.hasTown()) return null;
                    else return resident.getTown().getMayor().getTitle();
                } catch (NotRegisteredException nre) {
                    return null;
                }
            });
        }
    }

    private void updateScores() {
        RichTown.scores.clear();
        RichTownMayor.scores.clear();

        RichNation.scores.clear();
        RichNationKing.scores.clear();

        NationResidents.scores.clear();
        NationResidentsKing.scores.clear();

        TownResidents.scores.clear();
        TownResidentsMayor.scores.clear();

        TownLandClaimed.scores.clear();
        TownLandClaimedMayor.scores.clear();

        try {
            TreeMap<Integer, Town> towns = new TreeMap<>();
            for (Town town : TownyAPI.getInstance().getDataSource().getTowns()) {
                int key = (int) town.getAccount().getHoldingBalance();
                while (towns.containsKey(key)) {
                    key--;
                }
                towns.put(key, town);
            }
            int position = 1;
            for (int i : towns.descendingKeySet()) {
                RichTown.scores.put(position, new ScoreEntry<>(towns.get(i).getName(), towns.get(i).getAccount().getHoldingFormattedBalance()));
                RichTownMayor.scores.put(position, new ScoreEntry<>(towns.get(i).getMayor().getName(), towns.get(i).getAccount().getHoldingFormattedBalance()));
                if (position >= 10) break;
                else position++;
            }
        } catch (Exception e) {
            getLogger().severe("Error unable to calculate scores for richest nations! Error message: " + e.getMessage());
        }

        try {
            TreeMap<Integer, Nation> nations = new TreeMap<>();
            for (Nation nation : TownyAPI.getInstance().getDataSource().getNations()) {
                int key = (int) nation.getAccount().getHoldingBalance();
                while (nations.containsKey(key)) {
                    key--;
                }
                nations.put(key, nation);
            }
            int position = 1;
            for (int i : nations.descendingKeySet()) {
                RichNation.scores.put(position, new ScoreEntry<>(nations.get(i).getName(), nations.get(i).getAccount().getHoldingFormattedBalance()));
                RichNationKing.scores.put(position, new ScoreEntry<>(nations.get(i).getKing().getName(), nations.get(i).getAccount().getHoldingFormattedBalance()));
                if (position >= 10) break;
                else position++;
            }
        } catch (Exception e) {
            getLogger().severe("Error unable to calculate scores for richest nations! Error message: " + e.getMessage());
        }

        try {
            TreeMap<Integer, Nation> nations = new TreeMap<>();
            for (Nation nation : TownyAPI.getInstance().getDataSource().getNations()) {
                int key = nation.getResidents().size();
                while (nations.containsKey(key)) {
                    key--;
                }
                nations.put(key, nation);
            }
            int position = 1;
            for (int i : nations.descendingKeySet()) {
                NationResidents.scores.put(position, new ScoreEntry<>(nations.get(i).getName(), String.valueOf(nations.get(i).getResidents().size())));
                NationResidentsKing.scores.put(position, new ScoreEntry<>(nations.get(i).getKing().getName(), String.valueOf(nations.get(i).getResidents().size())));
                if (position >= 10) break;
                else position++;
            }
        } catch (Exception e) {
            getLogger().severe("Error unable to calculate scores for nation's residents! Error message: " + e.getMessage());
        }

        try {
            TreeMap<Integer, Town> towns = new TreeMap<>();
            for (Town town : TownyAPI.getInstance().getDataSource().getTowns()) {
                int key = town.getResidents().size();
                while (towns.containsKey(key)) {
                    key--;
                }
                towns.put(key, town);
            }
            int position = 1;
            for (int i : towns.descendingKeySet()) {
                TownResidents.scores.put(position, new ScoreEntry<>(towns.get(i).getName(), String.valueOf(towns.get(i).getResidents().size())));
                TownResidentsMayor.scores.put(position, new ScoreEntry<>(towns.get(i).getMayor().getName(), String.valueOf(towns.get(i).getResidents().size())));
                if (position >= 10) break;
                else position++;
            }
        } catch (Exception e) {
            getLogger().severe("Error unable to calculate scores for town's residents! Error message: " + e.getMessage());
        }

        try {
            TreeMap<Integer, Town> towns = new TreeMap<>();
            for (Town town : TownyAPI.getInstance().getDataSource().getTowns()) {
                int key = town.getTownBlocks().size();
                while (towns.containsKey(key)) {
                    key--;
                }
                towns.put(key, town);
            }
            int position = 1;
            for (int i : towns.descendingKeySet()) {
                TownLandClaimed.scores.put(position, new ScoreEntry<>(towns.get(i).getName(), String.valueOf(towns.get(i).getTownBlocks().size())));
                TownLandClaimedMayor.scores.put(position, new ScoreEntry<>(towns.get(i).getMayor().getName(), String.valueOf(towns.get(i).getTownBlocks().size())));
                if (position >= 10) break;
                else position++;
            }
        } catch (Exception e) {
            getLogger().severe("Error unable to calculate scores for town's land claimed! Error message: " + e.getMessage());
        }
    }
}
