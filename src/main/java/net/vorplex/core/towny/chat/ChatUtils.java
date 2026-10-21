package net.vorplex.core.towny.chat;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.vorplex.core.towny.VorplexTownyCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class ChatUtils {

    private static final Plugin mainPlugin = VorplexTownyCore.getInstance();
    private static String TOWN_CHAT_FORMAT = "<yellow>[<b><town></b>] <title><rank><sender>: <message>";
    private static String NATION_CHAT_FORMAT = "<aqua>[<b><nation>-<town></b>] <title><rank><sender>: <message>";
    private static String SPY_PREFIX = "<red>spy <gray>| ";

    public static List<Player> townChat = new ArrayList<>();
    public static List<Player> nationChat = new ArrayList<>();
    // first UUID for both spyOnTown and spyOnNation are player second is Town/Nation
    public static Map<UUID, Town> spyOnTown = new HashMap<UUID, Town>();
    public static Map<UUID, Nation> spyOnNation = new HashMap<UUID, Nation>();

    public static boolean isTownChatOn(Player player) {
        return townChat.contains(player);
    }
    public static boolean isNationChatOn(Player player) {
        return nationChat.contains(player);
    }

    public static void loadFormats()
    {
        TOWN_CHAT_FORMAT = mainPlugin
                .getConfig().getString("chat_formats.town_chat", "<yellow>[<b><town></b>] <title><rank><sender>: <message>");
        NATION_CHAT_FORMAT = mainPlugin
                .getConfig().getString("chat_formats.nation_chat", "<aqua>[<b><nation>-<town></b>] <title><rank><sender>: <message>");
        SPY_PREFIX = mainPlugin
                .getConfig().getString("chat_formats.spy_chat_prefix", "<red>spy <gray>| ");
    }

    public static boolean notInTown(Player player) {
        try {
            final Resident resident = TownyAPI.getInstance().getResident(player.getUniqueId());
            if (resident == null) throw new Exception("Resident is null!");
            Town town = resident.getTown();
            if (town == null) throw new Exception("Town is null!");
            return false;
        } catch (Exception ignored) {
            return true;
        }
    }

    public static boolean notInNation(Player player) {
        try {
            final Resident resident = TownyAPI.getInstance().getResident(player.getUniqueId());
            if (resident == null) throw new Exception("Resident is null!");
            Nation nation = resident.getNation();
            if (nation == null) throw new Exception("Nation is null!");
            return false;
        } catch (Exception ignored) {
            return true;
        }
    }

    public static void sendTownChat(Player player, String message) {
        if (notInTown(player)) {
            player.sendMessage(Component.text("Sorry you are not in a town so you can't use town chat!").color(NamedTextColor.RED));
            return;
        }

        Component finalMessage = ChatShareHook.applyTags(player, Component.text(message));
        final Resident senderResident = TownyAPI.getInstance().getResident(player);
        final String senderNickname = PlaceholderAPI.setPlaceholders(player, "%hexnicks_nick%");
        final Component nicknameComponent = LegacyComponentSerializer.legacySection().deserialize(senderNickname);
        final Town town = TownyAPI.getInstance().getTown(player);

        Component builtMessage = MiniMessage.miniMessage().deserialize(TOWN_CHAT_FORMAT,
                Placeholder.component("town", Component.text(town.getName())),
                Placeholder.component("title", Component.text(senderResident.getTitle() != null && !senderResident.getTitle().trim().isEmpty() ? senderResident.getTitle().trim() + " " : "")),
                Placeholder.component("rank", Component.text(senderResident.getHighestPriorityTownRank() != null ? senderResident.getHighestPriorityTownRank() + " " : "")),
                Placeholder.component("sender", nicknameComponent),
                Placeholder.component("message", finalMessage)
        );

        List<Resident> onlineResidents = town.getResidents().stream().filter((Resident resident) -> resident.getPlayer() != null && resident.isOnline() && resident.getPlayer().isOnline()).toList();
        onlineResidents.forEach(resident ->
                resident.getPlayer().sendMessage(builtMessage)
        );

        Component spyMessage = Component.textOfChildren(MiniMessage.miniMessage().deserialize(SPY_PREFIX),
                builtMessage);

        for (Map.Entry<UUID, Town> entry : spyOnTown.entrySet()) {
            if (entry.getValue() != town) continue;

            Player admin = Bukkit.getPlayer(entry.getKey());

            if (admin == null) continue;

            admin.sendMessage(spyMessage);
        }
        VorplexTownyCore.getInstance().getComponentLogger().info(builtMessage);
    }


    public static void sendNationChat(Player player, String message) {
        if (notInNation(player)) {
            player.sendMessage(Component.text("Sorry you are not in a nation so you can't use nation chat!").color(NamedTextColor.RED));
            return;
        }
        Component finalMessage = ChatShareHook.applyTags(player, Component.text(message));
        final Resident senderResident = TownyAPI.getInstance().getResident(player);
        final String senderNickname = PlaceholderAPI.setPlaceholders(player, "%hexnicks_nick%");
        final Component nicknameComponent = LegacyComponentSerializer.legacySection().deserialize(senderNickname);
        final Nation nation = TownyAPI.getInstance().getNation(player);

        Component builtMessage = MiniMessage.miniMessage().deserialize(NATION_CHAT_FORMAT,
                Placeholder.component("nation", Component.text(nation.getName())),
                Placeholder.component("town", Component.text(TownyAPI.getInstance().getTownName(player))),
                Placeholder.component("title", Component.text(senderResident.getTitle() != null && !senderResident.getTitle().trim().isEmpty() ? senderResident.getTitle().trim() + " " : "")),
                Placeholder.component("rank", Component.text(senderResident.getHighestPriorityNationRank() != null ? senderResident.getHighestPriorityNationRank() + " " : "")),
                Placeholder.component("sender", nicknameComponent),
                Placeholder.component("message", finalMessage)
        );

        List<Resident> onlineResidents = nation.getResidents().stream().filter((Resident resident) -> resident.getPlayer() != null && resident.isOnline() && resident.getPlayer().isOnline()).toList();
        onlineResidents.forEach(resident ->
                resident.getPlayer().sendMessage(builtMessage)
        );

        Component spyMessage = Component.textOfChildren(MiniMessage.miniMessage().deserialize(SPY_PREFIX),
                builtMessage);

        for (Map.Entry<UUID, Nation> entry : spyOnNation.entrySet()) {
            if (entry.getValue() != nation) continue;

            Player admin = Bukkit.getPlayer(entry.getKey());

            if (admin == null) continue;

            admin.sendMessage(spyMessage);
        }

        VorplexTownyCore.getInstance().getComponentLogger().info(builtMessage);
    }
}
