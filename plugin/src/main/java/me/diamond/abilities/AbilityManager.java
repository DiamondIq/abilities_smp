package me.diamond.abilities;

import org.bukkit.entity.Player;

import java.util.*;

public class AbilityManager {
    private static final Map<UUID, Map<AbilityType, Ability>> abilities = new HashMap<>();

    // Get ability
    public static Ability getAbility(Player player, AbilityType type) {
        Map<AbilityType, Ability> playerAbilities = abilities.get(player.getUniqueId());
        if (playerAbilities == null) return null;
        return playerAbilities.get(type);
    }

    public static Ability loadAbility(Player player, AbilityType type) {
        try {
            Ability ability = type.getClazz().getConstructor(Player.class).newInstance(player);
            abilities.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                    .put(type, ability);
            return ability;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void grantAbility(Player player, AbilityType type) {
        Ability ability = loadAbility(player, type);
        player.give(ability.getSpecialItems());
    }

    public static void removeAbility(Player player, AbilityType type) {
        Ability ability = abilities.get(player.getUniqueId()).remove(type);
        ability.clearUp();
    }

    public static Set<AbilityType> getAbilities(Player player) {
        return abilities.get(player.getUniqueId()).keySet();
    }
}
