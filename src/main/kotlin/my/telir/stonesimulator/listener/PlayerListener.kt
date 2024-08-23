package my.telir.stonesimulator.listener

import my.telir.stonesimulator.instance
import my.telir.stonesimulator.user.User
import net.minecraft.server.v1_12_R1.EntityArmorStand
import net.minecraft.server.v1_12_R1.EnumItemSlot
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.ArmorStand
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
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta


class PlayerListener : Listener {

    private val stoneHead = getStoneHead()

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player
        instance.users[player.uniqueId] = User(player.uniqueId)

        val location = player.location.apply { y -= 1.425 }
        val world = (location.world as CraftWorld).handle
        val nmsArmorStand = EntityArmorStand(world, location.x, location.y, location.z)

        nmsArmorStand.isNoGravity = true
        nmsArmorStand.setSlot(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(stoneHead))

        world.addEntity(nmsArmorStand)

        instance.armorstands[player.uniqueId] = (nmsArmorStand.bukkitEntity as ArmorStand).apply { isVisible = false }
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        instance.users.remove(e.player.uniqueId)
        instance.armorstands[e.player.uniqueId]?.remove()
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

    fun getStoneHead(): ItemStack {
        val item = ItemStack(Material.SKULL_ITEM, 1, 3)
        val meta = item.itemMeta as SkullMeta
        meta.owner = "stone_head_"
        item.itemMeta = meta
        return item
    }
}