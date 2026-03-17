package me.example.customshop.raid;

import me.example.customshop.items.ItemFactory;
import me.example.customshop.privateores.PrivateOreListener;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public class RaidExplosiveListener implements Listener {
    public static final String META_BLACK_TNT = "cs_black_tnt";
    private static final float BLACK_TNT_POWER = 7.0F;
    private static final int OBSIDIAN_BREAK_RADIUS = 4;

    private final Plugin plugin;
    private final Set<String> placedBlackTnt = new HashSet<>();

    public RaidExplosiveListener(Plugin plugin, PrivateOreListener privateOres) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        if (!isBlackTnt(e.getItemInHand())) return;
        placedBlackTnt.add(key(e.getBlockPlaced().getLocation()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        if (!placedBlackTnt.remove(key(e.getBlock().getLocation()))) return;
        e.setDropItems(false);
        if (e.getPlayer().getGameMode() != org.bukkit.GameMode.CREATIVE) {
            e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation().add(0.5, 0.2, 0.5), ItemFactory.blackTnt(plugin));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpawn(EntitySpawnEvent e) {
        if (!(e.getEntity() instanceof TNTPrimed tnt)) return;
        if (!placedBlackTnt.remove(key(tnt.getLocation()))) return;
        tnt.setMetadata(META_BLACK_TNT, new FixedMetadataValue(plugin, true));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPrime(ExplosionPrimeEvent e) {
        if (!isBlackTntEntity(e.getEntity())) return;
        e.setRadius(BLACK_TNT_POWER);
        e.setFire(false);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onExplode(EntityExplodeEvent e) {
        if (!isBlackTntEntity(e.getEntity())) return;
        breakObsidian(e.getLocation());
    }

    private void breakObsidian(Location center) {
        if (center.getWorld() == null) return;
        for (int x = -OBSIDIAN_BREAK_RADIUS; x <= OBSIDIAN_BREAK_RADIUS; x++) {
            for (int y = -OBSIDIAN_BREAK_RADIUS; y <= OBSIDIAN_BREAK_RADIUS; y++) {
                for (int z = -OBSIDIAN_BREAK_RADIUS; z <= OBSIDIAN_BREAK_RADIUS; z++) {
                    if ((x * x) + (y * y) + (z * z) > (OBSIDIAN_BREAK_RADIUS * OBSIDIAN_BREAK_RADIUS)) continue;
                    Block block = center.getWorld().getBlockAt(
                            center.getBlockX() + x,
                            center.getBlockY() + y,
                            center.getBlockZ() + z
                    );
                    Material type = block.getType();
                    if (type != Material.OBSIDIAN && type != Material.CRYING_OBSIDIAN) continue;
                    block.setType(Material.AIR, false);
                }
            }
        }
    }

    private boolean isBlackTnt(ItemStack item) {
        return "black_tnt".equals(data(item, "customshop_item"));
    }

    private boolean isBlackTntEntity(Entity entity) {
        return entity != null && entity.hasMetadata(META_BLACK_TNT);
    }

    private String data(ItemStack item, String key) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer().get(new org.bukkit.NamespacedKey(plugin, key), PersistentDataType.STRING);
    }

    private String key(Location location) {
        return location.getWorld().getName() + ':' + location.getBlockX() + ':' + location.getBlockY() + ':' + location.getBlockZ();
    }
}
