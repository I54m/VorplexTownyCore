package net.vorplex.core.towny;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.vorplex.core.towny.leaderboards.util.LeaderBoardSign;
import net.vorplex.core.towny.leaderboards.util.LeaderboardSignsCommand;
import net.vorplex.core.towny.leaderboards.nations.NationBank;
import net.vorplex.core.towny.leaderboards.nations.NationResidents;
import net.vorplex.core.towny.leaderboards.towns.TownBank;
import net.vorplex.core.towny.leaderboards.towns.TownLandClaimed;
import net.vorplex.core.towny.leaderboards.towns.TownResidents;
import net.vorplex.core.towny.plotvouchers.GiveCommand;
import net.vorplex.core.towny.plotvouchers.onInteract;
import net.vorplex.core.towny.records.LeaderboardInfo;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Town;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class VorplexTownyCore extends JavaPlugin {


    public static ArrayList<LeaderBoardSign> leaderBoardSigns = new ArrayList<>();
    private final File leaderboardSignsFile = new File(getDataFolder(), "leaderboard_signs.json");

    @Override
    public void onEnable() {
        long startTime = System.nanoTime();
        getComponentLogger().info("");
        getComponentLogger().info(Component.text("██╗   ██╗ ██████╗ ██████╗ ██████╗ ██╗     ███████╗██╗  ██╗").color(NamedTextColor.LIGHT_PURPLE));
        getComponentLogger().info(Component.text("██║   ██║██╔═══██╗██╔══██╗██╔══██╗██║     ██╔════╝╚██╗██╔╝").color(NamedTextColor.LIGHT_PURPLE));
        getComponentLogger().info(Component.text("██║   ██║██║   ██║██████╔╝██████╔╝██║     █████╗   ╚███╔╝").color(NamedTextColor.LIGHT_PURPLE));
        getComponentLogger().info(Component.text("╚██╗ ██╔╝██║   ██║██╔══██╗██╔═══╝ ██║     ██╔══╝   ██╔██╗").color(NamedTextColor.LIGHT_PURPLE));
        getComponentLogger().info(Component.text(" ╚████╔╝ ╚██████╔╝██║  ██║██║     ███████╗███████╗██╔╝ ██╗").color(NamedTextColor.LIGHT_PURPLE));
        getComponentLogger().info(Component.text("  ╚═══╝   ╚═════╝ ╚═╝  ╚═╝╚═╝     ╚══════╝╚══════╝╚═╝  ╚═╝").color(NamedTextColor.LIGHT_PURPLE));
        getComponentLogger().info(Component.text("       ___  __                    __   __   __   ___").color(NamedTextColor.DARK_PURPLE));
        getComponentLogger().info(Component.text("        |  /  \\ |  | |\\ | \\ /    /  ` /  \\ |__) |__ ").color(NamedTextColor.DARK_PURPLE));
        getComponentLogger().info(Component.text("        |  \\__/ |/\\| | \\|  |     \\__, \\__/ |  \\ |___").color(NamedTextColor.DARK_PURPLE));
        getComponentLogger().info("───────────────────────────────────────────────────────────");
        getComponentLogger().info(Component.text("Developed by I54m").color(NamedTextColor.RED));
        getComponentLogger().info(Component.text("v" + getPluginMeta().getVersion() + " Running on " + getServer().getVersion()).color(NamedTextColor.RED));
        getComponentLogger().info("───────────────────────────────────────────────────────────");

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getComponentLogger().error("PlaceholderAPI has not been detected!");
            throw new IllegalStateException("PlaceholderAPI is required for this plugin to run!");
        } else getComponentLogger().info(Component.text("PlaceholderAPI Detected!").color(NamedTextColor.GREEN));
        if (Bukkit.getPluginManager().getPlugin("Towny") == null) {
            getComponentLogger().error("Towny has not been detected!");
            throw new IllegalStateException("Towny is required for this plugin to run!");
        } else getComponentLogger().info(Component.text("Towny Detected!").color(NamedTextColor.GREEN));

        LoadLeaderboardSigns();

        getComponentLogger().info(Component.text("Registering Commands...").color(NamedTextColor.GREEN));
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(GiveCommand.COMMAND_NODE);
            commands.registrar().register(LeaderboardSignsCommand.COMMAND_NODE);
        });
        getComponentLogger().info(Component.text("Registered Commands!").color(NamedTextColor.GREEN));

        getComponentLogger().info(Component.text("Registering event listeners...").color(NamedTextColor.GREEN));
        Bukkit.getPluginManager().registerEvents(new onInteract(), this);
        getComponentLogger().info(Component.text("Registered event listeners!").color(NamedTextColor.GREEN));
        getComponentLogger().info(Component.text("Registering leaderboard placeholders for PAPI...").color(NamedTextColor.GREEN));
        new PAPIPlaceholders(this).register();
        getComponentLogger().info(Component.text("Registered leaderboard placeholders for PAPI!").color(NamedTextColor.GREEN));
        this.getServer().getScheduler().runTaskTimer(this, () -> this.getServer().getScheduler().runTaskAsynchronously(this, () -> {
            this.updateScores();
            //wait until scores are updated before triggering sign update on main thread - this avoids race conditions
            this.getServer().getScheduler().runTask(this, this::updateSigns);
        }), 20, 200);
        getComponentLogger().info(Component.text("Plugin loaded in: " + (System.nanoTime() - startTime) / 1000000 + "ms!").color(NamedTextColor.GREEN));
        getComponentLogger().info("───────────────────────────────────────────────────────────");
    }

    @Override
    public void onDisable() {
        SaveLeaderboardSigns();
    }

    private void SaveLeaderboardSigns() {
        getComponentLogger().info(Component.text("Saving leaderboard sign data...").color(NamedTextColor.GREEN));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter writer = new FileWriter(leaderboardSignsFile, false)) {
            gson.toJson(leaderBoardSigns, writer);
            // ensure writer is flushed and closed correctly
            writer.flush();
            writer.close();
            getComponentLogger().info(Component.text("Saved leaderboard sign data!").color(NamedTextColor.GREEN));
        } catch (IOException e) {
            getComponentLogger().error("An error occurred while saving leaderboard sign data!");
            e.printStackTrace();
        }
    }

    private void LoadLeaderboardSigns() {
        if (leaderboardSignsFile.exists()) {
            getComponentLogger().info(Component.text("Leaderboard sign data found! Attempting to load...").color(NamedTextColor.GREEN));
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            try (FileReader reader = new FileReader(leaderboardSignsFile)) {
                Type listType = new TypeToken<ArrayList<LeaderBoardSign>>(){}.getType();
                ArrayList<LeaderBoardSign> json = gson.fromJson(reader, listType);
                if (json != null && !json.isEmpty()) {
                    leaderBoardSigns = json;
                    getComponentLogger().info(Component.text("Loaded leaderboard sign data!").color(NamedTextColor.GREEN));
                } else getComponentLogger().info(Component.text("File was empty - No data to load!").color(NamedTextColor.GREEN));
            } catch (Exception e) {
                getComponentLogger().error("An error occurred while loading leaderboard sign data!");
                e.printStackTrace();
            }
        } else{
            getComponentLogger().info(Component.text("No Leaderboard sign data was found, Creating files...").color(NamedTextColor.YELLOW));
            try {
                if (!getDataFolder().exists()) getDataFolder().mkdir();
                if (!leaderboardSignsFile.exists()) leaderboardSignsFile.createNewFile();
                getComponentLogger().info(Component.text("Created leaderboard sign data files!").color(NamedTextColor.GREEN));
            } catch (IOException e) {
                getComponentLogger().error("An error occurred while creating leaderboard sign data files!");
                e.printStackTrace();
            }
        }
    }

    public static @NotNull ItemStack getVoucherItem(int amount) {
        final Style TITLE_STYLE = Style.style(
                NamedTextColor.LIGHT_PURPLE,
                TextDecoration.BOLD
        ).decoration(TextDecoration.ITALIC, false);
        final Style TITLE_SECONDARY_STYLE = Style.style(
                        NamedTextColor.GRAY
                ).decoration(TextDecoration.ITALIC, false)
                .decoration(TextDecoration.BOLD, false);
        final Style LORE_STYLE = Style.style(
                NamedTextColor.WHITE
        ).decoration(TextDecoration.ITALIC, false);

        ItemStack voucher = new ItemStack(Material.PAPER);
        ItemMeta vm = voucher.getItemMeta();
        vm.displayName(Component.text("Bonus Town Plot Voucher", TITLE_STYLE)
                .append(Component.text(" (Right Click)", TITLE_SECONDARY_STYLE)));
        ArrayList<Component> lore = new ArrayList<>();
        lore.add(Component.text("Redeem this to get extra town", LORE_STYLE));
        lore.add(Component.text("plots that your mayor can claim!", LORE_STYLE));
        vm.lore(lore);
        voucher.setItemMeta(vm);
        voucher.setAmount(amount);
        return voucher;
    }

    private void updateSigns(){
        for (int i = 0; i< leaderBoardSigns.size(); i++){
            LeaderBoardSign leaderBoardSign = leaderBoardSigns.get(i);
            try {
                leaderBoardSign.updateSign();
            } catch (Exception e) {
                leaderBoardSigns.remove(leaderBoardSign);
                getComponentLogger().error("Error occurred while updating sign at: {}! Sign will no longer be updated!", leaderBoardSign.getLocation());
                e.printStackTrace();
            }
        }
    }

    private void clearScores(){
        TownBank.scores.clear();
        NationBank.scores.clear();
        NationResidents.scores.clear();
        TownResidents.scores.clear();
        TownLandClaimed.scores.clear();
    }

    private void updateScores() {
        clearScores();
        // richest towns
        try {
            List<Town> towns = TownyAPI.getInstance().getTowns().stream()
                    .sorted(Comparator.comparingDouble((Town t) -> t.getAccount().getHoldingBalance()).reversed())
                    .limit(10)
                    .toList();

            for (int i = 0; i < towns.size(); i++) {
                Town t = towns.get(i);
                TownBank.scores.put(i + 1, new LeaderboardInfo(
                        t.getName(),
                        t.getAccount().getHoldingFormattedBalance(),
                        t.getMayor().getName()
                ));
            }

        } catch (Exception e) {
            getComponentLogger().error("Error unable to calculate scores for richest towns! Error message: {}", e.getMessage());
        }

        //most residents towns
        try {
            List<Town> towns = TownyAPI.getInstance().getTowns().stream()
                    .sorted(Comparator.comparingInt((Town t) -> t.getResidents().size()).reversed())
                    .limit(10)
                    .toList();

            for (int i = 0; i < towns.size(); i++) {
                Town t = towns.get(i);
                TownResidents.scores.put(i + 1, new LeaderboardInfo(t.getName(),
                        String.valueOf(t.getResidents().size()),
                        t.getMayor().getName()
                ));
            }
        } catch (Exception e) {
            getComponentLogger().error("Error unable to calculate scores for town's residents! Error message: {}", e.getMessage());
        }

        //most claims towns
        try {
            List<Town> towns = TownyAPI.getInstance().getTowns().stream()
                    .sorted(Comparator.comparingInt((Town t) -> t.getTownBlocks().size()).reversed())
                    .limit(10)
                    .toList();

            for (int i = 0; i < towns.size(); i++) {
                Town t = towns.get(i);
                TownLandClaimed.scores.put(i + 1, new LeaderboardInfo(t.getName(),
                        String.valueOf(t.getTownBlocks().size()),
                        t.getMayor().getName()
                ));
            }
        } catch (Exception e) {
            getComponentLogger().error("Error unable to calculate scores for town's land claimed! Error message: {}", e.getMessage());
        }

        //richest nations
        try {
            List<Nation> nations = TownyAPI.getInstance().getNations().stream()
                    .sorted(Comparator.comparingDouble((Nation n) -> n.getAccount().getHoldingBalance()).reversed())
                    .limit(10)
                    .toList();

            for (int i = 0; i < nations.size(); i++) {
                Nation n = nations.get(i);
                NationBank.scores.put(i + 1, new LeaderboardInfo(n.getName(),
                        n.getAccount().getHoldingFormattedBalance(),
                        n.getKing().getName()
                ));
            }
        } catch (Exception e) {
            getComponentLogger().error("Error unable to calculate scores for richest nations! Error message: {}", e.getMessage());
        }

        //most residents nations
        try {
            List<Nation> nations = TownyAPI.getInstance().getNations().stream()
                    .sorted(Comparator.comparingInt((Nation n) -> n.getResidents().size()).reversed())
                    .limit(10)
                    .toList();

            for (int i = 0; i < nations.size(); i++) {
                Nation n = nations.get(i);
                NationBank.scores.put(i + 1, new LeaderboardInfo(n.getName(),
                        n.getAccount().getHoldingFormattedBalance(),
                        n.getKing().getName()
                ));
            }
        } catch (Exception e) {
            getComponentLogger().error("Error unable to calculate scores for nation's residents! Error message: {}", e.getMessage());
        }
    }
}
