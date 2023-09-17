package me.serbob.toastedchatwave.Util;

import me.serbob.toastedchatwave.ToastedChatWave;
import org.bukkit.entity.Player;

import java.util.Set;

import static me.serbob.toastedchatwave.Managers.WaveManager.currentWave;

public class TierUtils {
    public static String getHighestTier(Player player) {
        Set<String> tiers = ToastedChatWave.instance.getConfig().getConfigurationSection("waves."+currentWave+".reward-commands").getKeys(false);
        int highestTier = 0;

        for (String tier : tiers) {
            try {
                int currentTier = Integer.parseInt(tier.substring(4));
                if (currentTier > highestTier && playerHasTier(player, tier)) {
                    highestTier = currentTier;
                }
            } catch (NumberFormatException ignored) {
                // If the tier format is incorrect, skip the tier and continue to the next one
            }
        }

        return highestTier == 0 ? null : "tier" + highestTier;
    }

    private static boolean playerHasTier(Player player, String tier) {
        String permission = "wave.reward." + tier;
        return player.hasPermission(permission);
    }
}
