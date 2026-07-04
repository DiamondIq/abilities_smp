package me.diamond.listener;

import me.diamond.abilities.AbilityManager;
import me.diamond.abilities.AbilityType;
import me.diamond.utils.ActionBarManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class PlayerJoinEvent implements Listener {

    private final Map<AbilityType, String> ICONS = Map.of(
            AbilityType.SORCERER, "\uE002",
            AbilityType.INFERNO, "\uE003",
            AbilityType.HACKER, "\uE004",
            AbilityType.AQUAMAN, "\uE005"
    );

    @EventHandler
    public void onJoinEvent(org.bukkit.event.player.PlayerJoinEvent event) {
        Player player = event.getPlayer();
        AbilityManager.loadAbilities(player);

        // 1. Create a root builder for your text
        TextComponent.Builder actionBarBuilder = Component.text();

        // 2. Safely look up their abilities and append them to the builder chain
        for (AbilityType ability : AbilityManager.getAbilities(player)) {
            String icon = ICONS.get(ability);
            if (icon != null) {
                actionBarBuilder.append(Component.text(icon).font(Key.key("smp", "custom")))
                        .append(Component.text("      ").font(Key.key("minecraft", "default")));
            }
        }

        // 3. Build the final component
        Component finalText = actionBarBuilder.build();

        // 4. Send the dynamically constructed text to your manager
        ActionBarManager.startActionBar(player, finalText);
    }
}