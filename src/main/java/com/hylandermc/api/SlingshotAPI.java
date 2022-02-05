package com.hylandermc.api;

import com.hylandermc.Slingshot;
import org.bukkit.Bukkit;

public class SlingshotAPI {

    public static void setJoinable(Boolean joinable) {
        getInstance().getGameManager().setJoinable(joinable);
    }

    /**
     * Sets the gamestate of the server
     * @param gameState - new game state
     */
    public static void setGameState(String gameState) {
        getInstance().getGameManager().setGameState(gameState);
    }

    /**
     * Sets the max players of the game
     * @param players - max players
     */
    public static void setMaxPlayers(int players) {
        getInstance().getGameManager().setMaxPlayers(players);
    }

    public static int getMaxPlayers() {
        return getInstance().getGameManager().getMaxPlayers();
    }

    public static boolean isJoinable() {
        return getInstance().getGameManager().isJoinable();
    }

    public static String getGameState() {
        return getInstance().getGameManager().getGameState();
    }

    private static Slingshot getInstance() {
        return (Slingshot) Bukkit.getPluginManager().getPlugin("Slingshot");
    }

}
