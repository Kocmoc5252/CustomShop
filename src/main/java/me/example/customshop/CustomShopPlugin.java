package me.example.customshop;

import me.example.customshop.custom.CustomFeatureListener;
import me.example.customshop.hook.CoinExchangerHook;
import me.example.customshop.items.BoostFurnaceListener;
import me.example.customshop.items.CharmTicker;
import me.example.customshop.npc.EmeraldShopNpcCommand;
import me.example.customshop.npc.EmeraldShopNpcManager;
import me.example.customshop.npc.ShopNpcCommand;
import me.example.customshop.npc.ShopNpcManager;
import me.example.customshop.privateores.PrivateOreListener;
import me.example.customshop.raid.RaidExplosiveListener;
import me.example.customshop.shop.EmeraldShopCommand;
import me.example.customshop.shop.EmeraldShopGUI;
import me.example.customshop.shop.ShopCommand;
import me.example.customshop.shop.ShopGUI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomShopPlugin extends JavaPlugin {

    private CoinExchangerHook coins;
    private ShopGUI shopGUI;
    private EmeraldShopGUI emeraldShopGUI;
    private ShopNpcManager npcManager;
    private EmeraldShopNpcManager emeraldNpcManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        applyPriceFloor();

        this.coins = new CoinExchangerHook();
        if (!coins.hook()) {
            getLogger().severe("CoinExchanger не найден/не включен. CustomShop отключен.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.shopGUI = new ShopGUI(this, coins);
        this.emeraldShopGUI = new EmeraldShopGUI(this, coins);
        this.npcManager = new ShopNpcManager(this, shopGUI);
        this.emeraldNpcManager = new EmeraldShopNpcManager(this, emeraldShopGUI);

        getCommand("shop").setExecutor(new ShopCommand(shopGUI));
        getCommand("eshop").setExecutor(new EmeraldShopCommand(emeraldShopGUI));
        getCommand("shopnpc").setExecutor(new ShopNpcCommand(npcManager));
        getCommand("eshopnpc").setExecutor(new EmeraldShopNpcCommand(emeraldNpcManager));

        Bukkit.getPluginManager().registerEvents(shopGUI, this);
        Bukkit.getPluginManager().registerEvents(emeraldShopGUI, this);
        Bukkit.getPluginManager().registerEvents(npcManager, this);
        Bukkit.getPluginManager().registerEvents(emeraldNpcManager, this);
        Bukkit.getPluginManager().registerEvents(new BoostFurnaceListener(this), this);
        PrivateOreListener privateOreListener = new PrivateOreListener(this);
        Bukkit.getPluginManager().registerEvents(privateOreListener, this);
        Bukkit.getPluginManager().registerEvents(new RaidExplosiveListener(this, privateOreListener), this);
        Bukkit.getPluginManager().registerEvents(new CustomFeatureListener(this), this);

        new CharmTicker(this).start();
        RecipeManager.register(this);

        if (getConfig().getBoolean("shop.npc.enabled", true)) {
            npcManager.spawnOrRespawn();
        }
        if (getConfig().getBoolean("eshop.npc.enabled", true)) {
            emeraldNpcManager.spawnOrRespawn();
        }

        getLogger().info("CustomShop enabled.");
    }

    @Override
    public void onDisable() {
        if (npcManager != null) npcManager.despawn();
        if (emeraldNpcManager != null) emeraldNpcManager.despawn();
    }

    private void applyPriceFloor() {
        if (getConfig().getDefaults() == null) {
            saveConfig();
            return;
        }

        boolean changed = false;
        ConfigurationSection defaults = getConfig().getDefaults().getConfigurationSection("prices");
        if (defaults != null) {
            for (String key : defaults.getKeys(false)) {
                int minPrice = defaults.getInt(key);
                String path = "prices." + key;
                if (getConfig().getInt(path, 0) < minPrice) {
                    getConfig().set(path, minPrice);
                    changed = true;
                }
            }
        }

        if (changed) {
            saveConfig();
        }
    }
}
