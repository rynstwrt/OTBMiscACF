package art.ryanstew.otbmisc

import art.ryanstew.otbmisc.donorcommands.*
import art.ryanstew.otbmisc.listeners.*
import art.ryanstew.otbmisc.placeholders.BedWarsPlaceholder
import art.ryanstew.otbmisc.placeholders.FactionNamePlaceholder
import art.ryanstew.otbmisc.playercommands.*
import art.ryanstew.otbmisc.staffcommands.KeepInvCommand
import art.ryanstew.otbmisc.staffcommands.NightVisionCommand
import art.ryanstew.otbmisc.staffcommands.OTBMiscCommand
import art.ryanstew.otbmisc.staffcommands.WhatWorldCommand
import co.aikar.commands.PaperCommandManager
import net.milkbowl.vault.economy.Economy
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.math.RoundingMode

class OTBMisc : JavaPlugin()
{
    /** Class variables **/
    private var mainConfigFile: File? = null
    private var mainConfig: FileConfiguration? = null
    private var moneyConfigFile: File? = null
    private var moneyConfig: FileConfiguration? = null
    private var homeConfigFile: File? = null
    private var homeConfig: FileConfiguration? = null
    private var mapConfigFile: File? = null
    private var mapConfig: FileConfiguration? = null
    var prefix = ""
    var hubCompassMap = mutableMapOf<Material, String>()

    override fun onEnable()
    {
        // set up config.yml
        if (mainConfigFile == null) mainConfigFile = File(dataFolder, "config.yml")
        if (!mainConfigFile!!.exists()) saveResource("config.yml", false)
        reloadMainConfig()
        saveMainConfig()
        loadHubCompassMap()
        prefix = getMainConfig().getString("prefix") ?: ""

        // set up money.yml
        if (moneyConfigFile == null) moneyConfigFile = File(dataFolder, "money.yml")
        if (!moneyConfigFile!!.exists()) saveResource("money.yml", false)
        reloadMoneyConfig()
        saveMoneyConfig()

        // set up homes.yml
        if (homeConfigFile == null) homeConfigFile = File(dataFolder, "homes.yml")
        if (!homeConfigFile!!.exists()) saveResource("homes.yml", false)
        reloadHomeConfig()
        saveHomeConfig()

        // set up imagemaps.yml
        if (mapConfigFile == null) mapConfigFile = File(dataFolder, "imagemaps.yml")
        if (!mapConfigFile!!.exists()) saveResource("imagemaps.yml", false)
        reloadMapConfig()
        saveMapConfig()

        // register PlaceholderAPI placeholders
        FactionNamePlaceholder(this).register()
        BedWarsPlaceholder().register()

        // register event listeners
        registerEvents()

        // set up an instance of PaperCommandManager
        val manager = PaperCommandManager(this)

        // register command replacements
        manager.commandCompletions.registerAsyncCompletion("enchantTabCompleteString") { Enchantment.values().map { it.key.key } }

        // player commands
        registerPlayerCommands(manager)

        // donor commands
        registerDonorCommands(manager)

        // staff commands
        registerStaffCommands(manager)

        // load map images
        loadMapImages()

        // schedule money checker
        scheduleMoneyChecker()
    }


    /** Commands **/
    // register player commands
    private fun registerPlayerCommands(manager: PaperCommandManager)
    {
        manager.registerCommand(CoinFlipCommand(this))
        manager.registerCommand(DiceRollCommand(this))
        manager.registerCommand(GayCommand(this))
        manager.registerCommand(RawChatCommand(this))
        manager.registerCommand(PayCommand(this))
        manager.registerCommand(RankCommand(this))
        manager.registerCommand(BalanceTopCommand(this))
        manager.registerCommand(GlobalBalanceTopCommand(this))
        manager.registerCommand(DoubleDoorsCommand(this))
        manager.registerCommand(CreeperMessagesCommand(this))
        manager.registerCommand(FactionsPrefixCommand(this))
        manager.registerCommand(HomeCommand(this))
        manager.registerCommand(SetHomeCommand(this))
        manager.registerCommand(HomesCommand(this))
        manager.registerCommand(DelHomeCommand(this))
        manager.registerCommand(StoneCutterCommand())
        manager.registerCommand(SmithingTableCommand())
        manager.registerCommand(CartographyTableCommand())
        manager.registerCommand(GrindstoneCommand())
        manager.registerCommand(LoomCommand())
        manager.registerCommand(SmeltInventoryCommand(this))
        manager.registerCommand(SmeltHandCommand(this))
        manager.registerCommand(UnsmeltHandCommand(this))
        manager.registerCommand(UnsmeltInventoryCommand(this))
    }

    // register donor commands
    private fun registerDonorCommands(manager: PaperCommandManager)
    {
        manager.registerCommand(DisenchantCommand(this))
        manager.registerCommand(GradientItemNameCommand(this))
        manager.registerCommand(SetPrefixCommand(this))
        manager.registerCommand(GradientNickCommand(this))
        manager.registerCommand(GradientSetPrefixCommand(this))
        manager.registerCommand(ImageMapCommand(this))
    }

    // register staff commands
    private fun registerStaffCommands(manager: PaperCommandManager)
    {
        manager.registerCommand(OTBMiscCommand(this))
        manager.registerCommand(NightVisionCommand(this))
        manager.registerCommand(WhatWorldCommand(this))
        manager.registerCommand(KeepInvCommand(this))
    }


    /** Events **/
    // register event listeners
    private fun registerEvents()
    {
        server.pluginManager.registerEvents(TNTListener(this), this)
        server.pluginManager.registerEvents(VoteCratePlaceListener(), this)
        server.pluginManager.registerEvents(KeepInvListener(this), this)
        server.pluginManager.registerEvents(DoubleDoorListener(this), this)
        server.pluginManager.registerEvents(CreeperAwwManListener(this), this)
        server.pluginManager.registerEvents(PlayerJoinListener(this), this)
        server.pluginManager.registerEvents(CMIRandomTeleportListener(this), this)
        server.pluginManager.registerEvents(HubCompassListener(this), this)
        server.pluginManager.registerEvents(MapInitializedListener(this), this)
    }


    /** Configurations **/
    // add defaults from new config to old config
    private fun addDefaults(config: FileConfiguration?, resourceName: String)
    {
        val resource: InputStream? = getResource(resourceName)

        if (config == null || resource == null)
        {
            logger.severe("Either the config or resource passed to addDefaults() does not exist in the jar file!")
            return
        }

        val defaultConfigStream = InputStreamReader(resource, "UTF-8")
        val defaultConfig = YamlConfiguration.loadConfiguration(defaultConfigStream)

        config.setDefaults(defaultConfig)
        config.options().copyDefaults(true)
    }

    // save the main config
    fun saveMainConfig()
    {
        if (mainConfig == null || mainConfigFile == null) return

        try
        {
            getMainConfig().save(mainConfigFile!!)
        }
        catch (e: IOException)
        {
            logger.severe("Could not save config to $mainConfigFile")
        }
    }

    // save the money config
    private fun saveMoneyConfig()
    {
        if (moneyConfig == null || moneyConfigFile == null) return

        try
        {
            getMoneyConfig().save(moneyConfigFile!!)
        }
        catch (e: IOException)
        {
            logger.severe("Could not save config to $moneyConfigFile")
        }
    }

    // save the home config
    fun saveHomeConfig()
    {
        if (homeConfig == null || homeConfigFile == null) return

        try
        {
            getHomeConfig().save(homeConfigFile!!)
        }
        catch (e: IOException)
        {
            logger.severe("Could not save config to $homeConfigFile")
        }
    }

    // save the map config
    fun saveMapConfig()
    {
        if (mapConfig == null || mapConfigFile == null) return

        try
        {
            getMapConfig().save(mapConfigFile!!)
        }
        catch (e: IOException)
        {
            logger.severe("Could not save config to $mapConfigFile")
        }
    }

    // get the main config
    fun getMainConfig(): FileConfiguration
    {
        if (mainConfig == null) reloadMainConfig()
        return mainConfig!!
    }

    // get the money config
    fun getMoneyConfig(): FileConfiguration
    {
        if (moneyConfig == null) reloadMoneyConfig()
        return moneyConfig!!
    }

    // get the home config
    fun getHomeConfig(): FileConfiguration
    {
        if (homeConfig == null) reloadHomeConfig()
        return homeConfig!!
    }

    fun getMapConfig(): FileConfiguration
    {
        if (mapConfig == null) reloadHomeConfig()
        return mapConfig!!
    }

    // reload the main config
    fun reloadMainConfig()
    {
        if (mainConfigFile == null) mainConfigFile = File(dataFolder, "config.yml")

        mainConfig = YamlConfiguration.loadConfiguration(mainConfigFile!!)

        addDefaults(mainConfig, "config.yml")
        prefix = getMainConfig().get("prefix").toString()
        loadHubCompassMap()
    }

    // reload the money config
    fun reloadMoneyConfig()
    {
        if (moneyConfigFile == null) moneyConfigFile = File(dataFolder, "money.yml")
        moneyConfig = YamlConfiguration.loadConfiguration(moneyConfigFile!!)

        addDefaults(moneyConfig, "money.yml")
    }

    // reload the home config
    fun reloadHomeConfig()
    {
        if (homeConfigFile == null) homeConfigFile = File(dataFolder, "homes.yml")
        homeConfig = YamlConfiguration.loadConfiguration(homeConfigFile!!)
    }

    // reload the map config
    fun reloadMapConfig()
    {
        if (mapConfigFile == null) mapConfigFile = File(dataFolder, "imagemaps.yml")
        mapConfig = YamlConfiguration.loadConfiguration(mapConfigFile!!)
    }

    // load contents of hubCompass into a map
    private fun loadHubCompassMap()
    {
        hubCompassMap = mutableMapOf()

        val contentSection = getMainConfig().getConfigurationSection("hubCompass.content")
        if (contentSection == null)
        {
            logger.severe("hubCompass.content is non-existent! This feature won't work!")
            return
        }

        val itemPaths = contentSection.getKeys(false)
        for (itemPath in itemPaths)
        {
            val commandString = contentSection.getString("$itemPath.command")
            val materialString = contentSection.getString("$itemPath.material")

            if (commandString == null || materialString == null)
            {
                logger.severe("hubCompass.content.command or hubCompass.content.material is non-existent! This feature won't work!")
                continue
            }

            var material: Material?
            try
            {
                material = Material.valueOf(materialString)
            }
            catch (exc: IllegalArgumentException)
            {
                logger.severe("hubCompass.content.$itemPath.material is invalid! This feature won't work!")
                continue
            }

            hubCompassMap[material] = commandString
        }
    }


    /** Money Scheduler **/
    private fun scheduleMoneyChecker()
    {
        val econ = server.servicesManager.getRegistration(Economy::class.java)!!.provider
        server.scheduler.scheduleSyncRepeatingTask(this, {
            val players = server.onlinePlayers
            val sharedWorldsSection = getMoneyConfig().getConfigurationSection("sharedWorlds")!!

            for (player in players)
            {
                if (!sharedWorldsSection.getKeys(false).contains(player.world.name)) continue

                val balance = econ.getBalance(player).toBigDecimal().setScale(2, RoundingMode.HALF_UP).toPlainString()
                getMoneyConfig().set("worldBalances.${sharedWorldsSection.get(player.world.name)}.${player.uniqueId}", balance)
            }

            saveMoneyConfig()

        }, 0L, 20L * 60 * 5)
    }


    /** Load map image **/
    fun loadMapImages()
    {

    }
}