package my.telir.stonesimulator.settings

import org.bukkit.configuration.file.FileConfiguration

data class Settings(private val cfg: FileConfiguration) {
    val xpCooldown = cfg.getLong("xpCooldown")
    val chatCooldown = cfg.getLong("chatCooldown")
    val pearlCooldown = cfg.getLong("pearlCooldown")
    val startXPToLevel = cfg.getLong("startXPToLevel")
    val adjustXPToLevel = cfg.getLong("adjustXPToLevel")
    val levelToTP = cfg.getInt("levelToTP")
    val levelToMove = cfg.getInt("levelToMove")
}