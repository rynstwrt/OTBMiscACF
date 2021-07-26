package art.ryanstew.otbmisc.playercommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.HomeUtil.HomeUtil.playerHasHomes
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CatchUnknown
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.Default
import org.bukkit.Location
import org.bukkit.entity.Player

@CommandAlias("sethome")
class SetHomeCommand(private val plugin: OTBMisc) : BaseCommand()
{
    @Default
    fun onNoArgs(player: Player)
    {
        if (playerHasHomes(plugin, player))
        {
            player.sendMessage("${plugin.prefix} &cYou must specify a home name!".toChatColor())
            return
        }

        saveHomeToConfig("homes.${player.uniqueId}.home", player.location)

        player.sendMessage("${plugin.prefix} &7Successfully set your home!".toChatColor())
    }

    @CatchUnknown
    @CommandCompletion("home")
    fun onArgument(player: Player, name: String)
    {
        if (!name.matches("[A-Za-z0-9]+".toRegex()))
        {
            player.sendMessage("${plugin.prefix} &cHome names must be alphanumeric!".toChatColor())
            return
        }

        saveHomeToConfig("homes.${player.uniqueId}.$name", player.location)

        player.sendMessage("${plugin.prefix} &7Successfully set home &a$name&7!".toChatColor())
    }

    private fun saveHomeToConfig(path: String, loc: Location)
    {
        plugin.getHomeConfig().set("${path}.worldUID", loc.world!!.uid.toString())
        plugin.getHomeConfig().set("${path}.x", loc.x)
        plugin.getHomeConfig().set("${path}.y", loc.y)
        plugin.getHomeConfig().set("${path}.z", loc.z)
        plugin.getHomeConfig().set("${path}.pitch", loc.pitch)
        plugin.getHomeConfig().set("${path}.yaw", loc.yaw)

        plugin.saveHomeConfig()
    }
}