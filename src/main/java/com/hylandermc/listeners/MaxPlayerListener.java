package com.hylandermc.listeners;

import com.hylandermc.Slingshot;
import com.hylandermc.config.MyConfig;
import com.hylandermc.managers.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class MaxPlayerListener implements Listener {

    private Slingshot plugin;
    private MyConfig config;
    private GameManager gameManager;
    public MaxPlayerListener(Slingshot instance) {
        this.plugin = instance;
        this.config = plugin.getOptions();
        this.gameManager = plugin.getGameManager();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        new BukkitRunnable() {
            public void run() {
                gameManager.setPlayersOnline(Bukkit.getOnlinePlayers().size());
            }
        }.runTaskLater(plugin, 10);

    }


    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if(config.getString("server_type").equalsIgnoreCase("game")) {
            gameManager.setPlayersOnline(Bukkit.getOnlinePlayers().size());
            if(Bukkit.getOnlinePlayers().size() == config.getInt("game_information.max_players")) {
                gameManager.setJoinable(false);
                return;
            }
        }
    }


}
