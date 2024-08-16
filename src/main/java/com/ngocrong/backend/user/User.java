package com.ngocrong.backend.user;


import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ngocrong.backend.model.MagicTree;
import org.apache.log4j.Logger;

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
        if (server.isMaintained) {
            return 5;
        }
        if (!isUsernameValid(username)) {
            return 6;
        }
        List<UserEntity> userDataList = GameRepo.getInstance().userRepo.findByUsernameAndPassword(username, password);
        if (userDataList.isEmpty()) {
            return 0;
        }
        UserEntity userData = userDataList.get(0);
        populateUserData(userData);

        if (isUserLoggedIn()) {
            disconnectExistingSessions();
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

    private boolean isUserLoggedIn() {
        return !SessionManager.findUser(username).isEmpty();
    }

    private void populateUserData(UserEntity userData) {
        setId(userData.getId());
        setStatus(userData.getStatus());
        setLockTime(userData.getLockTime());
    }

    private boolean isUsernameValid(String username) {
        return CharMatcher.javaLetterOrDigit().matchesAllOf(username);
    }

    public byte createChar(String name, byte gender, short hair) {
        try {
            if (!isNameValid(name)) {
                return 1;
            }
            if (isNameRestricted(name)) {
                return 5;
            }
            if (isNameTaken(name)) {
                return 4;
            }

            gender = validateGender(gender);
            hair = validateHair(gender, hair);

            PlayerEntity data = initializePlayerData(name, gender, hair);
            GameRepo.getInstance().playerRepo.save(data);
            return 0;
        } catch (Exception ex) {
            logger.error("Failed to create character!", ex);
        }
        return 3;
    }

    private PlayerEntity initializePlayerData(String name, byte gender, short hair) {
        Config config = DragonBall.getInstance().getServer().getConfig();
        Gson gson = new Gson();
        ArrayList<Item> itemBodys = initializeItems(gender, true);
        ArrayList<Item> itemBoxs = initializeItems(gender, false);

        PlayerEntity data = new PlayerEntity();
        data.serverId = config.getServerID();
        data.name = name;
        data.gender = gender;
        data.classId = gender;
        data.head = hair;
        data.task = "{\"id\":0,\"index\":0,\"count\":0}";
        data.gold = 2000L;
        data.diamond = 20;
        data.diamondLock = 0;
        data.itemBag = "[]";
        data.itemBody = gson.toJson(itemBodys);
        data.itemBox = gson.toJson(itemBoxs);
        data.boxCrackBall = "[]";
        data.map = gson.toJson(LOCATION[gender]);
        data.skill = "[]";
        data.info = gson.toJson(new CharacterInfo(gender));
        data.clan = -1;
        data.shortcut = "[-1,-1,-1,-1,-1,-1,-1,-1,-1,-1]";
        data.magicTree = gson.toJson(new MagicTree());
        data.numberCellBag = 20;
        data.numberCellBox = 20;
        data.friend = "[]";
        data.enemy = "[]";
        data.ship = 0;
        data.fusion = 1;
        data.porata = 0;
        data.itemTime = "[]";
        data.amulet = "[]";
        data.achievement = gson.toJson(initializeAchievements());
        data.timePlayed = 0;
        data.typeTrainning = 0;
        data.online = 0;
        data.timeAtSplitFusion = 0L;
        data.head2 = -1;
        data.body = -1;
        data.leg = -1;
        data.collectionBook= "[]";
        data.countNumberOfSpecialSkillChanges = 0;
        data.createTime = data.resetTime = new Timestamp(System.currentTimeMillis());
        return data;
    }

    private byte validateGender(byte gender) {
        if (gender < 0 || gender > 2) {
            return 0;
        }
        return gender;
    }

    private short validateHair(byte gender, short hair) {
        int[] hairOptions = HAIR_ID[gender];
        for (int option : hairOptions) {
            if (option == hair) {
                return (short) option;
            }
        }
        return (short) hairOptions[0];
    }

    private boolean isNameTaken(String name) {
        return !GameRepo.getInstance().playerRepo.findByName(name).isEmpty();
    }

    private boolean isNameRestricted(String name) {
        return name.contains("admin") || name.contains("server");
    }

    private boolean isNameValid(String name) {
        int len = name.length();
        return len >= 5 && len <= 15 && name.matches("^[a-z0-9]+$");
    }

    public void close() {
        session = null;
        password = null;
        username = null;
    }

  





    private void disconnectExistingSessions() {
        List<User> userList = SessionManager.findUser(username);
        for (User user : userList) {
            user.getSession().disconnect();
        }
    }





    private ArrayList<Item> initializeItems(byte gender, boolean isBodyItems) {
        ArrayList<Item> items = new ArrayList<>();
        if (isBodyItems) {
            switch (gender) {
                case 0 -> {
                    items.add(createItem(0, 0));
                    items.add(createItem(6, 1));
                }
                case 1 -> {
                    items.add(createItem(1, 0));
                    items.add(createItem(7, 1));
                }
                case 2 -> {
                    items.add(createItem(2, 0));
                    items.add(createItem(8, 1));
                }
            }
        } else {
            items.add(createItem(12, 0));
        }
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
        for (int i = 0; i < DragonBall.getInstance().getServer().getAchievements().size(); i++) {
            achievements.add(new Achievement(i));
        }
        return achievements;
    }




}