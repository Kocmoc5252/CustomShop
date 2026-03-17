package me.example.customshop.items;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class CharmTicker {
    private final Plugin plugin;
    private final NamespacedKey charmKey;

    public CharmTicker(Plugin plugin) {
        this.plugin = plugin;
        this.charmKey = new NamespacedKey(plugin, "charm_type");
    }

    public void start() {
        int period = plugin.getConfig().getInt("charm.tick-period", 20);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    applyCharm(p);
                }
            }
        }.runTaskTimer(plugin, 20L, period);
    }

    private void setBonusHearts(Player p, double maxHealth) {
        AttributeInstance attr = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attr == null) return;

        if (attr.getBaseValue() != maxHealth) {
            attr.setBaseValue(maxHealth);

            // чтобы не оказалось “текущее здоровье > максимум”
            if (p.getHealth() > maxHealth) p.setHealth(maxHealth);
        }
    }

    private void resetHearts(Player p) {
        AttributeInstance attr = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attr == null) return;

        if (attr.getBaseValue() != 20.0) {
            attr.setBaseValue(20.0);
            if (p.getHealth() > 20.0) p.setHealth(20.0);
        }
    }

    private void applyCharm(Player p) {
        ItemStack off = p.getInventory().getItemInOffHand();

        // ✅ Если в оффхэнде нет кулона — сбросить сердца и выйти
        if (off == null || !off.hasItemMeta()) {
            resetHearts(p);
            return;
        }

        String type = off.getItemMeta().getPersistentDataContainer().get(charmKey, PersistentDataType.STRING);

        // ✅ Если нет charm_type — сбросить сердца и выйти
        if (type == null) {
            resetHearts(p);
            return;
        }

        int dur = 40; // 2 секунды, постоянно обновляется

        switch (type) {
            case "speed" -> {
                resetHearts(p);
                eff(p, PotionEffectType.SPEED, dur, 0);
            }
            case "jump" -> {
                resetHearts(p);
                eff(p, PotionEffectType.JUMP_BOOST, dur, 0);
            }
            case "haste" -> {
                resetHearts(p);
                eff(p, PotionEffectType.HASTE, dur, 0);
            }
            case "water" -> {
                resetHearts(p);
                eff(p, PotionEffectType.WATER_BREATHING, dur, 0);
            }

            case "miner" -> {
                resetHearts(p);
                eff(p, PotionEffectType.HASTE, dur, 1);
                eff(p, PotionEffectType.SPEED, dur, 0);
                eff(p, PotionEffectType.NIGHT_VISION, dur, 0);
            }

            case "runner" -> {
                resetHearts(p);
                eff(p, PotionEffectType.SPEED, dur, 1);
                eff(p, PotionEffectType.JUMP_BOOST, dur, 1);
            }

            case "ultra" -> {
                resetHearts(p);
                eff(p, PotionEffectType.SPEED, dur, 1);
                eff(p, PotionEffectType.JUMP_BOOST, dur, 1);
                eff(p, PotionEffectType.WATER_BREATHING, dur, 0);
                eff(p, PotionEffectType.HASTE, dur, 1);
            }

            case "healer" -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.LUCK, 60, 0, true, false, true));
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 0, true, false, true));
                setBonusHearts(p, 24.0);
            }

            case "tank" -> {
                eff(p, PotionEffectType.RESISTANCE, dur, 0);
                setBonusHearts(p, 24.0);
            }

            case "blaze" -> {
                resetHearts(p);
                eff(p, PotionEffectType.FIRE_RESISTANCE, dur, 0);
                eff(p, PotionEffectType.STRENGTH, dur, 0);
            }

            case "ocean" -> {
                resetHearts(p);
                eff(p, PotionEffectType.WATER_BREATHING, dur, 0);
                eff(p, PotionEffectType.DOLPHINS_GRACE, dur, 0);
            }

            default -> {
                // ✅ неизвестный кулон — на всякий сбрасываем сердца
                resetHearts(p);
            }
        }
    }

    private void eff(Player p, PotionEffectType t, int dur, int amp) {
        p.addPotionEffect(new PotionEffect(t, dur, amp, true, false, true));
    }
}
