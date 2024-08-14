package com.ngocrong.backend.network;

import com.ngocrong.backend.character.Char;
import com.ngocrong.backend.consts.Cmd;
import org.apache.log4j.Logger;

import java.io.IOException;

public class MessageHandler implements IMessageHandler{
    private static final Logger logger = Logger.getLogger(MessageHandler.class);
    private Session session;
    private Service service;
    private Char _char;


    public MessageHandler(Session client) {
        this.session = client;
    }

    @Override
    public void setService(IService service) {
        this.service = (Service) service;
    }

    @Override
    public void onMessage(Message mss) {
        if (mss != null) {
            if (!session.isConnected()) {
                return;
            }
            byte command = mss.getCommand();
            try {
                switch (command) {
                    case Cmd.SUB_COMMAND:
                        messageSubCommand(mss);
                        break;
                    case Cmd.LOGIN2:
                        service.dialogMessage("Đăng ký tài khoản tại Ngọc Rồng Online");
                        break;
//                    case Cmd.NOT_LOGIN:
//                        messageNotLogin(mss);
//                        break;
//                    case Cmd.NOT_MAP:
//                        this.messageNotMap(mss);
//                        break;
//                    case Cmd.GET_IMAGE_SOURCE:
//                        session.getImageSource(mss);
//                        break;
//                    case Cmd.LUCKY_ROUND:
//                        if (_char != null && _char.zone != null) {
//                            _char.luckyRound(mss);
//                        }
//                        break;
//                    case Cmd.UPDATE_DATA:
//                        if (session.user != null) {
//                            service.updateData();
//                        }
//                        break;
//
//                    case Cmd.CHECK_MOVE:
//                        if (_char != null && _char.zone != null) {
//                            _char.checkMove(mss);
//                        }
//                        break;
//                    case Cmd.REQUEST_PEAN:
//                        if (_char != null && _char.zone != null) {
//                            _char.requestPean();
//                        }
//                        break;
//                    case Cmd.ACHIEVEMENT:
//                        if (_char != null && _char.zone != null) {
//                            _char.achievement(mss);
//                        }
//                        break;
//                    case Cmd.FINISH_UPDATE:
//                        session.finishUpdate();
//                        break;
//                    case Cmd.REQUEST_ICON:
//                        if (_char != null) {
//                            service.requestIcon(mss);
//                        }
//                        break;
//                    case Cmd.GET_BAG:
//                        if (_char != null) {
//                            service.getBag(mss);
//                        }
//                        break;
//                    case Cmd.REQUEST_NPCTEMPLATE:
//                        if (_char != null && _char.zone != null) {
//                            service.requestMobTemplate(mss);
//                        }
//                        break;
//                    case Cmd.UPDATE_CAPTION:
//                        if (_char != null) {
//                            service.updateCaption(mss);
//                        }
//                        break;
//                    case Cmd.GET_EFFDATA:
//                        if (_char != null && _char.zone != null) {
//                            service.requestEffectData(mss);
//                        }
//                        break;
//                    case Cmd.FINISH_LOADMAP:
//                        if (_char != null) {
//                            _char.finishLoadMap();
//                        }
//                        break;
//                    case Cmd.RADA_CARD:
//                        if (_char != null) {
//                            _char.collectionBookACtion(mss);
//                        }
//                        break;
//                    case Cmd.SPEACIAL_SKILL:
//                        if (_char != null) {
//                            _char.specialSkill(mss);
//                        }
//                        break;
//                    case Cmd.MAP_TRASPORT:
//                        if (_char != null && _char.zone != null) {
//                            _char.mapTransport(mss);
//                        }
//                        break;
//                    case Cmd.PLAYER_MOVE:
//                        if (_char != null && _char.zone != null) {
//                            _char.move(mss);
//                        }
//                        break;
//                    case Cmd.CHAT_MAP:
//                        if (_char != null && _char.zone != null) {
//                            _char.chatMap(mss);
//                        }
//                        break;
//                    case Cmd.MAP_CHANGE:
//                        if (_char != null && _char.zone != null) {
//                            _char.requestChangeMap();
//                        }
//                        break;
//                    case Cmd.BACKGROUND_TEMPLATE:
//                        if (_char != null && _char.zone != null) {
//                            service.requestBackgroundItem(mss);
//                        }
//                        break;
//                    case Cmd.MAP_OFFLINE:
//                        if (_char != null && _char.zone != null) {
//                            _char.mapOffline();
//                        }
//                        break;
//                    case Cmd.GET_ITEM:
//                        if (_char != null) {
//                            _char.getItem(mss);
//                        }
//                        break;
//                    case Cmd.CHANGE_ONSKILL:
//                        if (_char != null) {
//                            _char.changeOnSkill(mss);
//                        }
//                        break;
//                    case Cmd.OPEN_UI_MENU:
//                        if (_char != null && _char.zone != null) {
//                            _char.openUIMenu(mss);
//                        }
//                        break;
//                    case Cmd.MENU:
//                        if (_char != null && _char.zone != null) {
//                            _char.menu(mss);
//                        }
//                        break;
//                    case Cmd.OPEN_UI_ZONE:
//                        if (_char != null && _char.zone != null) {
//                            service.openUIZone();
//                        }
//                        break;
//                    case Cmd.ZONE_CHANGE:
//                        if (_char != null && _char.zone != null) {
//                            _char.requestChangeZone(mss);
//                        }
//                        break;
//
//                    case Cmd.PLAYER_ATTACK_NPC:
//                        if (_char != null && _char.zone != null) {
//                            _char.attackNpc(mss);
//                        }
//                        break;
//                    case Cmd.GOTO_PLAYER:
//                        if (_char != null && _char.zone != null) {
//                            _char.gotoPlayer(mss);
//                        }
//                        break;
//                    case Cmd.SKILL_SELECT:
//                        if (_char != null) {
//                            _char.selectSkill(mss);
//                        }
//                        break;
//
//                    case Cmd.OPEN_UI_CONFIRM:
//                        if (_char != null) {
//                            _char.confirmMenu(mss);
//                        }
//                        break;
//
//                    case Cmd.ME_LIVE:
//                        if (_char != null && _char.zone != null) {
//                            _char.wakeUpFromDead();
//                        }
//                        break;
//
//                    case Cmd.ME_BACK:
//                        if (_char != null && _char.zone != null) {
//                            _char.returnTownFromDead();
//                        }
//                        break;
//                    case Cmd.USE_ITEM:
//                        if (_char != null && _char.zone != null) {
//                            _char.useItem(mss);
//                        }
//                        break;
//                    case Cmd.ITEM_BUY:
//                        if (_char != null && _char.zone != null) {
//                            _char.buyItem(mss);
//                        }
//                        break;
//
//                    case Cmd.ITEM_SALE:
//                        if (_char != null && _char.zone != null) {
//                            _char.saleItem(mss);
//                        }
//                        break;
//
//                    case Cmd.MAGIC_TREE:
//                        if (_char != null && _char.zone != null) {
//                            _char.getMagicTree(mss);
//                        }
//                        break;
//
//                    case Cmd.SKILL_NOT_FOCUS:
//                        if (_char != null && _char.zone != null) {
//                            _char.skillNotFocus(mss);
//                        }
//                        break;
//
//                    case Cmd.PLAYER_MENU:
//                        if (_char != null && _char.zone != null) {
//                            _char.viewInfo(mss);
//                        }
//                        break;
//
//                    case Cmd.PLAYER_VS_PLAYER:
//                        if (_char != null && _char.zone != null) {
//                            _char.playerVsPlayer(mss);
//                        }
//                        break;
//
//                    case Cmd.CHAT_THEGIOI_CLIENT:
//                        if (_char != null) {
//                            _char.chatGlobal(mss);
//                        }
//                        break;
//
//                    case Cmd.FRIEND:
//                        if (_char != null) {
//                            _char.friendAction(mss);
//                        }
//                        break;
//
//                    case Cmd.ENEMY_LIST:
//                        if (_char != null) {
//                            _char.enemyAction(mss);
//                        }
//                        break;
//
//                    case Cmd.CHAT_PLAYER:
//                        if (_char != null) {
//                            _char.chatPlayer(mss);
//                        }
//                        break;
//                    case Cmd.CHANGE_FLAG:
//                        if (_char != null && _char.zone != null) {
//                            _char.changeFlag(mss);
//                        }
//                        break;
//
//                    case Cmd.GET_IMG_BY_NAME:
//                        if (_char != null && _char.zone != null) {
//                            this.service.getImgByName(mss);
//                        }
//                        break;
//
//                    case Cmd.PLAYER_ATTACK_PLAYER:
//                        if (_char != null && _char.zone != null) {
//                            _char.attackPlayer(mss);
//                        }
//                        break;
//
//                    case Cmd.COMBINNE:
//                        if (_char != null && _char.zone != null) {
//                            _char.combine(mss);
//                        }
//                        break;
//
//                    case Cmd.GIAO_DICH:
//                        if (_char != null && _char.zone != null) {
//                            _char.giaoDich(mss);
//                        }
//                        break;
//
//                    case Cmd.ITEMMAP_MYPICK:
//                        if (_char != null && _char.zone != null) {
//                            _char.pickItem(mss);
//                        }
//                        break;
//
//                    case Cmd.CLAN_IMAGE:
//                        if (_char != null) {
//                            service.clanImage(mss);
//                        }
//                        break;
//
//                    case Cmd.CLAN_SEARCH:
//                        if (_char != null) {
//                            _char.searchClan(mss);
//                        }
//                        break;
//
//                    case Cmd.CLAN_MEMBER:
//                        if (_char != null) {
//                            _char.viewClanMember(mss);
//                        }
//                        break;
//
//                    case Cmd.CLAN_CREATE_INFO:
//                        if (_char != null) {
//                            _char.createClan(mss);
//                        }
//                        break;
//
//                    case Cmd.CLAN_MESSAGE:
//                        if (_char != null) {
//                            _char.clanMessage(mss);
//                        }
//                        break;
//
//                    case Cmd.CLAN_INVITE:
//                        if (_char != null) {
//                            _char.clanInvite(mss);
//                        }
//                        break;
//
//                    case Cmd.CLAN_DONATE:
//                        if (_char != null) {
//                            _char.clanDonate(mss);
//                        }
//                        break;
//
//                    case Cmd.CLAN_REMOTE:
//                        if (_char != null) {
//                            _char.clanRemote(mss);
//                        }
//                        break;
//
//                    case Cmd.DISCIPLE_INFO:
//                        if (_char != null) {
//                            _char.discipleInfo();
//                        }
//                        break;
//
//                    case Cmd.CLAN_JOIN:
//                        if (_char != null) {
//                            _char.joinClan(mss);
//                        }
//                        break;
//
//                    case Cmd.CLAN_LEAVE:
//                        if (_char != null) {
//                            _char.leaveClan();
//                        }
//                        break;
//
//                    case Cmd.PET_STATUS:
//                        _char.petStatus(mss);
//                        break;
//
//                    case Cmd.TRANSPORT:
//                        _char.transportNow();
//                        break;
//
//                    case Cmd.CLIENT_INPUT:
//                        _char.confirmTextBox(mss);
//                        break;
//
//                    case Cmd.ANDROID_PACK:
//                        session.setDeviceInfo(mss);
//                        break;
//
//
//                    case Cmd.KIGUI: {
//                        if (_char != null && _char.zone != null) {
//                            _char.consignment(mss);
//                        }
//                        break;
//                    }

                    default:
                        logger.debug("CMD: " + mss.getCommand());
                        break;
                }
            } catch (Exception ex) {
                logger.error(String.format("failed! - CMD: %d", command), ex);
            }
        }
    }

    @Override
    public void setChar(Char _char) {
        this._char = _char;
    }

    @Override
    public void onConnectionFail() {
        logger.debug(String.format("Client %d: Kết nối thất bại!", session.id));
    }

    @Override
    public void onDisconnected() {
        logger.debug(String.format("Client %d: Mất kết nối!", session.id));
    }

    @Override
    public void onConnectOK() {
        logger.debug(String.format("Client %d: Kết nối thành công!", session.id));
    }

    @Override
    public void close() {
        session = null;
        service = null;

    }


    public void messageSubCommand(Message mss) throws IOException {
        if (mss != null) {
            byte command = mss.getReader().readByte();
            try {
                if (_char == null) {
                    return;
                }
                switch (command) {
                    case Cmd.POTENTIAL_UP:
                        logger.debug("vao POTENTIAL_UP");
//                        _char.getCharacterInfo().getP;
                        break;

//                    case Cmd.SAVE_RMS:
//                        _char.saveRms(mss);
//                        break;
//
//                    case Cmd.GET_PLAYER_MENU:
//                        _char.playerMenu(mss);
//                        break;
//
//                    case Cmd.PLAYER_MENU_ACTION:
//                        _char.playerMenuAction(mss);
//                        break;

//                    case Cmd.LOAD_RMS:
//                        _char.loadRms(mss);
//                        break;
                    default:
                        logger.debug(String.format("Client %d: messageSubCommand: %d", session.id, command));
                        break;
                }
            } catch (Exception ex) {
                logger.error(String.format("failed! - subCommand: %d", command), ex);
            }
        }
    }

    public void messageNotMap(Message mss) throws IOException {
        if (mss != null) {
            byte command = mss.getReader().readByte();
            try {
                switch (command) {
                    case Cmd.UPDATE_MAP:
                        if (session.user != null) {
                            service.updateMap();
                        }
                        break;
                    case Cmd.UPDATE_SKILL:
                        if (session.user != null) {
                            service.updateSkill();
                        }
                        break;
                    case Cmd.UPDATE_ITEM:
                        if (session.user != null) {
                            service.updateItem((byte) 0);
                            service.updateItem((byte) 1);
                            service.updateItem((byte) 2);
                        }
                        break;
                    case Cmd.REQUEST_MAPTEMPLATE:
                        if (session.user != null) {
                            service.requestMapTemplate(mss);
                        }
                        break;
                    case Cmd.CLIENT_OK:
                        if (session.user != null) {
                            //session.clientOK();
                        }
                        break;
                    case Cmd.CREATE_PLAYER:
                        if (session.user != null) {
                            session.createChar(mss);
                        }
                        break;

                    case Cmd.INPUT_CARD:
                        service.serverMessage("Bạn hãy truy cập trang: ngocrongonline.com để nạp tiền!");
                        break;

                    default:
                        logger.debug(String.format("Client %d: messageNotMap: %d", session.id, command));
                        break;
                }
            } catch (Exception ex) {
                logger.error(String.format("failed! - notMap: %d", command), ex);
            }
        }
    }

}
