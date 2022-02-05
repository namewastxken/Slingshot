package com.hylandermc;

import com.gmail.filoghost.holographicdisplays.HolographicDisplays;
import com.hylandermc.commands.SlingshotCommand;
import com.hylandermc.config.MyConfig;
import com.hylandermc.config.MyConfigManager;
import com.hylandermc.listeners.MaxPlayerListener;
import com.hylandermc.listeners.NPCInteractListener;
import com.hylandermc.managers.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Slingshot extends JavaPlugin {

    private MyConfigManager manager = new MyConfigManager(this);

    private MyConfig options = manager.getNewConfig("config.yml");
    @Getter
    private MyConfig NPCLocations = manager.getNewConfig("npclocations.yml");

    @Getter
    private MongoManager mongoManager = new MongoManager(this);

    @Getter
    private GameManager gameManager = new GameManager(this);

    @Getter private NPCManager npcManager = new NPCManager(this);
    @Getter private HologramLocationManager hologramLocationManager = new HologramLocationManager(this);
    @Getter private HologramManager hologramManager = new HologramManager(this);

    public void onEnable() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        setupConfig();
        load();
    }

    public void onDisable() {
        if(options.getString("server_type").equalsIgnoreCase("game")) {
            System.out.println("Attempting to set the joinable to false");
            gameManager.setJoinable(false);
            gameManager.setPlayersOnline(0);
        }
    }

    private void load() {
        gameManager = new GameManager(this);
        registerCommands();
        registerListeners();
        mongoManager.connect();

        if(options.getString("server_type").equalsIgnoreCase("game")) {
            new BukkitRunnable() {
                public void run() {
                    mongoManager.setupGameInformation();
                }
            }.runTaskLaterAsynchronously(this, 40);
        }

        if(options.getString("server_type").equalsIgnoreCase("lobby")) {
            gameManager.checkConnections();
        }

        new BukkitRunnable() {
            public void run() {
                if(options.getString("server_type").equalsIgnoreCase("lobby")) {
                    if(options.getBoolean("holograms.enabled")) {
                        if(getHolographicDisplays() != null) {
                            hologramManager.createHolograms();
                            hologramManager.startPlayerCountUpdateTask();
                            hologramManager.startHologramUpdateTask();
                        } else {
                            Bukkit.getConsoleSender().sendMessage("§c[Slingshot] Unable to use holograms, holographic displays is not installed.");
                        }
                    }
                }
            }
        }.runTaskLater(this, 20);
    }
    private void setupConfig() {
        //options = manager.getNewConfig("config.yml");
        options.saveConfig();


        if(!options.contains("mongo_database")) {
            options.set("mongo_database.host", "localhost");
            options.set("mongo_database.port", 27017);
            options.set("mongo_database.username", "username");
            options.set("mongo_database.password", "password");
            options.set("mongo_database.database_name", "database");
            options.saveConfig();
            options.reloadConfig();
        }

        if(!options.contains("server_type")) {
            options.set("server_type", "lobby");
            options.saveConfig();
            options.reloadConfig();
        }

        if(!options.contains("game_information")) {
            options.set("game_information.gamemode", "ArcherGames");
            options.set("game_information.server_name", "ag-1");
            options.set("game_information.max_players", 20);
            options.set("game_information.server_ip", "localhost");
            options.set("game_information.port", 25565);
            options.saveConfig();
            options.reloadConfig();
        }

        if(!options.contains("misc_options.auto_set_joinable")) {
            options.set("misc_options.auto_set_joinable", true);
        }

        if(!options.contains("holograms")) {
            options.set("holograms.enabled", false);
            options.set("holograms.npc_offset.x", 0.5);
            options.set("holograms.npc_offset.y", 3.0);
            options.set("holograms.npc_offset.z", 0.5);

            List<String> lore = new ArrayList<String>();
            lore.add("&a%gamemode%");
            lore.add("&7Right Click to join a game!");
            lore.add("&b&o%now_playing% &7are now playing!");
            options.set("holograms.hologram", lore.toArray());
            lore.clear();
        }

        if(!options.contains("messages")) {
            options.set("messages.setting_npc", "&aYou are now setting a &f%gamemode% &anpc, right click a npc to set.");
            options.set("messages.set_npc", "&aYou have successfully set a gamemode npc.");
            options.set("messages.no_game_available", "&cThere are no &f%gamemode% &cgames available. Try again soon!");
            options.set("messages.joining_game", "&b&oYou are now being connected to a &f%gamemode%&b&o game, please wait...");
        }
        options.saveConfig();
        options.reloadConfig();
    }

    private void registerCommands() {
        getCommand("slingshot").setExecutor(new SlingshotCommand(this));
    }

    private void registerListeners() {
        if(options.getString("server_type").equalsIgnoreCase("lobby")) {
            Bukkit.getPluginManager().registerEvents(new NPCInteractListener(this), this);
        }

        if(options.getString("server_type").equalsIgnoreCase("game")) {
            Bukkit.getPluginManager().registerEvents(new MaxPlayerListener(this), this);
        }
    }

    public MyConfig getOptions() {
        return options;
    }

    private HolographicDisplays getHolographicDisplays() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("HolographicDisplays");

        if(plugin != null) {
            return HolographicDisplays.getInstance();
        }
        return null;
    }
}
