package my.telir.stonesimulator.mongodb

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import my.telir.stonesimulator.instance
import my.telir.stonesimulator.user.User
import org.bson.Document
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*

class DatabaseManager {
    private val client: MongoClient

    init {
        val connectionString =
            YamlConfiguration.loadConfiguration(File(instance.dataFolder, "database.yml"))
                .getString("connectionString")

        client = MongoClient.create(connectionString)
    }

    private val database = client.getDatabase("minecraft")
    private val collection: MongoCollection<Document> = database.getCollection("users")

    suspend fun saveUser(user: User) {
        val userDocument = Document()
            .append("uuid", user.uuid.toString())
            .append("xp", user.xp)
            .append("level", user.level)
            .append("playtime", user.playTime)
            .append("xptime", user.xpTime)

        collection.deleteOne(Document("uuid", user.uuid.toString()))
        collection.insertOne(userDocument)
    }

    suspend fun loadUser(uuid: UUID): User? {
        val userDocument = collection.find(Document("uuid", uuid.toString())).firstOrNull() ?: return null

        return User(UUID.fromString(userDocument.getString("uuid")))
            .apply {
                xp = userDocument.getLong("xp")
                level = userDocument.getInteger("level")
                playTime = userDocument.getLong("playtime")
                xpTime = userDocument.getLong("xptime")
            }
    }
}