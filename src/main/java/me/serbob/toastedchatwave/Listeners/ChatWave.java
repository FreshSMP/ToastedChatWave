package me.serbob.toastedchatwave.Listeners;

import me.serbob.toastedchatwave.ToastedChatWave;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static me.serbob.toastedchatwave.Managers.WaveManager.*;

public class ChatWave implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(!isAvailable(player,event.getMessage()))
            return;
        sendRewardMessages(player);
        sendRewards(player);
        manageAftermath(player);
        if(ToastedChatWave.instance.getConfig().getBoolean("change_name_color")) {
            Bukkit.broadcastMessage(formatFinalMessage(player,event.getMessage()));
            event.setCancelled(true);
        }
        else event.setMessage(formatFinalMessage(player,event.getMessage()));
    }
}
