package com.ngocrong.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "nr_player")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer id;

    @Column(name = "user_id")
    public Integer userId;

    @Column(name = "server_id")
    public Integer serverId;

    @Column(name = "name")
    public String name;

    @Column(name = "gender")
    public Byte gender;

    @Column(name = "class_id")
    public Byte classId;

    @Column(name = "head")
    public Short head;

    @Column(name = "task")
    public String task;

    @Column(name = "gold")
    public Long gold;

    @Column(name = "gem")
    public Integer diamond;

    @Column(name = "gem_lock")
    public Integer diamondLock;

    @Column(name = "item_bag")
    public String itemBag;

    @Column(name = "item_body")
    public String itemBody;

    @Column(name = "item_box")
    public String itemBox;

    @Column(name = "box_crack_ball")
    public String boxCrackBall;

    @Column(name = "map")
    public String map;

    @Column(name = "skill")
    public String skill;

    @Column(name = "info")
    public String info;

    @Column(name = "clan")
    public Integer clan;

    @Column(name = "shortcut")
    public String shortcut;

    @Column(name = "magic_tree")
    public String magicTree;

    @Column(name = "number_cell_bag")
    public Integer numberCellBag;

    @Column(name = "number_cell_box")
    public Integer numberCellBox;

    @Column(name = "friend")
    public String friend;

    @Column(name = "enemy")
    public String enemy;

    @Column(name = "ship")
    public Byte ship;

    @Column(name = "fusion")
    public Integer fusion;

    @Column(name = "porata")
    public Integer porata;

    @Column(name = "item_time")
    public String itemTime;

    @Column(name = "amulet")
    public String amulet;

    @Column(name = "achievement")
    public String achievement;

    @Column(name = "studying")
    public String studying;

    @Column(name = "time_played")
    public Integer timePlayed;

    @Column(name = "type_trainning")
    public Byte typeTrainning;

    @Column(name = "online")
    public Byte online;

    @Column(name = "time_at_split_fusion")
    public Long timeAtSplitFusion;

    @Column(name = "head2")
    public Integer head2;

    @Column(name = "body")
    public Integer body;

    @Column(name = "leg")
    public Integer leg;

    @Column(name = "collection_book")
    public String collectionBook;

    @Column(name = "count_number_of_specialskill_changes")
    public Short countNumberOfSpecialSkillChanges;

    @Column(name = "special_skill")
    public String specialSkill;

    @Column(name = "login_time")
    public Timestamp loginTime;

    @Column(name = "logout_time")
    public Timestamp logoutTime;

    @Column(name = "create_time")
    public Timestamp createTime;

    @Column(name = "reset_time")
    public Timestamp resetTime;
}
