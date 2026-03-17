package me.example.customshop.shop;

import org.bukkit.inventory.ItemStack;

public record ShopItem(String id, int price, ItemStack item) {}
