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

public class ShopGUI implements Listener {
    private static final int[] GRID = {10, 12, 14, 16, 19, 21, 23, 25, 28, 30, 32, 34};

    private final Plugin plugin;
    private final CoinExchangerHook coins;
    private final String mainTitle;
    private final String confirmTitle;
    private final Map<UUID, ShopCategory> openedCategory = new HashMap<>();
    private final Map<UUID, Integer> openedPage = new HashMap<>();
    private final Map<UUID, ShopItem> pending = new HashMap<>();

    public ShopGUI(Plugin plugin, CoinExchangerHook coins) {
        this.plugin = plugin;
        this.coins = coins;
        this.mainTitle = color(plugin.getConfig().getString("shop.title", "&6&l[✦] МАГАЗИН KOCMOCSMP [✦]"));
        this.confirmTitle = mainTitle + color(" &8» &aПодтверждение");
    }

    public void openMain(Player p) {
        Inventory inv = Bukkit.createInventory(null, 54, mainTitle);
        fill(inv, Material.BLACK_STAINED_GLASS_PANE, "&0");
        inv.setItem(10, icon(Material.DIAMOND_SWORD, "&cОружие"));
        inv.setItem(12, icon(Material.DIAMOND_PICKAXE, "&bИнструменты"));
        inv.setItem(14, icon(Material.NETHERITE_CHESTPLATE, "&6Броня"));
        inv.setItem(16, icon(Material.DIAMOND_ORE, "&2Приват-руды"));
        inv.setItem(20, icon(Material.TOTEM_OF_UNDYING, "&dКулоны"));
        inv.setItem(22, icon(Material.FURNACE, "&6Печки"));
        inv.setItem(24, icon(Material.ENCHANTED_BOOK, "&5Кастомные книги"));
        inv.setItem(30, icon(Material.ENCHANTED_GOLDEN_APPLE, "&aКастом предметы"));
        inv.setItem(32, icon(Material.SPLASH_POTION, "&3Кастом зелья"));
        inv.setItem(34, icon(Material.ELYTRA, "&6Редкие предметы"));
        inv.setItem(40, icon(Material.TNT, "&4Рейд и база"));
        inv.setItem(49, icon(Material.BARRIER, "&c&l✖ Закрыть"));
        placeBalance(inv, p);
        openedCategory.remove(p.getUniqueId());
        openedPage.remove(p.getUniqueId());
        p.openInventory(inv);
    }

    private void openCategory(Player p, ShopCategory cat, int page) {
        List<ShopItem> items = itemsFor(cat);
        int maxPage = Math.max(0, (items.size() - 1) / GRID.length);
        int actualPage = Math.max(0, Math.min(page, maxPage));
        String title = mainTitle + color(" &8» ") + categoryName(cat);
        Inventory inv = Bukkit.createInventory(null, 54, title);
        fill(inv, Material.GRAY_STAINED_GLASS_PANE, "&0");
        inv.setItem(45, icon(Material.ARROW, "&a&l← Назад"));
        if (actualPage > 0) inv.setItem(47, icon(Material.SPECTRAL_ARROW, "&e&l← Страница " + actualPage));
        if (actualPage < maxPage) inv.setItem(51, icon(Material.SPECTRAL_ARROW, "&e&lСтраница " + (actualPage + 2) + " →"));
        inv.setItem(49, icon(Material.PAPER, "&f&lСтраница &e" + (actualPage + 1) + "&7/&e" + (maxPage + 1)));
        placeBalance(inv, p);
        int from = actualPage * GRID.length;
        int to = Math.min(items.size(), from + GRID.length);
        for (int i = from; i < to; i++) inv.setItem(GRID[i - from], priced(items.get(i).item(), items.get(i).price()));
        openedCategory.put(p.getUniqueId(), cat);
        openedPage.put(p.getUniqueId(), actualPage);
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

    private String categoryName(ShopCategory cat) {
        return switch (cat) {
            case WEAPONS -> color("&cОружие");
            case TOOLS -> color("&bИнструменты");
            case ARMOR -> color("&6Броня");
            case PRIVATE_ORES -> color("&2Приват-руды");
            case CHARMS -> color("&dКулоны");
            case FURNACES -> color("&6Печки");
            case CUSTOM_BOOKS -> color("&5Кастомные книги");
            case CUSTOM_ITEMS -> color("&aКастом предметы");
            case CUSTOM_POTIONS -> color("&3Кастом зелья");
            case RARE_ITEMS -> color("&6Редкие предметы");
            case BASE_UTILS -> color("&4Рейд и база");
        };
    }

    private List<ShopItem> itemsFor(ShopCategory cat) {
        return switch (cat) {
            case WEAPONS -> List.of(
                    new ShopItem("sword_3", price("sword_3", 150), ItemFactory.sword3(plugin)),
                    new ShopItem("sword_2", price("sword_2", 220), ItemFactory.sword2(plugin)),
                    new ShopItem("sword_1", price("sword_1", 620), ItemFactory.sword1(plugin)),
                    new ShopItem("sword_vampire", price("sword_vampire", 760), ItemFactory.swordVampire(plugin)),
                    new ShopItem("sword_frost", price("sword_frost", 740), ItemFactory.swordFrost(plugin)),
                    new ShopItem("axe_berserk", price("axe_berserk", 680), ItemFactory.axeBerserk(plugin)),
                    new ShopItem("bow_storm", price("bow_storm", 540), ItemFactory.bowStorm(plugin)),
                    new ShopItem("crossbow_multishot", price("crossbow_multishot", 320), ItemFactory.crossbowMultishot(plugin)),
                    new ShopItem("crossbow_phantom", price("crossbow_phantom", 480), ItemFactory.crossbowPhantom(plugin)),
                    new ShopItem("trident_poseidon", price("trident_poseidon", 720), ItemFactory.tridentPoseidon(plugin)),
                    new ShopItem("sword_executioner", price("sword_executioner", 980), ItemFactory.swordExecutioner(plugin)),
                    new ShopItem("axe_crusher", price("axe_crusher", 910), ItemFactory.axeCrusher(plugin)),
                    new ShopItem("bow_hunter", price("bow_hunter", 830), ItemFactory.bowHunter(plugin)),
                    new ShopItem("trident_hunter", price("trident_hunter", 900), ItemFactory.tridentHunter(plugin)),
                    new ShopItem("stick_kb", price("stick_kb", 150), ItemFactory.stickKb(plugin))
            );
            case TOOLS -> List.of(
                    new ShopItem("pickaxe_3", price("pickaxe_3", 220), ItemFactory.pickaxe3(plugin)),
                    new ShopItem("pickaxe_2", price("pickaxe_2", 300), ItemFactory.pickaxe2(plugin)),
                    new ShopItem("pickaxe_1", price("pickaxe_1", 580), ItemFactory.pickaxe1(plugin)),
                    new ShopItem("pickaxe_drill", price("pickaxe_drill", 790), ItemFactory.pickaxeDrill(plugin)),
                    new ShopItem("pickaxe_molten", price("pickaxe_molten", 690), ItemFactory.pickaxeMolten(plugin)),
                    new ShopItem("pickaxe_silk_ultra", price("pickaxe_silk_ultra", 430), ItemFactory.pickaxeSilkUltra(plugin)),
                    new ShopItem("axe_lumber", price("axe_lumber", 560), ItemFactory.axeLumber(plugin)),
                    new ShopItem("shovel_quake", price("shovel_quake", 510), ItemFactory.shovelQuake(plugin)),
                    new ShopItem("shovel_archaeologist", price("shovel_archaeologist", 300), ItemFactory.shovelArchaeologist(plugin)),
                    new ShopItem("shears_silk", price("shears_silk", 130), ItemFactory.shearsSilk(plugin)),
                    new ShopItem("hoe_1", price("hoe_1", 140), ItemFactory.hoe1(plugin)),
                    new ShopItem("hoe_2", price("hoe_2", 230), ItemFactory.hoe2(plugin)),
                    new ShopItem("hoe_3", price("hoe_3", 360), ItemFactory.hoe3(plugin))
            );
            case ARMOR -> List.of(
                    new ShopItem("helmet_shadow", price("helmet_shadow", 380), ItemFactory.helmetShadow(plugin)),
                    new ShopItem("chestplate_guardian", price("chestplate_guardian", 430), ItemFactory.chestplateGuardian(plugin)),
                    new ShopItem("leggings_shadow", price("leggings_shadow", 380), ItemFactory.leggingsShadow(plugin)),
                    new ShopItem("boots_volcano", price("boots_volcano", 390), ItemFactory.bootsVolcano(plugin)),
                    new ShopItem("helmet_gladiator", price("helmet_gladiator", 520), ItemFactory.helmetGladiator(plugin)),
                    new ShopItem("chestplate_gladiator", price("chestplate_gladiator", 620), ItemFactory.chestplateGladiator(plugin)),
                    new ShopItem("leggings_gladiator", price("leggings_gladiator", 560), ItemFactory.leggingsGladiator(plugin)),
                    new ShopItem("boots_gladiator", price("boots_gladiator", 500), ItemFactory.bootsGladiator(plugin)),
                    new ShopItem("chestplate_mirror", price("chestplate_mirror", 860), ItemFactory.chestplateMirror(plugin)),
                    new ShopItem("elytra_immortal", price("elytra_immortal", 850), ItemFactory.elytraImmortal(plugin))
            );
            case PRIVATE_ORES -> List.of(
                    new ShopItem("private_ore_coal", price("private_ore_coal", 650), ItemFactory.privateOreCoal(plugin)),
                    new ShopItem("private_ore_iron", price("private_ore_iron", 950), ItemFactory.privateOreIron(plugin)),
                    new ShopItem("private_ore_gold", price("private_ore_gold", 1300), ItemFactory.privateOreGold(plugin)),
                    new ShopItem("private_ore_diamond", price("private_ore_diamond", 1800), ItemFactory.privateOreDiamond(plugin)),
                    new ShopItem("private_ore_emerald", price("private_ore_emerald", 2600), ItemFactory.privateOreEmerald(plugin))
            );
            case CHARMS -> List.of(
                    new ShopItem("charm_speed", price("charm_speed", 300), ItemFactory.charm(plugin, "speed", "&aКулон скорости", "&7Скорость I")),
                    new ShopItem("charm_jump", price("charm_jump", 300), ItemFactory.charm(plugin, "jump", "&eКулон прыгучести", "&7Прыгучесть I")),
                    new ShopItem("charm_haste", price("charm_haste", 360), ItemFactory.charm(plugin, "haste", "&6Кулон спешки", "&7Спешка I")),
                    new ShopItem("charm_water", price("charm_water", 360), ItemFactory.charm(plugin, "water", "&bКулон водолаза", "&7Подводное дыхание")),
                    new ShopItem("charm_miner", price("charm_miner", 330), ItemFactory.charm(plugin, "miner", "&6Кулон шахтёра", "&7Спешка II", "&7Ночное зрение")),
                    new ShopItem("charm_runner", price("charm_runner", 370), ItemFactory.charm(plugin, "runner", "&eКулон бегуна", "&7Скорость II", "&7Прыгучесть II")),
                    new ShopItem("charm_healer", price("charm_healer", 450), ItemFactory.charm(plugin, "healer", "&dКулон целителя", "&7Регенерация I", "&7+2❤")),
                    new ShopItem("charm_tank", price("charm_tank", 520), ItemFactory.charm(plugin, "tank", "&fКулон титана", "&7Сопротивление I", "&7+2❤")),
                    new ShopItem("charm_blaze", price("charm_blaze", 470), ItemFactory.charm(plugin, "blaze", "&6Кулон пламени", "&7Огнестойкость", "&7Сила I")),
                    new ShopItem("charm_ocean", price("charm_ocean", 430), ItemFactory.charm(plugin, "ocean", "&bКулон прилива", "&7Подводное дыхание", "&7Грация дельфина")),
                    new ShopItem("charm_ultra", price("charm_ultra", 700), ItemFactory.charm(plugin, "ultra", "&dКомбо-кулон", "&7Скорость II", "&7Спешка II", "&7Прыгучесть II"))
            );
            case FURNACES -> List.of(
                    new ShopItem("furnace_x2", price("furnace_x2", 260), ItemFactory.furnaceX2(plugin)),
                    new ShopItem("furnace_x3", price("furnace_x3", 380), ItemFactory.furnaceX3(plugin)),
                    new ShopItem("furnace_x4", price("furnace_x4", 820), ItemFactory.furnaceX4(plugin)),
                    new ShopItem("furnace_x5", price("furnace_x5", 1500), ItemFactory.furnaceX5(plugin))
            );
            case CUSTOM_BOOKS -> List.of(
                    new ShopItem("book_bulldozer", price("book_bulldozer", 300), ItemFactory.customBook(plugin, "bulldozer", 1)),
                    new ShopItem("book_treecapitator", price("book_treecapitator", 320), ItemFactory.customBook(plugin, "treecapitator", 1)),
                    new ShopItem("book_poison_1", price("book_poison_1", 310), ItemFactory.customBook(plugin, "poison", 1)),
                    new ShopItem("book_poison_2", price("book_poison_2", 380), ItemFactory.customBook(plugin, "poison", 2)),
                    new ShopItem("book_wither_1", price("book_wither_1", 310), ItemFactory.customBook(plugin, "wither", 1)),
                    new ShopItem("book_wither_2", price("book_wither_2", 380), ItemFactory.customBook(plugin, "wither", 2)),
                    new ShopItem("book_lifesteal_1", price("book_lifesteal_1", 340), ItemFactory.customBook(plugin, "lifesteal", 1)),
                    new ShopItem("book_lifesteal_2", price("book_lifesteal_2", 430), ItemFactory.customBook(plugin, "lifesteal", 2)),
                    new ShopItem("book_frost_1", price("book_frost_1", 330), ItemFactory.customBook(plugin, "frost", 1)),
                    new ShopItem("book_frost_2", price("book_frost_2", 420), ItemFactory.customBook(plugin, "frost", 2)),
                    new ShopItem("book_berserk_1", price("book_berserk_1", 360), ItemFactory.customBook(plugin, "berserk", 1)),
                    new ShopItem("book_berserk_2", price("book_berserk_2", 450), ItemFactory.customBook(plugin, "berserk", 2)),
                    new ShopItem("book_guardian_1", price("book_guardian_1", 420), ItemFactory.customBook(plugin, "guardian", 1)),
                    new ShopItem("book_executioner_1", price("book_executioner_1", 470), ItemFactory.customBook(plugin, "executioner", 1)),
                    new ShopItem("book_executioner_2", price("book_executioner_2", 580), ItemFactory.customBook(plugin, "executioner", 2)),
                    new ShopItem("book_armorbreak_1", price("book_armorbreak_1", 450), ItemFactory.customBook(plugin, "armorbreak", 1)),
                    new ShopItem("book_armorbreak_2", price("book_armorbreak_2", 560), ItemFactory.customBook(plugin, "armorbreak", 2)),
                    new ShopItem("book_bloodrage_1", price("book_bloodrage_1", 420), ItemFactory.customBook(plugin, "bloodrage", 1)),
                    new ShopItem("book_bloodrage_2", price("book_bloodrage_2", 520), ItemFactory.customBook(plugin, "bloodrage", 2)),
                    new ShopItem("book_hunter_1", price("book_hunter_1", 430), ItemFactory.customBook(plugin, "hunter", 1)),
                    new ShopItem("book_hunter_2", price("book_hunter_2", 530), ItemFactory.customBook(plugin, "hunter", 2)),
                    new ShopItem("book_reflection_1", price("book_reflection_1", 480), ItemFactory.customBook(plugin, "reflection", 1)),
                    new ShopItem("book_eff_6", price("book_eff_6", 330), ItemFactory.customBook(plugin, "efficiency", 6)),
                    new ShopItem("book_eff_7", price("book_eff_7", 380), ItemFactory.customBook(plugin, "efficiency", 7)),
                    new ShopItem("book_eff_8", price("book_eff_8", 430), ItemFactory.customBook(plugin, "efficiency", 8)),
                    new ShopItem("book_eff_9", price("book_eff_9", 490), ItemFactory.customBook(plugin, "efficiency", 9)),
                    new ShopItem("book_eff_10", price("book_eff_10", 560), ItemFactory.customBook(plugin, "efficiency", 10)),
                    new ShopItem("book_sharp_6", price("book_sharp_6", 340), ItemFactory.customBook(plugin, "sharpness", 6)),
                    new ShopItem("book_sharp_7", price("book_sharp_7", 390), ItemFactory.customBook(plugin, "sharpness", 7)),
                    new ShopItem("book_sharp_8", price("book_sharp_8", 450), ItemFactory.customBook(plugin, "sharpness", 8)),
                    new ShopItem("book_sharp_9", price("book_sharp_9", 520), ItemFactory.customBook(plugin, "sharpness", 9)),
                    new ShopItem("book_sharp_10", price("book_sharp_10", 590), ItemFactory.customBook(plugin, "sharpness", 10)),
                    new ShopItem("book_fortune_5", price("book_fortune_5", 470), ItemFactory.customBook(plugin, "fortune", 5)),
                    new ShopItem("book_autosmelt_1", price("book_autosmelt_1", 360), ItemFactory.customBook(plugin, "autosmelt", 1)),
                    new ShopItem("book_lavawalker", price("book_lavawalker", 320), ItemFactory.customBook(plugin, "lavawalker", 1)),
                    new ShopItem("book_antichams", price("book_antichams", 560), ItemFactory.customBook(plugin, "antichams", 1)),
                    new ShopItem("book_magnet", price("book_magnet", 390), ItemFactory.customBook(plugin, "magnet", 1)),
                    new ShopItem("book_supermending", price("book_supermending", 640), ItemFactory.customBook(plugin, "supermending", 1)),
                    new ShopItem("book_greenifier", price("book_greenifier", 280), ItemFactory.customBook(plugin, "greenifier", 1)),
                    new ShopItem("book_immortality", price("book_immortality", 520), ItemFactory.customBook(plugin, "immortality", 1)),
                    new ShopItem("book_light_handle", price("book_light_handle", 360), ItemFactory.customBook(plugin, "light_handle", 1))
            );
            case CUSTOM_ITEMS -> List.of(
                    new ShopItem("reveal_dust", price("reveal_dust", 300), ItemFactory.revealDust(plugin)),
                    new ShopItem("trap_box", price("trap_box", 430), ItemFactory.trapBox(plugin)),
                    new ShopItem("plate_wall", price("plate_wall", 390), ItemFactory.plateWall(plugin)),
                    new ShopItem("giant_snowball", price("giant_snowball", 340), ItemFactory.giantSnowball(plugin)),
                    new ShopItem("holy_aura", price("holy_aura", 320), ItemFactory.holyAura(plugin)),
                    new ShopItem("killer_cpu", price("killer_cpu", 690), ItemFactory.killerCpu(plugin)),
                    new ShopItem("healing_pearl", price("healing_pearl", 520), ItemFactory.healingPearl(plugin)),
                    new ShopItem("disorientation", price("disorientation", 470), ItemFactory.disorientation(plugin)),
                    new ShopItem("fire_tornado", price("fire_tornado", 430), ItemFactory.fireTornado(plugin)),
                    new ShopItem("water_cube", price("water_cube", 380), ItemFactory.waterCube(plugin)),
                    new ShopItem("unreal_aura", price("unreal_aura", 480), ItemFactory.unrealAura(plugin)),
                    new ShopItem("blood_totem", price("blood_totem", 560), ItemFactory.bloodTotem(plugin)),
                    new ShopItem("smoke_bomb", price("smoke_bomb", 390), ItemFactory.smokeBomb(plugin)),
                    new ShopItem("purge_stone", price("purge_stone", 520), ItemFactory.purgeStone(plugin)),
                    new ShopItem("gravity_orb", price("gravity_orb", 470), ItemFactory.gravityOrb(plugin)),
                    new ShopItem("god_touch", price("god_touch", 240), ItemFactory.godTouch(plugin))
            );
            case CUSTOM_POTIONS -> List.of(
                    new ShopItem("storm_potion", price("storm_potion", 320), ItemFactory.customPotion(plugin, "storm", Material.SPLASH_POTION)),
                    new ShopItem("medic_potion", price("medic_potion", 280), ItemFactory.customPotion(plugin, "medic", Material.SPLASH_POTION)),
                    new ShopItem("burp_potion", price("burp_potion", 300), ItemFactory.customPotion(plugin, "burp", Material.SPLASH_POTION)),
                    new ShopItem("flash_potion", price("flash_potion", 290), ItemFactory.customPotion(plugin, "flash", Material.SPLASH_POTION)),
                    new ShopItem("titan_potion", price("titan_potion", 360), ItemFactory.customPotion(plugin, "titan", Material.SPLASH_POTION)),
                    new ShopItem("venom_potion", price("venom_potion", 310), ItemFactory.customPotion(plugin, "venom", Material.SPLASH_POTION)),
                    new ShopItem("shadow_potion", price("shadow_potion", 320), ItemFactory.customPotion(plugin, "shadow", Material.SPLASH_POTION)),
                    new ShopItem("rage_potion", price("rage_potion", 420), ItemFactory.customPotion(plugin, "rage", Material.SPLASH_POTION)),
                    new ShopItem("aegis_potion", price("aegis_potion", 430), ItemFactory.customPotion(plugin, "aegis", Material.SPLASH_POTION)),
                    new ShopItem("plague_potion", price("plague_potion", 410), ItemFactory.customPotion(plugin, "plague", Material.SPLASH_POTION)),
                    new ShopItem("frostbite_potion", price("frostbite_potion", 390), ItemFactory.customPotion(plugin, "frostbite", Material.SPLASH_POTION)),
                    new ShopItem("warp_potion", price("warp_potion", 440), ItemFactory.customPotion(plugin, "warp", Material.SPLASH_POTION))
            );
            case RARE_ITEMS -> List.of(
                    new ShopItem("elytra", price("elytra", 900), ItemFactory.rareSingle(plugin, Material.ELYTRA, "&6Элитры", 1)),
                    new ShopItem("ench_gapple", price("ench_gapple", 880), ItemFactory.rareSingle(plugin, Material.ENCHANTED_GOLDEN_APPLE, "&5Чареные яблоки", 4)),
                    new ShopItem("golden_apple", price("golden_apple", 360), ItemFactory.rareSingle(plugin, Material.GOLDEN_APPLE, "&6Золотые яблоки", 8)),
                    new ShopItem("empty_spawner", price("empty_spawner", 520), ItemFactory.emptySpawner(plugin)),
                    new ShopItem("totem_single", price("totem_single", 280), ItemFactory.totemSingle(plugin)),
                    new ShopItem("shulker_single", price("shulker_single", 420), ItemFactory.rareSingle(plugin, Material.SHULKER_BOX, "&dШалкер", 1)),
                    new ShopItem("ancient_debris_8", price("ancient_debris_8", 390), ItemFactory.rareSingle(plugin, Material.ANCIENT_DEBRIS, "&8Древний обломок", 8)),
                    new ShopItem("netherite_ingot_4", price("netherite_ingot_4", 520), ItemFactory.rareSingle(plugin, Material.NETHERITE_INGOT, "&8Незеритовые слитки", 4)),
                    new ShopItem("beacon_single", price("beacon_single", 740), ItemFactory.rareSingle(plugin, Material.BEACON, "&bМаяк", 1)),
                    new ShopItem("spawn_villager", price("spawn_villager", 320), ItemFactory.spawnEgg(plugin, Material.VILLAGER_SPAWN_EGG, "&aЯйцо жителя")),
                    new ShopItem("spawn_creeper", price("spawn_creeper", 320), ItemFactory.spawnEgg(plugin, Material.CREEPER_SPAWN_EGG, "&2Яйцо крипера")),
                    new ShopItem("spawn_charged_creeper", price("spawn_charged_creeper", 860), ItemFactory.chargedCreeperEgg(plugin)),
                    new ShopItem("spawn_skeleton", price("spawn_skeleton", 320), ItemFactory.spawnEgg(plugin, Material.SKELETON_SPAWN_EGG, "&7Яйцо скелета")),
                    new ShopItem("spawn_sheep", price("spawn_sheep", 260), ItemFactory.spawnEgg(plugin, Material.SHEEP_SPAWN_EGG, "&fЯйцо овцы")),
                    new ShopItem("name_tag_single", price("name_tag_single", 90), ItemFactory.nameTagSingle(plugin)),
                    new ShopItem("silk_book", price("silk_book", 260), ItemFactory.silkBook(plugin)),
                    new ShopItem("fireworks_16", price("fireworks_16", 130), ItemFactory.fireworks16(plugin))
            );
            case BASE_UTILS -> List.of(
                    new ShopItem("obsidian_bundle", price("obsidian_bundle", 180), ItemFactory.obsidianBundle(plugin)),
                    new ShopItem("crying_obsidian_bundle", price("crying_obsidian_bundle", 260), ItemFactory.cryingObsidianBundle(plugin)),
                    new ShopItem("end_crystal_pack", price("end_crystal_pack", 460), ItemFactory.endCrystalPack(plugin)),
                    new ShopItem("respawn_anchor_pack", price("respawn_anchor_pack", 380), ItemFactory.respawnAnchorPack(plugin)),
                    new ShopItem("cobweb_pack", price("cobweb_pack", 220), ItemFactory.cobwebPack(plugin)),
                    new ShopItem("tnt_pack", price("tnt_pack", 260), ItemFactory.tntPack(plugin)),
                    new ShopItem("black_tnt", price("black_tnt", 1280), ItemFactory.blackTntPack(plugin))
            );
        };
    }

    private int price(String id, int def) {
        return plugin.getConfig().getInt("prices." + id, def);
    }

    private ItemStack priced(ItemStack base, int price) {
        ItemStack it = base.clone();
        ItemMeta m = it.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (m.hasLore() && m.lore() != null) m.lore().forEach(c -> lore.add(LegacyComponentSerializer.legacySection().serialize(c)));
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

    private void placeBalance(Inventory inv, Player p) {
        inv.setItem(4, icon(Material.GOLD_NUGGET, "&e&l⛃ Баланс: &6" + coins.get(p.getUniqueId())));
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
            switch (e.getRawSlot()) {
                case 10 -> openCategory(p, ShopCategory.WEAPONS, 0);
                case 12 -> openCategory(p, ShopCategory.TOOLS, 0);
                case 14 -> openCategory(p, ShopCategory.ARMOR, 0);
                case 16 -> openCategory(p, ShopCategory.PRIVATE_ORES, 0);
                case 20 -> openCategory(p, ShopCategory.CHARMS, 0);
                case 22 -> openCategory(p, ShopCategory.FURNACES, 0);
                case 24 -> openCategory(p, ShopCategory.CUSTOM_BOOKS, 0);
                case 30 -> openCategory(p, ShopCategory.CUSTOM_ITEMS, 0);
                case 32 -> openCategory(p, ShopCategory.CUSTOM_POTIONS, 0);
                case 34 -> openCategory(p, ShopCategory.RARE_ITEMS, 0);
                case 40 -> openCategory(p, ShopCategory.BASE_UTILS, 0);
                case 49 -> p.closeInventory();
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
                ShopCategory cat = openedCategory.get(p.getUniqueId());
                int page = openedPage.getOrDefault(p.getUniqueId(), 0);
                if (cat == null) openMain(p); else openCategory(p, cat, page);
            } else if (e.getRawSlot() == 15) {
                ShopCategory cat = openedCategory.get(p.getUniqueId());
                int page = openedPage.getOrDefault(p.getUniqueId(), 0);
                if (cat == null) openMain(p); else openCategory(p, cat, page);
            }
            return;
        }

        ShopCategory cat = openedCategory.get(p.getUniqueId());
        int page = openedPage.getOrDefault(p.getUniqueId(), 0);
        if (e.getRawSlot() == 45) {
            openMain(p);
            return;
        }
        if (e.getRawSlot() == 47) {
            openCategory(p, cat, page - 1);
            return;
        }
        if (e.getRawSlot() == 51) {
            openCategory(p, cat, page + 1);
            return;
        }
        if (cat == null) return;
        int from = page * GRID.length;
        for (int i = 0; i < GRID.length; i++) {
            if (GRID[i] != e.getRawSlot()) continue;
            List<ShopItem> items = itemsFor(cat);
            int idx = from + i;
            if (idx >= items.size()) return;
            openConfirm(p, items.get(idx));
            return;
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
