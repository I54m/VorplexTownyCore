package net.vorplex.core.towny.plotvouchers;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.vorplex.core.towny.VorplexTownyCore;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GiveCommand {

    public static final LiteralCommandNode<CommandSourceStack> COMMAND_NODE = Commands.literal("giveplotvouchers")
                .requires(ctx -> ctx.getSender().hasPermission("vorplexcore.towny.plotvouchers.give") || ctx.getSender().isOp())
                .then(Commands.argument("targets", ArgumentTypes.players())
                    .then(Commands.argument("amount", IntegerArgumentType.integer(1, 99))
                            .suggests(GiveCommand::getAmountSuggestions)
                            .executes(GiveCommand::executeCommand)
                    )
                ).build();

    private static CompletableFuture<Suggestions> getAmountSuggestions(final CommandContext<CommandSourceStack> ctx, final SuggestionsBuilder builder) {
        // Suggest 1, 16, 32, and 64 to the user when they reach the 'amount' argument
        builder.suggest(1);
        builder.suggest(16);
        builder.suggest(32);
        builder.suggest(64);
        return builder.buildFuture();
    }

    private static int executeCommand(final CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        final PlayerSelectorArgumentResolver targetResolver = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class);
        final List<Player> targets = targetResolver.resolve(ctx.getSource());
        final CommandSender sender = ctx.getSource().getSender();
        final int amount = ctx.getArgument("amount", Integer.class);
        final ItemStack voucherItem = VorplexTownyCore.getVoucherItem(amount);
        targets.forEach(target -> {
            target.sendRichMessage("<light_purple>You have been given <amount>x Bonus Town Plot Voucher!", Placeholder.component("amount", Component.text(amount)));
            if (target.getInventory().firstEmpty() == -1) {
                //player's inventory is full so drop the item and alert them
                target.sendRichMessage("<red>Your Inventory was full so the plot vouchers were dropped on the ground near you!!");
                target.getWorld().dropItemNaturally(target.getLocation(), voucherItem);
            } else target.getInventory().addItem(voucherItem);
        });
        sender.sendRichMessage("<white>You have given <targets> players <amount>x plot vouchers each!", Placeholder.component("targets", Component.text(targets.size())), Placeholder.component("amount", Component.text(amount)));
        return Command.SINGLE_SUCCESS;
    }
}
