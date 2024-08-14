package com.ngocrong.backend.entity;


import com.ngocrong.backend.character.Char;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;


@Entity
@Table(name = "nr_clan_member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClanMemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Long id;

    @Column(name = "clan_id")
    public Integer clanId;

    @Column(name = "player_id")
    public Integer playerId;

    @Column(name = "name")
    public String name;

    @Column(name = "role")
    public Byte role;

    @Column(name = "head")
    public Short head;

    @Column(name = "body")
    public Short body;

    @Column(name = "leg")
    public Short leg;

    @Column(name = "power_point")
    public Long powerPoint;

    @Column(name = "donate")
    public Integer donate;

    @Column(name = "receive_donate")
    public Integer receiveDonate;

    @Column(name = "clan_point")
    public Integer clanPoint;

    @Column(name = "current_point")
    public Integer currentPoint;

    @Column(name = "clan_reward")
    public String clanReward;

    @Column(name = "join_time")
    public Timestamp joinTime;

    public ClanMemberEntity(int clanId, Char _char, byte role) {
        playerId = _char.getId();
        this.clanId = clanId;
        name = _char.getName();
        this.role = role;
        head = _char.getHead();
        body = _char.getBody();
        leg = _char.getLeg();
        clanPoint = 0;
        currentPoint = 0;
        donate = 0;
        receiveDonate = 0;
        powerPoint = _char.characterInfo.getPower();
        clanReward = "[]";
        joinTime = new Timestamp(System.currentTimeMillis());
    }
}
