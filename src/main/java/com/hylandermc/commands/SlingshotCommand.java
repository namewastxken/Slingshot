package com.hylandermc.commands;

import com.hylandermc.Slingshot;
import com.hylandermc.config.MyConfig;
import com.hylandermc.managers.MongoManager;
import com.hylandermc.managers.NPCManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SlingshotCommand implements CommandExecutor {

    private Slingshot plugin;
    private MyConfig config;
    private MongoManager mongoManager;
    private NPCManager npcManager;
    public SlingshotCommand(Slingshot instance) {
        this.plugin = instance;
        this.config = plugin.getOptions();
        this.mongoManager = plugin.getMongoManager();
        this.npcManager = plugin.getNpcManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, java.lang.String label, java.lang.String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("§cYou are unable to run this command in console.");
            return true;
        }

        Player p = (Player) sender;

        if(!p.isOp()) {
            p.sendMessage("§cNo Permission.");
            return true;
        }

        if(args.length == 0) {
            p.sendMessage("§7§m-------------------------");
            p.sendMessage("§b§lLobby Commands:");
            p.sendMessage("  §9* §b/slingshot setnpc <gamemode> §7- §7sets npc for gamemode quick join.");
            p.sendMessage("§7§m-------------------------");
        }

        if(args.length >= 1) {
            if(args[0].equalsIgnoreCase("firstjoin")) {
                StringBuilder sb = new StringBuilder();
                for(int i = 1; i < args.length; i++) {
                    sb.append(args[i]).append(" ");
                }

                //Bukkit.broadcastMessage("'" +sb.toString().trim() + "'");

                if(plugin.getGameManager().getFirstJoinable(sb.toString().trim()) == null) {
                    p.sendMessage("§cThere are no games available.");
                    return true;
                }

                p.sendMessage(plugin.getGameManager().getFirstJoinable(sb.toString().trim()));
            }

            if(args[0].equalsIgnoreCase("setnpc")) {

                if(!config.getString("server_type").equalsIgnoreCase("lobby")) {
                    p.sendMessage("§cThis command is disabled in a game server.");
                    return true;
                }

                StringBuilder sb = new StringBuilder();
                for(int i = 1; i < args.length; i++) {
                    sb.append(args[i]).append(" ");
                }

                if(npcManager.isSelectingNPC(p)) {
                    npcManager.stopNPCSelection(p);
                    npcManager.removeGamemode(p);
                    p.sendMessage("§cStopped npc selection.");
                    return true;
                }

                //Bukkit.broadcastMessage("'" +sb.toString().trim() + "'");
                p.sendMessage(config.getString("messages.setting_npc").replaceAll("%gamemode%", sb.toString().trim()).replaceAll("&", "§"));
                plugin.getNpcManager().startNPCSelection(p);
                plugin.getNpcManager().setGamemode(p, sb.toString().trim());
                return true;
            }

        }
        return true;
    }
}
