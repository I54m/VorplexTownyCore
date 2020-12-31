package com.i54m.vorplextownycore;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class onInteract implements Listener {

    private final HashMap<Player, Long> confirm = new HashMap<>();

    @EventHandler
    public void interactEvent(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Player player = event.getPlayer();
            if (player.getInventory().getItemInMainHand().getType().equals(Material.PAPER)) {
                ItemStack item = player.getInventory().getItemInMainHand();
                ItemStack voucher = new ItemStack(Material.PAPER);
                ItemMeta vm = voucher.getItemMeta();
                vm.setDisplayName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Bonus Town Plot Voucher" + ChatColor.GRAY + " (Right Click)");
                ArrayList<String> lore = new ArrayList<>();
                lore.add(ChatColor.WHITE + "Redeem this to get extra town");
                lore.add(ChatColor.WHITE + "plots that your mayor can claim!");
                vm.setLore(lore);
                voucher.setItemMeta(vm);
                voucher.setAmount(item.getAmount());
                if (item.equals(voucher)) {
                    Resident resident;
                    Town town;
                    try {
                        resident = TownyAPI.getInstance().getDataSource().getResident(player.getName());
                        town = resident.getTown();
                    } catch (NotRegisteredException nre) {
                        player.sendMessage(ChatColor.RED + "Sorry you are not in a town so you can't redeem plot vouchers, do /t new to create one");
                        return;
                    }
                    if (confirm.containsKey(player) && System.currentTimeMillis() <= confirm.get(player)) {
                        int amount = item.getAmount();
                        item.setType(Material.AIR);
                        item.setAmount(0);
                        town.addBonusBlocks(amount);
                        TownyAPI.getInstance().getDataSource().saveTown(town);
                        player.sendMessage(ChatColor.GREEN + "You redeemed " + item.getAmount() + " bonus town blocks for your town: " + town.getName());
                        Resident mayor = town.getMayor();
                        Player mayorPlayer = Bukkit.getPlayer(mayor.getUUID());
                        if (mayorPlayer != null && !mayorPlayer.equals(player))
                            mayorPlayer.sendMessage(ChatColor.GREEN + player.getName() + " just redeemed " + item.getAmount() + " plot vouchers for your town!");
                        confirm.remove(player);
                    } else if (!confirm.containsKey(player)) {
                        player.sendMessage(ChatColor.RED + "Please confirm you would like to redeem " + item.getAmount() + " plot vouchers for the town " + town.getName() + " this cannot be undone!");
                        confirm.put(player, (System.currentTimeMillis() + 30000));
                    } else {
                        confirm.remove(player);
                        player.sendMessage(ChatColor.RED + "Your 30s to confirm expired!");
                        player.sendMessage(ChatColor.RED + "Please confirm you would like to redeem " + item.getAmount() + " plot vouchers for the town " + town.getName() + " this cannot be undone!");
                        confirm.put(player, (System.currentTimeMillis() + 30000));
                    }
                }
            }
        }
    }
}
