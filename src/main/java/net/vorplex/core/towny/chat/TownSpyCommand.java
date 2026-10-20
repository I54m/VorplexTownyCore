package net.vorplex.core.towny.chat;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TownSpyCommand {

    public static final LiteralCommandNode<CommandSourceStack> COMMAND_NODE = Commands.literal("townspy")
            .requires(ctx -> ctx.getSender() instanceof Player
                    && ctx.getSender().hasPermission("vorplexcore.towny.spy"))
            .executes(TownSpyCommand::stopSpy)
            .then(Commands.argument("town", StringArgumentType.word())
                    .suggests(TownSpyCommand::suggestTowns)
                    .executes(TownSpyCommand::startSpy)
            ).build();

    private static int stopSpy(final CommandContext<CommandSourceStack> ctx) {
        final Player player = (Player) ctx.getSource().getSender();
        final UUID previous = ChatUtils.spyOnTown.remove(player.getUniqueId());

        if (previous == null) {
            player.sendRichMessage("<red>You aren't spying on any town.");
        } else {
            player.sendRichMessage("<yellow>Stopped spying on town chat.");
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int startSpy(final CommandContext<CommandSourceStack> ctx) {
        final Player player = (Player) ctx.getSource().getSender();
        final String name = StringArgumentType.getString(ctx, "town");
        final Town town = TownyAPI.getInstance().getTown(name);

        if (town == null) {
            player.sendRichMessage("<red>No town called <name> exists.",
                    Placeholder.unparsed("name", name));
            return Command.SINGLE_SUCCESS;
        }

        ChatUtils.spyOnTown.put(player.getUniqueId(), town.getUUID());
        player.sendRichMessage("<green>Now spying on <town>'s town chat.",
                Placeholder.unparsed("town", town.getName()));
        return Command.SINGLE_SUCCESS;
    }

    private static CompletableFuture<Suggestions> suggestTowns(final CommandContext<CommandSourceStack> ctx,
                                                               final SuggestionsBuilder builder) {
        final String typed = builder.getRemainingLowerCase();
        for (Town town : TownyAPI.getInstance().getTowns()) {
            if (town.getName().toLowerCase().startsWith(typed)) {
                builder.suggest(town.getName());
            }
        }
        return builder.buildFuture();
    }
}