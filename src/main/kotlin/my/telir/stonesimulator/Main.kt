package my.telir.stonesimulator

import my.telir.stonesimulator.listener.ChatListener
import my.telir.stonesimulator.listener.EnderPearlListener
import my.telir.stonesimulator.listener.PlayerListener
import my.telir.stonesimulator.listener.WorldListener
import my.telir.stonesimulator.settings.Settings
import my.telir.stonesimulator.user.User
import org.bukkit.Bukkit
import org.bukkit.Difficulty
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

lateinit var instance: Main

class Main : JavaPlugin() {
    lateinit var users: MutableMap<UUID, User>
    lateinit var armorstands: MutableMap<UUID, ArmorStand>
    lateinit var settings: Settings

    override fun onEnable() {
        init()
        userInit()

        object : BukkitRunnable() {
            override fun run() {
                instance.users.values.forEach { it.plusSecond() }
            }
        }.runTaskTimer(instance, 0, 20)

        Bukkit.getWorlds().forEach { it.difficulty = Difficulty.NORMAL }

        registerEvents(PlayerListener())
        registerEvents(ChatListener())
        registerEvents(EnderPearlListener())
        registerEvents(WorldListener())

        setExecutor("rc", object : TabExecutor {
            override fun onCommand(
                sender: CommandSender,
                command: Command,
                label: String,
                args: Array<out String>
            ): Boolean {
                sender.sendMessage("Reloading start...")
                server.reload()
                sender.sendMessage("...Done")
                return true
            }

            override fun onTabComplete(
                sender: CommandSender,
                command: Command,
                alias: String,
                args: Array<out String>
            ): List<String> {
                return listOf()
            }
        })
        setExecutor("test", object : TabExecutor {
            override fun onCommand(
                sender: CommandSender,
                command: Command,
                label: String,
                args: Array<out String>
            ): Boolean {
                return true
            }

            override fun onTabComplete(
                sender: CommandSender,
                command: Command,
                alias: String,
                args: Array<out String>
            ): List<String> {
                return listOf()
            }
        })
        setExecutor("playtime", object : TabExecutor {
            override fun onCommand(
                sender: CommandSender,
                command: Command,
                label: String,
                args: Array<out String>
            ): Boolean {
                val player: Player
                when (args.size) {
                    0 -> {
                        if (sender is ConsoleCommandSender) {
                            sender.sendMessage("You need to select player name!")
                            return true
                        } else player = sender as Player
                    }

                    1 -> {
                        val targetPlayer = Bukkit.getPlayer(args[0])
                        if (targetPlayer == null) {
                            sender.sendMessage("Player '${args[0]}' not found")
                            return true
                        } else player = targetPlayer
                    }

                    else -> return false
                }
                val user = users[player.uniqueId]!!

                if (sender == player) sender.sendMessage("Your playtime is ${user.playTime} sec.")
                else sender.sendMessage("'${player.displayName}' playtime is ${user.playTime} sec.")

                return true
            }

            override fun onTabComplete(
                sender: CommandSender,
                command: Command,
                alias: String,
                args: Array<out String>
            ): List<String> {
                if (args.size == 1) {
                    return Bukkit.getOnlinePlayers().map { it.name }
                }
                return listOf()
            }
        })
    }

    override fun onDisable() {
        armorstands.values.forEach { it.remove() }
    }

    private fun init() {
        instance = this

        users = mutableMapOf()
        armorstands = mutableMapOf()
        settings = Settings(config)
    }

    private fun userInit() {
        Bukkit.getOnlinePlayers().forEach { users[it.uniqueId] = User(it.uniqueId) }
    }

    private fun registerEvents(listener: Listener) {
        server.pluginManager.registerEvents(listener, this)
    }

    private fun setExecutor(commandName: String, executor: TabExecutor) {
        getCommand(commandName).executor = executor
    }
}