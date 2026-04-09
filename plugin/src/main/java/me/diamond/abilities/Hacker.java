package me.diamond.abilities;

import com.github.retrooper.packetevents.wrapper.play.server.*;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import lombok.Getter;
import lombok.Setter;
import me.diamond.SMP;
import me.diamond.utils.Utils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
@Setter
public class Hacker extends Ability {
    private boolean isBlinking;
    private BossBar bar;

    public Hacker(Player player) {
        super(AbilityType.HACKER, player);
    }

    @Override
    public ItemStack[] getSpecialItems() {
        ItemStack item = new ItemStack(Material.STICK);
        item.setData(DataComponentTypes.ITEM_MODEL, Key.key("smp", "hacker_ability"));
        item.setData(DataComponentTypes.CUSTOM_NAME, Utils.gradientText("HACKER", 0xcc00ff, 0x00ff38).decoration(TextDecoration.BOLD, true));
        item.setData(DataComponentTypes.LORE, ItemLore.lore().addLines(List.of(
                Component.text("[")
                        .color(NamedTextColor.GOLD)
                        .append(Component.translatable("Press %s",
                                Component.keybind("key.drop")
                                        .color(NamedTextColor.GRAY)
                                        .append(Component.text("]").color(NamedTextColor.GOLD))
                        ))
                        .append(Component.text(" Blink for 20s. This makes u look frozen in place but in reality you can move wherever u want.").color(NamedTextColor.GRAY)),
                Component.text("While doing this you are unable to interact with the world.").color(NamedTextColor.GRAY),
                Component.text("\uE000").font(Key.key("smp", "custom")).color(NamedTextColor.WHITE)
                        .append(Component.text(" This ability has a 3min cooldown.").font(Key.key("minecraft", "default")).color(NamedTextColor.GRAY))
        )).build());
        return new ItemStack[]{item};
    }

    public void setBlinking(boolean isBlinking) {
        this.isBlinking = isBlinking;

        //Force sync after finished blinking
        if (!isBlinking) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.equals(player)) continue;
                p.hidePlayer(SMP.getPlugin(), player);
                p.showPlayer(SMP.getPlugin(), player);
            }
        }
    }
}
