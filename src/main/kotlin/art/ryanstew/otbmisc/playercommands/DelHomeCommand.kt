package art.ryanstew.otbmisc.playercommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.HomeUtil
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CatchUnknown
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandCompletion
import co.aikar.commands.annotation.Default
import org.bukkit.entity.Player

@CommandAlias("delhome|deletehome|removehome")
class DelHomeCommand(private val plugin: OTBMisc) : BaseCommand()
{
    @Default
    fun onNoArguments(player: Player)
    {
        if (!HomeUtil.playerHasHomes(plugin, player))
        {
            player.sendMessage("${plugin.prefix} &cYou do not have any homes set!".toChatColor())
            return
        }

        val homeNames = plugin.getHomeConfig().getConfigurationSection("homes.${player.uniqueId}")!!.getKeys(false)

        if (homeNames.size == 1)
        {
            deleteHome(player, homeNames.first())
            return
        }

        player.sendMessage("${plugin.prefix} &cYou must specify a home name!".toChatColor())
    }

    @CatchUnknown
    @CommandCompletion("home")
    fun onWithArguments(player: Player, name: String)
    {
        if (!HomeUtil.playerHasHomes(plugin, player))
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

        deleteHome(player, foundHomeNames.first())
    }

    private fun deleteHome(player: Player, homeName: String)
    {
        plugin.getHomeConfig().set("homes.${player.uniqueId}.$homeName", null)

        // if that was their last home, remove their UUID from config
        val remainingHomeNames = plugin.getHomeConfig().getConfigurationSection("homes.${player.uniqueId}")!!.getKeys(false)
        if (remainingHomeNames.isEmpty())
        {
            plugin.getHomeConfig().set("homes.${player.uniqueId}", null)
        }

        plugin.saveHomeConfig()

        player.sendMessage("${plugin.prefix} &7Successfully deleted home &a$homeName&7.".toChatColor())
    }
}