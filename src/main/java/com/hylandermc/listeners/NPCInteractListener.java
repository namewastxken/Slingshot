package com.hylandermc.listeners;

import com.hylandermc.Slingshot;
import com.hylandermc.config.MyConfig;
import com.hylandermc.managers.GameManager;
import com.hylandermc.managers.HologramLocationManager;
import com.hylandermc.managers.HologramManager;
import com.hylandermc.managers.NPCManager;
import net.citizensnpcs.api.event.NPCRemoveEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NPCInteractListener implements Listener {

    private Slingshot plugin;
    private MyConfig config;
    private NPCManager locationManager;
    private HologramManager hologramManager;
    private HologramLocationManager hologramLocationManager;
    private GameManager gameManager;

    public NPCInteractListener(Slingshot instance) {
        this.plugin = instance;
        this.config = plugin.getOptions();
        this.hologramManager = plugin.getHologramManager();
        this.hologramLocationManager = plugin.getHologramLocationManager();
        this.locationManager = plugin.getNpcManager();
        this.gameManager = plugin.getGameManager();
    }

    @EventHandler
    public void onNPCRemove(NPCRemoveEvent e) {
        NPC npc = e.getNPC();
        if(locationManager.isNPCLocation(npc.getStoredLocation())) {
            locationManager.deleteNPCLocation(npc, npc.getStoredLocation());
            hologramManager.removeHologram(npc.getStoredLocation());
            return;
        }
    }

    @EventHandler
    public void onNPCInteract(NPCRightClickEvent e) {
        Player p = e.getClicker();
        NPC npc = e.getNPC();

        if(!locationManager.isSelectingNPC(p)) {
            if(locationManager.isNPC(npc)) {
                e.setCancelled(true);
               // p.sendMessage("Is NPC");
                //p.sendMessage("Stored Gamemode: " + locationManager.getGameModeFromNPC(npc.getStoredLocation()));

                if(gameManager.getFirstJoinable(locationManager.getGameModeFromNPC(npc)) != null) {
                    p.sendMessage(config.getString("messages.joining_game").replaceAll("&", "§").replaceAll("%gamemode%", locationManager.getGameModeFromNPC(npc)));
                    gameManager.sendPlayerToServer(p, gameManager.getFirstJoinable(locationManager.getGameModeFromNPC(npc)));
                } else {
                    p.sendMessage(config.getString("messages.no_game_available").replaceAll("&", "§").replaceAll("%gamemode%", locationManager.getGameModeFromNPC(npc)));
                    return;
                }
                return;
            }
        } else {

            if (locationManager.isNPCLocation(npc.getStoredLocation())) {
                p.sendMessage("§cYou cannot this npc as it is already stored.");
                locationManager.stopNPCSelection(p);
                locationManager.removeGamemode(p);
                return;
            }

            locationManager.stopNPCSelection(p);
            locationManager.setNPCLocation(npc.getStoredLocation(), locationManager.getGamemode(p));
            locationManager.setNPC(npc, locationManager.getGamemode(p));
            locationManager.removeGamemode(p);
            p.sendMessage(config.getString("messages.set_npc").replaceAll("&", "§"));

            if (config.getBoolean("holograms.enabled")) {
                plugin.getHologramManager().addNPCHologram(npc);
            }
            return;
        }
        return;
        // add effect later
    }
}
