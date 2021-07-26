package art.ryanstew.otbmisc.playercommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.HomeUtil.HomeUtil.playerHasHomes
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

@CommandAlias("home")
@Description("Teleport to your saved home!")
class HomeCommand(private val plugin: OTBMisc) : BaseCommand()
{
    @Default
    fun onNoArguments(player: Player)
    {
        if (!playerHasHomes(plugin, player))
        {
            player.sendMessage("${plugin.prefix} &cYou do not have any homes set!".toChatColor())
            return
        }

        val homeNames = plugin.getHomeConfig().getConfigurationSection("homes.${player.uniqueId}")!!.getKeys(false)

        if (homeNames.size == 1)
        {
            val homeName = homeNames.first()
            teleportToHome(player, getLocationFromHomeName(player, homeName), homeName)
            return
        }

        player.sendMessage("${plugin.prefix} &cYou must specify a home name!".toChatColor())
    }

    @CatchUnknown
    @CommandCompletion("home")
    fun onWithArgument(player: Player, @Single name: String)
    {
        if (!playerHasHomes(plugin, player))
        {
            player.sendMessage("${plugin.prefix} &cYou do not have any homes set!".toChatColor())
            return
        }

        val homeNames = plugin.getHomeConfig().getConfigurationSection("homes.${player.uniqueId}")!!.getKeys(false)
        val foundHomeNames = homeNames.filter { it.equals(name, true) }

        if (foundHomeNames.isEmpty())
        {
            player.sendMessage("${plugin.prefix} &cThat home was not found!".toChatColor())
            return
        }

        teleportToHome(player, getLocationFromHomeName(player, foundHomeNames.first()), foundHomeNames.first())
    }

    private fun teleportToHome(player: Player, location: Location, homeName: String)
    {
        player.teleport(location)
        player.sendMessage("${plugin.prefix} &7Teleporting you to &a${homeName}&7.".toChatColor())
    }

    private fun getLocationFromHomeName(player: Player, homeName: String) : Location
    {
        val section = plugin.getHomeConfig().getConfigurationSection("homes.${player.uniqueId}.${homeName}")!!

        val worldUUID = section.getString("worldUID")
        val x = section.getString("x")!!.toDouble()
        val y = section.getString("y")!!.toDouble()
        val z = section.getString("z")!!.toDouble()
        val yaw = section.getString("yaw")!!.toFloat()
        val pitch = section.getString("pitch")!!.toFloat()

        val world = plugin.server.getWorld(UUID.fromString(worldUUID)) ?: return player.world.spawnLocation
        return Location(world, x, y, z, yaw, pitch)
    }
}