package me.serbob.toastedchatwave.Listeners;

import me.serbob.toastedchatwave.ToastedChatWave;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static me.serbob.toastedchatwave.Managers.WaveManager.*;

public class ChatWave implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(!isAvailable(player,event.getMessage()))
            return;
        if(receiveRewards(player)) {
            sendRewardMessages(player);
            sendRewards(player);
            manageAftermath(player);
        }
        if(ToastedChatWave.instance.getConfig().getString("show_in_chat_method").equalsIgnoreCase("message_reformat")) {
            event.setFormat(formatFinalMessage(player, ChatColor.stripColor(event.getMessage())));
        } else {
            Bukkit.broadcastMessage(formatFinalMessage(player, ChatColor.stripColor(event.getMessage())));
            event.setCancelled(true);
        }
    }
}
