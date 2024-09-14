package me.serbob.toastedchatwave.Util;

import me.serbob.toastedchatwave.ToastedChatWave;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static me.serbob.toastedchatwave.Managers.WaveManager.currentWave;

public class TierUtils {
    private static final String PERMISSION_PREFIX = "wave.reward.";

    public static String getHighestTier(Player player) {
        ConfigurationSection waveSection = ToastedChatWave.instance.getConfig()
                .getConfigurationSection("waves." + currentWave);

        if (waveSection == null) {
            return null;
        }

        List<String> permissions = new ArrayList<>(waveSection.getConfigurationSection("reward-commands")
                .getKeys(false));
        Optional<String> highestTier = permissions.stream()
                .filter(tier -> tier.startsWith("tier"))
                .filter(tier -> player.hasPermission(PERMISSION_PREFIX + tier))
                .max((t1, t2) -> {
                    int num1 = Integer.parseInt(t1.substring(4));
                    int num2 = Integer.parseInt(t2.substring(4));
                    return Integer.compare(num1, num2);
                });

        return highestTier.orElse(null);
    }
}