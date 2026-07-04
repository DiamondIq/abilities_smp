package me.diamond;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import me.diamond.abilities.Ability;
import me.diamond.abilities.AbilityManager;
import me.diamond.abilities.AbilityType;
import me.diamond.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AbilityCommand {
    public static LiteralCommandNode<CommandSourceStack> createCommand() {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("ability")
                .requires(ctx -> ctx.getSender() instanceof Player);

        // Withdraw sub command
        root.then(Commands.literal("withdraw")
                .then(Commands.argument("ability", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            Player player = (Player) ctx.getSource().getSender();
                            AbilityManager.getAbilities(player).forEach(ability -> builder.suggest(ability.name().toLowerCase()));
                            return builder.buildFuture();
                        })
                        .executes(AbilityCommand::withdrawAbility)
                ));

        // Perks sub command
        root.then(Commands.literal("info")
                .then(Commands.argument("ability", StringArgumentType.word())
                        .suggests((_, builder) -> {
                            for (AbilityType ability : AbilityType.values()) {
                                builder.suggest(ability.name().toLowerCase());
                            }
                            return builder.buildFuture();
                        })
                        .executes(AbilityCommand::getAbilityPerks)
                ));

        // Give sub command: /ability give <player> <ability>
        root.then(Commands.literal("give")
                .requires(ctx -> ctx.getSender().hasPermission("abilities.give"))
                .then(Commands.argument("player", ArgumentTypes.player())
                        .then(Commands.argument("ability", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    Set<AbilityType> abilities = new HashSet<>(Arrays.asList(AbilityType.values()));
                                    try {
                                        PlayerSelectorArgumentResolver selector = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                                        Player player = selector.resolve(ctx.getSource()).get(0);
                                        abilities.removeAll(AbilityManager.getAbilities(player));
                                    } catch (Exception _) {
                                    }
                                    abilities.forEach(ability -> builder.suggest(ability.name().toLowerCase()));
                                    return builder.buildFuture();
                                })
                                .executes(AbilityCommand::giveAbility)
                        )
                )
        );

        // Remove sub command: /ability remove <player> <ability>
        root.then(Commands.literal("remove")
                .requires(ctx -> ctx.getSender().hasPermission("abilities.remove"))
                .then(Commands.argument("player", ArgumentTypes.player())
                        .then(Commands.argument("ability", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    Set<AbilityType> abilities = new HashSet<>();
                                    try {
                                        PlayerSelectorArgumentResolver selector = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
                                        Player player = selector.resolve(ctx.getSource()).get(0);
                                        abilities.addAll(AbilityManager.getAbilities(player));
                                    } catch (Exception _) {
                                    }
                                    abilities.forEach(ability -> builder.suggest(ability.name().toLowerCase()));
                                    return builder.buildFuture();
                                })
                                .executes(AbilityCommand::removeAbility)
                        )
                )
        );

        return root.build();
    }

    private static int withdrawAbility(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        String input = StringArgumentType.getString(ctx, "ability").toUpperCase();

        AbilityType type;

        try {
            type = AbilityType.valueOf(input);
        } catch (IllegalArgumentException e) {
            player.sendMessage(Component.text("Invalid ability!", NamedTextColor.RED));
            return 0;
        }

        if (AbilityManager.getAbility(player, type) == null) {
            player.sendMessage(Component.text("You don't have that ability!", NamedTextColor.RED));
            return 0;
        }

        AbilityManager.removeAbility(player, type);
        player.give(Ability.getEquipItem(type));
        player.sendMessage(Component.text("Ability withdrawn!", NamedTextColor.GREEN));

        return Command.SINGLE_SUCCESS;
    }

    private static int giveAbility(CommandContext<CommandSourceStack> ctx) {
        Player player;
        CommandSender sender = ctx.getSource().getSender();

        String input = StringArgumentType.getString(ctx, "ability").toUpperCase();
        AbilityType ability;

        // Parse target
        try {
            PlayerSelectorArgumentResolver selector = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
            player = selector.resolve(ctx.getSource()).get(0);
        } catch (Exception _) {
            sender.sendMessage(Component.text("Invalid Player!", NamedTextColor.RED));
            return 0;
        }

        // Parse ability
        try {
            ability = AbilityType.valueOf(input);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("Invalid ability!", NamedTextColor.RED));
            return 0;
        }

        if (AbilityManager.getAbility(player, ability) != null) {
            sender.sendMessage(Component.text(String.format("%s already has the %s ability", player.getName(), ability.name().toLowerCase(), NamedTextColor.RED)));
            return 0;
        }

        AbilityManager.grantAbility(player, ability);
        sender.sendMessage(Component.text("Successfully gave " + player.getName() + " the " + ability.name().toLowerCase() + " ability!", NamedTextColor.GREEN));

        return Command.SINGLE_SUCCESS;
    }

    private static int removeAbility(CommandContext<CommandSourceStack> ctx) {
        Player player;
        CommandSender sender = ctx.getSource().getSender();

        String input = StringArgumentType.getString(ctx, "ability").toUpperCase();
        AbilityType ability;

        // Parse target
        try {
            PlayerSelectorArgumentResolver selector = ctx.getArgument("player", PlayerSelectorArgumentResolver.class);
            player = selector.resolve(ctx.getSource()).get(0);
        } catch (Exception _) {
            sender.sendMessage(Component.text("Invalid Player!", NamedTextColor.RED));
            return 0;
        }

        // Parse ability
        try {
            ability = AbilityType.valueOf(input);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("Invalid ability!", NamedTextColor.RED));
            return 0;
        }

        if (AbilityManager.getAbility(player, ability) == null) {
            sender.sendMessage(Component.text(String.format("%s doesn't have the %s ability", player.getName(), ability.name().toLowerCase()), NamedTextColor.RED));
            return 0;
        }

        AbilityManager.removeAbility(player, ability);
        sender.sendMessage(Component.text(String.format("Successfully removed the %s ability from %s!", ability.name().toLowerCase(), player.getName()), NamedTextColor.GREEN));

        return Command.SINGLE_SUCCESS;
    }

    private static int getAbilityPerks(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getSender();
        String input = StringArgumentType.getString(ctx, "ability").toUpperCase();

        AbilityType type;

        try {
            type = AbilityType.valueOf(input);
        } catch (IllegalArgumentException e) {
            player.sendMessage(Component.text("Invalid ability!", NamedTextColor.RED));
            return 0;
        }

        player.sendMessage(Component.translatable("%s PERKS", type.name()).color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
        switch (type) {
            case INFERNO -> sendInfernoAbilities(player);
            case HACKER -> sendHackerAbilities(player);
            case AQUAMAN -> sendAquamanAbilities(player);
            case SORCERER -> sendSorcererAbilities(player);
        }
        ClickCallback.Options options = ClickCallback.Options.builder()
                .uses(ClickCallback.UNLIMITED_USES) // Allow clicking more than once!
                .lifetime(Duration.ofMinutes(10))    // Keeps it alive for 10 minutes so it doesn't cause a memory leak
                .build();
        player.sendMessage(Component.text("[Show Crafting Recipe]", NamedTextColor.GREEN)
                .clickEvent(ClickEvent.callback(audience -> showRecipe((Player) audience, type), options)));
        return Command.SINGLE_SUCCESS;
    }

    private static void showRecipe(Player player, AbilityType ability) {
        Component title = Component.text(ability + " ABILITY RECIPE", NamedTextColor.GOLD, TextDecoration.BOLD);
        Inventory menu = Bukkit.createInventory(player, InventoryType.WORKBENCH, title);

        // Set the result
        menu.setItem(0, Ability.getEquipItem(ability));

        Material[] ingredients = new Material[9];

        switch (ability) {
            case INFERNO -> {
                // Shape: "BMB", "PWP", "BMB"
                ingredients[0] = Material.BLAZE_POWDER;
                ingredients[1] = Material.MAGMA_CREAM;
                ingredients[2] = Material.BLAZE_POWDER;
                ingredients[3] = Material.PLAYER_HEAD;
                ingredients[4] = Material.WITHER_SKELETON_SKULL;
                ingredients[5] = Material.PLAYER_HEAD;
                ingredients[6] = Material.BLAZE_POWDER;
                ingredients[7] = Material.MAGMA_CREAM;
                ingredients[8] = Material.BLAZE_POWDER;
            }
            case HACKER -> {
                // Shape: "PNP", "NSN", "PNP"
                ingredients[0] = Material.PLAYER_HEAD;
                ingredients[1] = Material.NETHERITE_INGOT;
                ingredients[2] = Material.PLAYER_HEAD;
                ingredients[3] = Material.NETHERITE_INGOT;
                ingredients[4] = Material.NETHER_STAR;
                ingredients[5] = Material.NETHERITE_INGOT;
                ingredients[6] = Material.PLAYER_HEAD;
                ingredients[7] = Material.NETHERITE_INGOT;
                ingredients[8] = Material.PLAYER_HEAD;
            }
            case AQUAMAN -> {
                // Shape: "NPN", "PHP", "NPN"
                ingredients[0] = Material.NAUTILUS_SHELL;
                ingredients[1] = Material.PLAYER_HEAD;
                ingredients[2] = Material.NAUTILUS_SHELL;
                ingredients[3] = Material.PLAYER_HEAD;
                ingredients[4] = Material.HEART_OF_THE_SEA;
                ingredients[5] = Material.PLAYER_HEAD;
                ingredients[6] = Material.NAUTILUS_SHELL;
                ingredients[7] = Material.PLAYER_HEAD;
                ingredients[8] = Material.NAUTILUS_SHELL;
            }
            case SORCERER -> {
                // Shape: "ERE", "DHD", "ERE"
                ingredients[0] = Material.FERMENTED_SPIDER_EYE;
                ingredients[1] = Material.REDSTONE;
                ingredients[2] = Material.FERMENTED_SPIDER_EYE;
                ingredients[3] = Material.DIAMOND;
                ingredients[4] = Material.PLAYER_HEAD;
                ingredients[5] = Material.DIAMOND;
                ingredients[6] = Material.FERMENTED_SPIDER_EYE;
                ingredients[7] = Material.REDSTONE;
                ingredients[8] = Material.FERMENTED_SPIDER_EYE;
            }
        }

        for (int i = 0; i < 9; i++) {
            Material mat = ingredients[i];
            menu.setItem(i + 1, ItemStack.of(mat)); // i + 1 offsets index 0 to workbench slot 1
        }

        // Open it for the player
        player.openInventory(menu);
    }

    private static void sendHackerAbilities(Player player) {
        player.sendMessage(Component.text(" ● ", NamedTextColor.GRAY)
                .append(Utils.gradientText("Blink", 0x7f03fc, 0x03fc35))
                .append(Component.text(" - ", NamedTextColor.GRAY)
                        .append(Component.text(" Appear frozen to other players but in reality you can move freely. While doing this you are unable to interact with the world in any way. Triple tap the sneak key to start", NamedTextColor.DARK_PURPLE)))
                .append(Utils.gradientText("Anti KB", 0x7f03fc, 0x03fc35))
                .append(Component.text(" - ", NamedTextColor.GRAY)
                        .append(Component.text(" When getting hit, you will have a 15% chance to not take any horizontal knockback", NamedTextColor.DARK_PURPLE))));
    }

    private static void sendAquamanAbilities(Player player) {
        player.sendMessage(Component.text(" ● ", NamedTextColor.GRAY)
                .append(Utils.gradientText("Auto Crit", NamedTextColor.DARK_AQUA.value(), NamedTextColor.AQUA.value()))
                .append(Component.text(" - ", NamedTextColor.GRAY)
                        .append(Component.text(" 50% of your hits will become critical when fully submerged.", NamedTextColor.DARK_AQUA))));
        player.sendMessage(Component.text(" ● ", NamedTextColor.GRAY)
                .append(Utils.gradientText("Fast Mining", NamedTextColor.DARK_AQUA.value(), NamedTextColor.AQUA.value()))
                .append(Component.text(" - ", NamedTextColor.GRAY)
                        .append(Component.text(" Your underwater mining speed will be the same as above water.", NamedTextColor.DARK_AQUA))));
        player.sendMessage(Component.text(" ● ", NamedTextColor.GRAY)
                .append(Utils.gradientText("Swimming Boost", NamedTextColor.DARK_AQUA.value(), NamedTextColor.AQUA.value()))
                .append(Component.text(" - ", NamedTextColor.GRAY)
                        .append(Component.text(" You will be able to gain the dolphin's grace effect for 10s every 2 mins by triple tapping the sneak key.", NamedTextColor.DARK_AQUA))));
        player.sendMessage(Component.text(" ● ", NamedTextColor.GRAY)
                .append(Utils.gradientText("Permanent Effects", NamedTextColor.DARK_AQUA.value(), NamedTextColor.AQUA.value()))
                .append(Component.text(" - ", NamedTextColor.GRAY)
                        .append(Component.text(" You will gain permanent water breathing.", NamedTextColor.DARK_AQUA))));
    }

    private static void sendInfernoAbilities(Player player) {
        player.sendMessage(Component.text(" ● ", NamedTextColor.GRAY)
                .append(Utils.gradientText("Fire Breath", NamedTextColor.RED.value(), NamedTextColor.YELLOW.value()))
                .append(Component.text(" - ", NamedTextColor.GRAY)
                        .append(Component.text(" Create a beam of fire that ignites and damages anything on its way", NamedTextColor.YELLOW))));
        player.sendMessage(Component.text(" ● ", NamedTextColor.GRAY)
                .append(Utils.gradientText("Permanent Effects", NamedTextColor.RED.value(), NamedTextColor.YELLOW.value()))
                .append(Component.text(" - ", NamedTextColor.GRAY)
                        .append(Component.text(" You will gain permanent fire resistance", NamedTextColor.YELLOW))));
    }


    private static void sendSorcererAbilities(Player player) {
    }

}
