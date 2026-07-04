package me.diamond.abilities;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.PotionContents;
import lombok.Getter;
import me.diamond.SMP;
import me.diamond.utils.Utils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;

@Getter
public class Sorcerer extends Ability {

    public Sorcerer(Player player) {
        super(AbilityType.SORCERER, player);
    }

    @Override
    public ItemStack[] getSpecialItems() {
        ItemStack item = new ItemStack(Material.STICK);
        item.setData(DataComponentTypes.ITEM_MODEL, Key.key("smp", "sorcerer_ability"));
        item.setData(DataComponentTypes.CUSTOM_NAME, Utils.gradientText("SORCERER", 0x2e2e2e, 0xa1a1a1).decoration(TextDecoration.BOLD, true));
        item.setData(DataComponentTypes.LORE, ItemLore.lore().addLines(List.of(
                Component.text("[")
                        .color(NamedTextColor.GOLD)
                        .append(Component.translatable("Press %s",
                                Component.keybind("key.drop")
                                        .color(NamedTextColor.GRAY)
                                        .append(Component.text("]").color(NamedTextColor.GOLD))
                        ))
                        .append(Component.text(" Vanish into shadows for 5s.").color(NamedTextColor.GRAY)),
                Component.text("This grants invisibility, speed 4 and makes other players unable to hit you").color(NamedTextColor.GRAY),
                Component.text("\uE000").font(Key.key("smp", "custom")).color(NamedTextColor.WHITE)
                        .append(Component.text(" This ability has a 2min cooldown.").font(Key.key("minecraft", "default")).color(NamedTextColor.GRAY))
        )).build());

        ItemStack item2 = new ItemStack(Material.SPLASH_POTION);
        item2.setData(DataComponentTypes.ITEM_MODEL, Key.key("smp", "sorcerer_pot_ability"));
        item2.setData(DataComponentTypes.CUSTOM_NAME, Utils.gradientText("POTION OF MYSTERY", 0x52eb34, 0x52eb34).decoration(TextDecoration.BOLD, true));
        item2.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        item2.setData(DataComponentTypes.LORE, ItemLore.lore(List.of(
                Component.text("Mystery")
                        .decoration(TextDecoration.OBFUSCATED, true)
                        .color(NamedTextColor.RED)
                        .append(Component.text(" (00:20)")
                                .decoration(TextDecoration.OBFUSCATED, false)
                                .color(NamedTextColor.RED)),
                Component.text("\uE000")
                        .font(Key.key("smp", "custom"))
                        .color(NamedTextColor.WHITE)
                        .append(Component.text(" This ability has a 1min cooldown.")
                                .font(Key.key("minecraft", "default"))
                                .color(NamedTextColor.GRAY))
        )));

        return new ItemStack[]{item, item2};
    }

    public void setShadowed() {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        // Create a unique team just for this specific player
        String teamName = "shadowed_" + player.getName();
        Team shadowedTeam = scoreboard.getTeam(teamName);

        Bukkit.getOnlinePlayers().forEach(p -> p.hidePlayer(SMP.getPlugin(), player));
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 10, 1);
        player.addPotionEffect(PotionEffectType.SPEED.createEffect(5 * 20, 3));

        if (shadowedTeam == null) {
            shadowedTeam = scoreboard.registerNewTeam(teamName);
        }

        //Make them look semi transparent to themselves
        shadowedTeam.setCanSeeFriendlyInvisibles(true);

        // Add the player to their personal team
        shadowedTeam.addEntry(player.getName());

        player.addPotionEffect(PotionEffectType.INVISIBILITY.createEffect(5 * 20, 0));

        // Spawn particles
        BukkitRunnable particles = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }
                Location location = player.getEyeLocation();
                location.getWorld().spawnParticle(Particle.ASH, location, 100);
            }
        };
        particles.runTaskTimer(SMP.getPlugin(), 0, 1);

        // Disable shadow mode after duration
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(p -> p.showPlayer(SMP.getPlugin(), player));
                particles.cancel(); // stop particle task

                // Delete the team
                Team shadowedTeam = scoreboard.getTeam(teamName);
                shadowedTeam.unregister();
            }
        }.runTaskLater(SMP.getPlugin(), 5 * 20); // 5 seconds
    }
}
