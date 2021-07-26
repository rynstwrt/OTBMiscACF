package art.ryanstew.otbmisc.playercommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.Subcommand
import org.bukkit.entity.Player

@CommandAlias("doubledoors|dd|ddoor|ddoors")
@Description("Toggle opening double doors automatically!")
class DoubleDoorsCommand(private val plugin: OTBMisc) : BaseCommand()
{
    @Default
    @Subcommand("toggle")
    private fun toggleDoubleDoors(player: Player)
    {
        if (plugin.getMainConfig().getStringList("doubleDoorPlayers").contains(player.uniqueId.toString()))
            turnDoubleDoorsOff(player)
        else
            turnDoubleDoorsOn(player)
    }

    @Subcommand("on")
    private fun turnDoubleDoorsOn(player: Player)
    {
        val doubleDoorPlayers = plugin.getMainConfig().getStringList("doubleDoorPlayers").toSet()
        plugin.getMainConfig().set("doubleDoorPlayers", doubleDoorPlayers.toSet().plus(player.uniqueId.toString()).toList())
        plugin.saveMainConfig()
        player.sendMessage("${plugin.prefix} &aToggled double doors on.".toChatColor())
    }

    @Subcommand("off")
    private fun turnDoubleDoorsOff(player: Player)
    {
        val doubleDoorPlayers = plugin.getMainConfig().getStringList("doubleDoorPlayers").toSet()
        plugin.getMainConfig().set("doubleDoorPlayers", doubleDoorPlayers.toSet().minus(player.uniqueId.toString()).toList())
        plugin.saveMainConfig()
        player.sendMessage("${plugin.prefix} &aToggled double doors &coff&a.".toChatColor())
    }

}