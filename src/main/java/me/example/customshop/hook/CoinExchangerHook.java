package me.example.customshop.hook;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.UUID;

public class CoinExchangerHook {
    private Object coinManager;
    private Method mGet;
    private Method mAdd;
    private Method mTake;
    private Method mSave;

    public boolean hook() {
        Plugin p = Bukkit.getPluginManager().getPlugin("CoinExchanger");
        if (p == null || !p.isEnabled()) return false;

        try {
            Method getter = p.getClass().getMethod("getCoinManager");
            this.coinManager = getter.invoke(p);

            mGet  = coinManager.getClass().getMethod("get", UUID.class);
            mAdd  = coinManager.getClass().getMethod("add", UUID.class, long.class);
            mTake = coinManager.getClass().getMethod("take", UUID.class, long.class);
            mSave = coinManager.getClass().getMethod("save");

            return true;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    public long get(UUID u) {
        try {
            return ((Number) mGet.invoke(coinManager, u)).longValue();
        } catch (Throwable t) {
            return 0L;
        }
    }

    public boolean take(UUID u, long amount) {
        try {
            return (boolean) mTake.invoke(coinManager, u, amount);
        } catch (Throwable t) {
            return false;
        }
    }

    public void add(UUID u, long amount) {
        try {
            mAdd.invoke(coinManager, u, amount);
        } catch (Throwable ignored) {}
    }

    public void save() {
        try {
            mSave.invoke(coinManager);
        } catch (Throwable ignored) {}
    }
}
