package com.hylandermc.api;

import com.hylandermc.Slingshot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SlingshotLobbyAPI {

    public static void attemptPlayerSend(Player player, String gamemode) {
        if(getInstance().getGameManager().getFirstJoinable(gamemode) != null) {
            player.sendMessage(getInstance().getOptions().getString("messages.joining_game").replaceAll("&", "§").replaceAll("%gamemode%", gamemode));
            getInstance().getGameManager().sendPlayerToServer(player, getInstance().getGameManager().getFirstJoinable(gamemode));
        } else {
            player.sendMessage(getInstance().getOptions().getString("messages.no_game_available").replaceAll("&", "§").replaceAll("%gamemode%", gamemode));
            return;
        }
    }

    public static boolean isJoinable(String gamemode) {
        return getInstance().getGameManager().getFirstJoinable(gamemode) != null;
    }

    public static int getPlayersPlayingGamemode(String gamemode) {
        return getInstance().getHologramManager().getGamemodeNowPlayingMap().getOrDefault(gamemode, 0);
    }

    private static Slingshot getInstance() {
        return (Slingshot) Bukkit.getPluginManager().getPlugin("Slingshot");
    }
}
