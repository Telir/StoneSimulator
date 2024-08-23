package my.telir.stonesimulator.user

import my.telir.stonesimulator.instance
import my.telir.stonesimulator.util.HeadUtil
import net.minecraft.server.v1_12_R1.EntityArmorStand
import net.minecraft.server.v1_12_R1.EnumItemSlot
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.lang.System.currentTimeMillis
import java.util.*

private val settings = instance.settings
private val stoneHead = HeadUtil.getStoneHead()

data class User(val uuid: UUID) {
    var level = 1
        set(value) {
            field = value; tryUnlock(); player.level = level
            player.playerListName = "${player.displayName} §f[§9${level}§f] "
            instance.armorstands[uuid]?.customName = player.name + " §f[§9${level}§f]"
        }

    var xp = 0L
        set(value) {
            field = value; tryLevelUp(); player.exp = xp.toFloat() / requiredXP.toFloat()
        }

    val requiredXP: Long
        get() = settings.startXPToLevel + settings.adjustXPToLevel * (level - 1)

    var playTime = 0L

    var xpTime = 0L
    var chatCooldown = 0L
    var pearlCooldown = 0L

    var allowMove = false
    var allowTeleport = false

    private val player: Player
        get() = Bukkit.getPlayer(uuid)

    fun plusSecond() {
        playTime++
        xpTime++
        if (xpTime != settings.xpCooldown) return

        xp++
        xpTime = 0
    }

    private fun tryLevelUp() {
        if (xp == requiredXP) {
            player.sendMessage("§aLevel up! Your current level is ${level + 1}!")
            xp = 0
            level++
        }
    }

    private fun tryUnlock() {
        if (level >= settings.levelToMove && !allowMove) {
            allowMove = true
            player.walkSpeed = 0.01F
            player.sendMessage("§aCongratulations! Now you can move!")
        }

        if (level >= settings.levelToTP && !allowTeleport) {
            allowTeleport = true
            player.sendMessage("§aCongratulations! You have unlocked ender pearl")
            player.inventory.setItem(0, ItemStack(Material.ENDER_PEARL))
        }
    }

    fun chatReward() {
        player.sendMessage("§eChat reward +1 xp!")
        xp++
        chatCooldown = currentTimeMillis() + settings.chatCooldown * 1000
    }

    init {
        level = level
        xp = xp

        player.walkSpeed = 0.0F
        player.gameMode = GameMode.ADVENTURE

        player.inventory.clear()
        player.inventory.setItem(0, ItemStack(Material.ENDER_PEARL))

        player.foodLevel = 6
        player.saturation = 20.0F
        player.health = 20.0

        player.activePotionEffects.forEach { player.removePotionEffect(it.type) }
        player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 999999999, 250, false, false))
        player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, 999999999, 1, false, false))
        player.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, 999999999, 1, false, false))

        val location = player.location.apply { y -= 1.425 }
        val world = (location.world as CraftWorld).handle
        val nmsArmorStand = EntityArmorStand(world, location.x, location.y, location.z)

        nmsArmorStand.isNoGravity = true
        nmsArmorStand.setSlot(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(stoneHead))

        world.addEntity(nmsArmorStand)

        instance.armorstands[uuid] = (nmsArmorStand.bukkitEntity as ArmorStand).apply {
            isVisible = false
            customName = player.name + " §f[§9${level}§f]"
            isCustomNameVisible = true
        }
    }
}