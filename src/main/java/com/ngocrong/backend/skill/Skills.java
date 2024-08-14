package com.ngocrong.backend.skill;

import com.ngocrong.backend.lib.KeyValue;
import com.ngocrong.backend.model.NClass;
import com.ngocrong.backend.server.DragonBall;
import com.ngocrong.backend.server.Server;

import java.util.ArrayList;

public class Skills {
    private static final ArrayList<KeyValue<Integer, SkillBook>> books = new ArrayList<>();

    public static void init() {
        // sách đấm dragon
        books.add(new KeyValue(66, new SkillBook(0, 1, 0)));
        books.add(new KeyValue(67, new SkillBook(0, 2, 5 * 60 * 1000L)));
        books.add(new KeyValue(68, new SkillBook(0, 3, 15 * 60 * 1000L)));
        books.add(new KeyValue(69, new SkillBook(0, 4, 60 * 60 * 1000L)));
        books.add(new KeyValue(70, new SkillBook(0, 5, 12 * 60 * 60 * 1000L)));
        books.add(new KeyValue(71, new SkillBook(0, 6, 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(72, new SkillBook(0, 7, 36 * 60 * 60 * 1000L)));

        // sách đấm demo
        books.add(new KeyValue(79, new SkillBook(2, 1, 0)));
        books.add(new KeyValue(80, new SkillBook(2, 2, 5 * 60 * 1000L)));
        books.add(new KeyValue(81, new SkillBook(2, 3, 15 * 60 * 1000L)));
        books.add(new KeyValue(82, new SkillBook(2, 4, 60 * 60 * 1000L)));
        books.add(new KeyValue(83, new SkillBook(2, 5, 12 * 60 * 60 * 1000L)));
        books.add(new KeyValue(84, new SkillBook(2, 6, 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(86, new SkillBook(2, 7, 36 * 60 * 60 * 1000L)));

        // sách đấm galick
        books.add(new KeyValue(87, new SkillBook(4, 1, 0)));
        books.add(new KeyValue(88, new SkillBook(4, 2, 5 * 60 * 1000L)));
        books.add(new KeyValue(89, new SkillBook(4, 3, 15 * 60 * 1000L)));
        books.add(new KeyValue(90, new SkillBook(4, 4, 60 * 60 * 1000L)));
        books.add(new KeyValue(91, new SkillBook(4, 5, 12 * 60 * 60 * 1000L)));
        books.add(new KeyValue(92, new SkillBook(4, 6, 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(93, new SkillBook(4, 7, 36 * 60 * 60 * 1000L)));

        // sách kamejoko
        books.add(new KeyValue(94, new SkillBook(1, 1, 5 * 60 * 1000L)));
        books.add(new KeyValue(95, new SkillBook(1, 2, 10 * 60 * 1000L)));
        books.add(new KeyValue(96, new SkillBook(1, 3, 30 * 60 * 1000L)));
        books.add(new KeyValue(97, new SkillBook(1, 4, 2 * 60 * 60 * 1000L)));
        books.add(new KeyValue(98, new SkillBook(1, 5, 18 * 60 * 60 * 1000L)));
        books.add(new KeyValue(99, new SkillBook(1, 6, 36 * 60 * 60 * 1000L)));
        books.add(new KeyValue(100, new SkillBook(1, 7, 2 * 24 * 60 * 60 * 1000L)));

        // sách masenko
        books.add(new KeyValue(101, new SkillBook(3, 1, 5 * 60 * 1000L)));
        books.add(new KeyValue(102, new SkillBook(3, 2, 10 * 60 * 1000L)));
        books.add(new KeyValue(103, new SkillBook(3, 3, 30 * 60 * 1000L)));
        books.add(new KeyValue(104, new SkillBook(3, 4, 2 * 60 * 60 * 1000L)));
        books.add(new KeyValue(105, new SkillBook(3, 5, 18 * 60 * 60 * 1000L)));
        books.add(new KeyValue(106, new SkillBook(3, 6, 36 * 60 * 60 * 1000L)));
        books.add(new KeyValue(107, new SkillBook(3, 7, 2 * 24 * 60 * 60 * 1000L)));

        // sách atomic
        books.add(new KeyValue(108, new SkillBook(5, 1, 5 * 60 * 1000L)));
        books.add(new KeyValue(109, new SkillBook(5, 2, 10 * 60 * 1000L)));
        books.add(new KeyValue(110, new SkillBook(5, 3, 30 * 60 * 1000L)));
        books.add(new KeyValue(111, new SkillBook(5, 4, 2 * 60 * 60 * 1000L)));
        books.add(new KeyValue(112, new SkillBook(5, 5, 18 * 60 * 60 * 1000L)));
        books.add(new KeyValue(113, new SkillBook(5, 6, 36 * 60 * 60 * 1000L)));
        books.add(new KeyValue(114, new SkillBook(5, 7, 2 * 24 * 60 * 60 * 1000L)));

        // sách thái dương hạ san
        books.add(new KeyValue(115, new SkillBook(6, 1, 15 * 60 * 1000L)));
        books.add(new KeyValue(116, new SkillBook(6, 2, 60 * 60 * 1000L)));
        books.add(new KeyValue(117, new SkillBook(6, 3, 2 * 60 * 60 * 1000L)));
        books.add(new KeyValue(118, new SkillBook(6, 4, 8 * 60 * 60 * 1000L)));
        books.add(new KeyValue(119, new SkillBook(6, 5, 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(120, new SkillBook(6, 6, 2 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(121, new SkillBook(6, 7, 5 * 24 * 60 * 60 * 1000L)));

        // sách trị thương
        books.add(new KeyValue(122, new SkillBook(7, 1, 15 * 60 * 1000L)));
        books.add(new KeyValue(123, new SkillBook(7, 2, 60 * 60 * 1000L)));
        books.add(new KeyValue(124, new SkillBook(7, 3, 2 * 60 * 60 * 1000L)));
        books.add(new KeyValue(125, new SkillBook(7, 4, 8 * 60 * 60 * 1000L)));
        books.add(new KeyValue(126, new SkillBook(7, 5, 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(127, new SkillBook(7, 6, 2 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(128, new SkillBook(7, 7, 5 * 24 * 60 * 60 * 1000L)));

        // sách tái tạo năng lượng
        books.add(new KeyValue(129, new SkillBook(8, 1, 15 * 60 * 1000L)));
        books.add(new KeyValue(130, new SkillBook(8, 2, 60 * 60 * 1000L)));
        books.add(new KeyValue(131, new SkillBook(8, 3, 2 * 60 * 60 * 1000L)));
        books.add(new KeyValue(132, new SkillBook(8, 4, 8 * 60 * 60 * 1000L)));
        books.add(new KeyValue(133, new SkillBook(8, 5, 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(134, new SkillBook(8, 6, 2 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(135, new SkillBook(8, 7, 5 * 24 * 60 * 60 * 1000L)));

        // sách kaioken
        books.add(new KeyValue(300, new SkillBook(9, 1, 8 * 60 * 60 * 1000L)));
        books.add(new KeyValue(301, new SkillBook(9, 2, 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(302, new SkillBook(9, 3, 2 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(303, new SkillBook(9, 4, 5 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(304, new SkillBook(9, 5, 8 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(305, new SkillBook(9, 6, 12 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(306, new SkillBook(9, 7, 16 * 24 * 60 * 60 * 1000L)));

        // quả cầu khinh khí
        books.add(new KeyValue(307, new SkillBook(10, 1, 8 * 60 * 60 * 1000L)));
        books.add(new KeyValue(308, new SkillBook(10, 2, 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(309, new SkillBook(10, 3, 2 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(310, new SkillBook(10, 4, 5 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(311, new SkillBook(10, 5, 8 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(312, new SkillBook(10, 6, 12 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(313, new SkillBook(10, 7, 16 * 24 * 60 * 60 * 1000L)));

        // Makankosappo
        books.add(new KeyValue(328, new SkillBook(11, 1, 8 * 60 * 60 * 1000L)));
        books.add(new KeyValue(329, new SkillBook(11, 2, 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(330, new SkillBook(11, 3, 2 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(331, new SkillBook(11, 4, 5 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(332, new SkillBook(11, 5, 8 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(333, new SkillBook(11, 6, 12 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(334, new SkillBook(11, 7, 16 * 24 * 60 * 60 * 1000L)));

        //đẻ trứng
        books.add(new KeyValue(335, new SkillBook(12, 1, 8 * 60 * 60 * 1000L)));
        books.add(new KeyValue(336, new SkillBook(12, 2, 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(337, new SkillBook(12, 3, 2 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(338, new SkillBook(12, 4, 5 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(339, new SkillBook(12, 5, 8 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(340, new SkillBook(12, 6, 12 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(341, new SkillBook(12, 7, 16 * 24 * 60 * 60 * 1000L)));

        // biến hình
        books.add(new KeyValue(314, new SkillBook(13, 1, 8 * 60 * 60 * 1000L)));
        books.add(new KeyValue(315, new SkillBook(13, 2, 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(316, new SkillBook(13, 3, 2 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(317, new SkillBook(13, 4, 5 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(318, new SkillBook(13, 5, 8 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(319, new SkillBook(13, 6, 12 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(320, new SkillBook(13, 7, 16 * 24 * 60 * 60 * 1000L)));

        // tự phất bổ
        books.add(new KeyValue(321, new SkillBook(14, 1, 8 * 60 * 60 * 1000L)));
        books.add(new KeyValue(322, new SkillBook(14, 2, 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(323, new SkillBook(14, 3, 2 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(324, new SkillBook(14, 4, 5 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(325, new SkillBook(14, 5, 8 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(326, new SkillBook(14, 6, 12 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(327, new SkillBook(14, 7, 16 * 24 * 60 * 60 * 1000L)));

        // khiên năng lượng
        books.add(new KeyValue(434, new SkillBook(19, 1, 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(435, new SkillBook(19, 2, 2 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(436, new SkillBook(19, 3, 4 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(437, new SkillBook(19, 4, 7 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(438, new SkillBook(19, 5, 10 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(439, new SkillBook(19, 6, 14 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(440, new SkillBook(19, 7, 20 * 24 * 60 * 60 * 1000L)));

        // biến thành socola
        books.add(new KeyValue(474, new SkillBook(18, 1, 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(475, new SkillBook(18, 2, 2 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(476, new SkillBook(18, 3, 4 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(477, new SkillBook(18, 4, 7 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(478, new SkillBook(18, 5, 10 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(479, new SkillBook(18, 6, 14 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(480, new SkillBook(18, 7, 20 * 24 * 60 * 60 * 1000L)));

        // liên hoàn
        books.add(new KeyValue(481, new SkillBook(17, 1, 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(482, new SkillBook(17, 2, 2 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(483, new SkillBook(17, 3, 4 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(484, new SkillBook(17, 4, 7 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(485, new SkillBook(17, 5, 10 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(486, new SkillBook(17, 6, 14 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(487, new SkillBook(17, 7, 20 * 24 * 60 * 60 * 1000L)));

        // dịch chuyển
        books.add(new KeyValue(488, new SkillBook(20, 1, 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(489, new SkillBook(20, 2, 2 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(490, new SkillBook(20, 3, 4 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(491, new SkillBook(20, 4, 7 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(492, new SkillBook(20, 5, 10 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(493, new SkillBook(20, 6, 14 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(494, new SkillBook(20, 7, 20 * 24 * 60 * 60 * 1000L)));

        // thôi miên
        books.add(new KeyValue(495, new SkillBook(22, 1, 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(496, new SkillBook(22, 2, 2 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(497, new SkillBook(22, 3, 4 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(498, new SkillBook(22, 4, 7 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(499, new SkillBook(22, 5, 10 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(500, new SkillBook(22, 6, 14 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(501, new SkillBook(22, 7, 20 * 24 * 60 * 60 * 1000L)));

        // trói
        books.add(new KeyValue(502, new SkillBook(23, 1, 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(503, new SkillBook(23, 2, 2 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(504, new SkillBook(23, 3, 4 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(505, new SkillBook(23, 4, 7 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(506, new SkillBook(23, 5, 10 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(507, new SkillBook(23, 6, 14 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(508, new SkillBook(23, 7, 20 * 24 * 60 * 60 * 1000L)));

        // huýt sáo
        books.add(new KeyValue(509, new SkillBook(21, 1, 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(510, new SkillBook(21, 2, 2 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(511, new SkillBook(21, 3, 4 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(512, new SkillBook(21, 4, 7 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(513, new SkillBook(21, 5, 10 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(514, new SkillBook(21, 6, 14 * 24 * 60 * 60 * 1000L)));
        books.add(new KeyValue(515, new SkillBook(21, 7, 20 * 24 * 60 * 60 * 1000L)));
    }

    public static SkillBook getSkillBook(int key) {
        for (KeyValue<Integer, SkillBook> book : books) {
            if (book.getKey() == key) {
                return book.getValue();
            }
        }
        return null;
    }

    public static Skill getSkill(byte skillID, byte level) {
        Server server = DragonBall.getInstance().getServer();
        for (NClass nClass : server.nClasss) {
            for (SkillTemplate skillTemplate : nClass.skillTemplates) {
                if (skillTemplate.id == skillID) {
                    for (Skill skill : skillTemplate.skills) {
                        if (skill.point == level) {
                            return skill;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static Skill getSkill(byte planet, int templateID, int level) {
        Server server = DragonBall.getInstance().getServer();
        NClass nClass = server.nClasss.get(planet);
        for (SkillTemplate template : nClass.skillTemplates) {
            if (template.id == templateID) {
                for (Skill skill : template.skills) {
                    if (skill.point == level) {
                        return skill;
                    }
                }
                return null;
            }
        }
        return null;
    }
}
