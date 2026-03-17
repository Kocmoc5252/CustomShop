package me.example.customshop.npc;

import me.example.customshop.shop.ShopGUI;
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

public class ShopNpcManager implements Listener {

    private final Plugin plugin;
    private final ShopGUI shopGUI;

    private Villager villager;
    private UUID villagerId;
    private BukkitTask rotationTask;

    public ShopNpcManager(Plugin plugin, ShopGUI shopGUI) {
        this.plugin = plugin;
        this.shopGUI = shopGUI;
    }

    public void setNpcAt(org.bukkit.entity.Player p) {
        Location l = p.getLocation().clone();

        float yaw = l.getYaw();
        float pitch = 0f;

        FileConfiguration cfg = plugin.getConfig();
        cfg.set("npc.world", l.getWorld().getName());
        cfg.set("npc.x", l.getX());
        cfg.set("npc.y", l.getY());
        cfg.set("npc.z", l.getZ());
        cfg.set("npc.yaw", yaw);
        cfg.set("npc.pitch", pitch);
        plugin.saveConfig();

        spawnOrRespawn();
        p.sendMessage("§aNPC магазина установлен.");
    }

    public void removeNpc(org.bukkit.entity.Player p) {
        despawn();

        FileConfiguration cfg = plugin.getConfig();
        cfg.set("npc.world", null);
        cfg.set("npc.x", null);
        cfg.set("npc.y", null);
        cfg.set("npc.z", null);
        cfg.set("npc.yaw", null);
        cfg.set("npc.pitch", null);
        plugin.saveConfig();

        p.sendMessage("§cNPC магазина удалён.");
    }

    public void spawnOrRespawn() {
        despawn();

        FileConfiguration cfg = plugin.getConfig();
        String worldName = cfg.getString("npc.world");
        if (worldName == null) return;

        World w = Bukkit.getWorld(worldName);
        if (w == null) return;

        double x = cfg.getDouble("npc.x");
        double y = cfg.getDouble("npc.y");
        double z = cfg.getDouble("npc.z");
        float yaw = (float) cfg.getDouble("npc.yaw", 0);
        float pitch = 0f;

        Location loc = new Location(w, x, y, z, yaw, pitch);

        villager = (Villager) w.spawnEntity(loc, EntityType.VILLAGER);
        villagerId = villager.getUniqueId();

        villager.customName(LegacyComponentSerializer.legacyAmpersand()
                .deserialize("§6§lМАГАЗИН §7(ПКМ)"));
        villager.setCustomNameVisible(true);

        // "как не моб"
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setSilent(true);
        villager.setCollidable(false);
        villager.setCanPickupItems(false);
        villager.setRemoveWhenFarAway(false);
        villager.setAware(false);

        // фикс возраста/поведения
        villager.setAdult();
        villager.setAgeLock(true);

        // Paper: чтобы не исчезал
        villager.setPersistent(true);

        // физика
        villager.setGravity(false);

        // профессия (можешь поменять)
        villager.setProfession(Villager.Profession.TOOLSMITH);

        // жестко фиксируем yaw/pitch
        villager.teleport(new Location(w, x, y, z, yaw, 0f));
        villager.setRotation(yaw, 0f);

        // каждые 2 тика фикс головы/поворота (если иногда “клюёт”)
        rotationTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (villager == null || villager.isDead()) return;

            float fixedYaw = (float) cfg.getDouble("npc.yaw", villager.getLocation().getYaw());
            Location l = villager.getLocation();

            // сохраняем позицию, но сбрасываем pitch
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
        if (villager != null && !villager.isDead()) {
            villager.remove();
        }
        villager = null;
        villagerId = null;
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        if (!(e.getRightClicked() instanceof Villager v)) return;
        if (villagerId == null) return;
        if (!v.getUniqueId().equals(villagerId)) return;

        e.setCancelled(true);
        shopGUI.openMain(e.getPlayer());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (villagerId == null) return;
        if (e.getEntity().getUniqueId().equals(villagerId)) {
            e.setCancelled(true);
        }
    }
}
