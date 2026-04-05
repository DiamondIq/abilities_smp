package me.diamond;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import lombok.Getter;
import me.diamond.abilities.Ability;
import me.diamond.abilities.AbilityType;
import me.diamond.listener.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public final class SMP extends JavaPlugin {

    @Getter
    private static JavaPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        registerRecipes();

        //Register Listeners
        Bukkit.getPluginManager().registerEvents(new ItemDropEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerConsumeEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerHitEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PotionThrowEvent(), this);
        PacketEvents.getAPI().getEventManager().registerListener(new PlayerMoveEvent(), PacketListenerPriority.NORMAL);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerRecipes() {
        //Inferno
        ShapedRecipe inferno = new ShapedRecipe(new NamespacedKey(this, "inferno"), Ability.getEquipItem(AbilityType.INFERNO, "You will have permanent fire resistance and you have a 1/3 chance of setting someone on fire. You will have a special ability that lets you create an inferno around the player."));
        inferno.shape("FBM", "BWB", "MBF");
        inferno.setIngredient('F', Material.FLINT_AND_STEEL);
        inferno.setIngredient('B', Material.BLAZE_POWDER);
        inferno.setIngredient('M', Material.MAGMA_CREAM);
        inferno.setIngredient('W', Material.WITHER_SKELETON_SKULL);
    }
}
