package me.example.customshop.npc;

import me.example.customshop.shop.EmeraldShopGUI;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class EmeraldShopNpcManager implements Listener {
    private final Plugin plugin;
    private final EmeraldShopGUI shopGUI;
    private Villager villager;
    private UUID villagerId;
    private BukkitTask rotationTask;

    public EmeraldShopNpcManager(Plugin plugin, EmeraldShopGUI shopGUI) {
        this.plugin = plugin;
        this.shopGUI = shopGUI;
    }

    public void setNpcAt(org.bukkit.entity.Player p) {
        Location l = p.getLocation().clone();
        FileConfiguration cfg = plugin.getConfig();
        cfg.set("emerald_npc.world", l.getWorld().getName());
        cfg.set("emerald_npc.x", l.getX());
        cfg.set("emerald_npc.y", l.getY());
        cfg.set("emerald_npc.z", l.getZ());
        cfg.set("emerald_npc.yaw", l.getYaw());
        cfg.set("emerald_npc.pitch", 0f);
        plugin.saveConfig();
        spawnOrRespawn();
        p.sendMessage("§aNPC изумрудного магазина установлен.");
    }

    public void removeNpc(org.bukkit.entity.Player p) {
        despawn();
        FileConfiguration cfg = plugin.getConfig();
        cfg.set("emerald_npc.world", null);
        cfg.set("emerald_npc.x", null);
        cfg.set("emerald_npc.y", null);
        cfg.set("emerald_npc.z", null);
        cfg.set("emerald_npc.yaw", null);
        cfg.set("emerald_npc.pitch", null);
        plugin.saveConfig();
        p.sendMessage("§cNPC изумрудного магазина удалён.");
    }

    public void spawnOrRespawn() {
        despawn();
        FileConfiguration cfg = plugin.getConfig();
        String worldName = cfg.getString("emerald_npc.world");
        if (worldName == null) return;
        World w = Bukkit.getWorld(worldName);
        if (w == null) return;
        Location loc = new Location(w, cfg.getDouble("emerald_npc.x"), cfg.getDouble("emerald_npc.y"), cfg.getDouble("emerald_npc.z"), (float) cfg.getDouble("emerald_npc.yaw", 0), 0f);
        villager = (Villager) w.spawnEntity(loc, EntityType.VILLAGER);
        villagerId = villager.getUniqueId();
        villager.customName(LegacyComponentSerializer.legacyAmpersand().deserialize("§a§lИЗУМРУДЫ §7(ПКМ)"));
        villager.setCustomNameVisible(true);
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setSilent(true);
        villager.setCollidable(false);
        villager.setCanPickupItems(false);
        villager.setRemoveWhenFarAway(false);
        villager.setAware(false);
        villager.setAdult();
        villager.setAgeLock(true);
        villager.setPersistent(true);
        villager.setGravity(false);
        villager.setProfession(Villager.Profession.MASON);
        villager.teleport(loc);
        villager.setRotation(loc.getYaw(), 0f);
        rotationTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (villager == null || villager.isDead()) return;
            float fixedYaw = (float) cfg.getDouble("emerald_npc.yaw", villager.getLocation().getYaw());
            Location l = villager.getLocation();
            Location fixed = new Location(l.getWorld(), l.getX(), l.getY(), l.getZ(), fixedYaw, 0f);
            villager.teleport(fixed);
            villager.setRotation(fixedYaw, 0f);
        }, 1L, 2L);
    }

    public void despawn() {
        if (rotationTask != null) {
            rotationTask.cancel();
            rotationTask = null;
        }
        if (villager != null && !villager.isDead()) villager.remove();
        villager = null;
        villagerId = null;
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        if (!(e.getRightClicked() instanceof Villager v)) return;
        if (villagerId == null || !v.getUniqueId().equals(villagerId)) return;
        e.setCancelled(true);
        shopGUI.openMain(e.getPlayer());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (villagerId != null && e.getEntity().getUniqueId().equals(villagerId)) e.setCancelled(true);
    }
}
