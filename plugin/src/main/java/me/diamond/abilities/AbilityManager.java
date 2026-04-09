package me.diamond.abilities;

import me.diamond.SMP;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class AbilityManager {
    private static final Map<UUID, Map<AbilityType, Ability>> abilities = new HashMap<>();
    private static final NamespacedKey KEY = new NamespacedKey(SMP.getPlugin(), "abilities");

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
        PersistentDataContainer persistentDataContainer = player.getPersistentDataContainer();
        List<String> abilities = persistentDataContainer.get(KEY, PersistentDataType.LIST.strings());
        if (abilities != null) {
            abilities.add(type.name());
        } else {
            persistentDataContainer.set(KEY, PersistentDataType.LIST.strings(), List.of(type.name()));
        }

        Ability ability = loadAbility(player, type);
        player.give(ability.getSpecialItems());
    }

    public static void removeAbility(Player player, AbilityType type) {
        Ability ability = abilities.get(player.getUniqueId()).remove(type);
        ability.clearUp();
    }

    public static void loadAbilities(Player player) {
        PersistentDataContainer persistentDataContainer = player.getPersistentDataContainer();
        List<String> abilities = persistentDataContainer.get(KEY, PersistentDataType.LIST.strings());
        if (abilities != null) {
            for (String ability : abilities) {
                AbilityType type = AbilityType.valueOf(ability);
                loadAbility(player, type);
            }
        }
    }

    public static Set<AbilityType> getAbilities(Player player) {
        return abilities.get(player.getUniqueId()).keySet();
    }
}
