package com.hylandermc.managers;

import com.hylandermc.Slingshot;
import com.hylandermc.config.MyConfig;
import com.hylandermc.objects.FormattedLocation;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NPCManager {

    private Slingshot plugin;
    private MyConfig npcLocations;
    private FormattedLocation formatedLocation;
    private List<String> settingNPC;
    private Map<Player, String> gamemodeMap;
    public NPCManager(Slingshot instance) {
        this.plugin = instance;
        this.npcLocations = plugin.getNPCLocations();
        this.formatedLocation = new FormattedLocation();
        this.settingNPC = new ArrayList<String>();
        this.gamemodeMap = new HashMap<Player, String>();
    }

    /**
     * Adds new NPC location
     * @param location
     */
    public void setNPCLocation(Location location, String gamemode) {

        if(!npcLocations.contains("npclocations")) {
            ArrayList<String> locations = new ArrayList<String>();
            locations.add(formatedLocation.getFormattedLocation(location));

            npcLocations.set("npclocations", locations.toArray());
            npcLocations.set(formatedLocation.getFormattedLocation(location).replaceAll("\\.", "\\*"), gamemode);
            npcLocations.saveConfig();
            npcLocations.reloadConfig();
            return;
        }

        ArrayList<String> locations = (ArrayList<String>) npcLocations.getList("npclocations");

        locations.add(formatedLocation.getFormattedLocation(location));
        npcLocations.set("npclocations", locations.toArray());
        npcLocations.set(formatedLocation.getFormattedLocation(location).replaceAll("\\.", "\\*"), gamemode);
        npcLocations.saveConfig();
        npcLocations.reloadConfig();
    }

    public void setNPC(NPC npc, String gamemode) {
        npcLocations.set("npc" + npc.getId(), gamemode);
        npcLocations.saveConfig();
        npcLocations.reloadConfig();
    }

    /**
     * Removes npc location
     * @param location
     */
    public void deleteNPCLocation(NPC npc, Location location) {

        if(!npcLocations.contains("npclocations")) {
            return;
        }

        ArrayList<String> locations = (ArrayList<String>) npcLocations.getList("npclocations");

        if(locations.contains(formatedLocation.getFormattedLocation(location))) {
            locations.remove(formatedLocation.getFormattedLocation(location));
            npcLocations.set("npclocations", locations.toArray());
            npcLocations.saveConfig();
            npcLocations.reloadConfig();
        }
        npcLocations.removeKey("npc" + npc.getId());
        npcLocations.removeKey(formatedLocation.getFormattedLocation(location));
    }

    /**
     * Gets list of Locations.
     * @return
     */
    public ArrayList<Location> getNPCLocations() {
        ArrayList<Location> locations = new ArrayList<Location>();

        locations.clear();

        if(npcLocations.contains("npclocations")) {
            ArrayList<String> formattedLocations = (ArrayList<String>) npcLocations.getList("npclocations");
            for(String string : formattedLocations) {
                locations.add(formatedLocation.getLocationFromFormattedString(string));
            }
        }

        return locations;
    }

    /**
     * Boolean of whether npc is stored.
     * @param location
     * @return
     */
    public boolean isNPCLocation(Location location) {

        if(!npcLocations.contains("npclocations")) {
            return false;
        }

        ArrayList<String> locations = (ArrayList<String>) npcLocations.getList("npclocations");

        if(locations.contains(formatedLocation.getFormattedLocation(location))) {
            return true;
        }

        return false;
    }

    public boolean isNPC(NPC npc) {
        return npcLocations.contains("npc" + npc.getId());
    }


    /**
     *
     * @param location - location
     * @return stored gamemode
     */
    public String getGameModeFromLocation(Location location) {
        return npcLocations.getString(formatedLocation.getFormattedLocation(location).replaceAll("\\.", "\\*"));
    }

    public String getGameModeFromNPC(NPC npc) {
        return npcLocations.getString("npc" + npc.getId());
    }

    public void startNPCSelection(Player p) {
        settingNPC.add(p.getName());
    }

    public void stopNPCSelection(Player p) {
        settingNPC.remove(p.getName());
    }

    public boolean isSelectingNPC(Player p) {
        return settingNPC.contains(p.getName());
    }

    public String getGamemode(Player p) {
        return gamemodeMap.get(p);
    }

    public void removeGamemode(Player p) {
        gamemodeMap.remove(p);
    }

    public void setGamemode(Player p, String gamemode) {
        gamemodeMap.put(p, gamemode);
    }



}
