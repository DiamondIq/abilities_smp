package me.diamond.listener;

import me.diamond.abilities.AbilityManager;
import me.diamond.abilities.AbilityType;
import me.diamond.utils.ActionBarManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class PlayerJoinEvent implements Listener {

    private final Map<AbilityType, String> ICONS = Map.of(
            AbilityType.SORCERER, "\uE002",
            AbilityType.INFERNO, "\uE003"
    );

    @EventHandler
    public void onJoinEvent(org.bukkit.event.player.PlayerJoinEvent event) {
        AbilityManager.loadAbilities(event.getPlayer());

        Component text;
        for (AbilityType ability : AbilityManager.getAbilities(event.getPlayer())) {
            Component.text(ICONS.get(ability)).font(Key.key("smp", "custom")).append(Component.text("      ").font(Key.key("minecraft", "default")));
        }
        ActionBarManager.startActionBar(event.getPlayer(), Component.text("\uE002").font(Key.key("smp", "custom"))
                .append(Component.text("      ").font(Key.key("minecraft", "default")))
                .append(Component.text("\uE003").font(Key.key("smp", "custom")))
                .append(Component.text("      ").font(Key.key("minecraft", "default")))
                .append(Component.text("\uE001").font(Key.key("smp", "custom"))));
    }
}
