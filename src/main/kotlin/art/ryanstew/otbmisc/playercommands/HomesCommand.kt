package art.ryanstew.otbmisc.playercommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.HomeUtil.HomeUtil.getHomeListMessage
import art.ryanstew.otbmisc.util.HomeUtil.HomeUtil.playerHasHomes
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CatchUnknown
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import org.bukkit.entity.Player

@CommandAlias("homes")
class HomesCommand(private val plugin: OTBMisc) : BaseCommand()
{
    @Default
    @CatchUnknown
    fun onCommand(player: Player)
    {
        if (!playerHasHomes(plugin, player))
        {
            player.sendMessage("${plugin.prefix} &cYou do not have any homes set!".toChatColor())
            return
        }

        val homeNames = plugin.getHomeConfig().getConfigurationSection("homes.${player.uniqueId}")!!.getKeys(false)
        player.sendMessage(getHomeListMessage(plugin, homeNames))
    }
}