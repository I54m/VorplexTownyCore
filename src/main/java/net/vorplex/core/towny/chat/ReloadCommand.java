package net.vorplex.core.towny.chat;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.vorplex.core.towny.VorplexTownyCore;

public class ReloadCommand {

    public static final LiteralCommandNode<CommandSourceStack> COMMAND_NODE = Commands.literal("vorplexcore reload")
            .requires(ctx -> ctx.getSender().hasPermission("vorplexcore.towny.reload"))
            .executes(ReloadCommand::reloadCommand).build();


    private static int reloadCommand(CommandContext<CommandSourceStack> commandSourceStackCommandContex)
    {
        VorplexTownyCore.getInstance().reloadConfig();
        ChatUtils.loadFormats();

        return Command.SINGLE_SUCCESS;
    }
}
