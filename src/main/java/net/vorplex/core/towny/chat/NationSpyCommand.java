package net.vorplex.core.towny.chat;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class NationSpyCommand {

    public static final LiteralCommandNode<CommandSourceStack> COMMAND_NODE = Commands.literal("nationspy")
            .requires(ctx -> ctx.getSender() instanceof Player
                    && ctx.getSender().hasPermission("vorplexcore.towny.spy"))
            .executes(NationSpyCommand::stopSpy)
            .then(Commands.argument("nation", StringArgumentType.word())
                    .suggests(NationSpyCommand::suggestNations)
                    .executes(NationSpyCommand::startSpy)
            ).build();

    private static int stopSpy(final CommandContext<CommandSourceStack> ctx) {
        final Player player = (Player) ctx.getSource().getSender();
        final Nation previous = ChatUtils.spyOnNation.remove(player.getUniqueId());

        if (previous == null) {
            player.sendRichMessage("<red>You aren't spying on any nation.");
        } else {
            player.sendRichMessage("<yellow>Stopped spying on nation chat.");
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int startSpy(final CommandContext<CommandSourceStack> ctx) {
        final Player player = (Player) ctx.getSource().getSender();
        final String name = StringArgumentType.getString(ctx, "nation");
        final Nation nation = TownyAPI.getInstance().getNation(name);

        if (nation == null) {
            player.sendRichMessage("<red>No nation called <name> exists.",
                    Placeholder.unparsed("name", name));
            return Command.SINGLE_SUCCESS;
        }

        ChatUtils.spyOnNation.put(player.getUniqueId(), nation);
        player.sendRichMessage("<green>Now spying on <nation>'s nation chat.",
                Placeholder.unparsed("nation", nation.getName()));
        return Command.SINGLE_SUCCESS;
    }

    private static CompletableFuture<Suggestions> suggestNations(final CommandContext<CommandSourceStack> ctx,
                                                               final SuggestionsBuilder builder) {
        final String typed = builder.getRemainingLowerCase();
        for (Nation nation : TownyAPI.getInstance().getNations()) {
            if (nation.getName().toLowerCase().startsWith(typed)) {
                builder.suggest(nation.getName());
            }
        }
        return builder.buildFuture();
    }
}