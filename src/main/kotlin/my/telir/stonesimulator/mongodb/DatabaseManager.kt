package my.telir.stonesimulator.mongodb

import com.mongodb.BasicDBObject
import com.mongodb.client.model.UpdateOptions
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import my.telir.stonesimulator.instance
import my.telir.stonesimulator.user.User
import org.bson.Document
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*

class DatabaseManager {
    private val client: MongoClient?

    init {
        val connectionString: String? =
            YamlConfiguration.loadConfiguration(File(instance.dataFolder, "database.yml"))
                .getString("connectionString")
        client = if (connectionString != null) MongoClient.create(connectionString) else null
    }

    private val database: MongoDatabase? = client?.getDatabase("stonesimulator")
    private val collection: MongoCollection<Document>? = database?.getCollection("users")

    suspend fun saveUser(user: User) {
        if (collection == null) return

        val userDocument = Document()
            .append("uuid", user.uuid.toString())
            .append("level", user.level)
            .append("xp", user.xp)
            .append("playtime", user.playTime)
            .append("xptime", user.xpTime)
        val document = BasicDBObject().apply {
            put("\$set", userDocument)
        }
        collection.updateOne(Document("uuid", user.uuid.toString()), document, UpdateOptions().upsert(true))
    }

    suspend fun loadUser(uuid: UUID): User? {
        if (collection == null) return null

        val userDocument = collection.find(Document("uuid", uuid.toString())).firstOrNull() ?: return null

        return User(uuid).apply {
            level = userDocument.getInteger("level")
            xp = userDocument.getLong("xp")
            playTime = userDocument.getLong("playtime")
            xpTime = userDocument.getLong("xptime")
        }
    }
}