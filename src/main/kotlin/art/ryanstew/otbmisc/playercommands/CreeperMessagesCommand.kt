package art.ryanstew.otbmisc.playercommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.bukkit.entity.Player

@CommandAlias("creepermessages")
@Description("Toggle the aww man message when you send a message containing \"creeper\".")
class CreeperMessagesCommand(private val plugin: OTBMisc) : BaseCommand()
{
    @Default
    @Subcommand("toggle")
    fun playerMessageToggle(player: Player)
    {
        if (!plugin.getMainConfig().getStringList("noCreeperMessagePlayers").contains(player.uniqueId.toString())) playerMessageToggleOff(player)
        else playerMessageToggleOn(player)
    }

    @Subcommand("on")
    fun playerMessageToggleOn(player: Player)
    {
        val uuidList = plugin.getMainConfig().getStringList("noCreeperMessagePlayers")
        plugin.getMainConfig().set("noCreeperMessagePlayers", uuidList.toSet().minus(player.uniqueId.toString()).toList())
        plugin.saveMainConfig()

        player.sendMessage("${plugin.prefix} &2Toggled creeper messages &aon&2.".toChatColor())
    }

    @Subcommand("off")
    fun playerMessageToggleOff(player: Player)
    {
        val uuidList = plugin.getMainConfig().getStringList("noCreeperMessagePlayers")
        plugin.getMainConfig().set("noCreeperMessagePlayers", uuidList.toSet().plus(player.uniqueId.toString()).toList())
        plugin.saveMainConfig()

        player.sendMessage("${plugin.prefix} &2Toggled creeper messages &coff&2.".toChatColor())
    }

    @CatchUnknown
    fun onUnknown(player: Player)
    {
        player.sendMessage("${plugin.prefix} &cIncorrect usage! Usage: /creepermessages [<on|off|toggle>]".toChatColor())
    }
}