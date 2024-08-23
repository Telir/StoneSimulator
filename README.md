# Stone Simulator

**Stone Simulator** is a Minecraft plugin designed to relocate a well-known game 
into the cube world. From now on you can become a stone with its own struggles 
and difficulties, such as idleness and chilling out. The plugin gives the 
opportunity to experience the world from the perspective of a stone that has 
a main task: simply lying still and observing the world around.


## Features

- **Track Playtime:** Keep track of how long players have been chilling on the server.
- **Level System:** Compete with other players for the title of the ultimate 
  idler, as your level increases with your inactivity.


## Commands

### `/playtime [player]`
- **Description:** Displays the total playtime of the player.
- **Usage:**
    - `/playtime` — Shows your own playtime.
    - `/playtime [player]` — Shows the playtime of the specified player.

### `/level [player]`
- **Description:** Displays the level of the player.
- **Usage:**
    - `/level` — Displays your current level.
    - `/level [player]` — Displays the level of the specified player.


## Installation

1. Download the latest version of the plugin from the [releases page](#).
2. Place the `StoneSimulator.jar` file into your server's `plugins` directory.
3. Place the `config.yml`' file into your server's `plugins/StoneSimulator` directory.
  You can also modify the values in it to suit your preferences.
4. **[Optional] Connect to MongoDB:** To store player data using MongoDB:
   - Create a `database.yml` file in the `plugins/StoneSimulator` directory.
   - Add your MongoDB connection string under the key `connectionString`.
   - Example:
    ```yaml
    connectionString: mongodb://username:password@host:port/database
    ```
5. Restart your server to load the plugin.


## Support

For issues, suggestions, or contributions, feel free to open an issue on the [GitHub repository](#) or contact the development team.