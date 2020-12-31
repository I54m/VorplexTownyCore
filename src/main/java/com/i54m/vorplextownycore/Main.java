package com.i54m.vorplextownycore;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("LeaderHeads") == null) {
            getLogger().severe("Leaderheads has not been detected!");
            getLogger().severe("This plugin requires Leaderheads to work!");
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
        new RichTownMayor();
        new RichNationKing();
        new RichNation();
        new RichTown();
        if (Bukkit.getPluginManager().getPlugin("MVdWPlaceholderAPI") != null)
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
    }
}
