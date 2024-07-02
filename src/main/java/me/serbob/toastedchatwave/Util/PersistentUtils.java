package me.serbob.toastedchatwave.Util;

import de.tr7zw.changeme.nbtapi.NBTEntity;
import org.bukkit.entity.Player;

public class PersistentUtils {
    public static boolean hasKey(Player player, String key) {
        NBTEntity nbtEntity = new NBTEntity(player);
        return nbtEntity.hasKey(key);
    }

    public static boolean hasKeyInt(Player player, String key) {
        NBTEntity nbtEntity = new NBTEntity(player);
        return nbtEntity.hasKey(key);
    }

    public static String getKey(Player player, String key) {
        if (!hasKey(player, key)) {
            return null;
        }

        NBTEntity nbtEntity = new NBTEntity(player);
        return nbtEntity.getString(key);
    }

    public static int getKeyInt(Player player, String key) {
        if (!hasKeyInt(player, key)) {
            return 0;
        }

        NBTEntity nbtEntity = new NBTEntity(player);
        return nbtEntity.getInteger(key);
    }

    public static void setKey(Player player, String key, String value) {
        NBTEntity nbtEntity = new NBTEntity(player);
        nbtEntity.setString(key, value);
    }

    public static void setKey(Player player, String key, int value) {
        NBTEntity nbtEntity = new NBTEntity(player);
        nbtEntity.setInteger(key, value);
    }
}
