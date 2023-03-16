/**
 * SkillAPI
 * com.sucy.skill.data.io.SQLIO
 * <p>
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2014 Steven Sucy
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software") to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.sucy.skill.data.io;

import com.mengcraft.simpleorm.MongoWrapper;
import com.mengcraft.simpleorm.ORM;
import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.player.PlayerAccounts;
import com.sucy.skill.data.Settings;
import com.sucy.skill.log.Logger;
import heypixel.com.mongodb.BasicDBObject;
import io.lumine.mythic.bukkit.utils.lib.jooq.tools.json.JSONObject;
import lombok.Getter;
import mc.promcteam.engine.mccore.config.parse.DataSection;
import mc.promcteam.engine.mccore.config.parse.JSONParser;
import mc.promcteam.engine.mccore.sql.direct.SQLDatabase;
import mc.promcteam.engine.mccore.sql.direct.SQLTable;
import mc.promcteam.engine.mccore.util.VersionManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Loads player data from the MongoDB Database
 */
public class MongoIO extends IOManager {

    @Getter
    private MongoWrapper.MongoDatabaseWrapper mongoCollection;

    /**
     * Initializes the SQL IO Manager
     *
     * @param api API reference
     */
    public MongoIO(SkillAPI api) {
        super(api);
        manageMongo();
    }

    private void manageMongo() {
        MongoWrapper mongoWrapper = ORM.globalMongoWrapper();
        mongoWrapper.ping();
        Settings settings = SkillAPI.getSettings();
        this.mongoCollection = mongoWrapper.open(
                settings.getMongoDatabase(),
                settings.getMongoTable());
        this.mongoCollection.open(dbCollection -> {
            dbCollection.createIndex(
                    new BasicDBObject("_id", ""),
                    (new BasicDBObject("name", "_id")).append("unique", true));

        });
    }

    @Override
    public HashMap<String, PlayerAccounts> loadAll() {

        HashMap<String, PlayerAccounts> result = new HashMap<String, PlayerAccounts>();
        for (Player player : VersionManager.getOnlinePlayers()) {
            result.put(player.getUniqueId().toString().toLowerCase(), load(player));
        }

        return result;
    }

    @Override
    public PlayerAccounts loadData(OfflinePlayer player) {
        if (player == null) return null;

        return load(player);
    }

    @Override
    public void saveData(PlayerAccounts data) {
        saveSingle(data);
    }

    @Override
    public void saveAll() {
        HashMap<String, PlayerAccounts> data = SkillAPI.getPlayerAccountData();
        ArrayList<String> keys = new ArrayList<String>(data.keySet());
        for (String key : keys) {
            saveSingle(data.get(key));
        }
    }

    private PlayerAccounts load(OfflinePlayer player) {
        try {
            Map<?, ?> map = this.mongoCollection.find(Map.class, new BasicDBObject("_id", player.getUniqueId().toString().toLowerCase()));
            String s = JSONObject.toJSONString(map);

            DataSection file = JSONParser.parseText(s); //TODO 检查此处是否工作良好
            return loadMongo(player, file);
        } catch (Exception ex) {
            Logger.bug("Failed to load data from the SQL Database - " + ex.getMessage());
            return null;
        }
    }

    private void saveSingle(PlayerAccounts data) {
        DataSection file = save(data);
        Set<Map.Entry<String, Object>> entries = file.entrySet();

//        Map<String, Object> mapFromSet = entries.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b)->b));
        Map<String, Object> mapFromSet = new HashMap<>();
        toMap(file, mapFromSet);

        try {
            String playerKey = data.getOfflinePlayer().getUniqueId().toString().toLowerCase();

            boolean isEmpty = this.mongoCollection.find(Map.class, new BasicDBObject("_id", playerKey)) == null;

            this.mongoCollection.open(dbCollection -> {
                if (isEmpty) {
                    System.out.println(mapFromSet);
                    dbCollection.save(new BasicDBObject(ORM.serialize(mapFromSet)).append("_id", playerKey));
                } else {
                    dbCollection.update(new BasicDBObject("_id", playerKey), new BasicDBObject("$set", new BasicDBObject(ORM.serialize(mapFromSet))), true, false);
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void toMap(DataSection dataSection, Map<String, Object> map) {
        for (String key : dataSection.keys()) {
            if (dataSection.get(key) instanceof DataSection) {
                DataSection ds = (DataSection) dataSection.get(key);
                HashMap<String, Object> mp = new HashMap<>();
                toMap(ds, mp);
                map.put(key, mp);
            } else {
                map.put(key, dataSection.get(key));
            }

        }
    }
}
