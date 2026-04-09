package me.diamond;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.diamond.abilities.AbilityManager;
import me.diamond.abilities.AbilityType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class AbilityCommand {
    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("ability")
                .requires(ctx -> ctx.getSender() instanceof Player)
                .then(Commands.literal("withdraw")
                        .then(Commands.argument("ability", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    Player player = (Player) ctx.getSource().getSender();

                                    for (AbilityType ability : AbilityManager.getAbilities(player)) {
                                        builder.suggest(ability.name().toLowerCase());
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(AbilityCommand::withdrawAbility)
                        )
                )
                .build();
    }

    private static int withdrawAbility(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        String input = StringArgumentType.getString(ctx, "ability").toUpperCase();

        AbilityType type;

        try {
            type = AbilityType.valueOf(input);
        } catch (IllegalArgumentException e) {
            player.sendMessage(Component.text("Invalid ability!").color(NamedTextColor.RED));
            return 0;
        }

        if (AbilityManager.getAbility(player, type) == null) {
            player.sendMessage(Component.text("You don't have that ability!").color(NamedTextColor.RED));
            return 0;
        }

        AbilityManager.removeAbility(player, type);
        //Todo - give item
        player.sendMessage(Component.text("Ability withdrawn!").color(NamedTextColor.GREEN));

        return Command.SINGLE_SUCCESS;
    }
}
