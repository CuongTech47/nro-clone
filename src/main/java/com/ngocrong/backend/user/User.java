package com.ngocrong.backend.user;


import com.google.common.base.CharMatcher;
import com.google.gson.Gson;
import com.ngocrong.backend.character.CharacterInfo;
import com.ngocrong.backend.entity.PlayerEntity;
import com.ngocrong.backend.entity.UserEntity;
import com.ngocrong.backend.item.Item;
import com.ngocrong.backend.model.Achievement;
import com.ngocrong.backend.network.Session;
import com.ngocrong.backend.repository.GameRepo;
import com.ngocrong.backend.server.Config;
import com.ngocrong.backend.server.DragonBall;
import com.ngocrong.backend.server.Server;
import com.ngocrong.backend.server.SessionManager;
import lombok.Data;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class User {

    private static final Logger logger = Logger.getLogger(User.class);
    private static final int[][] HAIR_ID = {{64, 30, 31}, {9, 29, 32}, {6, 27, 28}};
    private static final int[][] LOCATION = {{39, 100, 384}, {40, 100, 384}, {41, 100, 384}};

    private int id;
    private String username;
    private String password;
    private int status;
    private Session session;
    private Timestamp lockTime;




    public User(String username, String password, Session session) {
        this.username = username.toLowerCase();
        this.password = password;
        this.session = session;
    }

    public int login() throws SQLException {
        Server server = DragonBall.getInstance().getServer();
        if (server.isMaintained()) {
            return 5;
        }
        if (!isValidUsername(username)) {
            return 6;
        }
        UserEntity userData = authenticateUser(username, password);
        if (userData == null) {
            return 0;
        }
        initializeUser(userData);
        return handleExistingSessions(username);
    }

    public byte createChar(String name, byte gender, short hair) {
        try {
            if (!isValidCharacterName(name)) {
                return 1;
            }
            if (isReservedName(name)) {
                return 5;
            }
            if (isNameExists(name)) {
                return 4;
            }

            gender = validateGender(gender);
            hair = selectHairStyle(gender, hair);

            PlayerEntity data = initializePlayerData(name, gender, hair);
            GameRepo.getInstance().playerRepo.save(data);

            return 0;
        } catch (Exception ex) {
            logger.error("Character creation failed!", ex);
            return 3;
        }
    }

    public void close() {
        session = null;
        password = null;
        username = null;
    }

    private boolean isValidUsername(String username) {
        return CharMatcher.javaLetterOrDigit().matchesAllOf(username);
    }

    private UserEntity authenticateUser(String username, String password) {
        List<UserEntity> userDataList = GameRepo.getInstance().userRepo.findByUsernameAndPassword(username, password);
        return userDataList.isEmpty() ? null : userDataList.get(0);
    }

    private void initializeUser(UserEntity userData) {
        this.id = userData.getId();
        this.status = userData.getStatus();
        this.lockTime = userData.getLockTime();
    }

    private int handleExistingSessions(String username) {
        List<User> userList = SessionManager.findUser(username);
        if (!userList.isEmpty()) {
            disconnectExistingSessions(userList);
            return 3;
        }
        if (status == 1) {
            return 2;
        }
        if (lockTime != null) {
            return 4;
        }
        return 1;
    }

    private void disconnectExistingSessions(List<User> userList) {
        userList.forEach(user -> user.getSession().disconnect());
    }

    private boolean isValidCharacterName(String name) {
        int length = name.length();
        Pattern pattern = Pattern.compile("^[a-z0-9]+$");
        Matcher matcher = pattern.matcher(name);
        return length >= 5 && length <= 15 && matcher.find();
    }

    private boolean isReservedName(String name) {
        return name.contains("admin") || name.contains("server");
    }

    private boolean isNameExists(String name) {
        return !GameRepo.getInstance().playerRepo.findByName(name).isEmpty();
    }

    private byte validateGender(byte gender) {
        return (gender < 0 || gender > 2) ? 0 : gender;
    }

    private short selectHairStyle(byte gender, short hair) {
        int[] hairOptions = HAIR_ID[gender];
        for (int hairOption : hairOptions) {
            if (hairOption == hair) {
                return hair;
            }
        }
        return (short) hairOptions[0];
    }

    private PlayerEntity initializePlayerData(String name, byte gender, short hair) {
        Config config = DragonBall.getInstance().getServer().getConfig();
        List<Item> itemBodys = initializeItems(gender);
        List<Item> itemBoxs = initializeBoxItems();
//        MagicTree magicTree = new MagicTree();
        CharacterInfo info = new CharacterInfo(gender);
        info.recovery(CharacterInfo.ALL, 100, false);
        info.setSatamina();

        Gson gson = new Gson();
        PlayerEntity data = new PlayerEntity();
        data.userId = id;
        data.serverId = config.getServerID();
        data.name = name;
        data.gender = gender;
        data.classId = gender;
        data.head = hair;
        data.task = "{\"id\":0,\"index\":0,\"count\":0}";
        data.gold = 2000L;
        data.diamond = 20;

        data.itemBody = gson.toJson(itemBodys);
        data.itemBox = gson.toJson(itemBoxs);
        data.map = gson.toJson(LOCATION[gender]);
        data.info = gson.toJson(info);
//        data.magicTree = gson.toJson(magicTree);
        data.numberCellBag = 20;
        data.numberCellBox = 20;
        data.achievement = gson.toJson(initializeAchievements());
        data.createTime = new Timestamp(System.currentTimeMillis());
        data.resetTime = data.createTime;
        return data;
    }

    private List<Item> initializeItems(byte gender) {
        List<Item> items = new ArrayList<>();
        items.add(createItem(gender == 0 ? 0 : gender == 1 ? 1 : 2, 0));
        items.add(createItem(gender == 0 ? 6 : gender == 1 ? 7 : 8, 1));
        return items;
    }

    private List<Item> initializeBoxItems() {
        List<Item> items = new ArrayList<>();
        items.add(createItem(12, 0));
        return items;
    }

    private Item createItem(int itemId, int indexUI) {
        Item item = new Item(itemId);
        item.indexUI = indexUI;
        item.quantity = 1;
        item.setDefaultOptions();
        return item;
    }

    private List<Achievement> initializeAchievements() {
        List<Achievement> achievements = new ArrayList<>();
        int totalAchievements = DragonBall.getInstance().getServer().getAchievements().size();
        for (int i = 0; i < totalAchievements; i++) {
            achievements.add(new Achievement(i));
        }
        return achievements;
    }


}