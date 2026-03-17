package me.example.customshop.privateores;

import me.example.customshop.items.ItemFactory;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class PrivateOreListener implements Listener {
    private final Plugin plugin;
    private final File storageFile;
    private final YamlConfiguration storage;
    private final Map<String, Claim> claims = new LinkedHashMap<>();

    public PrivateOreListener(Plugin plugin) {
        this.plugin = plugin;
        this.storageFile = new File(plugin.getDataFolder(), "private_ores.yml");
        this.storage = YamlConfiguration.loadConfiguration(storageFile);
        load();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        Claim inside = claimAt(e.getBlockPlaced().getLocation());
        if (inside != null && !canUse(e.getPlayer(), inside)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(color("&cЭто чужой приват."));
            return;
        }

        ItemStack item = e.getItemInHand();
        Integer radius = privateRadius(item);
        if (radius == null) return;

        Claim created = new Claim(
                e.getPlayer().getUniqueId(),
                e.getBlockPlaced().getWorld().getName(),
                e.getBlockPlaced().getX(),
                e.getBlockPlaced().getY(),
                e.getBlockPlaced().getZ(),
                radius,
                itemId(item)
        );

        Claim conflict = firstConflict(created, null);
        if (conflict != null) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(color("&cЭтот приват пересекается с другим."));
            return;
        }

        claims.put(created.key(), created);
        save();
        int size = created.radius * 2 + 1;
        e.getPlayer().sendMessage(color("&aПриват поставлен: &f" + size + "x" + size + "&a."));
        e.getPlayer().sendMessage(color("&7Чтобы снять его, сломай руду в присяде."));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        Claim claim = claimAt(e.getBlock().getLocation());
        if (claim == null) return;

        if (claim.isCore(e.getBlock().getLocation())) {
            if (!canUse(e.getPlayer(), claim)) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(color("&cЭто ядро чужого привата."));
                return;
            }
            if (!e.getPlayer().isSneaking()) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(color("&eЧтобы снять приват, сломай руду в присяде."));
                return;
            }

            claims.remove(claim.key());
            save();
            e.setCancelled(true);
            e.getBlock().setType(org.bukkit.Material.AIR, false);
            ItemStack dropped = ItemFactory.privateCoreById(plugin, claim.itemId);
            if (dropped != null) {
                e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation().add(0.5, 0.5, 0.5), dropped);
            }
            e.getPlayer().sendMessage(color("&cПриват снят."));
            return;
        }

        if (!canUse(e.getPlayer(), claim)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(color("&cНельзя ломать блоки в чужом привате."));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        Block clicked = e.getClickedBlock();
        if (clicked == null) return;
        Claim claim = claimAt(clicked.getLocation());
        if (claim == null || canUse(e.getPlayer(), claim)) return;
        e.setCancelled(true);
        e.getPlayer().sendMessage(color("&cНельзя использовать блоки в чужом привате."));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent e) {
        protect(e.blockList());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockExplode(BlockExplodeEvent e) {
        protect(e.blockList());
    }

    private void protect(Collection<Block> blocks) {
        blocks.removeIf(block -> claimAt(block.getLocation()) != null);
    }

    private boolean canUse(Player player, Claim claim) {
        return player.hasPermission("customshop.admin") || claim.owner.equals(player.getUniqueId());
    }

    private Claim claimAt(Location loc) {
        if (loc.getWorld() == null) return null;
        for (Claim claim : claims.values()) {
            if (claim.contains(loc)) return claim;
        }
        return null;
    }

    private Claim firstConflict(Claim created, Claim ignore) {
        for (Claim claim : claims.values()) {
            if (ignore != null && claim.key().equals(ignore.key())) continue;
            if (created.intersects(claim)) return claim;
        }
        return null;
    }

    private Integer privateRadius(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer().get(ItemFactoryKey.radius(plugin), PersistentDataType.INTEGER);
    }

    private String itemId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer().get(ItemFactoryKey.item(plugin), PersistentDataType.STRING);
    }

    private void load() {
        claims.clear();
        ConfigurationSection root = storage.getConfigurationSection("claims");
        if (root == null) return;
        for (String key : root.getKeys(false)) {
            ConfigurationSection section = root.getConfigurationSection(key);
            if (section == null) continue;
            Claim claim = new Claim(
                    UUID.fromString(section.getString("owner")),
                    section.getString("world"),
                    section.getInt("x"),
                    section.getInt("y"),
                    section.getInt("z"),
                    section.getInt("radius"),
                    section.getString("item", "private_ore_coal")
            );
            claims.put(claim.key(), claim);
        }
    }

    private void save() {
        storage.set("claims", null);
        for (Claim claim : claims.values()) {
            String path = "claims." + claim.key();
            storage.set(path + ".owner", claim.owner.toString());
            storage.set(path + ".world", claim.world);
            storage.set(path + ".x", claim.x);
            storage.set(path + ".y", claim.y);
            storage.set(path + ".z", claim.z);
            storage.set(path + ".radius", claim.radius);
            storage.set(path + ".item", claim.itemId);
        }
        try {
            if (!storageFile.getParentFile().exists()) storageFile.getParentFile().mkdirs();
            storage.save(storageFile);
        } catch (IOException ex) {
            plugin.getLogger().warning("Не удалось сохранить private_ores.yml: " + ex.getMessage());
        }
    }

    private String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private static final class Claim {
        private final UUID owner;
        private final String world;
        private final int x;
        private final int y;
        private final int z;
        private final int radius;
        private final String itemId;

        private Claim(UUID owner, String world, int x, int y, int z, int radius, String itemId) {
            this.owner = owner;
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.radius = radius;
            this.itemId = itemId;
        }

        private String key() {
            return world + "_" + x + "_" + y + "_" + z;
        }

        private boolean contains(Location loc) {
            World current = loc.getWorld();
            if (current == null || !current.getName().equals(world)) return false;
            return Math.abs(loc.getBlockX() - x) <= radius && Math.abs(loc.getBlockZ() - z) <= radius;
        }

        private boolean intersects(Claim other) {
            if (!world.equals(other.world)) return false;
            return Math.abs(x - other.x) <= (radius + other.radius)
                    && Math.abs(z - other.z) <= (radius + other.radius);
        }

        private boolean isCore(Location loc) {
            World current = loc.getWorld();
            return current != null
                    && current.getName().equals(world)
                    && loc.getBlockX() == x
                    && loc.getBlockY() == y
                    && loc.getBlockZ() == z;
        }
    }

    private static final class ItemFactoryKey {
        private static org.bukkit.NamespacedKey radius(Plugin plugin) {
            return new org.bukkit.NamespacedKey(plugin, "cs_private_radius");
        }

        private static org.bukkit.NamespacedKey item(Plugin plugin) {
            return new org.bukkit.NamespacedKey(plugin, "customshop_item");
        }
    }
}
