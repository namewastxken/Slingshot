package com.hylandermc.managers;

import com.hylandermc.Slingshot;
import com.hylandermc.config.MyConfig;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

public class GameManager {

    private Slingshot plugin;
    private MongoManager mongoManager;
    private MyConfig config;
    public GameManager(Slingshot instance) {
        this.plugin = instance;
        this.config = plugin.getOptions();
        this.mongoManager = plugin.getMongoManager();
    }

    /**
     * Sets the current game server as joinable
     * @param joinable - boolean
     */
    public void setJoinable(Boolean joinable) {
        String gamemode = config.getString("game_information.gamemode");
        String server = config.getString("game_information.server_name");

        MongoCollection<Document> collection = mongoManager.getDatabase().getCollection(gamemode);

        Document document = mongoManager.getDocument(collection,  server);

        Document updateInformation = new Document();
        updateInformation.put("joinable", joinable);

        Document updateDocument = new Document();
        updateDocument.put("$set", updateInformation);
        collection.updateOne(document, updateDocument);
    }

    /**
     * Sets the gamestate of the server
     * @param gameState - new game state
     */
    public void setGameState(String gameState) {
        String gamemode = config.getString("game_information.gamemode");
        String server = config.getString("game_information.server_name");
        MongoCollection<Document> collection = mongoManager.getDatabase().getCollection(gamemode);

        Document document = mongoManager.getDocument(collection,  server);

        Document updateInformation = new Document();
        updateInformation.put("gameState", gameState);

        Document updateDocument = new Document();
        updateDocument.put("$set", updateInformation);
        collection.updateOne(document, updateDocument);
    }

    /**
     * Sets the max players of the game
     * @param players - max players
     */
    public void setMaxPlayers(int players) {
        String gamemode = config.getString("game_information.gamemode");
        String server = config.getString("game_information.server_name");

        MongoCollection<Document> collection = mongoManager.getDatabase().getCollection(gamemode);

        Document document = mongoManager.getDocument(collection,  server);

        Document updateInformation = new Document();
        updateInformation.put("maxPlayers", players);

        Document updateDocument = new Document();
        updateDocument.put("$set", updateInformation);
        collection.updateOne(document, updateDocument);
    }

    public void setPlayersOnline(int playersOnline) {
        String gamemode = config.getString("game_information.gamemode");
        String server = config.getString("game_information.server_name");

        MongoCollection<Document> collection = mongoManager.getDatabase().getCollection(gamemode);

        Document document = mongoManager.getDocument(collection,  server);

        Document updateInformation = new Document();
        updateInformation.put("onlinePlayers", playersOnline);

        Document updateDocument = new Document();
        updateDocument.put("$set", updateInformation);
        collection.updateOne(document, updateDocument);
    }


    public int getMaxPlayers() {
        String gamemode = config.getString("game_information.gamemode");
        String server = config.getString("game_information.server_name");

        int players = getIntegerField(gamemode, server, "maxPlayers");
        return players;
    }

    public boolean isJoinable() {
        String gamemode = config.getString("game_information.gamemode");
        String server = config.getString("game_information.server_name");
        Object joinable = getField(gamemode, server, "joinable");
        return (Boolean) joinable;
    }

    public String getGameState() {
        String gamemode = config.getString("game_information.gamemode");
        String server = config.getString("game_information.server_name");

        return (String) getField(gamemode, server, "gameState");
    }

    public Object getField(String gamemode, String server, String field) {
        MongoCollection<Document> collection = mongoManager.getDatabase().getCollection(gamemode);

        Document document = mongoManager.getDocument(collection,  server);

        return document.get(field);
    }

    public int getIntegerField(String gamemode, String server, String field) {
        MongoCollection<Document> collection = mongoManager.getDatabase().getCollection(gamemode);

        Document document = mongoManager.getDocument(collection,  server);
        if(document == null || document.getInteger(field) == null) {
            return -1;
        }
        return document.getInteger(field);
    }

    public List<String> getJoinableServers(String gamemode) {

        List<String> list = new ArrayList<String>();
        MongoCollection<Document> collection = mongoManager.getDatabase().getCollection(gamemode);
        FindIterable<Document> documents = collection.find(eq("joinable", true));

        for(Document doc : documents) {
            list.add(doc.getString("serverName"));
        }
        return list;
    }

    public String getServerIP(String server) {

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        ByteArrayDataInput in = ByteStreams.newDataInput(b.toByteArray());

        try {
            out.writeUTF("ServerIP");
            out.writeUTF(server);

            String serverName = in.readUTF();
            String ip = in.readUTF();
            short port = in.readShort();
            return ip + ":" + port;
        } catch (IOException e) {
            return null;
        }
    }

    public boolean isConnectable(String gamemode, String server) {
        String host = (String) getField(gamemode, server, "serverIp");
        int port = getIntegerField(gamemode, server, "serverPort");

        if(host == null || port == -1) {
            return false;
        }

        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port), 3);
            socket.close();
            return true;
        } catch (IOException e) {
            if(!socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        }
    }

    public void checkConnections() {
        new BukkitRunnable() {
            public void run() {
                for(String gamemode : mongoManager.getDatabase().listCollectionNames()) {
                    MongoCollection<Document> collection = mongoManager.getDatabase().getCollection(gamemode);
                    for(Document document : collection.find(eq("joinable", true))) {
                        if(document.getString("serverName") == null) {
                            continue;
                        }

                        if(!isConnectable(gamemode, document.getString("serverName"))) {
                            System.out.println("Server " + document.getString("serverName") + " is offline, removing from server queue.");
                            setField(gamemode, document.getString("serverName"), "joinable", false);
                            setField(gamemode, document.getString("serverName"), "onlinePlayers", 0);
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 20, 120 * 20);
    }

    public String getFirstJoinable(String gamemode) {
        MongoCollection<Document> collection = mongoManager.getDatabase().getCollection(gamemode);
        Document document = collection.find(eq("joinable", true)).first();

        if(document == null) {
            return null;
        }

        return document.getString("serverName");
    }

    public void sendPlayerToServer(Player p, String server) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Connect");
        output.writeUTF(server);
        p.sendPluginMessage(this.plugin, "BungeeCord", output.toByteArray());
    }

    private void setField(String gamemode, String server, String field, Object fieldValue) {
        MongoCollection<Document> collection = mongoManager.getDatabase().getCollection(gamemode);

        Document document = mongoManager.getDocument(collection,  server);

        Document updateInformation = new Document();
        updateInformation.put(field, fieldValue);

        Document updateDocument = new Document();
        updateDocument.put("$set", updateInformation);
        collection.updateOne(document, updateDocument);
    }

    public void updateMultipleFields(String gamemode, String server, Map<String, Object> map) {
        MongoCollection<Document> collection = mongoManager.getDatabase().getCollection(gamemode);

        Document document = mongoManager.getDocument(collection,  server);

        Document updateInformation = new Document();

        for(String string : map.keySet()) {
            updateInformation.put(string, map.get(string));
        }

        Document updateDocument = new Document();
        updateDocument.put("$set", updateInformation);
        collection.updateOne(document, updateDocument);
    }
}
