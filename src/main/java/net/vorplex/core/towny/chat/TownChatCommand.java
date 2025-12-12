package net.vorplex.core.towny.chat;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class TownChatCommand {

    public static final LiteralCommandNode<CommandSourceStack> COMMAND_NODE = Commands.literal("townchat")
            .requires(ctx -> ctx.getSender() instanceof Player)
            .executes(TownChatCommand::toggleCommand)
            .then(Commands.argument("message", StringArgumentType.greedyString())
                    .executes((ctx) -> {
                        ChatUtils.sendTownChat((Player) ctx.getSource().getSender(), StringArgumentType.getString(ctx, "message"));
                        return Command.SINGLE_SUCCESS;
                    })
            ).build();

    private static int toggleCommand(final CommandContext<CommandSourceStack> ctx) {
        final CommandSender sender = ctx.getSource().getSender();
        final Player player = (Player) sender;
        if (ChatUtils.isTownChatOn(player)) {
            ChatUtils.townChat.remove(player);
            player.sendRichMessage("<red>Toggled Town Chat off!");
        } else {
            if (ChatUtils.notInTown(player)) {
                player.sendMessage(Component.text("Sorry you are not in a town so you can't use town chat!").color(NamedTextColor.RED));
            } else {
                if (ChatUtils.isNationChatOn(player)) {
                    ChatUtils.nationChat.remove(player);
                    player.sendRichMessage("<red>Toggled Nation Chat off!");
                }
                ChatUtils.townChat.add(player);
                player.sendRichMessage("<green>Toggled Town Chat on!");
            }
        }
        return Command.SINGLE_SUCCESS;
    }

}
