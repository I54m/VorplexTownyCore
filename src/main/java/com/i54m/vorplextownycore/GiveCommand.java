package com.i54m.vorplextownycore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class GiveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (s.equals("giveplotvouchers")) {
            if (!commandSender.hasPermission("vorplexcore.townyplotvouchers.give")) {
                commandSender.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
                return false;
            }
            if (strings.length >= 1) {
                Player player = Bukkit.getPlayerExact(strings[0]);
                if (player == null) {
                    commandSender.sendMessage("That is not an online player's name!");
                    return false;
                }
                ItemStack item = new ItemStack(Material.PAPER);
                ItemMeta im = item.getItemMeta();
                im.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Bonus Town Plot Voucher" + ChatColor.GRAY + " (Right Click)");
                ArrayList<String> lore = new ArrayList<>();
                lore.add(ChatColor.WHITE + "Redeem this to get extra town");
                lore.add(ChatColor.WHITE + "plots that your mayor can claim!");
                im.setLore(lore);
                item.setItemMeta(im);
                if (strings.length >= 2) {
                    try {
                        item.setAmount(Integer.parseInt(strings[1]));
                    } catch (NumberFormatException nfe) {
                        commandSender.sendMessage(ChatColor.DARK_RED + "Error: " + ChatColor.RED + strings[1] + " is not an integer!");
                        return false;
                    }
                } else item.setAmount(1);
                player.getInventory().addItem(item);
                return true;
            } else {
                commandSender.sendMessage("/giveplotvouchers <player> [amount]");
                return false;
            }
        }
        return false;
    }
}
