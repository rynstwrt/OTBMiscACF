package art.ryanstew.otbmisc.playercommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.bukkit.entity.Player

@CommandAlias("factionsprefix|fprefix")
@Description("Toggle your factions prefix on or off!")
class FactionsPrefixCommand(private val plugin: OTBMisc) : BaseCommand()
{
    @Default
    @CatchUnknown
    @Subcommand("toggle")
    fun onToggle(player: Player)
    {
        if (plugin.getMainConfig().getStringList("noFactionsPrefixPlayers").contains(player.uniqueId.toString()))
            onShowPrefix(player)
        else
            onHidePrefix(player)
    }

    @Subcommand("on")
    fun onShowPrefix(player: Player)
    {
        val userList = plugin.getMainConfig().getStringList("noFactionsPrefixPlayers")
        plugin.getMainConfig().set("noFactionsPrefixPlayers", userList.toSet().minus(player.uniqueId.toString()).toList())
        plugin.saveMainConfig()

        player.sendMessage("${plugin.prefix} &2Toggled your factions prefix &aon&2.".toChatColor())
    }

    @Subcommand("off")
    fun onHidePrefix(player: Player)
    {
        val userList = plugin.getMainConfig().getStringList("noFactionsPrefixPlayers")
        plugin.getMainConfig().set("noFactionsPrefixPlayers", userList.toSet().plus(player.uniqueId.toString()).toList())
        plugin.saveMainConfig()

        player.sendMessage("${plugin.prefix} &2Toggled your factions prefix &coff&2.".toChatColor())
    }
}