package net.vorplex.core.towny.chat;
import net.memoreeze.chatshare.ChatSharePlugin;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import net.kyori.adventure.text.Component;

public class ChatShareHook {

    private static ChatSharePlugin chatSharePlugin;

    public static void setup(Plugin chatShare)
    {
        chatSharePlugin = (ChatSharePlugin) chatShare;
    }

    public static Component applyTags(Player player, Component message)
    {
        if (chatSharePlugin == null) return message;

        return chatSharePlugin.applyTags(player, message);
    }

}
