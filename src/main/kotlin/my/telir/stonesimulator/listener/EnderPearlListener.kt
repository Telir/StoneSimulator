package my.telir.stonesimulator.listener

import my.telir.stonesimulator.instance
import my.telir.stonesimulator.util.MathUtil
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerInteractEvent
import java.lang.System.currentTimeMillis

class EnderPearlListener : Listener {
    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        val player = e.player
        val user = instance.users[player.uniqueId]!!

        if (player.inventory.itemInMainHand.type != Material.ENDER_PEARL) return
        if (e.action != Action.RIGHT_CLICK_AIR && e.action != Action.RIGHT_CLICK_BLOCK) return

        e.isCancelled = true

        if (!user.allowTeleport) {
            player.sendMessage("§cEnder pearl will be available from ${instance.settings.levelToTP} level!")
            return
        }

        if (currentTimeMillis() > user.pearlCooldown) {
            e.isCancelled = false
            e.item.amount++
            user.pearlCooldown = currentTimeMillis() + instance.settings.pearlCooldown * 1000
        } else {
            val cooldown = MathUtil.round(((user.pearlCooldown - currentTimeMillis()).toDouble() / 1000), 1)
            player.sendMessage("§cCooldown $cooldown seconds")
        }
    }

    @EventHandler
    fun onProjectileHit(e: ProjectileHitEvent) {
        val projectile = e.entity
        if (projectile.type != EntityType.ENDER_PEARL) return

        val player = projectile.shooter as? Player ?: return
        val playerLocation = player.location
        val location = projectile.location.block.location.apply {
            pitch = playerLocation.pitch
            yaw = playerLocation.yaw
            x += 0.5
            y += 0.25
            z += 0.5
        }

        player.teleport(location)
    }
}