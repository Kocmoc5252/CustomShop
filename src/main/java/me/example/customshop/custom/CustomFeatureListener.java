package me.example.customshop.custom;

import me.example.customshop.items.ItemFactory;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.TileState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.*;
import org.bukkit.block.data.Ageable;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.bukkit.inventory.EquipmentSlot;

import java.util.*;

public class CustomFeatureListener implements Listener {
    private static final LegacyComponentSerializer SEC = LegacyComponentSerializer.legacySection();

    private final Plugin plugin;
    private final Map<Location, ArmorStand> processors = new HashMap<>();
    private final Map<Location, Inventory> processorInv = new HashMap<>();
    private final Set<Location> protectedTempBlocks = new HashSet<>();
    private final Set<Location> tempWaterBlocks = new HashSet<>();
    private final Random random = new Random();
    private final Map<UUID, Map<String, Long>> customCooldowns = new HashMap<>();

    public CustomFeatureListener(Plugin plugin) {
        this.plugin = plugin;
        startAntiGlowTicker();
        startProcessorTicker();
    }

    private NamespacedKey key(String k) {
        return new NamespacedKey(plugin, k);
    }

    private String data(ItemStack it, String key) {
        if (it == null || !it.hasItemMeta()) return null;
        return it.getItemMeta().getPersistentDataContainer().get(this.key(key), PersistentDataType.STRING);
    }

    private Integer dataInt(ItemStack it, String key) {
        if (it == null || !it.hasItemMeta()) return null;
        return it.getItemMeta().getPersistentDataContainer().get(this.key(key), PersistentDataType.INTEGER);
    }

    private boolean hasEnchant(ItemStack it, String type) {
        return it != null
                && it.hasItemMeta()
                && it.getItemMeta().getPersistentDataContainer().has(key("cs_ench_" + type), PersistentDataType.INTEGER);
    }

    private int enchantLevel(ItemStack it, String type) {
        if (it == null || !it.hasItemMeta()) return 0;
        return it.getItemMeta().getPersistentDataContainer().getOrDefault(key("cs_ench_" + type), PersistentDataType.INTEGER, 0);
    }

    private void setEnchant(ItemStack it, String type, int level) {
        ItemMeta meta = it.getItemMeta();
        meta.getPersistentDataContainer().set(key("cs_ench_" + type), PersistentDataType.INTEGER, level);

        List<String> lore = new ArrayList<>();
        if (meta.lore() != null) meta.lore().forEach(c -> lore.add(SEC.serialize(c)));

        String line = customEnchantLore(type, level);
        String prefix = ChatColor.stripColor(line).split(" ")[0];
        lore.removeIf(s -> ChatColor.stripColor(s).startsWith(prefix));
        lore.add(line);

        meta.lore(lore.stream().map(LegacyComponentSerializer.legacyAmpersand()::deserialize).toList());
        meta.removeEnchant(Enchantment.LURE);
        meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        it.setItemMeta(meta);
    }

    private String customEnchantLore(String type, int level) {
        return switch (type) {
            case "bulldozer" -> "&6Бульдозер I";
            case "treecapitator" -> "&2Дровосек I";
            case "poison" -> "&aЯд " + roman(level);
            case "wither" -> "&8Вред " + roman(level);
            case "lavawalker" -> "&6Лавоход I";
            case "antichams" -> "&5Анти-Чамс I";
            case "magnet" -> "&eМагнит I";
            case "efficiency" -> "&bЭффективность " + roman(level);
            case "sharpness" -> "&cОстрота " + roman(level);
            case "supermending" -> "&dУлучшенная починка I";
            case "greenifier" -> "&aОзеленитель " + roman(level);
            case "immortality" -> "&fНеубиваемость " + roman(level);
            case "light_handle" -> "&bЛёгкая рукоять I";
            case "unstable" -> "&8Нестабильный " + roman(level);
            case "lifesteal" -> "&cВампиризм " + roman(level);
            case "frost" -> "&bЛедяной удар " + roman(level);
            case "guardian" -> "&fСтраж " + roman(level);
            case "berserk" -> "&4Берсерк " + roman(level);
            case "autosmelt" -> "&6Автоплавка I";
            case "executioner" -> "&4Казнь " + roman(level);
            case "armorbreak" -> "&7Раскол " + roman(level);
            case "hunter" -> "&aОхотник " + roman(level);
            case "reflection" -> "&bОтражение " + roman(level);
            case "bloodrage" -> "&cЯрость " + roman(level);
            case "brittle" -> "&7Хрупкость " + roman(level);
            case "drained" -> "&8Истощение " + roman(level);
            case "jinxed" -> "&5Сглаз " + roman(level);
            default -> "&fКастом";
        };
    }

    private String roman(int n) {
        return switch (n) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            default -> String.valueOf(n);
        };
    }

    private boolean isPickOrShovel(Material m) { return m.name().endsWith("PICKAXE") || m.name().endsWith("SHOVEL"); }
    private boolean isAxe(Material m) { return m.name().endsWith("AXE") && !m.name().endsWith("PICKAXE"); }
    private boolean isSword(Material m) { return m.name().endsWith("SWORD"); }
    private boolean isBoots(Material m) { return m.name().endsWith("BOOTS"); }
    private boolean isChest(Material m) { return m.name().endsWith("CHESTPLATE"); }
    private boolean isHoe(Material m) { return m.name().endsWith("HOE"); }
    private boolean isTrident(Material m) { return m == Material.TRIDENT; }
    private boolean isBowLike(Material m) { return m == Material.BOW || m == Material.CROSSBOW || m == Material.TRIDENT; }
    private boolean isEnchantBook(Material m) { return m == Material.BOOK; }
    private boolean isToolish(Material m) { return isPickOrShovel(m) || isAxe(m) || isHoe(m); }
    private boolean isMagnetTool(Material m) { return isPickOrShovel(m) || isAxe(m); }

    private double customBookChance(int cost) {
        if (cost >= 30) return 0.30;
        if (cost >= 20) return 0.22;
        if (cost >= 10) return 0.14;
        return 0.08;
    }

    private ItemStack randomTableBook(int cost) {
        List<ItemStack> pool = new ArrayList<>();
        addBook(pool, "bulldozer", 1);
        addBook(pool, "treecapitator", 1);
        addBook(pool, "magnet", 1);
        addBook(pool, "greenifier", 1);
        addBook(pool, "autosmelt", 1);
        addBook(pool, "lavawalker", 1);
        addBook(pool, "antichams", 1);
        addBook(pool, "guardian", 1);
        addBook(pool, "reflection", 1);
        addBook(pool, "light_handle", 1);
        addBook(pool, "immortality", 1);
        addBook(pool, "supermending", 1);
        addScaledCombatBooks(pool, cost, "poison");
        addScaledCombatBooks(pool, cost, "wither");
        addScaledCombatBooks(pool, cost, "lifesteal");
        addScaledCombatBooks(pool, cost, "frost");
        addScaledCombatBooks(pool, cost, "berserk");
        addScaledCombatBooks(pool, cost, "executioner");
        addScaledCombatBooks(pool, cost, "armorbreak");
        addScaledCombatBooks(pool, cost, "bloodrage");
        addScaledCombatBooks(pool, cost, "hunter");
        if (cost >= 16) {
            for (int level = 6; level <= maxTableVanillaLevel(cost); level++) {
                addBook(pool, "efficiency", level);
                addBook(pool, "sharpness", level);
            }
        }
        if (cost >= 24) addBook(pool, "fortune", 5);
        return pool.get(random.nextInt(pool.size()));
    }

    private void addBook(List<ItemStack> pool, String type, int level) {
        pool.add(ItemFactory.customBook(plugin, type, level));
    }

    private void addScaledCombatBooks(List<ItemStack> pool, int cost, String type) {
        addBook(pool, type, 1);
        if (cost >= 24) addBook(pool, type, 2);
    }

    private int maxTableVanillaLevel(int cost) {
        if (cost >= 30) return 10;
        if (cost >= 27) return 9;
        if (cost >= 24) return 8;
        if (cost >= 20) return 7;
        return 6;
    }

    private int rollTableLevel(String type, int cost) {
        return switch (type) {
            case "poison", "wither", "lifesteal", "frost", "berserk", "executioner", "armorbreak", "bloodrage", "hunter" -> cost >= 24 && random.nextDouble() < (cost >= 30 ? 0.70 : 0.45) ? 2 : 1;
            case "efficiency", "sharpness" -> 6 + random.nextInt(Math.max(1, maxTableVanillaLevel(cost) - 5));
            case "fortune" -> 5;
            default -> 1;
        };
    }

    private boolean rollTableEnchant(ItemStack item, int cost) {
        List<String> candidates = new ArrayList<>();
        List<String> all = List.of(
                "bulldozer", "treecapitator", "poison", "wither", "efficiency", "sharpness", "lavawalker",
                "antichams", "magnet", "supermending", "greenifier", "immortality", "light_handle", "fortune",
                "lifesteal", "frost", "guardian", "berserk", "autosmelt", "executioner", "armorbreak",
                "hunter", "reflection", "bloodrage"
        );
        for (String type : all) {
            if (canApply(item.getType(), type)) candidates.add(type);
        }
        if (candidates.isEmpty()) return false;

        Collections.shuffle(candidates, random);
        String type = candidates.get(0);
        int level = rollTableLevel(type, cost);
        if (type.equals("efficiency")) item.addUnsafeEnchantment(Enchantment.EFFICIENCY, level);
        else if (type.equals("sharpness")) item.addUnsafeEnchantment(Enchantment.SHARPNESS, level);
        else if (type.equals("fortune")) item.addUnsafeEnchantment(Enchantment.FORTUNE, level);
        else setEnchant(item, type, level);
        return true;
    }

    @EventHandler
    public void onPrepareEnchant(PrepareItemEnchantEvent e) {
        ItemStack item = e.getItem();
        if (item == null || item.getType().isAir()) return;
        if (random.nextDouble() > 0.35) return;
        if (isPickOrShovel(item.getType())) {
            e.getOffers()[0] = e.getOffers()[0];
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent e) {
        ItemStack item = e.getItem();
        if (item == null) return;
        int cost = e.getExpLevelCost();
        if (isEnchantBook(item.getType()) && random.nextDouble() < customBookChance(cost)) {
            ItemStack rolled = randomTableBook(cost);
            e.getEnchantsToAdd().clear();
            item.setType(rolled.getType());
            item.setItemMeta(rolled.getItemMeta());
            return;
        }

        double chance = cost >= 30 ? 0.36 : cost >= 24 ? 0.27 : cost >= 18 ? 0.18 : 0.10;
        if (random.nextDouble() < chance) {
            rollTableEnchant(item, cost);
            if (cost >= 30 && random.nextDouble() < 0.12) {
                rollTableEnchant(item, cost);
            }
        }
    }
    @EventHandler(ignoreCancelled = true)
    public void onProcessorBreak(BlockBreakEvent e) {
        Location loc = e.getBlock().getLocation();
        if (!processors.containsKey(loc)) return;

        e.setDropItems(false);

        ArmorStand marker = processors.remove(loc);
        if (marker != null) marker.remove();

        processorInv.remove(loc);

        e.getBlock().setType(Material.AIR, false);

        e.getBlock().getWorld().dropItemNaturally(
                loc,
                ItemFactory.killerCpu(plugin)
        );
    }
    @EventHandler
    public void onAnvil(PrepareAnvilEvent e) {
        ItemStack left = e.getInventory().getFirstItem();
        ItemStack right = e.getInventory().getSecondItem();
        if (left == null || right == null) return;

        String type = data(right, "cs_book_type");
        Integer level = dataInt(right, "cs_book_level");
        if (type == null || level == null) return;
        if (!canApply(left.getType(), type)) return;

        ItemStack out = left.clone();
        if (type.equals("efficiency")) out.addUnsafeEnchantment(Enchantment.EFFICIENCY, level);
        else if (type.equals("sharpness")) out.addUnsafeEnchantment(Enchantment.SHARPNESS, level);
        else if (type.equals("fortune")) out.addUnsafeEnchantment(Enchantment.FORTUNE, level);
        else setEnchant(out, type, level);

        e.setResult(out);
        try {
            e.getInventory().setRepairCost(Math.max(6, Math.min(18, level + 5)));
        } catch (Throwable ignored) {}
    }

    private boolean canApply(Material material, String type) {
        return switch (type) {
            case "bulldozer" -> isPickOrShovel(material);
            case "treecapitator" -> isAxe(material);
            case "poison", "wither", "sharpness", "lifesteal", "frost", "berserk", "executioner", "armorbreak", "bloodrage" -> isSword(material) || isAxe(material);
            case "efficiency", "fortune" -> material.name().endsWith("PICKAXE");
            case "autosmelt" -> isPickOrShovel(material);
            case "lavawalker" -> isBoots(material);
            case "antichams", "guardian", "reflection" -> isChest(material);
            case "magnet" -> isMagnetTool(material);
            case "greenifier" -> isHoe(material);
            case "immortality" -> material == Material.ELYTRA;
            case "light_handle" -> isTrident(material);
            case "hunter" -> isBowLike(material);
            case "unstable", "brittle", "drained", "jinxed", "supermending" -> material.getMaxDurability() > 0;
            default -> false;
        };
    }

    @EventHandler
    public void onLootGenerate(LootGenerateEvent e) {
        List<ItemStack> loot = e.getLoot();
        for (ItemStack stack : loot) {
            if (stack == null || stack.getType().isAir()) continue;
            if (stack.getType().getMaxDurability() <= 0) continue;
            if (!isLootCurseCandidate(stack.getType())) continue;
            if (random.nextDouble() < 0.17) {
                applyRandomLootCurse(stack);
            }
        }
    }

    private boolean isLootCurseCandidate(Material material) {
        return isToolish(material)
                || isSword(material)
                || isAxe(material)
                || isBowLike(material)
                || material == Material.ELYTRA
                || material.name().endsWith("HELMET")
                || material.name().endsWith("CHESTPLATE")
                || material.name().endsWith("LEGGINGS")
                || material.name().endsWith("BOOTS");
    }

    private void applyRandomLootCurse(ItemStack stack) {
        double r = random.nextDouble();
        if (r < 0.34) setEnchant(stack, "unstable", random.nextDouble() < 0.35 ? 2 : 1);
        else if (r < 0.62) setEnchant(stack, "brittle", random.nextDouble() < 0.30 ? 2 : 1);
        else if (r < 0.86) setEnchant(stack, "drained", random.nextDouble() < 0.28 ? 2 : 1);
        else setEnchant(stack, "jinxed", 1);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();
        ItemStack tool = p.getInventory().getItemInMainHand();

        if (b.getType() == Material.SPAWNER) {
            boolean god = "god_touch".equals(data(tool, "customshop_item"));
            boolean markedSpawner = b.getState() instanceof TileState ts
                    && ts.getPersistentDataContainer().has(key("cs_empty_spawner"), PersistentDataType.BYTE);
            e.setExpToDrop(0);
            e.setDropItems(false);
            if (god || (markedSpawner && random.nextDouble() < 0.09)) {
                b.getWorld().dropItemNaturally(b.getLocation(), ItemFactory.emptySpawner(plugin));
            }
            if (god && tool.getItemMeta() instanceof Damageable dmg) {
                dmg.setDamage(dmg.getDamage() + 1);
                tool.setItemMeta((ItemMeta) dmg);
            }
            return;
        }

        if (hasEnchant(tool, "bulldozer") && isPickOrShovel(tool.getType())) {
            bulldoze(e, tool);
        }
        if (hasEnchant(tool, "treecapitator") && isAxe(tool.getType()) && isLog(b.getType())) {
            cutTree(p, b, tool);
        }
        if ((hasEnchant(tool, "magnet") && isMagnetTool(tool.getType())) || hasEnchant(tool, "autosmelt")) {
            Collection<ItemStack> drops = processDrops(tool, b.getDrops(tool, p));
            e.setDropItems(false);
            new BukkitRunnable() {
                @Override public void run() {
                    if (hasEnchant(tool, "magnet") && isMagnetTool(tool.getType())) drops.forEach(drop -> giveOrDrop(p, drop));
                    else drops.forEach(drop -> b.getWorld().dropItemNaturally(b.getLocation(), drop));
                }
            }.runTask(plugin);
        }
        int drained = enchantLevel(tool, "drained");
        if (drained > 0 && random.nextDouble() < (0.10 * drained)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 20 * (2 + drained), Math.max(0, drained - 1), true, false, true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 2, 0, true, false, true));
        }
    }

    private void bulldoze(BlockBreakEvent origin, ItemStack tool) {
        Block center = origin.getBlock();
        Player p = origin.getPlayer();
        Material target = center.getType();
        for (int x = -1; x <= 1; x++) for (int y = -1; y <= 1; y++) for (int z = -1; z <= 1; z++) {
            if (x == 0 && y == 0 && z == 0) continue;
            Block b = center.getRelative(x, y, z);
            if (b.getType() != target || b.getType() == Material.BEDROCK) continue;
            Collection<ItemStack> drops = processDrops(tool, b.getDrops(tool, p));
            b.setType(Material.AIR, false);
            if (hasEnchant(tool, "magnet")) drops.forEach(drop -> giveOrDrop(p, drop));
            else drops.forEach(drop -> b.getWorld().dropItemNaturally(b.getLocation(), drop));
        }
    }

    private boolean isLog(Material m) {
        return m.name().endsWith("_LOG") || m.name().endsWith("_STEM") || m.name().endsWith("_HYPHAE");
    }

    private void cutTree(Player p, Block start, ItemStack tool) {
        Set<Block> visited = new HashSet<>();
        Deque<Block> q = new ArrayDeque<>();
        q.add(start);
        while (!q.isEmpty() && visited.size() < 128) {
            Block b = q.poll();
            if (!visited.add(b) || !isLog(b.getType())) continue;
            Collection<ItemStack> drops = processDrops(tool, b.getDrops(tool, p));
            b.setType(Material.AIR, false);
            if (hasEnchant(tool, "magnet")) drops.forEach(drop -> giveOrDrop(p, drop));
            else drops.forEach(drop -> b.getWorld().dropItemNaturally(b.getLocation(), drop));
            for (int x = -1; x <= 1; x++) for (int y = -1; y <= 1; y++) for (int z = -1; z <= 1; z++) q.add(b.getRelative(x, y, z));
        }
    }

    private void giveOrDrop(Player p, ItemStack item) {
        Map<Integer, ItemStack> left = p.getInventory().addItem(item);
        left.values().forEach(rem -> p.getWorld().dropItemNaturally(p.getLocation(), rem));
    }


    private Collection<ItemStack> processDrops(ItemStack tool, Collection<ItemStack> drops) {
        if (!hasEnchant(tool, "autosmelt")) return drops;
        List<ItemStack> out = new ArrayList<>();
        for (ItemStack drop : drops) {
            out.add(smelt(drop));
        }
        return out;
    }

    private ItemStack smelt(ItemStack source) {
        Material result = switch (source.getType()) {
            case RAW_IRON -> Material.IRON_INGOT;
            case RAW_GOLD -> Material.GOLD_INGOT;
            case RAW_COPPER -> Material.COPPER_INGOT;
            case COBBLESTONE -> Material.STONE;
            case SAND, RED_SAND -> Material.GLASS;
            case CLAY_BALL -> Material.BRICK;
            case NETHERRACK -> Material.NETHER_BRICK;
            default -> null;
        };
        if (result == null) return source;
        return new ItemStack(result, source.getAmount());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlaceSpawner(BlockPlaceEvent e) {
        if (!"empty_spawner".equals(data(e.getItemInHand(), "customshop_item"))) return;
        if (e.getBlockPlaced().getState() instanceof CreatureSpawner spawner) {
            spawner.getPersistentDataContainer().set(key("cs_empty_spawner"), PersistentDataType.BYTE, (byte) 1);
            spawner.update();
        }
    }

    private void affectNearbyFire(Player p) {
        World w = p.getWorld();
        Location c = p.getLocation();

        w.spawnParticle(Particle.FLAME, c, 120, 2.5, 1.0, 2.5, 0.03);
        w.spawnParticle(Particle.SMOKE, c, 40, 2.0, 0.8, 2.0, 0.01);

        for (Entity en : w.getNearbyEntities(c, 6, 6, 6)) {
            if (en == p) continue;
            if (!(en instanceof LivingEntity le)) continue;
            le.setFireTicks(6 * 20);
        }
    }

    private void spawnWaterCube(Location base) {
        World w = base.getWorld();
        List<Block> changed = new ArrayList<>();

        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block b = base.clone().add(x, y, z).getBlock();
                    if (b.getType() != Material.AIR) continue;

                    b.setType(Material.WATER, false);
                    protectedTempBlocks.add(b.getLocation());
                    changed.add(b);
                }
            }
        }

        removeLater(changed, 13 * 20L);
    }

    private boolean isOnCustomCooldown(Player p, String id) {
        Map<String, Long> map = customCooldowns.get(p.getUniqueId());
        if (map == null) return false;

        Long until = map.get(id);
        return until != null && until > System.currentTimeMillis();
    }

    private boolean startCustomCooldown(Player p, String id, Material mat, int ticks) {
        if (isOnCustomCooldown(p, id)) return false;

        customCooldowns
                .computeIfAbsent(p.getUniqueId(), k -> new HashMap<>())
                .put(id, System.currentTimeMillis() + (ticks * 50L));

        p.setCooldown(mat, ticks); // белая заливка в инвентаре
        return true;
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack it = e.getItem();
        if (it == null || !it.hasItemMeta()) return;

        String id = data(it, "customshop_item");

        Player p = e.getPlayer();

        if (hasEnchant(it, "greenifier") && isHoe(it.getType()) && e.getClickedBlock() != null) {
            Block clicked = e.getClickedBlock();
            if (clicked.getBlockData() instanceof Ageable ageable && ageable.getAge() < ageable.getMaximumAge()) {
                ageable.setAge(Math.min(ageable.getMaximumAge(), ageable.getAge() + Math.max(1, enchantLevel(it, "greenifier"))));
                clicked.setBlockData(ageable, true);
                clicked.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, clicked.getLocation().add(0.5, 0.8, 0.5), 12, 0.25, 0.25, 0.25, 0.01);
                if (it.getItemMeta() instanceof Damageable dmg) {
                    dmg.setDamage(dmg.getDamage() + 1);
                    it.setItemMeta((ItemMeta) dmg);
                }
                e.setCancelled(true);
                return;
            }
        }

        if (id == null) return;

        switch (id) {
            case "reveal_dust" -> {
                e.setCancelled(true);
                e.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
                e.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
                if (!startCustomCooldown(p, id, it.getType(), 20 * 8)) return;

                consumeOne(p, it);
                revealNearby(p);
            }
            case "disorientation" -> {
                e.setCancelled(true);
                e.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
                e.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
                if (!startCustomCooldown(p, id, it.getType(), 20 * 10)) return;

                consumeOne(p, it);
                disorientNearby(p);
            }
            case "fire_tornado" -> {
                e.setCancelled(true);
                e.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
                e.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
                if (!startCustomCooldown(p, id, it.getType(), 20 * 12)) return;

                consumeOne(p, it);
                affectNearbyFire(p);
            }
            case "holy_aura" -> {
                e.setCancelled(true);
                e.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
                e.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
                if (!startCustomCooldown(p, id, it.getType(), 20 * 14)) return;

                consumeOne(p, it);
                clearBadEffects(p);
                p.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, p.getLocation().add(0, 1, 0), 50, 0.5, 0.8, 0.5, 0.1);
            }
            case "trap_box" -> {
                e.setCancelled(true);
                e.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
                e.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
                if (!startCustomCooldown(p, id, it.getType(), 20 * 18)) return;

                consumeOne(p, it);
                Block target = p.getTargetBlockExact(5);
                if (target != null) {
                    spawnTrap(target.getRelative(BlockFace.UP).getLocation());
                } else {
                    spawnTrap(p.getLocation().getBlock().getLocation());
                }
            }
            case "plate_wall" -> {
                e.setCancelled(true);
                e.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
                e.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
                if (!startCustomCooldown(p, id, it.getType(), 20 * 16)) return;

                consumeOne(p, it);
                spawnWall(p);
            }
            case "water_cube" -> {
                e.setCancelled(true);
                e.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
                e.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
                if (!startCustomCooldown(p, id, it.getType(), 20 * 16)) return;
                consumeOne(p, it);
                Block target = p.getTargetBlockExact(6);
                if (target != null) {
                    spawnWaterCube(target.getRelative(BlockFace.UP).getLocation());
                } else {
                    spawnWaterCube(p.getLocation().getBlock().getLocation().add(0, 1, 0));
                }
            }
            case "unreal_aura" -> {
                e.setCancelled(true);
                e.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
                e.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
                if (!startCustomCooldown(p, id, it.getType(), 20 * 15)) return;

                consumeOne(p, it);
                unrealAura(p);
            }
            case "killer_cpu" -> {
                e.setCancelled(true);
                e.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
                e.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
                if (!startCustomCooldown(p, id, it.getType(), 20 * 20)) return;

                consumeOne(p, it);
                placeProcessor(p);
            }
            case "spawn_charged_creeper" -> {
                e.setCancelled(true);
                e.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
                e.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
                spawnChargedCreeper(p, e);
                consumeOne(p, it);
            }
            case "blood_totem" -> {
                e.setCancelled(true);
                e.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
                e.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
                if (!startCustomCooldown(p, id, it.getType(), 20 * 26)) return;
                consumeOne(p, it);
                activateBloodTotem(p);
            }
            case "smoke_bomb" -> {
                e.setCancelled(true);
                e.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
                e.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
                if (!startCustomCooldown(p, id, it.getType(), 20 * 16)) return;
                consumeOne(p, it);
                smokeBomb(p);
            }
            case "purge_stone" -> {
                e.setCancelled(true);
                e.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
                e.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
                if (!startCustomCooldown(p, id, it.getType(), 20 * 18)) return;
                consumeOne(p, it);
                purgeStone(p);
            }
            case "gravity_orb" -> {
                e.setCancelled(true);
                e.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
                e.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
                if (!startCustomCooldown(p, id, it.getType(), 20 * 15)) return;
                consumeOne(p, it);
                gravityOrb(p);
            }

            // ванильные
            case "healing_pearl", "giant_snowball", "golden_apple", "ench_gapple" -> {
            }
        }
    }

    private void spawnChargedCreeper(Player p, PlayerInteractEvent e) {
        Location spawnLoc;
        if (e.getClickedBlock() != null) {
            spawnLoc = e.getClickedBlock().getRelative(e.getBlockFace()).getLocation().add(0.5, 0.0, 0.5);
        } else {
            Block target = p.getTargetBlockExact(6);
            if (target != null) {
                spawnLoc = target.getRelative(BlockFace.UP).getLocation().add(0.5, 0.0, 0.5);
            } else {
                spawnLoc = p.getLocation().add(p.getLocation().getDirection().normalize().multiply(2.0));
            }
        }
        World world = p.getWorld();
        Creeper creeper = world.spawn(spawnLoc, Creeper.class);
        creeper.setPowered(true);
        world.spawnParticle(Particle.ELECTRIC_SPARK, spawnLoc.clone().add(0, 1, 0), 40, 0.4, 0.6, 0.4, 0.02);
        world.playSound(spawnLoc, Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1.0f, 1.15f);
    }

    private void activateBloodTotem(Player p) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 20 * 10, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 8, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 14, 1));
        p.getWorld().spawnParticle(Particle.CRIMSON_SPORE, p.getLocation().add(0, 1, 0), 55, 0.6, 0.8, 0.6, 0.02);
        p.getWorld().playSound(p.getLocation(), Sound.ITEM_TOTEM_USE, 0.85f, 0.8f);
    }

    private void smokeBomb(Player p) {
        Location c = p.getLocation();
        World w = p.getWorld();
        w.spawnParticle(Particle.LARGE_SMOKE, c.clone().add(0, 1, 0), 140, 2.6, 1.0, 2.6, 0.02);
        w.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, c.clone().add(0, 0.7, 0), 70, 2.2, 0.6, 2.2, 0.01);
        p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 5, 0));
        for (Entity en : w.getNearbyEntities(c, 5.5, 4, 5.5)) {
            if (en == p || !(en instanceof LivingEntity le)) continue;
            le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 4, 0));
            le.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 20 * 4, 0));
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 2, 0));
        }
    }

    private void purgeStone(Player p) {
        Location c = p.getLocation();
        World w = p.getWorld();
        w.spawnParticle(Particle.ENCHANT, c.clone().add(0, 1, 0), 120, 2.3, 1.0, 2.3, 0.08);
        for (Entity en : w.getNearbyEntities(c, 6, 4, 6)) {
            if (en == p || !(en instanceof LivingEntity le)) continue;
            clearPositiveEffects(le);
            le.damage(2.0, p);
            w.spawnParticle(Particle.WITCH, le.getLocation().add(0, 1, 0), 25, 0.35, 0.45, 0.35, 0.02);
        }
    }

    private void gravityOrb(Player p) {
        Location c = p.getLocation();
        World w = p.getWorld();
        w.spawnParticle(Particle.PORTAL, c.clone().add(0, 1, 0), 140, 1.8, 0.8, 1.8, 0.18);
        for (Entity en : w.getNearbyEntities(c, 6, 4, 6)) {
            if (en == p || !(en instanceof LivingEntity le)) continue;
            Vector pull = c.toVector().subtract(le.getLocation().toVector());
            if (pull.lengthSquared() < 0.01) pull = new Vector(0.01, 0, 0.01);
            pull.normalize().multiply(1.1).setY(0.20);
            le.setVelocity(pull);
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 3, 1));
        }
        w.playSound(c, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 0.9f, 0.75f);
    }

    private void clearPositiveEffects(LivingEntity le) {
        PotionEffectType[] good = {
                PotionEffectType.SPEED, PotionEffectType.HASTE, PotionEffectType.STRENGTH,
                PotionEffectType.RESISTANCE, PotionEffectType.REGENERATION, PotionEffectType.ABSORPTION,
                PotionEffectType.FIRE_RESISTANCE, PotionEffectType.WATER_BREATHING, PotionEffectType.NIGHT_VISION,
                PotionEffectType.INVISIBILITY, PotionEffectType.JUMP_BOOST, PotionEffectType.DOLPHINS_GRACE
        };
        for (PotionEffectType type : good) le.removePotionEffect(type);
    }

    private void consumeOne(Player p, ItemStack it) {
        if (p.getGameMode() == GameMode.CREATIVE) return;
        it.setAmount(it.getAmount() - 1);
    }

    private void revealNearby(Player p) {
        World w = p.getWorld();
        Location c = p.getLocation();
        w.spawnParticle(Particle.ENCHANT, c, 150, 3, 1, 3, 0.05);
        for (Entity en : w.getNearbyEntities(c, 6, 6, 6)) {
            if (en == p) continue;
            if (!(en instanceof LivingEntity le)) continue;
            le.setGlowing(true);
            le.setMetadata("cs_temp_glow", new FixedMetadataValue(plugin, System.currentTimeMillis() + 7000L));
        }
    }

    private void disorientNearby(Player p) {
        World w = p.getWorld();
        Location c = p.getLocation();
        w.spawnParticle(Particle.PORTAL, c, 120, 2.5, 1, 2.5, 0.25);
        w.spawnParticle(Particle.SMOKE, c, 45, 2, .5, 2, 0.03);
        for (Entity en : w.getNearbyEntities(c, 6, 6, 6)) {
            if (en == p) continue;
            if (!(en instanceof LivingEntity le)) continue;
            le.setGlowing(true);
            le.setMetadata("cs_temp_glow", new FixedMetadataValue(plugin, System.currentTimeMillis() + 5000L));
            le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
            le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 120, 0));
        }
    }

    private void fireTornado(Player p) {
        World w = p.getWorld();
        Location c = p.getLocation();
        for (int i = 0; i < 3; i++) {
            w.spawnParticle(Particle.FLAME, c.clone().add(0, i * 0.7, 0), 70, 2.5, 0.2, 2.5, 0.03);
            w.spawnParticle(Particle.SMOKE, c.clone().add(0, i * 0.7, 0), 30, 2, 0.15, 2, 0.02);
        }
        for (Entity en : w.getNearbyEntities(c, 6, 6, 6)) {
            if (en == p) continue;
            if (en instanceof LivingEntity le) {
                le.setFireTicks(Math.max(le.getFireTicks(), 20 * 6));
            }
        }
    }

    private void unrealAura(Player p) {
        World w = p.getWorld();
        Location c = p.getLocation();
        w.spawnParticle(Particle.END_ROD, c.clone().add(0, 1, 0), 90, 1.6, 0.8, 1.6, 0.03);
        for (Entity en : w.getNearbyEntities(c, 6, 6, 6)) {
            if (en == p) continue;
            if (!(en instanceof LivingEntity le)) continue;
            Vector knock = le.getLocation().toVector().subtract(c.toVector());
            if (knock.lengthSquared() < 0.01) knock = new Vector(0.01, 0, 0.01);
            knock.normalize().multiply(1.35).setY(0.28);
            le.setVelocity(knock);
            le.setGlowing(true);
            le.setMetadata("cs_temp_glow", new FixedMetadataValue(plugin, System.currentTimeMillis() + 5000L));
        }
    }

    private void clearBadEffects(Player p) {
        PotionEffectType[] bad = {
                PotionEffectType.BLINDNESS, PotionEffectType.DARKNESS, PotionEffectType.SLOWNESS,
                PotionEffectType.MINING_FATIGUE, PotionEffectType.WEAKNESS, PotionEffectType.POISON,
                PotionEffectType.WITHER, PotionEffectType.GLOWING, PotionEffectType.HUNGER,
                PotionEffectType.NAUSEA, PotionEffectType.LEVITATION, PotionEffectType.BAD_OMEN,
                PotionEffectType.UNLUCK
        };
        for (PotionEffectType type : bad) p.removePotionEffect(type);
        p.setGlowing(false);
    }

    private void spawnTrap(Location base) {
        List<Material> mats = List.of(Material.ANDESITE, Material.COBBLESTONE, Material.STONE);
        List<Block> changed = new ArrayList<>();
        for (int x = -2; x <= 2; x++) for (int y = 0; y <= 4; y++) for (int z = -2; z <= 2; z++) {
            boolean wall = x == -2 || x == 2 || z == -2 || z == 2 || y == 0 || y == 4;
            if (!wall) continue;
            Block b = base.clone().add(x, y, z).getBlock();
            if (b.getType() != Material.AIR) continue;
            b.setType(mats.get(Math.floorMod(x + y + z, mats.size())));
            protectedTempBlocks.add(b.getLocation());
            changed.add(b);
        }
        removeLater(changed, 13 * 20L);
    }

    private void spawnWall(Player p) {
        Location start = p.getLocation().getBlock().getLocation();
        Vector dir = p.getLocation().getDirection().setY(0).normalize();
        Vector side = new Vector(-dir.getZ(), 0, dir.getX());
        List<Block> changed = new ArrayList<>();
        for (int i = -2; i <= 2; i++) for (int y = 0; y < 3; y++) {
            Location l = start.clone().add(dir.clone().multiply(2)).add(side.clone().multiply(i)).add(0, y, 0);
            Block b = l.getBlock();
            if (b.getType() != Material.AIR) continue;
            b.setType((i + y) % 2 == 0 ? Material.STONE : Material.COBBLESTONE);
            protectedTempBlocks.add(b.getLocation());
            changed.add(b);
        }
        removeLater(changed, 13 * 20L);
    }

    private void spawnWaterCube(Player p) {
        Block target = p.getTargetBlockExact(6);
        Location center = target != null
                ? target.getLocation()
                : p.getLocation().getBlock().getLocation().add(p.getLocation().getDirection().setY(0).normalize().multiply(2));
        List<Block> changed = new ArrayList<>();
        for (int x = -1; x <= 1; x++) for (int y = -1; y <= 1; y++) for (int z = -1; z <= 1; z++) {
            Block b = center.clone().add(x, y, z).getBlock();
            if (!b.getType().isAir()) continue;
            b.setType(Material.WATER, false);
            protectedTempBlocks.add(b.getLocation());
            tempWaterBlocks.add(b.getLocation());
            changed.add(b);
        }
        removeLaterWater(changed, 20L * 8);
    }

    private void removeLater(List<Block> changed, long ticks) {
        new BukkitRunnable() {
            @Override public void run() {
                for (Block b : changed) {
                    protectedTempBlocks.remove(b.getLocation());
                    if (!b.isEmpty()) b.setType(Material.AIR);
                }
            }
        }.runTaskLater(plugin, ticks);
    }

    private void removeLaterWater(List<Block> changed, long ticks) {
        new BukkitRunnable() {
            @Override public void run() {
                for (Block b : changed) {
                    protectedTempBlocks.remove(b.getLocation());
                    tempWaterBlocks.remove(b.getLocation());
                    if (b.getType() == Material.WATER) b.setType(Material.AIR, false);
                }
            }
        }.runTaskLater(plugin, ticks);
    }

    @EventHandler(ignoreCancelled = true)
    public void onProtectedBreak(BlockBreakEvent e) {
        if (protectedTempBlocks.contains(e.getBlock().getLocation())) e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onProtectedExplode(EntityExplodeEvent e) {
        e.blockList().removeIf(b -> protectedTempBlocks.contains(b.getLocation()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onWaterFlow(BlockFromToEvent e) {
        if (tempWaterBlocks.contains(e.getBlock().getLocation()) || tempWaterBlocks.contains(e.getToBlock().getLocation())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLaunch(org.bukkit.event.entity.ProjectileLaunchEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player p)) return;
        ItemStack main = p.getInventory().getItemInMainHand();
        String id = data(main, "customshop_item");
        if (e.getEntity() instanceof Snowball snow && "giant_snowball".equals(id)) {
            snow.setMetadata("cs_giant_snowball", new FixedMetadataValue(plugin, true));
            p.setCooldown(Material.SNOWBALL, 20 * 4);
        }
        if (e.getEntity() instanceof EnderPearl pearl && "healing_pearl".equals(id)) {
            pearl.setMetadata("cs_healing_pearl", new FixedMetadataValue(plugin, true));
            p.setCooldown(Material.ENDER_PEARL, 20 * 10);
        }
        int hunter = enchantLevel(main, "hunter");
        if (hunter > 0 && (e.getEntity() instanceof AbstractArrow || e.getEntity() instanceof Trident)) {
            e.getEntity().setMetadata("cs_proj_hunter", new FixedMetadataValue(plugin, hunter));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onHit(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Snowball snow && snow.hasMetadata("cs_giant_snowball")) {
            Location l = snow.getLocation();
            l.getWorld().spawnParticle(Particle.SNOWFLAKE, l, 80, 1.2, 0.4, 1.2, 0.05);
            for (Entity en : l.getWorld().getNearbyEntities(BoundingBox.of(l, 2, 2, 2))) {
                if (en instanceof LivingEntity le) le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 5));
            }
        }
        if (e.getEntity() instanceof EnderPearl pearl && pearl.hasMetadata("cs_healing_pearl") && pearl.getShooter() instanceof Player p) {
            Bukkit.getScheduler().runTask(plugin, () -> p.setHealth(p.getMaxHealth()));
        }
        if (e.getHitEntity() instanceof LivingEntity le && e.getEntity().hasMetadata("cs_proj_hunter")) {
            int hunter = e.getEntity().getMetadata("cs_proj_hunter").get(0).asInt();
            le.setGlowing(true);
            le.setMetadata("cs_temp_glow", new FixedMetadataValue(plugin, System.currentTimeMillis() + 4000L));
            le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * (2 + hunter), Math.max(0, hunter - 1)));
            le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * (2 + hunter), 0));
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent e) {
        ThrownPotion potion = e.getPotion();
        ItemStack item = potion.getItem();
        String type = data(item, "cs_potion_type");
        if (type == null) return;

        List<LivingEntity> targets = new ArrayList<>(e.getAffectedEntities());
        if (targets.isEmpty()) {
            for (Entity entity : potion.getLocation().getWorld().getNearbyEntities(potion.getLocation(), 4.0, 2.5, 4.0)) {
                if (entity instanceof LivingEntity le) {
                    targets.add(le);
                }
            }
        }

        for (LivingEntity le : targets) {
            applyCustomPotionEffect(type, le);
        }
    }

    private void applyCustomPotionEffect(String type, LivingEntity le) {
        switch (type) {
            case "storm" -> {
                le.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 20 * 120, 1));
                le.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 20 * 120, 1));
                le.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 120, 1));
                le.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 120, 1));
            }
            case "medic" -> {
                le.setHealth(le.getMaxHealth());
                le.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 120, 1));
            }
            case "burp" -> {
                le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 18, 1));
                le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 6, 0));
                le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 15, 0));
                le.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 20 * 12, 0));
            }
            case "flash" -> {
                le.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 20 * 5, 0));
                le.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 1));
            }
            case "titan" -> {
                le.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 20 * 90, 1));
                le.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 90, 0));
                le.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 90, 1));
            }
            case "venom" -> {
                le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 10, 1));
                le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 10, 0));
                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 6, 0));
            }
            case "shadow" -> {
                le.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 45, 1));
                le.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 25, 0));
                le.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 60, 0));
            }
            case "rage" -> {
                le.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 20 * 60, 2));
                le.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60, 1));
                le.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 20 * 45, 0));
            }
            case "aegis" -> {
                le.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 55, 1));
                le.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 70, 2));
                le.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 90, 0));
            }
            case "plague" -> {
                le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 16, 1));
                le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 7, 0));
                le.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20 * 20, 1));
                le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 12, 0));
            }
            case "frostbite" -> {
                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 12, 1));
                le.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 20 * 10, 0));
                le.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 8, 0));
            }
            case "warp" -> {
                le.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 16, 2));
                le.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 20 * 16, 1));
                le.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 20, 0));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemDamage(PlayerItemDamageEvent e) {
        ItemStack item = e.getItem();
        if (item == null || !item.hasItemMeta()) return;
        if (item.getType() == Material.ELYTRA && hasEnchant(item, "immortality") && random.nextDouble() < 0.88) {
            e.setCancelled(true);
            return;
        }
        if (item.getType() == Material.TRIDENT && hasEnchant(item, "light_handle") && random.nextDouble() < 0.5) {
            e.setCancelled(true);
        }
        int brittle = enchantLevel(item, "brittle");
        if (brittle > 0) {
            e.setDamage(e.getDamage() + brittle);
        }
        int drained = enchantLevel(item, "drained");
        if (drained > 0 && random.nextDouble() < (0.08 * drained)) {
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 20 * (2 + drained), Math.max(0, drained - 1), true, false, true));
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20 * 5, 0, true, false, true));
        }
        int unstable = enchantLevel(item, "unstable");
        if (unstable <= 0) return;
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable dmg)) return;
        int max = item.getType().getMaxDurability();
        if (max <= 0) return;
        int after = dmg.getDamage() + e.getDamage();
        double remaining = (max - after) / (double) max;
        double threshold = unstable >= 2 ? 0.25 : 0.10;
        if (remaining <= threshold && random.nextDouble() < 0.08) {
            item.setAmount(0);
            e.getPlayer().sendMessage(ChatColor.DARK_GRAY + "Нестабильный предмет не выдержал нагрузки.");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onArmorDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        int jinxed = totalEquippedEnchant(p, "jinxed");
        if (jinxed > 0 && random.nextDouble() < (0.10 * jinxed)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * (2 + jinxed), 0, true, false, true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * (2 + jinxed), 0, true, false, true));
        }
        ItemStack chest = p.getInventory().getChestplate();
        int guardian = enchantLevel(chest, "guardian");
        if (guardian <= 0) return;
        double after = p.getHealth() - e.getFinalDamage();
        if (after > 10.0) return;
        if (isOnCustomCooldown(p, "guardian_proc")) return;
        customCooldowns.computeIfAbsent(p.getUniqueId(), k -> new HashMap<>())
                .put("guardian_proc", System.currentTimeMillis() + 8_000L);
        p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20 * 4, Math.max(0, guardian - 1), true, false, true));
        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 3, 0, true, false, true));
    }

    private int totalEquippedEnchant(Player player, String type) {
        int level = 0;
        for (ItemStack piece : player.getInventory().getArmorContents()) {
            level += enchantLevel(piece, type);
        }
        return level;
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemMend(PlayerItemMendEvent e) {
        if (hasEnchant(e.getItem(), "supermending")) {
            e.setRepairAmount(e.getRepairAmount() * 2);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent e) {
        ItemStack item = e.getItem();
        if (item != null && item.getType() == Material.ENCHANTED_GOLDEN_APPLE) {
            e.getPlayer().setCooldown(Material.ENCHANTED_GOLDEN_APPLE, 20 * 22);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player victim && e.getDamager() instanceof LivingEntity attacker) {
            ItemStack chest = victim.getInventory().getChestplate();
            int reflection = enchantLevel(chest, "reflection");
            if (reflection > 0 && !isOnCustomCooldown(victim, "reflection_proc")) {
                customCooldowns.computeIfAbsent(victim.getUniqueId(), k -> new HashMap<>())
                        .put("reflection_proc", System.currentTimeMillis() + 5000L);
                double reflect = Math.max(1.5, e.getFinalDamage() * (0.18 + (0.06 * reflection)));
                attacker.damage(reflect);
                victim.getWorld().spawnParticle(Particle.CRIT, victim.getLocation().add(0, 1, 0), 20, 0.4, 0.6, 0.4, 0.02);
            }
        }

        if (!(e.getDamager() instanceof Player p)) return;
        ItemStack weapon = p.getInventory().getItemInMainHand();
        if (!(isSword(weapon.getType()) || isAxe(weapon.getType())) || !(e.getEntity() instanceof LivingEntity le)) return;
        int poison = enchantLevel(weapon, "poison");
        int wither = enchantLevel(weapon, "wither");
        int lifesteal = enchantLevel(weapon, "lifesteal");
        int frost = enchantLevel(weapon, "frost");
        int berserk = enchantLevel(weapon, "berserk");
        int executioner = enchantLevel(weapon, "executioner");
        int armorbreak = enchantLevel(weapon, "armorbreak");
        int drained = enchantLevel(weapon, "drained");
        int jinxed = enchantLevel(weapon, "jinxed");
        if (drained > 0 && random.nextDouble() < (0.10 * drained)) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * (2 + drained), 0, true, false, true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 20 * 5, 0, true, false, true));
        }
        if (jinxed > 0 && random.nextDouble() < 0.14) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20 * 4, 0, true, false, true));
            p.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 20 * 3, 0, true, false, true));
        }
        if (poison > 0) le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 4, poison - 1));
        if (wither > 0) le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 4, wither - 1));
        if (frost > 0) le.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 3, Math.max(0, frost - 1)));
        if (berserk > 0 && p.getHealth() <= (p.getMaxHealth() / 2.0)) {
            e.setDamage(e.getDamage() + (1.5 * berserk));
        }
        if (executioner > 0 && (le.getHealth() - e.getFinalDamage()) <= (le.getMaxHealth() * 0.40)) {
            e.setDamage(e.getDamage() + (1.75 * executioner));
        }
        if (armorbreak > 0 && e.getEntity() instanceof Player target) {
            damageArmor(target, armorbreak);
        }
        if (lifesteal > 0) {
            double heal = Math.min(p.getMaxHealth(), p.getHealth() + lifesteal);
            p.setHealth(heal);
        }
    }

    private void damageArmor(Player target, int amount) {
        ItemStack[] armor = target.getInventory().getArmorContents();
        boolean changed = false;
        for (ItemStack piece : armor) {
            if (piece == null || piece.getType().isAir() || !(piece.getItemMeta() instanceof Damageable dmg)) continue;
            dmg.setDamage(dmg.getDamage() + amount);
            piece.setItemMeta((ItemMeta) dmg);
            changed = true;
        }
        if (changed) {
            target.getWorld().spawnParticle(Particle.CRIT, target.getLocation().add(0, 1, 0), 8, 0.35, 0.45, 0.35, 0.01);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        if (killer == null) return;
        ItemStack weapon = killer.getInventory().getItemInMainHand();
        int bloodrage = enchantLevel(weapon, "bloodrage");
        if (bloodrage <= 0) return;
        killer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * (4 + bloodrage), Math.max(0, bloodrage - 1), true, false, true));
        killer.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 4, 0, true, false, true));
    }

    private void startAntiGlowTicker() {
        new BukkitRunnable() {
            @Override public void run() {
                long now = System.currentTimeMillis();
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        if (entity.hasMetadata("cs_temp_glow")) {
                            long until = entity.getMetadata("cs_temp_glow").get(0).asLong();
                            if (until < now) {
                                entity.setGlowing(false);
                                entity.removeMetadata("cs_temp_glow", plugin);
                            }
                        }
                    }
                    for (Player p : world.getPlayers()) {
                        ItemStack chest = p.getInventory().getChestplate();
                        if (hasEnchant(chest, "antichams")) {
                            p.removePotionEffect(PotionEffectType.GLOWING);
                            p.setGlowing(false);
                        }
                        ItemStack boots = p.getInventory().getBoots();
                        if (hasEnchant(boots, "lavawalker")) {
                            Location l = p.getLocation().subtract(0, 1, 0);
                            for (int x = -1; x <= 1; x++) for (int z = -1; z <= 1; z++) {
                                Block b = l.clone().add(x, 0, z).getBlock();
                                if (b.getType() == Material.LAVA) b.setType(Material.OBSIDIAN);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 10L, 10L);
    }

    private Inventory createProcessorInventory() {
        Inventory inv = Bukkit.createInventory(null, 9, SEC.deserialize("§5§l⚙ Процессор-убийца"));

        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.displayName(SEC.deserialize("§8 "));
        filler.setItemMeta(fillerMeta);

        for (int i = 0; i < inv.getSize(); i++) {
            inv.setItem(i, filler);
        }

        inv.setItem(4, defaultProcessorSword());

        ItemStack info = new ItemStack(Material.PAPER);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.displayName(SEC.deserialize("§d§lИнформация"));
        infoMeta.lore(List.of(
                SEC.deserialize("§7В слот по центру можно положить меч."),
                SEC.deserialize("§7Процессор атакует только §cмобов§7."),
                SEC.deserialize("§7Радиус работы: §f16 блоков"),
                SEC.deserialize("§7Урон зависит от меча и чар."),
                SEC.deserialize("§7Меч тратит прочность при ударах.")
        ));
        info.setItemMeta(infoMeta);

        inv.setItem(0, info);
        inv.setItem(8, info.clone());
        return inv;
    }

    private ItemStack defaultProcessorSword() {
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.displayName(SEC.deserialize("§fЖелезный меч процессора"));
        meta.lore(List.of(
                SEC.deserialize("§7Стандартный меч для процессора"),
                SEC.deserialize("§7Прочность III")
        ));
        sword.setItemMeta(meta);
        sword.addUnsafeEnchantment(Enchantment.UNBREAKING, 3);
        return sword;
    }

    private double getSwordDamage(ItemStack sword) {
        if (sword == null || sword.getType().isAir() || !isSword(sword.getType())) return 0.0;

        double damage = switch (sword.getType()) {
            case WOODEN_SWORD, GOLDEN_SWORD -> 4.0;
            case STONE_SWORD -> 5.0;
            case IRON_SWORD -> 6.0;
            case DIAMOND_SWORD -> 7.0;
            case NETHERITE_SWORD -> 8.0;
            default -> 4.0;
        };

        int sharp = sword.getEnchantmentLevel(Enchantment.SHARPNESS);
        if (sharp > 0) damage += 0.5 + (sharp * 0.5);

        int customSharp = enchantLevel(sword, "sharpness");
        if (customSharp > 5) damage += (customSharp - 5) * 0.5;

        return damage;
    }

    private void placeProcessor(Player p) {
        Block b = p.getTargetBlockExact(5);
        if (b == null) return;

        Block place = b.getRelative(BlockFace.UP);
        if (place.getType() != Material.AIR) return;

        place.setType(Material.LODESTONE, false);
        Inventory inv = createProcessorInventory();

        ArmorStand marker = place.getWorld().spawn(place.getLocation().add(0.5, 0, 0.5), ArmorStand.class, as -> {
            as.setVisible(false);
            as.setMarker(true);
            as.setGravity(false);
            as.setInvulnerable(true);
            as.customName(SEC.deserialize("§5§l⚙ Процессор-убийца"));
            as.setCustomNameVisible(true);
        });

        processors.put(place.getLocation(), marker);
        processorInv.put(place.getLocation(), inv);
    }

    private void startProcessorTicker() {
        new BukkitRunnable() {
            @Override public void run() {
                Iterator<Map.Entry<Location, ArmorStand>> it = processors.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Location, ArmorStand> en = it.next();
                    Location loc = en.getKey();

                    if (loc.getBlock().getType() != Material.LODESTONE) {
                        en.getValue().remove();
                        processorInv.remove(loc);
                        it.remove();
                        continue;
                    }

                    Inventory inv = processorInv.get(loc);
                    if (inv == null) continue;

                    ItemStack sword = inv.getItem(4);
                    if (sword == null || sword.getType().isAir() || !isSword(sword.getType())) continue;

                    double damage = getSwordDamage(sword);
                    int hits = 0;
                    for (Entity entity : loc.getWorld().getNearbyEntities(loc.clone().add(0.5, 0.5, 0.5), 16, 16, 16)) {
                        if (!(entity instanceof Monster m)) continue;
                        m.damage(damage);
                        hits++;
                    }
                    if (hits > 0) {
                        ItemMeta meta = sword.getItemMeta();
                        if (meta instanceof Damageable dmg) {
                            dmg.setDamage(dmg.getDamage() + hits);
                            sword.setItemMeta(meta);
                            if (dmg.getDamage() >= sword.getType().getMaxDurability()) inv.setItem(4, null);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onBlockUse(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getClickedBlock() == null) return;

        Location loc = e.getClickedBlock().getLocation();
        if (!processors.containsKey(loc)) return;

        e.setCancelled(true);
        e.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
        e.setUseItemInHand(org.bukkit.event.Event.Result.DENY);

        Bukkit.getScheduler().runTask(plugin, () -> {
            Inventory inv = processorInv.get(loc);
            if (inv != null) e.getPlayer().openInventory(inv);
        });
    }

    @EventHandler public void onProcessorClose(InventoryCloseEvent e) {}

    @EventHandler
    public void onProcessorClick(InventoryClickEvent e) {
        String title = ChatColor.stripColor(e.getView().getTitle());
        if (!"⚙ Процессор-убийца".equals(title) && !"Процессор-убийца".equals(title)) return;

        if (e.getClickedInventory() == null) return;
        Inventory top = e.getView().getTopInventory();
        Inventory clicked = e.getClickedInventory();

        if (clicked.equals(top)) {
            if (e.getRawSlot() != 4) {
                e.setCancelled(true);
                return;
            }
            if (e.getClick() == ClickType.NUMBER_KEY) {
                ItemStack hotbar = e.getWhoClicked().getInventory().getItem(e.getHotbarButton());
                if (hotbar != null && !hotbar.getType().isAir() && !isSword(hotbar.getType())) {
                    e.setCancelled(true);
                    return;
                }
            }
            ItemStack cursor = e.getCursor();
            if (cursor != null && !cursor.getType().isAir() && !isSword(cursor.getType())) {
                e.setCancelled(true);
            }
            return;
        }

        if (clicked.equals(e.getView().getBottomInventory()) && e.isShiftClick()) {
            ItemStack current = e.getCurrentItem();
            if (current == null || current.getType().isAir() || !isSword(current.getType())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onProcessorDrag(InventoryDragEvent e) {
        String title = ChatColor.stripColor(e.getView().getTitle());
        if (!"⚙ Процессор-убийца".equals(title) && !"Процессор-убийца".equals(title)) return;

        for (int slot : e.getRawSlots()) {
            if (slot != 4) {
                e.setCancelled(true);
                return;
            }
        }

        ItemStack oldCursor = e.getOldCursor();
        if (oldCursor != null && !oldCursor.getType().isAir() && !isSword(oldCursor.getType())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent e) {
        e.getDrops().removeIf(it -> data(it, "charm_type") != null);
    }
}
