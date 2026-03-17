package me.example.customshop.npc;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EmeraldShopNpcCommand implements CommandExecutor {
    private final EmeraldShopNpcManager npc;

    public EmeraldShopNpcCommand(EmeraldShopNpcManager npc) {
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
            p.sendMessage(ChatColor.YELLOW + "Используй: /eshopnpc set | /eshopnpc remove");
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
        p.sendMessage(ChatColor.YELLOW + "Используй: /eshopnpc set | /eshopnpc remove");
        return true;
    }
}
