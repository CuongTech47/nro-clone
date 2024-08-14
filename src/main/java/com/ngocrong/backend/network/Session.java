package com.ngocrong.backend.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.character.CharService;
import com.ngocrong.backend.character.CharacterInfo;
import com.ngocrong.backend.clan.Clan;
import com.ngocrong.backend.clan.ClanManager;
import com.ngocrong.backend.collection.Card;
import com.ngocrong.backend.consts.Cmd;
import com.ngocrong.backend.disciple.Disciple;
import com.ngocrong.backend.entity.PlayerEntity;
import com.ngocrong.backend.item.Amulet;
import com.ngocrong.backend.item.Item;
import com.ngocrong.backend.item.ItemTime;
import com.ngocrong.backend.model.Achievement;
import com.ngocrong.backend.model.Friend;
import com.ngocrong.backend.model.History;
import com.ngocrong.backend.model.MagicTree;
import com.ngocrong.backend.repository.GameRepo;
import com.ngocrong.backend.server.Config;
import com.ngocrong.backend.server.DragonBall;
import com.ngocrong.backend.server.Server;
import com.ngocrong.backend.server.SessionManager;
import com.ngocrong.backend.skill.Skill;
import com.ngocrong.backend.skill.SkillBook;
import com.ngocrong.backend.skill.Skills;
import com.ngocrong.backend.skill.SpecialSkill;
import com.ngocrong.backend.task.Task;
import com.ngocrong.backend.user.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Data
public class Session implements ISession {
    private static final Logger logger = Logger.getLogger(Session.class);
    private static final Lock lock = new ReentrantLock();
    private byte[] key;
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    public int id;
    private IMessageHandler messageHandler;
    @Getter
    private IService service;
    private boolean isConnected , isLogin;
    private byte curR, curW;
    private Thread collectorThread;
    private Thread sendThread;
    private String version;

    @Getter
    private byte zoomLevel;
    private int width;
    private int height;
    private int device; // 0-PC, 1- APK, 2-IOS
    public User user;
    public Char _char;
    private boolean isSetClientInfo;
    public boolean isEnter = false;
    public String deviceInfo;
    private String ip;

    public Session(Socket socket, String ip, int id) throws IOException {
        this.socket = socket;
        this.ip = ip;
        this.id = id;
        this.dis = new DataInputStream(socket.getInputStream());
        this.dos = new DataOutputStream(socket.getOutputStream());
        setHandler(new MessageHandler(this));
        this.messageHandler.onConnectOK();
    }






    @Override
    public boolean isConnected() {
        return this.isConnected;
    }

    @Override
    public void setHandler(IMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void sendMessage(Message message) {
        try {
            doSendMessage(message);
        } catch (IOException e) {
            logger.error("Failed to send message", e);
        }
    }

    @Override
    public void setService(IService service) {
        this.service = service;
    }

    @Override
    public void close() {
        try {
            try {
                if (isConnected()) {
                    messageHandler.onDisconnected();
                }
                if (_char != null) {
                    _char.logout();
                }
                cleanNetwork();
            } finally {
                if (ip != null) {
                    int count = Server.ips.getOrDefault(ip, 0) - 1;
                    if (count <= 0) {
                        Server.ips.remove(ip);
                    } else {
                        Server.ips.put(ip, count);
                    }
                    deviceInfo = null;
                }
                SessionManager.removeSession(this);
            }
            System.gc();
        } catch (Exception ignored) {
        }
    }

    private void cleanNetwork() {
        curR = 0;
        curW = 0;
        isConnected = false;
        isLogin = false;
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
            if (dos != null) {
                dos.close();
                dos = null;
            }
            if (dis != null) {
                dis.close();
                dis = null;
            }
            if (sendThread != null && sendThread.isAlive()) {
                sendThread.stop();
                sendThread = null;
            }
            if (collectorThread != null && collectorThread.isAlive()) {
                collectorThread.stop();
                collectorThread = null;
            }
            System.gc();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void disconnect() {
        if (socket != null && socket.isConnected()) {
            try {
                socket.close();
            } catch (IOException ex) {
                logger.error("failed!", ex);
            }
        }
    }


    protected synchronized void  doSendMessage(Message m) throws IOException {
        if (m == null) {
            return;
        }
        byte[] data = m.getData();
        byte command = m.getCommand();
        writeCommand(command);
        if (data != null) {
            writeData(data, command);
        }
        dos.flush();
        m.cleanup();
    }

    private void writeCommand(byte command) throws IOException {
        dos.writeByte(isConnected ? writeKey(command) : command);
    }

    private void writeData(byte[] data, byte command) throws IOException {
        int size = data.length;
        writeDataSize(size, command);
        if (isConnected) {
            for (int i = 0; i < data.length; i++) {
                data[i] = writeKey(data[i]);
            }
        }
        dos.write(data);
    }

    private void writeDataSize(int size, byte command) throws IOException {
        if (isConnected) {
            if (isSpecialMessage(command)) {
                dos.writeByte(writeKey((byte) ((size & 255) - 128)));
                dos.writeByte(writeKey((byte) ((size >> 8) - 128)));
                dos.writeByte(writeKey((byte) ((size >> 16) - 128)));
            } else {
                dos.writeByte(writeKey((byte) (size >> 8)));
                dos.writeByte(writeKey((byte) (size & 255)));
            }
        } else {
            dos.writeByte(size & 256);
            dos.writeByte(size & 255);
        }
    }

    private byte writeKey(byte b) {
        byte b2 = curW;
        curW = (byte) (b2 + 1);
        byte result = (byte) ((key[b2] & 255) ^ (b & 255));
        if (curW >= key.length) {
            curW %= key.length;
        }
        return result;
    }

    private static boolean isSpecialMessage(int command) {
        return command == Cmd.BACKGROUND_TEMPLATE || command == Cmd.GET_EFFDATA || command == Cmd.REQUEST_NPCTEMPLATE
                || command == Cmd.REQUEST_ICON || command == Cmd.GET_IMAGE_SOURCE || command == Cmd.UPDATE_DATA
                || command == Cmd.GET_IMG_BY_NAME;
    }

    private byte readKey(byte b) {
        byte b2 = curR;
        curR = (byte) (b2 + 1);
        byte result = (byte) ((key[(int) b2] & 255) ^ ((int) b & 255));
        if (curR >= key.length) {
            curR = (byte) (curR % key.length);
        }
        return result;
    }


    public void createChar(Message mss) {
        try {
            String name = mss.getReader().readUTF();
            byte gender = mss.getReader().readByte();
            short hair = mss.getReader().readByte();
            byte status = user.createChar(name, gender, hair);
            Service sv = (Service) this.service;
            if (status == 0) {
                if (loadChar()) {
                    enter();
                }
            } else if (status == 1) {
                sv.dialogMessage("Tên nhân vật từ 6 đến 15 ký tự.");
            } else if (status == 2) {
                sv.dialogMessage("Tên nhân vật không được có ký tự đặc biệt.");
            } else if (status == 3) {
                sv.dialogMessage("Có lỗi xảy ra.");
            } else if (status == 4) {
                sv.dialogMessage("Tên nhân vật đã tồn tại.");
            } else if (status == 5) {
                sv.dialogMessage("Tên nhân vật không được chứa các từ này.");
            }
        } catch (IOException ex) {
            logger.error("failed!", ex);
        }
    }

    private void enter() {
        lock.lock();
        try {
            if (!isEnter) {
                List<User> userList = SessionManager.findUserById(user.getId());
                if (!userList.isEmpty()) {
                    for (User u : userList) {
                        u.getSession().disconnect();
                    }
                    disconnect();
                    return;
                }
                if (socket == null || !socket.isConnected() || deviceInfo == null) {
                    return;
                }
                if (SessionManager.deviceInvalid(deviceInfo)) {
                    return;
                }
                isEnter = true;
                Service sv = (Service) service;
                service.setChar(_char);
                messageHandler.setChar(_char);
                _char.setService((CharService) sv);
                _char.setSession(this);
                _char.enter();
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean loadChar() {
        try {
            Server server = DragonBall.getInstance().getServer();
            Config config = server.getConfig();
            List<PlayerEntity> dataList = GameRepo.getInstance().playerRepo.findByUserIdAndServerId(user.getId(), config.getServerID());
            if (dataList.isEmpty()) {
                return true;
            }
            PlayerEntity data = dataList.get(0);
            long now = System.currentTimeMillis();
            if (data.logoutTime != null) {
                long time = now - data.logoutTime.getTime();
                long delayLogin = 10000L;
                if (time < 0) {
                    time = delayLogin;
                }
                if (time < delayLogin) {
                    int delay = (int) ((delayLogin - time) / 1000);
                    ((Service) service).dialogMessage(String.format("Vui lòng thử lại sau %d giây", delay));
                    return false;
                }
            }
            Gson gson = new Gson();
            _char = new Char();
            _char.setId(data.id);
            _char.setName(data.name);
            _char.setGold(Math.max(data.gold, 0));
            _char.setDiamond(Math.max(data.diamond, 0));
            _char.setDiamondLock(Math.max(data.diamondLock, 0));
            _char.setClassId(data.classId);
            _char.setResetTime(data.resetTime);
            _char.setClanID( data.clan);
            if (_char.getClanID() != -1) {
                Clan clan = ClanManager.getInstance().findClanById(_char.getClanID());
                if (clan != null && clan.getMember(_char.getId()) != null) {
                    _char.clan = clan;
                    _char.setBag(_char.clan.imgID);
                } else {
                    _char.setClanID(-1);
                    _char.setBag((byte) -1);
                }
            }
            _char.setGender(data.gender);
            String task = data.task;
            if (task != null && !task.isEmpty()) {
                _char.setTaskMain(gson.fromJson(task, Task.class));
                _char.getTaskMain().initTask(_char.getGender());
            }
            _char.setHeadDefault(data.head);
            _char.setTypeTraining(data.typeTrainning);
            _char.setNumberCellBag(data.numberCellBag);
            _char.setNumberCellBox(data.numberCellBox);
            _char.setTimePlayed(data.timePlayed);
            _char.setNewMember(now - data.createTime.getTime() < 2592000000L);
            _char.setShip(data.ship);
            _char.setCountNumberOfSpecialSkillChanges(data.countNumberOfSpecialSkillChanges);
            String specialSkill = data.specialSkill;
            if (specialSkill != null && !specialSkill.isEmpty() && !specialSkill.equals("null")) {
                _char.setSpecialSkill(gson.fromJson(specialSkill, SpecialSkill.class));
                _char.getSpecialSkill().setTemplate();
            }
            _char.characterInfo = gson.fromJson(data.info, CharacterInfo.class);
            _char.setSkills(new ArrayList<>());
            JSONArray skills = new JSONArray(data.skill);
            int lent2 = skills.length();
            for (int i = 0; i < lent2; i++) {
                JSONObject obj = skills.getJSONObject(i);
                int templateId = obj.getInt("id");
                int level = obj.getInt("level");
                long lastTimeUseThisSkill = obj.getLong("last_time_use");
                Skill skill = Skills.getSkill(_char.getClassId(), templateId, level);
                if (skill != null) {
                    Skill skill2 = skill.clone();
                    if (data.id != 1) {
                        skill2.lastTimeUseThisSkill = lastTimeUseThisSkill;
                    }

                    _char.getSkills().add(skill2);

                }
            }
            if (!_char.getSkills().isEmpty()) {
                _char.select = _char.getSkills().get(0);
            }
            History history = new History(_char.getId(), History.LOGIN);
            history.setExtras(this.ip);
            _char.itemBody = new Item[10];
            try {
                JSONArray itemBody = new JSONArray(data.itemBody);
                int lent = itemBody.length();
                for (int i = 0; i < lent; i++) {
                    try {
                        Item item = new Item();
                        item.load(itemBody.getJSONObject(i));
                        int index = item.template.getType();
                        if (index == 32) {
                            index = 6;
                        } else if (index == 23 || index == 24) {
                            index = 7;
                        } else if (index == 11) {
                            index = 8;
                        } else if (index == 40) {
                            index = 9;
                        }
                        if (index > 9) {
                            index = 9;
                        }
                        _char.itemBody[index] = item;
                        history.addItem(item);
                    } catch (Exception e) {
                        logger.error("failed!", e);
                    }
                }
            } catch (Exception e) {
                logger.debug("failed!", e);
            }
            _char.itemBag = new Item[_char.getNumberCellBag()];
            try {
                JSONArray itemBag = new JSONArray(data.itemBag);
                int lent = itemBag.length();
                for (int i = 0; i < lent; i++) {
                    try {
                        Item item = new Item();
                        item.load(itemBag.getJSONObject(i));
                        int index = item.indexUI;
                        _char.itemBag[index] = item;
                        history.addItem(item);
                    } catch (Exception e) {
                        logger.error("failed!", e);
                    }
                }
            } catch (Exception e) {
                logger.debug("failed!", e);
            }
            _char.itemBox = new Item[_char.getNumberCellBox()];
            try {
                JSONArray itemBox = new JSONArray(data.itemBox);
                int lent = itemBox.length();
                for (int i = 0; i < lent; i++) {
                    try {
                        Item item = new Item();
                        item.load(itemBox.getJSONObject(i));
                        int index = item.indexUI;
                        _char.itemBox[index] = item;
                        history.addItem(item);
                    } catch (Exception e) {
                        logger.error("failed!", e);
                    }
                }
            } catch (Exception e) {
                logger.debug("failed!", e);
            }
            history.save();
            _char.boxCrackBall = new ArrayList<>();
            try {
                JSONArray cr = new JSONArray(data.boxCrackBall);
                int lent = cr.length();
                for (int i = 0; i < lent; i++) {
                    try {
                        Item item = new Item();
                        item.load(cr.getJSONObject(i));
                        _char.boxCrackBall.add(item);
                    } catch (Exception e) {
                        logger.error("failed!", e);
                    }
                }
            } catch (Exception e) {
                logger.error("failed!", e);
            }
            if (data.studying != null) {
                JSONObject st = new JSONObject(data.studying);
                int stID = st.getInt("id");
                int stLevel = st.getInt("level");
                long studying_time = st.getLong("studying_time");
                _char.setStudying( new SkillBook(stID, stLevel, studying_time));
            }
            _char.setFusionType( data.fusion);
            _char.typePorata = data.porata;
            if (_char.getFusionType() != 1) {
                _char.setNhapThe(true);
            }
            _char.setMagicTree(gson.fromJson(data.magicTree, MagicTree.class));
            if (_char.getMagicTree() == null) {
                MagicTree magicTree = new MagicTree();
                magicTree.level = 1;
                _char.setMagicTree(magicTree);
            }
            _char.getMagicTree().planet = _char.getGender();
            _char.getMagicTree().init();
            JSONArray mapInfo = new JSONArray(data.map);
            _char.setMapEnter(mapInfo.getInt(0));
            _char.setX((short) mapInfo.getInt(1));
            _char.setY((short) mapInfo.getInt(2));
            _char.setShortcut(gson.fromJson(data.shortcut, byte[].class));
            _char.characterInfo.applyCharLevelPercent();
            _char.effects = new ArrayList<>();
            _char.friends = gson.fromJson(data.friend, new TypeToken<List<Friend>>() {
            }.getType());
            _char.enemies = gson.fromJson(data.enemy, new TypeToken<List<Friend>>() {
            }.getType());
            _char.setAmulets(gson.fromJson(data.amulet, new TypeToken<List<Amulet>>() {}.getType()));
            _char.achievements = gson.fromJson(data.achievement, new TypeToken<List<Achievement>>() {
            }.getType());
            _char.itemTimes = gson.fromJson(data.itemTime, new TypeToken<ArrayList<ItemTime>>() {
            }.getType());
            _char.setTimeAtSplitFusion(data.timeAtSplitFusion);
            ArrayList<Card> cards = gson.fromJson(data.collectionBook, new TypeToken<List<Card>>() {
            }.getType());
            if (cards != null) {
                _char.setCards(cards);
            }
            _char.initializedCollectionBook();
            _char.characterInfo.setPowerLimited();
            _char.characterInfo.setCharacter(this._char);
            _char.setStatusItemTime();
            _char.myDisciple = loadDisciple(-_char.id);
        } catch (Exception ex) {
            logger.error("loadChar", ex);
        }
        return true;
    }

    private Disciple loadDisciple(int i) {
        try {
            Gson gson = new Gson();
            Optional<DiscipleData> discipleOptional = GameRepository.getInstance().disciple.findById(id);
            if (discipleOptional.isPresent()) {
                DiscipleData discipleData = discipleOptional.get();
                Disciple deTu = new Disciple();
                deTu.typeDisciple = discipleData.type;
                deTu.id = id;
                deTu.name = discipleData.name;
                deTu.gender = deTu.classId = discipleData.planet;
                deTu.discipleStatus = discipleData.status;
                deTu.skills = new ArrayList<>();
                JSONArray skills = new JSONArray(discipleData.skill);
                int lent2 = skills.length();
                for (int i = 0; i < lent2; i++) {
                    JSONObject obj = skills.getJSONObject(i);
                    int templateId = obj.getInt("id");
                    int level = obj.getInt("level");
                    long lastTimeUseThisSkill = obj.getLong("last_time_use");
                    Skill skill = Skills.getSkill((byte) templateId, (byte) level);
                    if (skill != null) {
                        Skill skill2 = skill.clone();
                        skill2.lastTimeUseThisSkill = lastTimeUseThisSkill;
                        deTu.addSkill(skill2);
                    }
                }
                deTu.skillOpened = (byte) deTu.skills.size();
                deTu.itemBody = new Item[10];
                JSONArray itemBody = new JSONArray(discipleData.itemBody);
                int lent = itemBody.length();
                for (int i = 0; i < lent; i++) {
                    Item item = new Item();
                    item.load(itemBody.getJSONObject(i));
                    int index = item.template.type;
                    if (index == 32) {
                        index = 6;
                    }
                    deTu.itemBody[index] = item;
                }
                deTu.info = gson.fromJson(discipleData.info, Info.class);
                deTu.info.applyCharLevelPercent();
                deTu.info.setPowerLimited();
                deTu.info.setChar(deTu);
                deTu.info.setInfo();
                return deTu;
            }
        } catch (Exception ex) {
            logger.error("failed!", ex);
        }
        return null;
    }
}
