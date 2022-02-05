package com.hylandermc.managers;

import com.hylandermc.Slingshot;
import com.hylandermc.config.MyConfig;
import com.hylandermc.objects.FormattedLocation;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.mongodb.client.MongoCollection;
import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HologramManager {

    private Slingshot plugin;
    private MyConfig config;
    private HologramLocationManager hologramLocationManager;
    @Getter private Map<String, Integer> gamemodeNowPlayingMap;
    public HologramManager(Slingshot instance) {
        plugin = instance;
        config = plugin.getOptions();
        hologramLocationManager = plugin.getHologramLocationManager();
        gamemodeNowPlayingMap = new HashMap<String, Integer>();
    }

    /**
     * Creates holograms for each stored location.
     */
    public void createHologramsLegacy() {
        for(Location location : hologramLocationManager.getLocations()) {
            if(config.getBoolean("holograms.enabled")) {

                Location hologram = location.clone();

                Hologram holo = HologramsAPI.createHologram(plugin, hologram);

                ArrayList<String> hologramList = (ArrayList<String>) config.getList("holograms.hologram");

                Location npcLocation = hologram.clone().subtract(config.getDouble("holograms.npc_offset.x"), config.getDouble("holograms.npc_offset.y"), config.getDouble("holograms.npc_offset.z"));
               // Bukkit.broadcastMessage(new FormattedLocation().getFormattedLocation(npcLocation).replaceAll("\\*", "\\."));

                for(String hologramLine : hologramList) {
                    holo.appendTextLine(hologramLine.replaceAll("&", "§").replaceAll("%gamemode%", plugin.getNpcManager().getGameModeFromLocation(new FormattedLocation().getLocationFromFormattedString(new FormattedLocation().getFormattedLocation(npcLocation).replaceAll("\\*", "\\.")))));
                }
            }
        }
    }

    public void createHolograms() {
        for(NPC npc : CitizensAPI.getNPCRegistry()) {
            if(plugin.getNPCLocations().contains("npc" + npc.getId())) {
                Location hologram = npc.getStoredLocation().clone().add(config.getDouble("holograms.npc_offset.x"), config.getDouble("holograms.npc_offset.y"), config.getDouble("holograms.npc_offset.z"));

                Hologram holo = HologramsAPI.createHologram(plugin, hologram);

                ArrayList<String> hologramList = (ArrayList<String>) config.getList("holograms.hologram");

                for(String hologramLine : hologramList) {
                    holo.appendTextLine(hologramLine.replaceAll("&", "§").replaceAll("%gamemode%", plugin.getNPCLocations().getString("npc" + npc.getId())));
                }
            }
        }
    }


    /**
     * Stores and creates hologram for location.
     * @param location
     */
    public void addHologram(Location location) {
        if(config.getBoolean("holograms.enabled")) {
            Location hologram = location.clone();
            hologram.add(config.getDouble("holograms.offset.x"), config.getDouble("holograms.offset.y"), config.getDouble("holograms.offset.z"));

            Hologram holo = HologramsAPI.createHologram(plugin, hologram);

            plugin.getHologramLocationManager().addHologramLocation(hologram);

            ArrayList<String> hologramList = (ArrayList<String>) config.getList("holograms.hologram");

            for(String hologramLine : hologramList) {
                holo.appendTextLine(hologramLine.replaceAll("&", "§"));
            }
        }
    }

    /**
     * Stores and creates hologram for location.
     * @param location
     */
    public void addNPCHologramLegacy(Location location) {
        if(config.getBoolean("holograms.enabled")) {
            Location hologram = location.clone();
            hologram.add(config.getDouble("holograms.npc_offset.x"), config.getDouble("holograms.npc_offset.y"), config.getDouble("holograms.npc_offset.z"));

            Hologram holo = HologramsAPI.createHologram(plugin, hologram);

            plugin.getHologramLocationManager().addHologramLocation(hologram);

            ArrayList<String> hologramList = (ArrayList<String>) config.getList("holograms.hologram");

            for(String hologramLine : hologramList) {
                holo.appendTextLine(hologramLine.replaceAll("&", "§").replaceAll("%gamemode%", plugin.getNpcManager().getGameModeFromLocation(location)));
            }
        }
    }

    public void addNPCHologram(NPC npc) {
        if(config.getBoolean("holograms.enabled")) {
            Location hologram = npc.getStoredLocation().clone();
            hologram.add(config.getDouble("holograms.npc_offset.x"), config.getDouble("holograms.npc_offset.y"), config.getDouble("holograms.npc_offset.z"));

            Hologram holo = HologramsAPI.createHologram(plugin, hologram);

            plugin.getHologramLocationManager().addHologramLocation(hologram);

            ArrayList<String> hologramList = (ArrayList<String>) config.getList("holograms.hologram");

            for(String hologramLine : hologramList) {
                holo.appendTextLine(hologramLine.replaceAll("&", "§").replaceAll("%gamemode%", plugin.getNPCLocations().getString("npc" + npc.getId())));
            }
        }
    }

    /**
     * removes a hologram.
     * @param location
     */
    public void removeHologram(Location location) {
        for (Hologram hologram : HologramsAPI.getHolograms(plugin)) {
            if (hologram.getLocation().distance(location) <= (config.getDouble("holograms.offset.y") + config.getDouble("holograms.offset.x") + config.getDouble("holograms.offset.z")) || hologram.getLocation().distance(location) <= (config.getDouble("holograms.npc_offset.y") + config.getDouble("holograms.npc_offset.x") + config.getDouble("holograms.npc_offset.z"))) {
                hologram.delete();
                plugin.getHologramLocationManager().deleteHologramLocation(hologram.getLocation());
            }
        }
    }
    @Deprecated
    public void updateAllHologramsLegacy() {
        for(Hologram hologram : HologramsAPI.getHolograms(plugin)) {
            if(config.getBoolean("holograms.enabled")) {
                Location hologramLocation =  hologram.getLocation();


                ArrayList<String> hologramList = (ArrayList<String>) config.getList("holograms.hologram");

                Location npcLocation = hologram.getLocation().clone().subtract(config.getDouble("holograms.npc_offset.x"), config.getDouble("holograms.npc_offset.y"), config.getDouble("holograms.npc_offset.z"));
                // Bukkit.broadcastMessage(new FormattedLocation().getFormattedLocation(npcLocation).replaceAll("\\*", "\\."));
                //hologram.clearLines();

                for(int i = 0; i  < hologramList.size(); i++) {
                    if(hologramList.get(i).contains("%now_playing")) {
                        hologram.getLine(i).removeLine();
                        //hologram.insertTextLine(i, hologramList.get(i).replaceAll("&", "§")
                          //      .replaceAll("%gamemode%", plugin.getNpcManager().getGameModeFromNPC(new FormattedLocation().getLocationFromFormattedString(new FormattedLocation().getFormattedLocation(npcLocation).replaceAll("\\*", "\\.")))).replaceAll("%now_playing%", String.valueOf(gamemodeNowPlayingMap.getOrDefault(plugin.getNpcManager().getGameModeFromNPC(new FormattedLocation().getLocationFromFormattedString(new FormattedLocation().getFormattedLocation(npcLocation).replaceAll("\\*", "\\."))), 0))));
                    }
                }
            }
        }
    }

    public void updateAllHolograms() {
        ArrayList<String> hologramList = (ArrayList<String>) config.getList("holograms.hologram");

        for(Hologram hologram : HologramsAPI.getHolograms(plugin)) {
            for(NPC npc : CitizensAPI.getNPCRegistry()) {

                if(hologramList.size() == 0) {
                    continue;
                }

                for(int i = 0; i  < hologramList.size(); i++) {

                    if(!plugin.getNPCLocations().contains("npc" + npc.getId())) {
                        continue;
                    }


                    double offset = config.getDouble("holograms.npc_offset.x") + config.getDouble("holograms.npc_offset.y") + config.getDouble("holograms.npc_offset.z");

                    if(!(npc.getStoredLocation().distance(hologram.getLocation()) <= offset)) {
                        continue;
                    }

                    if(hologramList.get(i).contains("%now_playing")) {
                        hologram.getLine(i).removeLine();
                        hologram.insertTextLine(i, hologramList.get(i).replaceAll("&", "§")
                                .replaceAll("%gamemode%", plugin.getNPCLocations().getString("npc" + npc.getId())).replaceAll("%now_playing%", String.valueOf(gamemodeNowPlayingMap.getOrDefault(plugin.getNPCLocations().getString("npc" + npc.getId()), 0))));
                    }
                }
            }
        }
    }

    public void updatePlayerCountForGamemodes() {
        gamemodeNowPlayingMap.clear();
        Thread thread = new Thread(() -> {
            for(String gamemode : plugin.getMongoManager().getDatabase().listCollectionNames()) {
                MongoCollection<Document> collection = plugin.getMongoManager().getDatabase().getCollection(gamemode);
                for(Document document : collection.find()) {
                    if(document == null) {
                        continue;
                    }

                    if(document.getString("serverName") == null || document.getInteger("onlinePlayers") == null) {
                        continue;
                    }
                    //Bukkit.broadcastMessage("GAMEMODE: '" + gamemode + "'");
                    if(!gamemodeNowPlayingMap.containsKey(gamemode)) {
                        gamemodeNowPlayingMap.put(gamemode, document.getInteger("onlinePlayers", 0));
                    } else {
                        int amt = gamemodeNowPlayingMap.get(gamemode) + document.getInteger("onlinePlayers", 0);
                        gamemodeNowPlayingMap.put(gamemode, amt);
                    }
                }
            }
        });
        thread.start();
    }

    public void startPlayerCountUpdateTask() {
        new BukkitRunnable() {
            public void run() {
                updatePlayerCountForGamemodes();
            }
        }.runTaskTimerAsynchronously(plugin, 40, 3 * 20);
    }

    public void startHologramUpdateTask() {
        new BukkitRunnable() {
            public void run() {
                updateAllHolograms();
            }
        }.runTaskTimer(plugin, 40, 5 * 20);
    }
}
