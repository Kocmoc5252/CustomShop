package me.example.customshop.items;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class BoostFurnaceListener implements Listener {
    private final Plugin plugin;
    private final NamespacedKey keyMult;
    private final NamespacedKey keyItemTag;

    public BoostFurnaceListener(Plugin plugin) {
        this.plugin = plugin;
        this.keyMult = new NamespacedKey(plugin, "boost_mult");
        this.keyItemTag = new NamespacedKey(plugin, "customshop_item");
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        ItemStack hand = e.getItemInHand();
        if (hand == null || !hand.hasItemMeta()) return;

        String tag = hand.getItemMeta().getPersistentDataContainer()
                .get(keyItemTag, PersistentDataType.STRING);
        if (tag == null) return;

        int mult;
        switch (tag) {
            case "furnace_x2" -> mult = Math.max(1, plugin.getConfig().getInt("furnace.speed-multiplier.x2", 2));
            case "furnace_x3" -> mult = Math.max(1, plugin.getConfig().getInt("furnace.speed-multiplier.x3", 3));
            case "furnace_x4" -> mult = Math.max(1, plugin.getConfig().getInt("furnace.speed-multiplier.x4", 4));
            case "furnace_x5" -> mult = Math.max(1, plugin.getConfig().getInt("furnace.speed-multiplier.x5", 5));
            default -> { return; }
        }

        Block b = e.getBlockPlaced();
        if (!(b.getState() instanceof Furnace f)) return;

        f.getPersistentDataContainer().set(keyMult, PersistentDataType.INTEGER, mult);
        f.update();
    }

    @EventHandler
    public void onStartSmelt(FurnaceStartSmeltEvent e) {
        if (!(e.getBlock().getState() instanceof Furnace f)) return;

        Integer mult = f.getPersistentDataContainer().get(keyMult, PersistentDataType.INTEGER);
        if (mult == null || mult <= 1) return;

        int total = e.getTotalCookTime();        // обычное время
        int faster = Math.max(1, total / mult);  // реально быстрее
        e.setTotalCookTime(faster);
    }
}
