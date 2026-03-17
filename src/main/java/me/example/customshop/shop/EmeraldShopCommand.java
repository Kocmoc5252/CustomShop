package me.example.customshop.shop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EmeraldShopCommand implements CommandExecutor {
    private final EmeraldShopGUI gui;

    public EmeraldShopCommand(EmeraldShopGUI gui) {
        this.gui = gui;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;
        if (!p.hasPermission("customshop.use")) return true;
        gui.openMain(p);
        return true;
    }
}
