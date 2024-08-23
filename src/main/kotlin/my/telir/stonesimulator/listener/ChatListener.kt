package my.telir.stonesimulator.listener

import my.telir.stonesimulator.instance
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.HoverEvent.Action
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.scheduler.BukkitRunnable
import java.lang.System.currentTimeMillis

class ChatListener : Listener {

    @EventHandler
    fun onPlayerChat(e: AsyncPlayerChatEvent) {
        val player = e.player
        val user = instance.users[player.uniqueId]!!
        e.isCancelled = true

        val levelText = TextComponent("§f[§9${user.level}§f]").apply {
            this.hoverEvent = HoverEvent(Action.SHOW_TEXT, arrayOf(TextComponent("§e${user.xp}/${user.requiredXP} XP")))
        }
        e.recipients.forEach {
            it.spigot().sendMessage(
                TextComponent("${player.displayName} "),
                levelText,
                TextComponent(" » ${e.message}")
            )
        }

        object : BukkitRunnable() {
            override fun run() {
                if (currentTimeMillis() > user.chatCooldown) {
                    user.chatReward()
                }
            }
        }.runTaskLater(instance, 1)
    }
}