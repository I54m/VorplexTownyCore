package net.vorplex.core.towny.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class AsyncChatListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        if (ChatUtils.isTownChatOn(player)){
            event.viewers().clear();
            event.setCancelled(true);
            ChatUtils.sendTownChat(player, PlainTextComponentSerializer.plainText().serialize(event.originalMessage()));
        } else if (ChatUtils.isNationChatOn(player)){
            event.viewers().clear();
            event.setCancelled(true);
            ChatUtils.sendNationChat(player, PlainTextComponentSerializer.plainText().serialize(event.originalMessage()));
        }
    }
}
