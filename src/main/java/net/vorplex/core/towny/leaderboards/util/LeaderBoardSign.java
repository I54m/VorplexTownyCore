package net.vorplex.core.towny.leaderboards.util;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.vorplex.core.towny.VorplexTownyCore;
import net.vorplex.core.towny.leaderboards.nations.NationBank;
import net.vorplex.core.towny.leaderboards.nations.NationResidents;
import net.vorplex.core.towny.leaderboards.towns.TownBank;
import net.vorplex.core.towny.leaderboards.towns.TownLandClaimed;
import net.vorplex.core.towny.leaderboards.towns.TownResidents;
import net.vorplex.core.towny.records.LeaderboardInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;

import java.util.List;
import java.util.TreeMap;


public class LeaderBoardSign {

    private String world;
    private double x;
    private double y;
    private double z;

    @Setter
    @Getter
    private String leaderboard;
    @Setter
    @Getter
    private int position;

    public LeaderBoardSign(Location loc, String leaderboard, int position) {
        this.world = loc.getWorld().getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.leaderboard = leaderboard;
        this.position = position;
    }

    public Location getLocation() {
        World w = Bukkit.getWorld(world);
        return new Location(w, x, y, z, 0, 0);
    }

    public void updateSign() throws IllegalStateException {
        TreeMap<Integer, LeaderboardInfo> scoresCopy = switch (leaderboard.toLowerCase()) {
            case "townbank" -> TownBank.scores;
            case "townlandclaimed" -> TownLandClaimed.scores;
            case "townresidents" -> TownResidents.scores;
            case "nationresidents" -> NationResidents.scores;
            case "nationbank" -> NationBank.scores;
            default -> throw new IllegalStateException("The " + leaderboard + " Leaderboard could not be found!");
        };
        Block block = Bukkit.getWorld(world).getBlockAt(getLocation());
        if (block.getState() instanceof Sign sign){
            String town = scoresCopy.get(position) == null ? "???" : scoresCopy.get(position).town();
            String score = scoresCopy.get(position) == null ? "???" : scoresCopy.get(position).score();
            SignSide signSide = sign.getSide(Side.FRONT);
            signSide.lines().set(0, Component.text("       ").color(NamedTextColor.GRAY).decorate(TextDecoration.STRIKETHROUGH)
                    .append(Component.text(" #" + position + " ").color(NamedTextColor.BLACK).decoration(TextDecoration.STRIKETHROUGH, false))
                    .append(Component.text("       ").color(NamedTextColor.GRAY).decorate(TextDecoration.STRIKETHROUGH)));
            signSide.lines().set(1, Component.text(town).color(NamedTextColor.GOLD));
            signSide.lines().set(2, Component.text(score).color(NamedTextColor.YELLOW));
            signSide.lines().set(3, Component.text("                   ").color(NamedTextColor.GRAY).decorate(TextDecoration.STRIKETHROUGH));

            sign.update();
        } else throw new IllegalStateException("Leaderboard Sign is no longer a sign!");
    }

    public void clearSign() {
        Block block = Bukkit.getWorld(world).getBlockAt(getLocation());
        if (block.getState() instanceof Sign sign){
            SignSide signSide = sign.getSide(Side.FRONT);
            signSide.lines().set(0, Component.text(position));
            signSide.lines().set(1, Component.text(leaderboard));
            signSide.lines().set(2, Component.text(""));
            signSide.lines().set(3, Component.text(""));
            sign.update();
        }
    }

    public static LeaderBoardSign getByLocation(Location location){
        List<LeaderBoardSign> list = VorplexTownyCore.leaderBoardSigns.stream().filter(leaderBoardSign -> leaderBoardSign.getLocation().equals(location)).toList();
        return list.isEmpty() ? null : list.getFirst();
    }
}
