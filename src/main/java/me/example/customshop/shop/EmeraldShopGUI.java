package me.example.customshop.shop;

import me.example.customshop.hook.CoinExchangerHook;
import me.example.customshop.items.ItemFactory;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EmeraldShopGUI implements Listener {
    private final Plugin plugin;
    private final CoinExchangerHook coins;
    private final String mainTitle;
    private final String confirmTitle;
    private final Map<UUID, ShopItem> pending = new HashMap<>();

    public EmeraldShopGUI(Plugin plugin, CoinExchangerHook coins) {
        this.plugin = plugin;
        this.coins = coins;
        this.mainTitle = color(plugin.getConfig().getString("eshop.title", "&a&l✦ ИЗУМРУДНЫЙ МАГАЗИН ✦"));
        this.confirmTitle = mainTitle + color(" &8» &aПодтверждение");
    }

    public void openMain(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, mainTitle);
        fill(inv, Material.GREEN_STAINED_GLASS_PANE, "&0");
        inv.setItem(11, priced(ItemFactory.emeraldPack(plugin, 1), price("emerald_1", 35)));
        inv.setItem(13, priced(ItemFactory.emeraldPack(plugin, 10), price("emerald_10", 300)));
        inv.setItem(15, priced(ItemFactory.emeraldPack(plugin, 24), price("emerald_24", 730)));
        inv.setItem(22, icon(Material.BARRIER, "&c&l✖ Закрыть"));
        placeBalance(inv, p);
        p.openInventory(inv);
    }

    private void openConfirm(Player p, ShopItem si) {
        Inventory inv = Bukkit.createInventory(null, 27, confirmTitle);
        fill(inv, Material.BLACK_STAINED_GLASS_PANE, "&0");
        inv.setItem(13, priced(si.item(), si.price()));
        inv.setItem(11, icon(Material.LIME_CONCRETE, "&a&l✔ КУПИТЬ"));
        inv.setItem(15, icon(Material.RED_CONCRETE, "&c&l✖ ОТМЕНА"));
        pending.put(p.getUniqueId(), si);
        p.openInventory(inv);
    }

    private List<ShopItem> items() {
        return List.of(
                new ShopItem("emerald_1", price("emerald_1", 35), ItemFactory.emeraldPack(plugin, 1)),
                new ShopItem("emerald_10", price("emerald_10", 300), ItemFactory.emeraldPack(plugin, 10)),
                new ShopItem("emerald_24", price("emerald_24", 730), ItemFactory.emeraldPack(plugin, 24))
        );
    }

    private int price(String id, int def) {
        return plugin.getConfig().getInt("prices." + id, def);
    }

    private void placeBalance(Inventory inv, Player p) {
        long bal = coins.get(p.getUniqueId());
        inv.setItem(4, icon(Material.GOLD_NUGGET, "&e&l⛃ Баланс: &6" + bal));
    }

    private ItemStack priced(ItemStack base, int price) {
        ItemStack it = base.clone();
        ItemMeta m = it.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (m.hasLore() && m.lore() != null) {
            m.lore().forEach(c -> lore.add(LegacyComponentSerializer.legacySection().serialize(c)));
        }
        lore.add(color("&8────────────────────"));
        lore.add(color("&eЦена: &6" + price + " &eмонет"));
        m.lore(lore.stream().map(s -> LegacyComponentSerializer.legacyAmpersand().deserialize(s)).toList());
        it.setItemMeta(m);
        return it;
    }

    private ItemStack icon(Material mat, String name) {
        ItemStack it = new ItemStack(mat);
        ItemMeta m = it.getItemMeta();
        m.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(color(name)));
        it.setItemMeta(m);
        return it;
    }

    private void fill(Inventory inv, Material mat, String name) {
        ItemStack it = icon(mat, name);
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, it);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getView().getTitle().startsWith(mainTitle)) e.setCancelled(true);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        String title = e.getView().getTitle();
        if (!title.startsWith(mainTitle)) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null || e.getCurrentItem().getType().isAir()) return;

        if (title.equals(mainTitle)) {
            if (e.getRawSlot() == 22) {
                p.closeInventory();
                return;
            }
            if (e.getRawSlot() == 11 || e.getRawSlot() == 13 || e.getRawSlot() == 15) {
                int idx = e.getRawSlot() == 11 ? 0 : e.getRawSlot() == 13 ? 1 : 2;
                openConfirm(p, items().get(idx));
            }
            return;
        }

        if (title.equals(confirmTitle)) {
            if (e.getRawSlot() == 11) {
                ShopItem si = pending.remove(p.getUniqueId());
                if (si == null) {
                    p.closeInventory();
                    return;
                }
                if (coins.get(p.getUniqueId()) < si.price()) {
                    p.sendMessage(color("&cНедостаточно монет."));
                    p.closeInventory();
                    return;
                }
                if (!coins.take(p.getUniqueId(), si.price())) {
                    p.sendMessage(color("&cНе удалось списать монеты."));
                    p.closeInventory();
                    return;
                }
                coins.save();
                Map<Integer, ItemStack> left = p.getInventory().addItem(si.item().clone());
                left.values().forEach(it -> p.getWorld().dropItemNaturally(p.getLocation(), it));
                p.sendMessage(color("&aПокупка успешна: &f" + displayName(si.item()) + " &7за &6" + si.price() + " &7монет."));
                openMain(p);
            } else if (e.getRawSlot() == 15) {
                openMain(p);
            }
        }
    }

    private String displayName(ItemStack it) {
        if (!it.hasItemMeta()) return it.getType().name();
        ItemMeta meta = it.getItemMeta();
        if (meta.hasItemName()) return LegacyComponentSerializer.legacySection().serialize(meta.itemName());
        if (meta.hasDisplayName()) return LegacyComponentSerializer.legacySection().serialize(meta.displayName());
        return it.getType().name();
    }

    private String color(String s) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', s);
    }
}
