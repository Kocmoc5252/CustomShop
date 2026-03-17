package me.example.customshop.items;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionType;

import java.util.*;

public class ItemFactory {
    private static final LegacyComponentSerializer AMP = LegacyComponentSerializer.legacyAmpersand();

    public static ItemStack sword1(Plugin plugin) {
        ItemStack it = simple(Material.NETHERITE_SWORD, "&4&l[✦] УЛЬТРА КЛИНОК", "&6Кастомный &7незеритовый меч", "&7Сильные чары, огромный урон.");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.SHARPNESS, 6, true);
        m.addEnchant(Enchantment.SWEEPING_EDGE, 4, true);
        m.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
        m.addEnchant(Enchantment.LOOTING, 4, true);
        m.addEnchant(Enchantment.UNBREAKING, 5, true);
        m.addEnchant(Enchantment.MENDING, 1, true);
        m.addEnchant(Enchantment.KNOCKBACK, 2, true);
        m.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "cs_bonus_dmg", 3.0, AttributeModifier.Operation.ADD_NUMBER));
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        mark(plugin, m, "customshop_item", "sword_1");
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack sword2(Plugin plugin) {
        ItemStack it = simple(Material.NETHERITE_SWORD, "&c&l[⚔] КЛИНОК ВОИНА", "&7Незеритовый меч.", "&7Сильные чары.");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.SHARPNESS, 5, true);
        m.addEnchant(Enchantment.UNBREAKING, 4, true);
        m.addEnchant(Enchantment.MENDING, 1, true);
        m.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        mark(plugin, m, "customshop_item", "sword_2");
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack sword3(Plugin plugin) {
        ItemStack it = simple(Material.DIAMOND_SWORD, "&b&l[⚔] МЕЧ ДОБЫТЧИКА", "&7Алмазный меч.", "&7Средние чары.");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.SHARPNESS, 4, true);
        m.addEnchant(Enchantment.UNBREAKING, 3, true);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        mark(plugin, m, "customshop_item", "sword_3");
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack stickKb(Plugin plugin) {
        ItemStack it = simple(Material.STICK, "&d&l[✦] ПАЛКА ОТДАЧИ", "&7Кастомная палка.", "&7Сильно откидывает.");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.KNOCKBACK, 4, true);
        m.addEnchant(Enchantment.UNBREAKING, 3, true);
        mark(plugin, m, "customshop_item", "stick_kb");
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack pickaxe1(Plugin plugin) {
        ItemStack it = simple(Material.NETHERITE_PICKAXE, "&3&l[⛏] КИРКА ШАХТЁРА", "&7Незеритовая кирка.", "&7Ломает моментально.");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.EFFICIENCY, 6, true);
        m.addEnchant(Enchantment.FORTUNE, 4, true);
        m.addEnchant(Enchantment.UNBREAKING, 5, true);
        m.addEnchant(Enchantment.MENDING, 1, true);
        mark(plugin, m, "customshop_item", "pickaxe_1");
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack pickaxe2(Plugin plugin) {
        ItemStack it = simple(Material.NETHERITE_PICKAXE, "&6&l[⛏] КИРКА ЛЮБИТЕЛЯ", "&7Незеритовая кирка.", "&7Хорошая эффективность.");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.EFFICIENCY, 5, true);
        m.addEnchant(Enchantment.FORTUNE, 3, true);
        m.addEnchant(Enchantment.UNBREAKING, 4, true);
        m.addEnchant(Enchantment.MENDING, 1, true);
        mark(plugin, m, "customshop_item", "pickaxe_2");
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack pickaxe3(Plugin plugin) {
        ItemStack it = simple(Material.DIAMOND_PICKAXE, "&b&l[⛏] КИРКА ГОРНЯКА", "&7Алмазная кирка.", "&7Хорошая кирка.");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.EFFICIENCY, 4, true);
        m.addEnchant(Enchantment.UNBREAKING, 3, true);
        mark(plugin, m, "customshop_item", "pickaxe_3");
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack pickaxeSilkUltra(Plugin plugin) {
        ItemStack it = simple(Material.DIAMOND_PICKAXE, "&b&l[⛏] ШЁЛКОВАЯ КИРКА", "&7Алмазная кирка с Шёлковым касанием.", "&7Очень полезна для редких блоков.");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.EFFICIENCY, 5, true);
        m.addEnchant(Enchantment.SILK_TOUCH, 1, true);
        m.addEnchant(Enchantment.UNBREAKING, 4, true);
        m.addEnchant(Enchantment.MENDING, 1, true);
        mark(plugin, m, "customshop_item", "pickaxe_silk_ultra");
        it.setItemMeta(m);
        return it;
    }


    public static ItemStack craftedEmeraldSword(Plugin plugin) {
        ItemStack it = simple(Material.DIAMOND_SWORD, "&a&l[⚔] ИЗУМРУДНЫЙ МЕЧ", "&7Крафтовый изумрудный меч.", "&7Острота II и Прочность II.");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.SHARPNESS, 2, true);
        m.addEnchant(Enchantment.UNBREAKING, 2, true);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        mark(plugin, m, "customshop_item", "crafted_emerald_sword");
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack craftedEmeraldPickaxe(Plugin plugin) {
        ItemStack it = simple(Material.DIAMOND_PICKAXE, "&a&l[⛏] ИЗУМРУДНАЯ КИРКА", "&7Крафтовая изумрудная кирка.", "&7Прочность II и Эффективность II.");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.UNBREAKING, 2, true);
        m.addEnchant(Enchantment.EFFICIENCY, 2, true);
        mark(plugin, m, "customshop_item", "crafted_emerald_pickaxe");
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack craftedEmeraldHoe(Plugin plugin) {
        ItemStack it = simple(Material.DIAMOND_HOE, "&a&l[⛏] ИЗУМРУДНАЯ МОТЫГА", "&7Крафтовая изумрудная мотыга.", "&7Прочность II и Эффективность II.");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.UNBREAKING, 2, true);
        m.addEnchant(Enchantment.EFFICIENCY, 2, true);
        mark(plugin, m, "customshop_item", "crafted_emerald_hoe");
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack hoe1(Plugin plugin) { return farmerHoe(plugin, Material.IRON_HOE, "hoe_1", "&a&l[⛏] МОТЫГА ФЕРМЕРА I", 0, 2); }
    public static ItemStack hoe2(Plugin plugin) { return farmerHoe(plugin, Material.DIAMOND_HOE, "hoe_2", "&2&l[⛏] МОТЫГА ФЕРМЕРА II", 0, 3); }
    public static ItemStack hoe3(Plugin plugin) { return farmerHoe(plugin, Material.NETHERITE_HOE, "hoe_3", "&6&l[⛏] МОТЫГА ФЕРМЕРА III", 1, 4); }

    private static ItemStack farmerHoe(Plugin plugin, Material material, String id, String name, int greenifierLevel, int unbreaking) {
        ItemStack it = simple(material, name,
                greenifierLevel > 0 ? "&7Топовая незеритовая мотыга с Озеленителем." : "&7Надёжная мотыга для фермы.",
                greenifierLevel > 0 ? "&7Ускоряет рост культур при ПКМ." : "&7Без кастомных чар, просто удобный инструмент.");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.UNBREAKING, unbreaking, true);
        m.addEnchant(Enchantment.EFFICIENCY, Math.min(3 + Math.max(0, greenifierLevel - 1), 5), true);
        mark(plugin, m, "customshop_item", id);
        it.setItemMeta(m);
        if (greenifierLevel > 0) {
            return withCustomEnchant(plugin, it, "greenifier", greenifierLevel);
        }
        return it;
    }

    public static ItemStack shearsSilk(Plugin plugin) {
        ItemStack it = simple(Material.SHEARS, "&a&l[✂] НОЖНИЦЫ ТРАВНИКА", "&7Ножницы с &fШёлковым касанием", "&7Выпадает все, что не выпадет просто так.");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.SILK_TOUCH, 1, true);
        m.addEnchant(Enchantment.UNBREAKING, 4, true);
        m.addEnchant(Enchantment.MENDING, 1, true);
        mark(plugin, m, "customshop_item", "shears_silk");
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack emeraldHelmet(Plugin plugin) { return emeraldArmor(plugin, Material.DIAMOND_HELMET, "emerald_helmet", "&a&l[✦] ИЗУМРУДНЫЙ ШЛЕМ"); }
    public static ItemStack emeraldChestplate(Plugin plugin) { return emeraldArmor(plugin, Material.DIAMOND_CHESTPLATE, "emerald_chestplate", "&a&l[✦] ИЗУМРУДНЫЙ НАГРУДНИК"); }
    public static ItemStack emeraldLeggings(Plugin plugin) { return emeraldArmor(plugin, Material.DIAMOND_LEGGINGS, "emerald_leggings", "&a&l[✦] ИЗУМРУДНЫЕ ПОНOЖИ"); }
    public static ItemStack emeraldBoots(Plugin plugin) { return emeraldArmor(plugin, Material.DIAMOND_BOOTS, "emerald_boots", "&a&l[✦] ИЗУМРУДНЫЕ БОТИНКИ"); }

    private static ItemStack emeraldArmor(Plugin plugin, Material mat, String id, String name) {
        ItemStack it = simple(mat, name, "&7Алмазная броня с изумрудной отделкой.", "&7Сразу идёт с лёгкими чарами.");
        ItemMeta raw = it.getItemMeta();
        raw.addEnchant(Enchantment.PROTECTION, 2, true);
        raw.addEnchant(Enchantment.UNBREAKING, 2, true);
        if (raw instanceof ArmorMeta meta) {
            try {
                meta.setTrim(new ArmorTrim(TrimMaterial.EMERALD, TrimPattern.SILENCE));
            } catch (Throwable ignored) {}
            mark(plugin, meta, "customshop_item", id);
            it.setItemMeta(meta);
            return it;
        }
        mark(plugin, raw, "customshop_item", id);
        it.setItemMeta(raw);
        return it;
    }

    public static ItemStack charm(Plugin plugin, String type, String name, String... loreLines) {
        ItemStack it = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta m = it.getItemMeta();
        applyStyledName(m, name);
        List<String> lore = new ArrayList<>(Arrays.asList(loreLines));
        lore.add("&8Во 2-й руке");
        m.lore(lore.stream().map(AMP::deserialize).toList());
        m.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        mark(plugin, m, "charm_type", type);
        mark(plugin, m, "customshop_item", "charm_" + type);
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack furnaceX2(Plugin plugin) { return furnace(plugin, "x2", 2); }
    public static ItemStack furnaceX3(Plugin plugin) { return furnace(plugin, "x3", 3); }
    public static ItemStack furnaceX4(Plugin plugin) { return furnace(plugin, "x4", 4); }
    public static ItemStack furnaceX5(Plugin plugin) { return furnace(plugin, "x5", 5); }

    private static ItemStack furnace(Plugin plugin, String key, int multi) {
        ItemStack it = simple(Material.FURNACE, "&6&l[🔥] СУПЕР-ПЕЧЬ &ex" + multi, "&7x" + multi + " скорость плавки", "&7Для использования поставь и плавь");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.UNBREAKING, 1, true);
        m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        mark(plugin, m, "customshop_item", "furnace_" + key);
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack customBook(Plugin plugin, String type, int level) {
        String name = switch (type) {
            case "bulldozer" -> "&6Бульдозер";
            case "treecapitator" -> "&2Дровосек";
            case "poison" -> "&aЯд " + roman(level);
            case "wither" -> "&8Вред " + roman(level);
            case "efficiency" -> "&bЭффективность " + roman(level);
            case "sharpness" -> "&cОстрота " + roman(level);
            case "lavawalker" -> "&6Лавоход";
            case "antichams" -> "&5Анти-Чамс";
            case "magnet" -> "&eМагнит";
            case "supermending" -> "&dУлучшенная починка";
            case "greenifier" -> "&aОзеленитель " + roman(level);
            case "immortality" -> "&fНеубиваемость";
            case "light_handle" -> "&bЛёгкая рукоять";
            case "fortune" -> "&6Удача " + roman(level);
            case "unstable" -> "&8Нестабильный " + roman(level);
            case "lifesteal" -> "&cВампиризм " + roman(level);
            case "frost" -> "&bЛедяной удар " + roman(level);
            case "guardian" -> "&fСтраж " + roman(level);
            case "berserk" -> "&4Берсерк " + roman(level);
            case "autosmelt" -> "&6Автоплавка";
            default -> "&fКнига";
        };
        String apply = switch (type) {
            case "bulldozer" -> "&7Кирка / лопата";
            case "treecapitator" -> "&7Топор";
            case "poison", "wither", "sharpness", "lifesteal", "frost", "berserk" -> "&7Меч / топор";
            case "efficiency", "fortune" -> "&7Кирка";
            case "autosmelt" -> "&7Кирка / лопата";
            case "lavawalker" -> "&7Ботинки";
            case "antichams", "guardian" -> "&7Нагрудник";
            case "magnet" -> "&7Кирка / топор / лопата";
            case "supermending" -> "&7Любой предмет";
            case "greenifier" -> "&7Мотыга";
            case "immortality" -> "&7Элитры";
            case "light_handle" -> "&7Трезубец";
            case "unstable" -> "&cПроклятие";
            default -> "&7Наковальня";
        };
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta m = it.getItemMeta();
        applyStyledName(m, name);
        m.lore(List.of(AMP.deserialize(apply), AMP.deserialize("&8Наковальня")));
        mark(plugin, m, "customshop_item", "book_" + type + "_" + level);
        mark(plugin, m, "cs_book_type", type);
        m.getPersistentDataContainer().set(new NamespacedKey(plugin, "cs_book_level"), PersistentDataType.INTEGER, level);
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack revealDust(Plugin plugin) { return taggedStack(plugin, Material.SUGAR, 16, "&b&l[✦] ЯВНАЯ ПЫЛЬ", "reveal_dust", "&7ПКМ: подсвечивает всех в радиусе &f6 блоков&7.", "&7Есть визуальный круг эффекта."); }
    public static ItemStack trapBox(Plugin plugin) { return taggedStack(plugin, Material.NETHERITE_SCRAP, 8, "&8&l[✦] ТРАПКА", "trap_box", "&7ПКМ: спавнит коробку-ловушку на &e13 сек&7.", "&7Ломать никому нельзя."); }
    public static ItemStack plateWall(Plugin plugin) { return taggedStack(plugin, Material.DRIED_KELP, 8, "&2&l[✦] ПЛАСТ", "plate_wall", "&7ПКМ: стена &f5x3x1 &7на &e13 сек&7.", "&7Ломать никому нельзя."); }
    public static ItemStack giantSnowball(Plugin plugin) { return taggedStack(plugin, Material.SNOWBALL, 16, "&f&l[✦] ГИГАНТСКИЙ СНЕЖОК", "giant_snowball", "&7При попадании замедляет в зоне &f2x2&7.", "&7Отмечает зону частицами."); }
    public static ItemStack holyAura(Plugin plugin) { return taggedStack(plugin, Material.PHANTOM_MEMBRANE, 16, "&f&l[✦] БОЖЬЯ АУРА", "holy_aura", "&7ПКМ: снимает все плохие эффекты.", "&7Снимает и свечение тоже."); }
    public static ItemStack killerCpu(Plugin plugin) { return taggedStack(plugin, Material.CHAIN_COMMAND_BLOCK, 1, "&5&l[✦] ПРОЦЕССОР-УБИЙЦА", "killer_cpu", "&7Блок с GUI на 1 слот.", "&7Бьёт всех мобов в радиусе &f16 блоков&7.", "&7Теперь задевает всех сразу, а не по одному.", "&7По умолчанию внутри железный меч Прочность III."); }
    public static ItemStack healingPearl(Plugin plugin) { return taggedStack(plugin, Material.ENDER_PEARL, 8, "&d&l[✦] ЛЕЧЕБНЫЙ ЖЕМЧУГ", "healing_pearl", "&7Телепортирует как обычный жемчуг,", "&7но после прилёта полностью лечит."); }
    public static ItemStack disorientation(Plugin plugin) { return taggedStack(plugin, Material.ENDER_EYE, 16, "&5&l[✦] ДЕЗОРИЕНТАЦИЯ", "disorientation", "&7ПКМ: свечение + слепота + иссушение", "&7в радиусе &f6 блоков&7."); }
    public static ItemStack fireTornado(Plugin plugin) { return taggedStack(plugin, Material.FIRE_CHARGE, 16, "&6&l[✦] ОГНЕННЫЙ СМЕРЧ", "fire_tornado", "&7ПКМ: поджигает всех в радиусе &f6 блоков&7.", "&7Отмечает зону пламенем."); }
    public static ItemStack waterCube(Plugin plugin) { return taggedStack(plugin, Material.HEART_OF_THE_SEA, 8, "&b&l[✦] ВОДЯНОЙ КУБ", "water_cube", "&7ПКМ: создаёт &f3x3x3 &7водяной куб.", "&7Куб не растекается и исчезает сам."); }
    public static ItemStack unrealAura(Plugin plugin) { return taggedStack(plugin, Material.HEAVY_CORE, 12, "&d&l[✦] НЕРЕАЛЬНАЯ АУРА", "unreal_aura", "&7ПКМ: отталкивает всех вокруг", "&7и подсвечивает на &f5 секунд&7."); }
    public static ItemStack godTouch(Plugin plugin) {
        ItemStack it = taggedStack(plugin, Material.GOLDEN_PICKAXE, 1, "&6&l[✦] БОЖЬЕ КАСАНИЕ", "god_touch", "&7Ломает только спавнеры.", "&7Шанс выпадения: &a100%&7.", "&cИмеет всего 1 прочность.");
        ItemMeta m = it.getItemMeta();
        ((Damageable) m).setDamage(Material.GOLDEN_PICKAXE.getMaxDurability() - 1);
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack customPotion(Plugin plugin, String type, Material material) {
        ItemStack it = new ItemStack(material);
        PotionMeta m = (PotionMeta) it.getItemMeta();
        String name = switch (type) {
            case "storm" -> "&bШторм";
            case "medic" -> "&dМедик";
            case "burp" -> "&2Токсин";
            case "flash" -> "&fФлешка";
            case "titan" -> "&6Титан";
            case "venom" -> "&aЯдовитый туман";
            case "shadow" -> "&8Тень";
            default -> "&fЗелье";
        };
        Component component = styledComponent(name);
        try {
            m.itemName(component);
        } catch (Throwable ignored) {}
        m.displayName(component);
        m.lore(List.of(AMP.deserialize("&8Кастомное зелье")));
        try {
            m.setBasePotionType(PotionType.WATER);
        } catch (Throwable ignored) {}
        m.setColor(switch (type) {
            case "storm" -> Color.AQUA;
            case "medic" -> Color.FUCHSIA;
            case "burp" -> Color.OLIVE;
            case "flash" -> Color.WHITE;
            case "titan" -> Color.ORANGE;
            case "venom" -> Color.LIME;
            case "shadow" -> Color.GRAY;
            default -> Color.GRAY;
        });
        mark(plugin, m, "customshop_item", type + "_potion");
        mark(plugin, m, "cs_potion_type", type);
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack privateOreCoal(Plugin plugin) {
        return privateClaimCore(plugin, Material.COAL_ORE, "private_ore_coal", "&8Угольная приват-руда", 4);
    }

    public static ItemStack privateOreIron(Plugin plugin) {
        return privateClaimCore(plugin, Material.IRON_ORE, "private_ore_iron", "&fЖелезная приват-руда", 6);
    }

    public static ItemStack privateOreGold(Plugin plugin) {
        return privateClaimCore(plugin, Material.GOLD_ORE, "private_ore_gold", "&6Золотая приват-руда", 8);
    }

    public static ItemStack privateOreDiamond(Plugin plugin) {
        return privateClaimCore(plugin, Material.DIAMOND_ORE, "private_ore_diamond", "&bАлмазная приват-руда", 10);
    }

    public static ItemStack privateOreEmerald(Plugin plugin) {
        return privateClaimCore(plugin, Material.EMERALD_ORE, "private_ore_emerald", "&aИзумрудная приват-руда", 13);
    }

    public static ItemStack privateCoreById(Plugin plugin, String id) {
        return switch (id) {
            case "private_ore_coal" -> privateOreCoal(plugin);
            case "private_ore_iron" -> privateOreIron(plugin);
            case "private_ore_gold" -> privateOreGold(plugin);
            case "private_ore_diamond" -> privateOreDiamond(plugin);
            case "private_ore_emerald" -> privateOreEmerald(plugin);
            default -> null;
        };
    }

    public static ItemStack obsidianBundle(Plugin plugin) {
        return namedBundle(plugin, Material.OBSIDIAN, 64, "&8Пак обсидиана", "obsidian_bundle");
    }

    public static ItemStack cryingObsidianBundle(Plugin plugin) {
        return namedBundle(plugin, Material.CRYING_OBSIDIAN, 32, "&5Плачущий обсидиан", "crying_obsidian_bundle");
    }

    public static ItemStack endCrystalPack(Plugin plugin) {
        return namedBundle(plugin, Material.END_CRYSTAL, 8, "&dНабор эндер-кристаллов", "end_crystal_pack");
    }

    public static ItemStack respawnAnchorPack(Plugin plugin) {
        return namedBundle(plugin, Material.RESPAWN_ANCHOR, 4, "&6Набор якорей возрождения", "respawn_anchor_pack");
    }

    public static ItemStack cobwebPack(Plugin plugin) {
        return namedBundle(plugin, Material.COBWEB, 32, "&fПак паутины", "cobweb_pack");
    }

    public static ItemStack tntPack(Plugin plugin) {
        return namedBundle(plugin, Material.TNT, 64, "&cЯщик TNT", "tnt_pack");
    }

    public static ItemStack dynamiteProcessor(Plugin plugin) {
        return taggedStack(plugin, Material.BLAZE_POWDER, 1, "&6Процессор динамита", "dynamite_processor",
                "!&8Компонент для Tier Black TNT");
    }

    public static ItemStack blackTnt(Plugin plugin) {
        return taggedStack(plugin, Material.TNT, 1, "&8Tier Black TNT", "black_tnt",
                "!&8Рейдовый заряд",
                "!&7Ломает обсидиан",
                "!&7Радиус взрыва увеличен",
                "!&7В чужом привате: &fкидай на землю");
    }

    public static ItemStack blackTntPack(Plugin plugin) {
        ItemStack it = blackTnt(plugin);
        it.setAmount(4);
        return it;
    }

    public static ItemStack rareSingle(Plugin plugin, Material mat, String name, int amount, String... lore) {
        ItemStack it = taggedStack(plugin, mat, amount, name, "rare_" + mat.name().toLowerCase(Locale.ROOT), lore);
        ItemMeta m = it.getItemMeta();
        if (mat == Material.GOLDEN_APPLE) {
            m.removeEnchant(Enchantment.LURE);
            m.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
            it.setItemMeta(m);
            return it;
        }
        if (mat == Material.ENCHANTED_GOLDEN_APPLE || mat == Material.ELYTRA) {
            m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            it.setItemMeta(m);
        }
        return it;
    }

    public static ItemStack totemSingle(Plugin plugin) {
        ItemStack it = new ItemStack(Material.TOTEM_OF_UNDYING, 1);
        ItemMeta m = it.getItemMeta();
        applyStyledName(m, "&eТотем бессмертия");
        mark(plugin, m, "customshop_item", "totem_single");
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack nameTagSingle(Plugin plugin) {
        ItemStack it = new ItemStack(Material.NAME_TAG, 1);
        ItemMeta m = it.getItemMeta();
        applyStyledName(m, "&fБирка");
        mark(plugin, m, "customshop_item", "name_tag_single");
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack silkBook(Plugin plugin) {
        ItemStack it = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta m = it.getItemMeta();
        applyStyledName(m, "&bШёлковое касание");
        m.lore(List.of(AMP.deserialize("&8Наковальня")));
        m.addEnchant(Enchantment.SILK_TOUCH, 1, true);
        mark(plugin, m, "customshop_item", "silk_book");
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack fireworks16(Plugin plugin) {
        return taggedStack(plugin, Material.FIREWORK_ROCKET, 16, "&d&l[✦] ФЕЙЕРВЕРКИ", "fireworks_16", "&7Набор из 16 фейерверков.");
    }

    public static ItemStack crossbowMultishot(Plugin plugin) {
        ItemStack it = simple(Material.CROSSBOW, "&5&l[✦] АРБАЛЕТ РАСЩЕПЛЕНИЯ", "&7Готов для боя сразу из магазина.");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.MULTISHOT, 1, true);
        m.addEnchant(Enchantment.QUICK_CHARGE, 2, true);
        m.addEnchant(Enchantment.UNBREAKING, 3, true);
        mark(plugin, m, "customshop_item", "crossbow_multishot");
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack swordVampire(Plugin plugin) {
        ItemStack it = simple(Material.NETHERITE_SWORD, "&cКлинок вампира");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.SHARPNESS, 6, true);
        m.addEnchant(Enchantment.SWEEPING_EDGE, 3, true);
        m.addEnchant(Enchantment.UNBREAKING, 5, true);
        m.addEnchant(Enchantment.MENDING, 1, true);
        m.addEnchant(Enchantment.LOOTING, 4, true);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        mark(plugin, m, "customshop_item", "sword_vampire");
        it.setItemMeta(m);
        return withCustomEnchant(plugin, it, "lifesteal", 2);
    }

    public static ItemStack swordFrost(Plugin plugin) {
        ItemStack it = simple(Material.NETHERITE_SWORD, "&bЛедяной клинок");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.SHARPNESS, 6, true);
        m.addEnchant(Enchantment.UNBREAKING, 5, true);
        m.addEnchant(Enchantment.MENDING, 1, true);
        m.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        mark(plugin, m, "customshop_item", "sword_frost");
        it.setItemMeta(m);
        return withCustomEnchant(plugin, it, "frost", 2);
    }

    public static ItemStack axeBerserk(Plugin plugin) {
        ItemStack it = simple(Material.NETHERITE_AXE, "&4Топор берсерка");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.SHARPNESS, 6, true);
        m.addEnchant(Enchantment.EFFICIENCY, 5, true);
        m.addEnchant(Enchantment.UNBREAKING, 5, true);
        m.addEnchant(Enchantment.MENDING, 1, true);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        mark(plugin, m, "customshop_item", "axe_berserk");
        it.setItemMeta(m);
        return withCustomEnchant(plugin, it, "berserk", 2);
    }

    public static ItemStack bowStorm(Plugin plugin) {
        ItemStack it = simple(Material.BOW, "&eЛук шторма");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.POWER, 6, true);
        m.addEnchant(Enchantment.PUNCH, 2, true);
        m.addEnchant(Enchantment.FLAME, 1, true);
        m.addEnchant(Enchantment.INFINITY, 1, true);
        m.addEnchant(Enchantment.UNBREAKING, 4, true);
        mark(plugin, m, "customshop_item", "bow_storm");
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack crossbowPhantom(Plugin plugin) {
        ItemStack it = simple(Material.CROSSBOW, "&5Арбалет фантома");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.QUICK_CHARGE, 3, true);
        m.addEnchant(Enchantment.PIERCING, 4, true);
        m.addEnchant(Enchantment.UNBREAKING, 4, true);
        m.addEnchant(Enchantment.MENDING, 1, true);
        mark(plugin, m, "customshop_item", "crossbow_phantom");
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack tridentPoseidon(Plugin plugin) {
        ItemStack it = simple(Material.TRIDENT, "&3Трезубец Посейдона");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.IMPALING, 7, true);
        m.addEnchant(Enchantment.LOYALTY, 3, true);
        m.addEnchant(Enchantment.CHANNELING, 1, true);
        m.addEnchant(Enchantment.UNBREAKING, 5, true);
        m.addEnchant(Enchantment.MENDING, 1, true);
        mark(plugin, m, "customshop_item", "trident_poseidon");
        it.setItemMeta(m);
        return withCustomEnchant(plugin, it, "light_handle", 1);
    }

    public static ItemStack pickaxeDrill(Plugin plugin) {
        ItemStack it = simple(Material.NETHERITE_PICKAXE, "&6Бур шахтёра");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.EFFICIENCY, 7, true);
        m.addEnchant(Enchantment.FORTUNE, 5, true);
        m.addEnchant(Enchantment.UNBREAKING, 5, true);
        m.addEnchant(Enchantment.MENDING, 1, true);
        mark(plugin, m, "customshop_item", "pickaxe_drill");
        it.setItemMeta(m);
        it = withCustomEnchant(plugin, it, "bulldozer", 1);
        return withCustomEnchant(plugin, it, "magnet", 1);
    }

    public static ItemStack pickaxeMolten(Plugin plugin) {
        ItemStack it = simple(Material.NETHERITE_PICKAXE, "&6Пламенная кирка");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.EFFICIENCY, 6, true);
        m.addEnchant(Enchantment.FORTUNE, 4, true);
        m.addEnchant(Enchantment.UNBREAKING, 4, true);
        m.addEnchant(Enchantment.MENDING, 1, true);
        mark(plugin, m, "customshop_item", "pickaxe_molten");
        it.setItemMeta(m);
        return withCustomEnchant(plugin, it, "autosmelt", 1);
    }

    public static ItemStack axeLumber(Plugin plugin) {
        ItemStack it = simple(Material.NETHERITE_AXE, "&2Топор лесоруба");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.EFFICIENCY, 6, true);
        m.addEnchant(Enchantment.SHARPNESS, 5, true);
        m.addEnchant(Enchantment.UNBREAKING, 5, true);
        m.addEnchant(Enchantment.MENDING, 1, true);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        mark(plugin, m, "customshop_item", "axe_lumber");
        it.setItemMeta(m);
        it = withCustomEnchant(plugin, it, "treecapitator", 1);
        return withCustomEnchant(plugin, it, "magnet", 1);
    }

    public static ItemStack shovelQuake(Plugin plugin) {
        ItemStack it = simple(Material.NETHERITE_SHOVEL, "&eЛопата катаклизма");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.EFFICIENCY, 7, true);
        m.addEnchant(Enchantment.UNBREAKING, 5, true);
        m.addEnchant(Enchantment.MENDING, 1, true);
        mark(plugin, m, "customshop_item", "shovel_quake");
        it.setItemMeta(m);
        it = withCustomEnchant(plugin, it, "bulldozer", 1);
        return withCustomEnchant(plugin, it, "autosmelt", 1);
    }

    public static ItemStack shovelArchaeologist(Plugin plugin) {
        ItemStack it = simple(Material.DIAMOND_SHOVEL, "&bЛопата археолога");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.EFFICIENCY, 6, true);
        m.addEnchant(Enchantment.SILK_TOUCH, 1, true);
        m.addEnchant(Enchantment.UNBREAKING, 4, true);
        m.addEnchant(Enchantment.MENDING, 1, true);
        mark(plugin, m, "customshop_item", "shovel_archaeologist");
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack craftedEmeraldAxe(Plugin plugin) {
        ItemStack it = simple(Material.DIAMOND_AXE, "&aИзумрудный топор");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.SHARPNESS, 2, true);
        m.addEnchant(Enchantment.EFFICIENCY, 2, true);
        m.addEnchant(Enchantment.UNBREAKING, 2, true);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        mark(plugin, m, "customshop_item", "crafted_emerald_axe");
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack craftedEmeraldShovel(Plugin plugin) {
        ItemStack it = simple(Material.DIAMOND_SHOVEL, "&aИзумрудная лопата");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.EFFICIENCY, 3, true);
        m.addEnchant(Enchantment.UNBREAKING, 2, true);
        mark(plugin, m, "customshop_item", "crafted_emerald_shovel");
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack helmetGladiator(Plugin plugin) {
        ItemStack it = simple(Material.NETHERITE_HELMET, "&6Шлем гладиатора");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.PROTECTION, 6, true);
        m.addEnchant(Enchantment.RESPIRATION, 5, true);
        m.addEnchant(Enchantment.AQUA_AFFINITY, 1, true);
        m.addEnchant(Enchantment.UNBREAKING, 5, true);
        m.addEnchant(Enchantment.MENDING, 1, true);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        mark(plugin, m, "customshop_item", "helmet_gladiator");
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack chestplateGladiator(Plugin plugin) {
        ItemStack it = simple(Material.NETHERITE_CHESTPLATE, "&6Нагрудник гладиатора");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.PROTECTION, 6, true);
        m.addEnchant(Enchantment.THORNS, 3, true);
        m.addEnchant(Enchantment.UNBREAKING, 5, true);
        m.addEnchant(Enchantment.MENDING, 1, true);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        mark(plugin, m, "customshop_item", "chestplate_gladiator");
        it.setItemMeta(m);
        return withCustomEnchant(plugin, it, "guardian", 1);
    }

    public static ItemStack leggingsGladiator(Plugin plugin) {
        ItemStack it = simple(Material.NETHERITE_LEGGINGS, "&6Поножи гладиатора");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.PROTECTION, 6, true);
        m.addEnchant(Enchantment.SWIFT_SNEAK, 3, true);
        m.addEnchant(Enchantment.UNBREAKING, 5, true);
        m.addEnchant(Enchantment.MENDING, 1, true);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        mark(plugin, m, "customshop_item", "leggings_gladiator");
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack bootsGladiator(Plugin plugin) {
        ItemStack it = simple(Material.NETHERITE_BOOTS, "&6Ботинки гладиатора");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.PROTECTION, 6, true);
        m.addEnchant(Enchantment.FEATHER_FALLING, 6, true);
        m.addEnchant(Enchantment.DEPTH_STRIDER, 3, true);
        m.addEnchant(Enchantment.SOUL_SPEED, 3, true);
        m.addEnchant(Enchantment.UNBREAKING, 5, true);
        m.addEnchant(Enchantment.MENDING, 1, true);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        mark(plugin, m, "customshop_item", "boots_gladiator");
        it.setItemMeta(m);
        return withCustomEnchant(plugin, it, "lavawalker", 1);
    }

    public static ItemStack helmetShadow(Plugin plugin) {
        ItemStack it = simple(Material.DIAMOND_HELMET, "&5Шлем тени");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.PROTECTION, 5, true);
        m.addEnchant(Enchantment.RESPIRATION, 3, true);
        m.addEnchant(Enchantment.AQUA_AFFINITY, 1, true);
        m.addEnchant(Enchantment.UNBREAKING, 4, true);
        m.addEnchant(Enchantment.MENDING, 1, true);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        mark(plugin, m, "customshop_item", "helmet_shadow");
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack chestplateGuardian(Plugin plugin) {
        ItemStack it = simple(Material.DIAMOND_CHESTPLATE, "&fНагрудник стража");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.PROTECTION, 5, true);
        m.addEnchant(Enchantment.PROJECTILE_PROTECTION, 5, true);
        m.addEnchant(Enchantment.UNBREAKING, 4, true);
        m.addEnchant(Enchantment.MENDING, 1, true);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        mark(plugin, m, "customshop_item", "chestplate_guardian");
        it.setItemMeta(m);
        it = withCustomEnchant(plugin, it, "guardian", 1);
        return withCustomEnchant(plugin, it, "antichams", 1);
    }

    public static ItemStack leggingsShadow(Plugin plugin) {
        ItemStack it = simple(Material.DIAMOND_LEGGINGS, "&5Поножи тени");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.PROTECTION, 5, true);
        m.addEnchant(Enchantment.SWIFT_SNEAK, 3, true);
        m.addEnchant(Enchantment.UNBREAKING, 4, true);
        m.addEnchant(Enchantment.MENDING, 1, true);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        mark(plugin, m, "customshop_item", "leggings_shadow");
        it.setItemMeta(m);
        return it;
    }

    public static ItemStack bootsVolcano(Plugin plugin) {
        ItemStack it = simple(Material.DIAMOND_BOOTS, "&cВулканические ботинки");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.FIRE_PROTECTION, 6, true);
        m.addEnchant(Enchantment.FEATHER_FALLING, 5, true);
        m.addEnchant(Enchantment.DEPTH_STRIDER, 3, true);
        m.addEnchant(Enchantment.UNBREAKING, 4, true);
        m.addEnchant(Enchantment.MENDING, 1, true);
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        mark(plugin, m, "customshop_item", "boots_volcano");
        it.setItemMeta(m);
        return withCustomEnchant(plugin, it, "lavawalker", 1);
    }

    public static ItemStack elytraImmortal(Plugin plugin) {
        ItemStack it = simple(Material.ELYTRA, "&fНеубиваемые элитры");
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.UNBREAKING, 5, true);
        m.addEnchant(Enchantment.MENDING, 1, true);
        mark(plugin, m, "customshop_item", "elytra_immortal");
        it.setItemMeta(m);
        return withCustomEnchant(plugin, it, "immortality", 1);
    }

    public static ItemStack emeraldPack(Plugin plugin, int amount) {
        return new ItemStack(Material.EMERALD, amount);
    }

    public static ItemStack emptySpawner(Plugin plugin) {
        return taggedStack(plugin, Material.SPAWNER, 1, "&cПустой спавнер", "empty_spawner");
    }

    public static ItemStack spawnEgg(Plugin plugin, Material mat, String name) {
        return taggedStack(plugin, mat, 1, name, "egg_" + mat.name().toLowerCase(Locale.ROOT), "!&7Редкий предмет.");
    }

    public static ItemStack chargedCreeperEgg(Plugin plugin) {
        return taggedStack(plugin, Material.CREEPER_SPAWN_EGG, 1, "&bЯйцо заряженного крипера", "spawn_charged_creeper",
                "!&7Спавнит &bзаряженного крипера",
                "!&7Можно юзать даже в чужом привате");
    }

    public static ItemStack withCustomEnchant(Plugin plugin, ItemStack item, String type, int level) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "cs_ench_" + type), PersistentDataType.INTEGER, level);
        List<String> lore = new ArrayList<>();
        if (meta.lore() != null) meta.lore().forEach(c -> lore.add(LegacyComponentSerializer.legacySection().serialize(c)));
        lore.add(customEnchantLore(type, level));
        meta.lore(lore.stream().map(AMP::deserialize).toList());
        meta.removeEnchant(Enchantment.LURE);
        meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack privateClaimCore(Plugin plugin, Material mat, String id, String name, int radius) {
        int size = radius * 2 + 1;
        ItemStack it = simple(mat, name,
                "!&7Приват: &f" + size + "x" + size,
                "!&7Снять: &fсломай в присяде");
        ItemMeta m = it.getItemMeta();
        mark(plugin, m, "customshop_item", id);
        m.getPersistentDataContainer().set(new NamespacedKey(plugin, "cs_private_radius"), PersistentDataType.INTEGER, radius);
        m.addEnchant(Enchantment.LURE, 1, true);
        m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        it.setItemMeta(m);
        return it;
    }

    private static ItemStack namedBundle(Plugin plugin, Material mat, int amount, String name, String id, String... lore) {
        ItemStack it = simple(mat, name, lore);
        it.setAmount(amount);
        ItemMeta m = it.getItemMeta();
        mark(plugin, m, "customshop_item", id);
        it.setItemMeta(m);
        return it;
    }

    private static String customEnchantLore(String type, int level) {
        return switch (type) {
            case "bulldozer" -> "&6Бульдозер I";
            case "treecapitator" -> "&2Дровосек I";
            case "magnet" -> "&eМагнит I";
            case "lavawalker" -> "&6Лавоход I";
            case "antichams" -> "&5Анти-Чамс I";
            case "greenifier" -> "&aОзеленитель " + roman(level);
            case "immortality" -> "&fНеубиваемость " + roman(level);
            case "light_handle" -> "&bЛёгкая рукоять I";
            case "unstable" -> "&8Нестабильный " + roman(level);
            case "lifesteal" -> "&cВампиризм " + roman(level);
            case "frost" -> "&bЛедяной удар " + roman(level);
            case "guardian" -> "&fСтраж " + roman(level);
            case "berserk" -> "&4Берсерк " + roman(level);
            case "autosmelt" -> "&6Автоплавка I";
            default -> "&7Кастом " + roman(level);
        };
    }

    private static ItemStack taggedStack(Plugin plugin, Material mat, int amount, String name, String id, String... lore) {
        ItemStack it = simple(mat, name, lore);
        it.setAmount(amount);
        ItemMeta m = it.getItemMeta();
        m.addEnchant(Enchantment.LURE, 1, true);
        m.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        mark(plugin, m, "customshop_item", id);
        it.setItemMeta(m);
        return it;
    }

    private static ItemStack simple(Material mat, String name, String... lore) {
        ItemStack it = new ItemStack(mat);
        ItemMeta m = it.getItemMeta();
        applyStyledName(m, name);
        List<Component> cleanLore = Arrays.stream(lore)
                .filter(Objects::nonNull)
                .filter(line -> line.startsWith("!"))
                .map(line -> line.substring(1))
                .map(line -> (Component) AMP.deserialize(line))
                .toList();
        if (!cleanLore.isEmpty()) {
            m.lore(cleanLore);
        }
        it.setItemMeta(m);
        return it;
    }

    private static void applyStyledName(ItemMeta meta, String legacyName) {
        Component component = styledComponent(legacyName);
        try {
            meta.itemName(component);
        } catch (Throwable ignored) {
            meta.displayName(component);
        }
    }

    private static Component styledComponent(String legacyName) {
        String sanitized = legacyName
                .replace("&l", "")
                .replace("§l", "")
                .replaceAll("\\[[^]]+\\]\\s*", "");
        return AMP.deserialize(sanitized)
                .decoration(TextDecoration.BOLD, false)
                .decoration(TextDecoration.ITALIC, false);
    }

    private static String roman(int n) {
        return switch (n) {
            case 1 -> "I"; case 2 -> "II"; case 3 -> "III"; case 4 -> "IV"; case 5 -> "V";
            case 6 -> "VI"; case 7 -> "VII"; case 8 -> "VIII"; case 9 -> "IX"; case 10 -> "X";
            default -> String.valueOf(n);
        };
    }

    public static void mark(Plugin plugin, ItemMeta meta, String key, String value) {
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, key), PersistentDataType.STRING, value);
    }
}
