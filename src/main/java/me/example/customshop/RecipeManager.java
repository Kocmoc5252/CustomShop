package me.example.customshop;

import me.example.customshop.items.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public final class RecipeManager {
    private RecipeManager() {}

    public static void register(JavaPlugin plugin) {
        add(plugin, "crafted_emerald_sword", ItemFactory.craftedEmeraldSword(plugin), " E ", " E ", " S ", Material.EMERALD, Material.STICK, null);
        add(plugin, "crafted_emerald_pickaxe", ItemFactory.craftedEmeraldPickaxe(plugin), "EEE", " S ", " S ", Material.EMERALD, Material.STICK, null);
        add(plugin, "crafted_emerald_axe", ItemFactory.craftedEmeraldAxe(plugin), "EE ", "ES ", " S ", Material.EMERALD, Material.STICK, null);
        add(plugin, "crafted_emerald_shovel", ItemFactory.craftedEmeraldShovel(plugin), " E ", " S ", " S ", Material.EMERALD, Material.STICK, null);
        add(plugin, "crafted_emerald_hoe", ItemFactory.craftedEmeraldHoe(plugin), "EE ", " S ", " S ", Material.EMERALD, Material.STICK, null);
        add(plugin, "emerald_helmet", ItemFactory.emeraldHelmet(plugin), "EEE", "E E", "   ", Material.EMERALD, null, null);
        add(plugin, "emerald_chestplate", ItemFactory.emeraldChestplate(plugin), "E E", "EEE", "EEE", Material.EMERALD, null, null);
        add(plugin, "emerald_leggings", ItemFactory.emeraldLeggings(plugin), "EEE", "E E", "E E", Material.EMERALD, null, null);
        add(plugin, "emerald_boots", ItemFactory.emeraldBoots(plugin), "   ", "E E", "E E", Material.EMERALD, null, null);
    }

    private static void add(JavaPlugin plugin, String key, org.bukkit.inventory.ItemStack result, String r1, String r2, String r3, Material primary, Material secondary, Material extra) {
        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(plugin, key), result);
        recipe.shape(r1, r2, r3);
        String shape = r1 + r2 + r3;
        if (shape.indexOf('E') >= 0) recipe.setIngredient('E', primary);
        if (shape.indexOf('B') >= 0) recipe.setIngredient('B', Material.EMERALD_BLOCK);
        if (secondary != null) {
            char secondaryKey = switch (secondary) {
                case STICK -> 'S';
                case NETHERITE_INGOT -> 'N';
                default -> 'S';
            };
            if (shape.indexOf(secondaryKey) >= 0) recipe.setIngredient(secondaryKey, secondary);
        }
        if (extra != null) {
            char extraKey = extra == Material.NETHERITE_INGOT ? 'N' : extra == Material.STICK ? 'S' : 'X';
            if (shape.indexOf(extraKey) >= 0) recipe.setIngredient(extraKey, extra);
        }
        Bukkit.addRecipe(recipe);
    }
}
