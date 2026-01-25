package net.vorplex.core.towny.plotvouchers;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.vorplex.core.towny.VorplexTownyCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class PlayerInteractListener implements Listener {

    private final HashMap<Player, Long> confirm = new HashMap<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void interactEvent(PlayerInteractEvent event) {
        if (event.getHand() != null && event.getHand().equals(EquipmentSlot.HAND)
                && (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();
            if (isVoucherItem(item)) {
                event.setCancelled(true);
                Resident resident;
                Town town;
                try {
                    resident = TownyAPI.getInstance().getResident(player.getUniqueId());
                    if (resident == null) throw new Exception("Resident is null!");
                    town = resident.getTown();
                } catch (Exception ignored) {
                    player.sendMessage(Component.text("Sorry you are not in a town so you can't redeem plot vouchers, do /t new to create one").color(NamedTextColor.RED));
                    return;
                }
                if (confirm.containsKey(player) && System.currentTimeMillis() <= confirm.get(player)) {
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

    private boolean isVoucherItem(@NotNull ItemStack item) {
        if (item.getType() != Material.PAPER) return false;

        NamespacedKey voucherKey = new NamespacedKey(VorplexTownyCore.getInstance(), "plot_voucher");
        if (item.getPersistentDataContainer().has(voucherKey, PersistentDataType.BOOLEAN)) return true;

        return isLegacyVoucherItem(item);
    }

    private boolean isLegacyVoucherItem(@NotNull ItemStack item) {
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

        ItemStack voucher = ItemStack.of(Material.PAPER);
        ItemMeta vm = voucher.getItemMeta();
        vm.displayName(Component.text("Bonus Town Plot Voucher", TITLE_STYLE)
                .append(Component.text(" (Right Click)", TITLE_SECONDARY_STYLE)));
        ArrayList<Component> voucherLore = new ArrayList<>();
        voucherLore.add(Component.text("Redeem this to get extra town", LORE_STYLE));
        voucherLore.add(Component.text("plots that your mayor can claim!", LORE_STYLE));
        vm.lore(voucherLore);
        voucher.setItemMeta(vm);

        String expectedName = PlainTextComponentSerializer.plainText().serialize(voucher.displayName());
        ArrayList<String> expectedLore = new ArrayList<>();
        Objects.requireNonNull(voucher.lore()).forEach(lore -> expectedLore.add(PlainTextComponentSerializer.plainText().serialize(lore)));

        String itemName = PlainTextComponentSerializer.plainText().serialize(item.displayName());
        ArrayList<String> itemLore = new ArrayList<>();
        item.lore().forEach(lore -> itemLore.add(PlainTextComponentSerializer.plainText().serialize(lore)));

        return itemName.equals(expectedName) && itemLore.equals(expectedLore);
    }
}
