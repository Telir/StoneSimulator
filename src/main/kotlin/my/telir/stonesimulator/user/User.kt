package my.telir.stonesimulator.user

import my.telir.stonesimulator.instance
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.lang.System.currentTimeMillis
import java.util.*

private val settings = instance.settings

data class User(val uuid: UUID) {
    var level = 1
        set(value) {
            field = value; tryUnlock(); player.level = level
            player.playerListName = "${player.displayName} §f[§9${level}§f]"
        }

    var xp = 0L
        set(value) {
            field = value; tryLevelUp(); player.exp = xp.toFloat() / requiredXP.toFloat()
        }

//    val totalXP: Long
//        get() = xp + (level - 1) * settings.startXPToLevel + max(0, level - 2) * settings.adjustXPToLevel

    val requiredXP: Long
        get() = settings.startXPToLevel - settings.adjustXPToLevel * (level - 1)

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
        player.sendMessage("$xp/$requiredXP")
    }

    private fun tryLevelUp() {
        if (xp == requiredXP) {
            player.sendMessage("§aLevel up! Your current level is ${level + 1}!")
            xp = 0
            level++
        }
    }

    private fun tryUnlock() {
        if (level == settings.levelToMove && !allowMove) {
            allowMove = true
            player.walkSpeed = 0.01F
            player.sendMessage("§aCongratulations! Now you can move!")
        }

        if (level == settings.levelToTP && !allowTeleport) {
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
        val player = Bukkit.getPlayer(uuid)!!
        player.walkSpeed = 0.0F
        player.gameMode = GameMode.ADVENTURE

        player.inventory.clear()
        player.inventory.setItem(0, ItemStack(Material.ENDER_PEARL))

        player.exp = 0.0F
        player.level = 1

        player.foodLevel = 6
        player.saturation = 20.0F

        player.activePotionEffects.forEach { player.removePotionEffect(it.type) }
        player.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 999999999, 250, false, false))
        player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, 999999999, 1, false, false))
        player.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, 999999999, 1, false, false))

    }
}