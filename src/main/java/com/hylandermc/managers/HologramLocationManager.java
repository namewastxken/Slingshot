package com.hylandermc.managers;

import com.hylandermc.Slingshot;
import com.hylandermc.config.MyConfig;
import com.hylandermc.objects.FormattedLocation;
import org.bukkit.Location;

import java.util.ArrayList;

public class HologramLocationManager {
    private Slingshot plugin;
    private MyConfig config;
    private MyConfig npcLocations;
    private FormattedLocation formatedLocation;
    public HologramLocationManager(Slingshot instance) {
        plugin = instance;
        config = plugin.getOptions();
        npcLocations = plugin.getNPCLocations();
        formatedLocation = new FormattedLocation();
    }

    /**
     * Adds a location to the npc locations to keep track of where holograms are located.
     * @param location
     */
    public void addHologramLocation(Location location) {

        if(!npcLocations.contains("hologramlocations")) {

            ArrayList<String> locations = new ArrayList<String>();
            locations.add(formatedLocation.getFormattedLocation(location));

            npcLocations.set("hologramlocations", locations.toArray());
            npcLocations.saveConfig();
            npcLocations.reloadConfig();
            return;
        }

        ArrayList<String> locations = (ArrayList<String>) npcLocations.getList("hologramlocations");

        locations.add(formatedLocation.getFormattedLocation(location));
        npcLocations.set("hologramlocations", locations.toArray());
        npcLocations.saveConfig();
        npcLocations.reloadConfig();
    }

    public void deleteHologramLocation(Location location) {

        if(!npcLocations.contains("hologramlocations")) {
            return;
        }

        ArrayList<String> locations = (ArrayList<String>) npcLocations.getList("hologramlocations");

        if(locations.contains(formatedLocation.getFormattedLocation(location))) {
            locations.remove(formatedLocation.getFormattedLocation(location));
            npcLocations.set("hologramlocations", locations.toArray());
            npcLocations.saveConfig();
            npcLocations.reloadConfig();
        }
    }

    /**
     * @return lists holograms
     */
    public ArrayList<Location> getLocations() {
        ArrayList<Location> locations = new ArrayList<Location>();

        locations.clear();

        if(npcLocations.contains("hologramlocations")) {
            ArrayList<String> formattedLocations = (ArrayList<String>) npcLocations.getList("hologramlocations");
            for(String string : formattedLocations) {
                locations.add(formatedLocation.getLocationFromFormattedString(string));
            }
        }

        return locations;
    }

    /**
     * @param location - location query
     * @returns boolean whether it is a valid location.
     */
    public boolean isHologramLocation(Location location) {

        if(!npcLocations.contains("hologramlocations")) {
            return false;
        }

        ArrayList<String> locations = (ArrayList<String>) npcLocations.getList("hologramlocations");

        if(locations.contains(formatedLocation.getFormattedLocation(location))) {
            return true;
        }

        return false;
    }



}
