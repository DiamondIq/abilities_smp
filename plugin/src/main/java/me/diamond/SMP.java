package me.diamond;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
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

    public static int MAX_ABILITIES;
    @Getter
    private static JavaPlugin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        //Config
        saveResource("config.yml", false);
        saveDefaultConfig();
        MAX_ABILITIES = getConfig().getInt("max-abilities");

        //Register recipes
        registerRecipes();

        //Register Listeners
        registerListeners();

        //Register Commands
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> commands.registrar().register(AbilityCommand.createCommand()));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new ItemDropEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerConsumeEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PotionThrowEvent(), this);
        PacketEvents.getAPI().getEventManager().registerListener(new PlayerMoveEvent(), PacketListenerPriority.NORMAL);
    }

    private void registerRecipes() {
        //Inferno
        ShapedRecipe inferno = new ShapedRecipe(new NamespacedKey(this, "inferno"), Ability.getEquipItem(AbilityType.INFERNO));
        inferno.shape("BMB", "PWP", "BMB");
        inferno.setIngredient('P', Material.PLAYER_HEAD);
        inferno.setIngredient('B', Material.BLAZE_POWDER);
        inferno.setIngredient('M', Material.MAGMA_CREAM);
        inferno.setIngredient('W', Material.WITHER_SKELETON_SKULL);

        //Hacker
        ShapedRecipe hacker = new ShapedRecipe(new NamespacedKey(this, "hacker"), Ability.getEquipItem(AbilityType.HACKER));
        hacker.shape("PNP", "NSN", "PNP");
        hacker.setIngredient('P', Material.PLAYER_HEAD);
        hacker.setIngredient('N', Material.NETHERITE_INGOT);
        hacker.setIngredient('S', Material.NETHER_STAR);

        //Aqua Man
        ShapedRecipe aquaman = new ShapedRecipe(new NamespacedKey(this, "aquaman"), Ability.getEquipItem(AbilityType.AQUAMAN));
        aquaman.shape(" P ", "NHN", " P ");
        aquaman.setIngredient('P', Material.PLAYER_HEAD);
        aquaman.setIngredient('N', Material.NAUTILUS_SHELL);
        aquaman.setIngredient('H', Material.HEART_OF_THE_SEA);

        //Sorcerer
        ShapedRecipe sorcerer = new ShapedRecipe(new NamespacedKey(this, "sorcerer"), Ability.getEquipItem(AbilityType.SORCERER));
        sorcerer.shape("ERE", "DHD", "ERE");
        sorcerer.setIngredient('H', Material.PLAYER_HEAD);
        sorcerer.setIngredient('R', Material.REDSTONE);
        sorcerer.setIngredient('D', Material.DIAMOND);
        sorcerer.setIngredient('E', Material.FERMENTED_SPIDER_EYE);

        //Add Recipes
        getServer().addRecipe(inferno);
        getServer().addRecipe(sorcerer);
        getServer().addRecipe(hacker);
        getServer().addRecipe(aquaman);
    }
}
