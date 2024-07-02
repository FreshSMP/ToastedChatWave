package me.serbob.toastedchatwave.Listeners.priorities;

import me.serbob.toastedchatwave.ToastedChatWave;
import me.serbob.toastedchatwave.Util.ChatwaveUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static me.serbob.toastedchatwave.Managers.WaveManager.*;
import static me.serbob.toastedchatwave.Managers.WaveManager.formatFinalMessage;

public class MonitorPriorityChatWave implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
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
            event.setFormat(ChatwaveUtil.c(formatFinalMessage(player, ChatColor.stripColor(event.getMessage()))));
        } else {
            Bukkit.broadcastMessage(ChatwaveUtil.c(formatFinalMessage(player, ChatColor.stripColor(event.getMessage()))));
            event.setCancelled(true);
        }
    }
}
