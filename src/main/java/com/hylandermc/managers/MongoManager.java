package com.hylandermc.managers;

import com.hylandermc.Slingshot;
import com.hylandermc.config.MyConfig;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;

public class MongoManager {

    private Slingshot plugin;
    private MyConfig config;
    @Getter private MongoClient mongoClient;
    @Getter private MongoDatabase database;
    public MongoManager(Slingshot instance) {
        this.plugin = instance;
        this.config = plugin.getOptions();
    }


    public void connect() {
        try {
            final MongoCredential credential = MongoCredential.createCredential(
                    config.getString("mongo_database.username"),
                    config.getString("mongo_database.database_name"),
                    config.getString("mongo_database.password").toCharArray()
            );
            mongoClient = new MongoClient(new ServerAddress(config.getString("mongo_database.host"), config.getInt("mongo_database.port")), Collections.singletonList(credential));
            database = mongoClient.getDatabase(config.getString("mongo_database.database_name"));
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("§c[Slingshot] Cannot connect to MongoDB.");
        }
    }

    public void setupGameInformation() {
        MongoCollection<Document> collection = database.getCollection(config.getString("game_information.gamemode"));

        String server = config.getString("game_information.server_name");

        if(!hasDocument(collection, server)) {
            Document document = new Document();
            document.put("serverName", server);
            document.put("gameState", "n/a");
            document.put("joinable", true);
            document.put("onlinePlayers", 0);
            document.put("maxPlayers", config.getInt("game_information.max_players"));
            collection.insertOne(document);
        } else {

            // updating information
            Map<String, Object> map = new HashMap<String, Object>();
            if(plugin.getGameManager() == null) {
                Bukkit.getConsoleSender().sendMessage("§cGAME MANAGER IS NULL");
            }

            map.put("onlinePlayers", 0);
            if(config.getInt("game_information.max_players") != plugin.getGameManager().getMaxPlayers()) {
                plugin.getGameManager().setMaxPlayers(config.getInt("game_information.max_players"));
                map.put("maxPlayers", config.getInt("game_information.max_players"));
            }

            map.put("gameState", "n/a");
            if(config.getBoolean("misc_options.auto_set_joinable")) {
                map.put("joinable", true);
            }
            map.put("serverIp", config.getString("game_information.server_ip"));
            map.put("serverPort", config.getInt("game_information.port"));
            plugin.getGameManager().updateMultipleFields(config.getString("game_information.gamemode"), config.getString("game_information.server_name"), map);
        }
    }

    public boolean hasDocument(MongoCollection<Document> collection, String server) {
        Document document = collection.find(eq("serverName", server)).first();
        if(document != null) {
           // System.out.print(document.toString());
            return true;
        }
        return false;
    }

    public Document getDocument(MongoCollection<Document> collection, String server) {
        Document document = collection.find(eq("serverName", server)).first();
        if(document != null) {
            //System.out.print(document.toString());
            return document;
        }
        return null;
    }

}
