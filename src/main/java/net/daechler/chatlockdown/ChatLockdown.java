package net.daechler.chatlockdown;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChatLockdown extends JavaPlugin implements Listener, CommandExecutor {

    private boolean chatLocked = false; // flag to indicate whether chat is locked
    private Set<UUID> bypassPlayers = new HashSet<>(); // set of players who can bypass the lockdown

    @Override
    public void onEnable() {
        // register plugin events and commands
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
        getCommand("lockdown").setExecutor(this);
    }

    // handle /lockdown command
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("lockdown")) {
            if (!sender.hasPermission("chatlockdown.use")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                return true;
            }

            chatLocked = !chatLocked;
            if (chatLocked) {
                Bukkit.broadcastMessage(ChatColor.RED + "Chat has been locked by an admin.");
            } else {
                Bukkit.broadcastMessage(ChatColor.GREEN + "Chat has been unlocked by an admin.");
            }
            return true;
        }

        return false;
    }

    // handle player chat event
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (chatLocked && !bypassPlayers.contains(event.getPlayer().getUniqueId()) && !event.getPlayer().hasPermission("chatlockdown.bypass")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Chat is currently locked by an admin.");
        }
    }

    // add a player to the bypass list
    public void addBypassPlayer(Player player) {
        bypassPlayers.add(player.getUniqueId());
    }

    // remove a player from the bypass list
    public void removeBypassPlayer(Player player) {
        bypassPlayers.remove(player.getUniqueId());
    }
}
