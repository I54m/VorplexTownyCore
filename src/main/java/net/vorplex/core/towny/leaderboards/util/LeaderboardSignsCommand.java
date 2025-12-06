package net.vorplex.core.towny.leaderboards.util;

import net.vorplex.core.towny.VorplexTownyCore;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class LeaderboardSignsCommand {

    public static final LiteralCommandNode<CommandSourceStack> COMMAND_NODE = Commands.literal("vtl")
            .requires(ctx -> ctx.getSender() instanceof Player)
            .then(Commands.literal("signs")
                .then(Commands.literal("add")
                        .requires(ctx -> ctx.getSender().hasPermission("vorplexcore.towny.leaderboards.signs.add") || ctx.getSender().isOp())
                    .then(Commands.argument("leaderboard", StringArgumentType.word())
                            .suggests(LeaderboardSignsCommand::getLeaderboardSuggestions)
                            .then(Commands.argument("position", IntegerArgumentType.integer(1,10))
                                    .executes(LeaderboardSignsCommand::executeAddCommand))
                    )
                ).then(Commands.literal("remove")
                            .requires(ctx -> ctx.getSender().hasPermission("vorplexcore.towny.leaderboards.signs.remove") || ctx.getSender().isOp())
                            .executes(LeaderboardSignsCommand::executeRemoveCommand)
                )
            ).build();

    private static CompletableFuture<Suggestions> getLeaderboardSuggestions(final CommandContext<CommandSourceStack> ctx, final SuggestionsBuilder builder) {
        builder.suggest("TownBank");
        builder.suggest("TownLandClaimed");
        builder.suggest("TownResidents");
        builder.suggest("NationBank");
        builder.suggest("NationResidents");
        return builder.buildFuture();
    }

    private static int executeAddCommand(final CommandContext<CommandSourceStack> ctx) {
        final String leaderboard = ctx.getArgument("leaderboard", String.class);
        final int position = ctx.getArgument("position", Integer.class);

        Set<Material> ignoredBlocks = new HashSet<>();
        ignoredBlocks.add(Material.AIR);
        ignoredBlocks.add(Material.WATER);
        ignoredBlocks.add(Material.LAVA);

        if (ctx.getSource().getExecutor() instanceof Player player) {
            Block currentBlock  = player.getTargetBlock(ignoredBlocks, 5);
            if (currentBlock.getState() instanceof Sign){
                LeaderBoardSign lbs = new LeaderBoardSign(currentBlock.getLocation(), leaderboard, position);
                try {
                    lbs.updateSign();
                } catch (IllegalStateException e) {
                    player.sendRichMessage("<red>An error occurred while processing the first update of the sign!");
                    player.sendRichMessage("<red>" + e.getMessage());
                    e.printStackTrace();
                    return Command.SINGLE_SUCCESS;
                }
                if (LeaderBoardSign.getByLocation(currentBlock.getLocation()) == null) {
                    VorplexTownyCore.leaderBoardSigns.add(lbs);
                    player.sendRichMessage("<green>Leaderboard sign added!");
                } else player.sendRichMessage("<red>That sign is already a registered leaderboard sign! Do /vtl signs remove - to remove it!");
            } else
                player.sendRichMessage("<red>You must be looking at a sign to do this!");
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int executeRemoveCommand(final CommandContext<CommandSourceStack> ctx) {
        Set<Material> ignoredBlocks = new HashSet<>();
        ignoredBlocks.add(Material.AIR);
        ignoredBlocks.add(Material.WATER);
        ignoredBlocks.add(Material.LAVA);

        if (ctx.getSource().getExecutor() instanceof Player player) {
            Block currentBlock  = player.getTargetBlock(ignoredBlocks, 5);
            if (currentBlock.getState() instanceof Sign){
                LeaderBoardSign leaderBoardSign = LeaderBoardSign.getByLocation(currentBlock.getLocation());
                if (leaderBoardSign != null) {
                    VorplexTownyCore.leaderBoardSigns.remove(leaderBoardSign);
                    leaderBoardSign.clearSign();
                    player.sendRichMessage("<green>Leaderboard sign removed!");
                } else player.sendRichMessage("<red>That sign is not a registered leaderboard sign!");
            } else
                player.sendRichMessage("<red>You must be looking at a sign to do this!");
        }
        return Command.SINGLE_SUCCESS;
    }
}
