package net.vorplex.core.towny.chat;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ChatUtils {

    private static final String TOWN_CHAT_FORMAT = "<yellow>[<town>] <title><rank><sender>: <message>";
    private static final String NATION_CHAT_FORMAT = "<aqua>[<nation>-<town>] <title><rank><sender>: <message>";

    public static List<Player> townChat = new ArrayList<>();
    public static List<Player> nationChat = new ArrayList<>();

    public static boolean isTownChatOn(Player player) {
        return townChat.contains(player);
    }
    public static boolean isNationChatOn(Player player) {
        return nationChat.contains(player);
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
        Town town = TownyAPI.getInstance().getTown(player);
        List<Resident> onlineResidents = town.getResidents().stream().filter((Resident resident) -> resident.getPlayer() != null && resident.getPlayer().isOnline()).toList();
        onlineResidents.forEach(resident ->
                resident.getPlayer().sendRichMessage(TOWN_CHAT_FORMAT,
                        Placeholder.component("town", Component.text(town.getName())),
                        Placeholder.component("title", Component.text(!resident.getTitle().trim().isEmpty() ? resident.getTitle().trim() + " " : "")),
                        Placeholder.component("rank", Component.text(resident.getHighestPriorityTownRank() != null ? resident.getHighestPriorityTownRank() + " " : "")),
                        Placeholder.component("sender", Component.text(player.getName())),
                        Placeholder.component("message", Component.text(message))
                )
        );
    }


    public static void sendNationChat(Player player, String message) {
        if (notInNation(player)) {
            player.sendMessage(Component.text("Sorry you are not in a nation so you can't use nation chat!").color(NamedTextColor.RED));
            return;
        }
        Nation nation = TownyAPI.getInstance().getNation(player);
        List<Resident> onlineResidents = nation.getResidents().stream().filter((Resident resident) -> resident.getPlayer() != null && resident.getPlayer().isOnline()).toList();
        onlineResidents.forEach(resident ->
                resident.getPlayer().sendRichMessage(NATION_CHAT_FORMAT,
                        Placeholder.component("nation", Component.text(nation.getName())),
                        Placeholder.component("town", Component.text(TownyAPI.getInstance().getTownName(player))),
                        Placeholder.component("title", Component.text(!resident.getTitle().trim().isEmpty() ? resident.getTitle().trim() + " " : "")),
                        Placeholder.component("rank", Component.text(resident.getHighestPriorityNationRank() != null ? resident.getHighestPriorityNationRank() + " " : "")),
                        Placeholder.component("sender", Component.text(player.getName())),
                        Placeholder.component("message", Component.text(message))
                )
        );
    }
}
