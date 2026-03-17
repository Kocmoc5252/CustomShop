package me.example.customshop.npc;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class ShopNpcCommand implements CommandExecutor {
    private final ShopNpcManager npc;

    public ShopNpcCommand(ShopNpcManager npc) {
        this.npc = npc;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;

        if (!p.hasPermission("customshop.admin")) {
            p.sendMessage(ChatColor.RED + "Нет прав.");
            return true;
        }

        if (args.length == 0) {
            p.sendMessage(ChatColor.YELLOW + "Используй: /shopnpc set | /shopnpc remove");
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            npc.setNpcAt(p);
            return true;
        }

        if (args[0].equalsIgnoreCase("remove")) {
            npc.removeNpc(p);
            return true;
        }

        p.sendMessage(ChatColor.YELLOW + "Используй: /shopnpc set | /shopnpc remove");
        return true;
    }
}
