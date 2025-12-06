package net.vorplex.core.towny.plotvouchers;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.vorplex.core.towny.VorplexTownyCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class onInteract implements Listener {

    private final HashMap<Player, Long> confirm = new HashMap<>();

    @EventHandler
    public void interactEvent(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Player player = event.getPlayer();
            if (player.getInventory().getItemInMainHand().getType().equals(Material.PAPER)) {
                ItemStack item = player.getInventory().getItemInMainHand();
                ItemStack voucher = VorplexTownyCore.getVoucherItem(item.getAmount());
                if (item.equals(voucher)) {
                    event.setCancelled(true);
                    Resident resident;
                    Town town;
                    try {
                        resident = TownyAPI.getInstance().getResident(player.getName());
                        town = resident.getTown();
                    } catch (NotRegisteredException e) {
                        player.sendMessage(Component.text("Sorry you are not in a town so you can't redeem plot vouchers, do /t new to create one").color(NamedTextColor.RED));
                        return;
                    }
                    if (confirm.containsKey(player) && System.currentTimeMillis() <= (confirm.get(player) - 29800)) {
                        //prevent double triggering within 200ms
                        return;
                    } else if (confirm.containsKey(player) && System.currentTimeMillis() <= confirm.get(player)) {
                        confirm.remove(player);
                        int amount = item.getAmount();
                        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                        player.updateInventory();
                        town.addBonusBlocks(amount);
                        TownyAPI.getInstance().getDataSource().saveTown(town);
                        player.sendMessage(Component.text("You redeemed " + amount + " bonus town blocks for your town: " + town.getName()).color(NamedTextColor.GREEN));
                        player.playSound(Sound.sound(Key.key("minecraft", "entity.player.levelup"), Sound.Source.MASTER, 0.5f, 1.0f));
                        Resident mayor = town.getMayor();
                        Player mayorPlayer = Bukkit.getPlayer(mayor.getUUID());
                        if (mayorPlayer != null && !mayorPlayer.equals(player))
                            mayorPlayer.sendMessage(Component.text(player.getName() + " just redeemed " + item.getAmount() + " plot vouchers for your town!").color(NamedTextColor.GREEN));
                    } else if (!confirm.containsKey(player)) {
                        player.sendMessage(Component.text("Please confirm you would like to redeem " + item.getAmount() + " plot vouchers for the town " + town.getName() + " this cannot be undone!").color(NamedTextColor.RED));
                        confirm.put(player, (System.currentTimeMillis() + 30000));
                    } else {
                        confirm.remove(player);
                        player.sendMessage(Component.text("Your 30s to confirm expired!").color(NamedTextColor.RED));
                        player.sendMessage(Component.text("Please confirm you would like to redeem " + item.getAmount() + " plot vouchers for the town " + town.getName() + " this cannot be undone!").color(NamedTextColor.RED));
                        confirm.put(player, (System.currentTimeMillis() + 30000));
                    }
                }
            }
        }
    }
}
