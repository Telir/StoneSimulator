package my.telir.stonesimulator.listener

import kotlinx.coroutines.runBlocking
import my.telir.stonesimulator.instance
import my.telir.stonesimulator.user.User
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.*


class PlayerListener : Listener {

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player
        val uuid = player.uniqueId

        var user: User?
        runBlocking { user = instance.databaseManager.loadUser(uuid) }
        if (user == null) user = User(uuid)

        instance.users[uuid] = user!!
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        val uuid = e.player.uniqueId
        runBlocking { instance.databaseManager.saveUser(instance.users[uuid]!!) }
        instance.users.remove(uuid)
        instance.armorstands[uuid]?.remove()
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerMove(e: PlayerMoveEvent) {
        val player = e.player
        val user = instance.users[player.uniqueId]!!

        if (!user.allowMove) {
            val from = e.from
            val to = Location(from.world, from.x, e.to.y, from.z, e.to.yaw, e.to.pitch)
            e.to = to
        }

        instance.armorstands[player.uniqueId]!!.teleport(player.location.apply { y -= 1.425 })
    }

    @EventHandler
    fun onPlayerTeleport(e: PlayerTeleportEvent) {
        if (e.cause == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) e.isCancelled = true
    }

    @EventHandler
    fun onEntityDamage(e: EntityDamageEvent) {
        e.isCancelled = true
    }

    @EventHandler
    fun onFoodLevelChange(e: FoodLevelChangeEvent) {
        val player = e.entity as? Player ?: return
        e.foodLevel = 6
        player.saturation = 20.0F
    }

    @EventHandler
    fun onPlayerDropItem(e: PlayerDropItemEvent) {
        e.isCancelled = true
    }

    @EventHandler
    fun onPlayerPickupItem(e: EntityPickupItemEvent) {
        if (e.entity is Player) e.isCancelled = true
    }

    @EventHandler
    fun onPlayerSwapHandItems(e: PlayerSwapHandItemsEvent) {
        e.isCancelled = true
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        e.isCancelled = true
    }

    @EventHandler
    fun onInventoryDrag(e: InventoryDragEvent) {
        e.isCancelled = true
    }

    @EventHandler
    fun onPlayerExpChange(e: PlayerExpChangeEvent) {
        e.amount = 0
    }
}