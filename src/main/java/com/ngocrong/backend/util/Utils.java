package com.ngocrong.backend.util;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.ngocrong.backend.lib.KeyValue;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Utils {
    public static Random rand = new Random();
    private static Pattern pattern = Pattern.compile("^[a-z0-9]{6,15}$");
    private static Pattern patternAlphaNumeric = Pattern.compile("^[a-z0-9]$");

    public static String unaccent(String src) {
        return Normalizer
                .normalize(src, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }

    public static boolean validateName(String name) {
        return pattern.matcher(name).matches();
    }

    public static String cutPng(String str) {
        String result = str;
        if (str.contains(".png")) {
            result = str.replace(".png", "");
        }
        return result;
    }

    public static ZonedDateTime getLocalDateTime() {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        return zonedNow;
    }

    public static long percentOf(long x, long p) {
        return x * p / 100;
    }

    public static long getFolderSize(File folder) {
        long length = 0;
        File[] files = folder.listFiles();
        int count = files.length;
        for (int i = 0; i < count; i++) {
            if (files[i].isFile()) {
                length += files[i].length();
            } else {
                length += getFolderSize(files[i]);
            }
        }
        return length;
    }

    public static int getCountDay(Timestamp date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int oldYear = cal.get(Calendar.YEAR);
        int oldDay = cal.get(Calendar.DAY_OF_YEAR);
        int nowYear = LocalDateTime.now().getYear();
        int nowDay = LocalDateTime.now().getDayOfYear();
        if (oldYear == nowYear) {
            return nowDay - oldDay;
        }
        if (oldYear > nowYear) {
            return -1;
        }
        return 365 * (nowYear - oldYear) + nowDay - oldDay;
    }

    public static int getItemGoldByQuantity(int gold) {
        int itemID = -1;
        if (gold < 200) {
            itemID = 76;
        } else if (gold < 1000) {
            itemID = 188;
        } else if (gold < 10000) {
            itemID = 189;
        } else {
            itemID = 190;
        }
        return itemID;
    }

    public static String formatNumber(long number) {
        String text = "";
        String text2 = "";
        if (number >= 1000000000L) {
            text2 = "tỉ";
            long num = number % 1000000000L / 10000000L;
            number /= 1000000000L;
            text = number + "";
            if (num >= 10L) {
                if (num % 10L == 0L) {
                    num /= 10L;
                }
                text = text + "," + num + text2;

            } else if (num > 0L) {
                text = text + ",0" + num + text2;
            } else {
                text += text2;
            }
        } else if (number >= 1000000L) {
            text2 = "tr";
            long num2 = number % 1000000L / 10000L;
            number /= 1000000L;
            text = number + "";
            if (num2 >= 10L) {
                if (num2 % 10L == 0L) {
                    num2 /= 10L;
                }
                text = text + "," + num2 + text2;
            } else if (num2 > 0L) {
                text = text + ",0" + num2 + text2;
            } else {
                text += text2;
            }
        } else if (number
                >= 10000L) {
            text2 = "k";
            long num3 = number % 1000L / 10L;
            number /= 1000L;
            text = number + "";
            if (num3 >= 10L) {
                if (num3 % 10L == 0L) {
                    num3 /= 10L;
                }
                text = text + "," + num3 + text2;
            } else if (num3 > 0L) {
                text = text + ",0" + num3 + text2;
            } else {
                text += text2;
            }
        } else {
            text = number + "";
        }
        return text;
    }

    public static String timeAgo(int seconds) {
        int minutes = seconds / 60;
        if (minutes > 0) {
            return minutes + " phút";
        } else {
            return seconds + " giây";
        }
    }

    public static byte getIndexWithArray(byte value, byte[] array, boolean isCorrect) {
        byte index = -1;
        for (byte i = 0; i < array.length; i++) {
            if (isCorrect) {
                if (array[i] == value) {
                    index = i;
                    break;
                }
            } else {
                if (array[i] != value) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    public static int getDiagonalOfRectangle(int w, int h) {
        return (int) Math.sqrt((w * w) + (h * h));
    }

    public static void setTimeout(Runnable runnable, long delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (Exception ignored) {
            }
        }).start();
    }

    public static void setScheduled(Runnable runnable, long intervalSecond, int hour, int minute) {
        LocalDateTime localNow = LocalDateTime.now();
        ZoneId currentZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
        if (zonedNow.getHour() == hour && zonedNow.getMinute() > minute) {
            runnable.run();
        }
        ZonedDateTime zonedNext = zonedNow.withHour(hour).withMinute(minute).withSecond(0);
        if (zonedNow.compareTo(zonedNext) > 0) {
            zonedNext = zonedNext.plusSeconds(intervalSecond);
        }
        Duration duration = Duration.between(zonedNow, zonedNext);
        long delay = duration.getSeconds();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(runnable, delay, intervalSecond, TimeUnit.SECONDS);
    }

    public static boolean checkExistKey(Object key, ArrayList<KeyValue> list) {
        for (KeyValue keyValue : list) {
            if (keyValue.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    public static String replace(String text, String regex, String replacement) {
        return text.replace(regex, replacement);
    }

    public static String getTime(int timeRemainS) {
        int num = 0;
        if (timeRemainS > 60) {
            num = timeRemainS / 60;
            timeRemainS %= 60;
        }
        int num2 = 0;
        if (num > 60) {
            num2 = num / 60;
            num %= 60;
        }
        int num3 = 0;
        if (num2 > 24) {
            num3 = num2 / 24;
            num2 %= 24;
        }
        String text = "";
        if (num3 > 0) {
            text += num3;
            text += "d";
            text = text + num2 + "h";
        } else if (num2 > 0) {
            text += num2;
            text += "h";
            text = text + num + "'";
        } else {
            if (num > 9) {
                text += num;
            } else {
                text = text + "0" + num;
            }
            text += ":";
            if (timeRemainS > 9) {
                text += timeRemainS;
            } else {
                text = text + "0" + timeRemainS;
            }
        }
        return text;
    }

    public static String getTimeAgo(int timeRemainS) {
        int num = 0;
        if (timeRemainS > 60) {
            num = timeRemainS / 60;
            timeRemainS %= 60;
        }
        int num2 = 0;
        if (num > 60) {
            num2 = num / 60;
            num %= 60;
        }
        int num3 = 0;
        if (num2 > 24) {
            num3 = num2 / 24;
            num2 %= 24;
        }
        String text = "";
        if (num3 > 0) {
            text += num3;
            text += " ngày";
            text = text + num2 + " giờ";
        } else if (num2 > 0) {
            text += num2;
            text += " giờ";
            text = text + num + " phút";
        } else {
            if (num == 0) {
                num = 1;
            }
            text += num;
            text += " phút";
        }
        return text;
    }

    public static byte[] getFile(String url) {
        try {
            File file = new File(url);
            if (file.exists()) {
                return Files.readAllBytes(file.toPath());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static boolean checkPassword(String hashed, String plaintext) {
        if (hashed == null || hashed.isEmpty() || plaintext == null || plaintext.isEmpty()) {
            return false;
        }
        return BCrypt.verifyer().verify(plaintext.toCharArray(), hashed).verified;
    }

    public static String currencyFormat(long m) {
        String text = "";
        long num = m / 1000L + 1L;
        int num2 = 0;
        while ((long) num2 < num) {
            if (m < 1000L) {
                text = m + text;
                break;
            }
            long num3 = m % 1000L;
            if (num3 == 0L) {
                text = ".000" + text;
            } else if (num3 < 10L) {
                text = ".00" + num3 + text;
            } else if (num3 < 100L) {
                text = ".0" + num3 + text;
            } else {
                text = "." + num3 + text;
            }
            m /= 1000L;
            num2++;
        }
        return text;
    }

    public static int[] formatPercent(long quantity, long total) {
        long t = quantity * 10000 / total;
        int[] p = new int[2];
        p[0] = (int) (t / 100);
        p[1] = (int) (t - (p[0] * 100));
        return p;
    }

    public static void saveFile(String url, byte[] ab) {
        try {
            File f = new File(url);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(url);
            fos.write(ab);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int nextInt(int max) {
        return rand.nextInt(max);
    }

    public static long nextLong(long min, long max) {
        if (min >= max) {
            return max;
        }
        return min + (long) (Math.random() * (max - min));
    }

    public static int nextInt(int min, int max) {
        if (min >= max) {
            return max;
        }
        return rand.nextInt(max + 1 - min) + min;
    }

    public static int getDistance(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}
