package com.hylandermc.objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class FormattedLocation {
    public FormattedLocation() {

    }

    public String getFormattedLocation(Location location) {
        String loc = "(" + location.getWorld().getName() + ", " + location.getX() + ", " + location.getY() + ", " + location.getZ()  + ")";
        return loc;
    }

    public Location getLocationFromFormattedString(String format) {
        String cut = format.substring(1,format.length() -1).replaceAll(" ", "");

        String[] information = cut.split(",");
        String worldName = information[0];
        double x = Double.parseDouble(information[1]);
        double y = Double.parseDouble(information[2]);
        double z = Double.parseDouble(information[3]);

        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }

}
