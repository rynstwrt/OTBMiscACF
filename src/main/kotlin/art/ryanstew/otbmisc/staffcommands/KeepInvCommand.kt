package art.ryanstew.otbmisc.staffcommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("keepinv|keepinventory")
@CommandPermission("otbmisc.keepinv")
@Description("Toggle keepinv for you or a player!")
class KeepInvCommand(private val plugin: OTBMisc) : BaseCommand()
{
    private fun toggleKeepInv(player: Player, sender: CommandSender?)
    {
        if (plugin.getMainConfig().getStringList("keepInvPlayers").contains(player.uniqueId.toString()))
        {
            onKeepInvOffCommand(player)
            sender?.sendMessage("${plugin.prefix} &aSuccessfully toggled keepinv &coff&a for ${player.displayName()}&a!".toChatColor())
        }
        else
        {
            onKeepInvOnCommand(player)
            sender?.sendMessage("${plugin.prefix} &aSuccessfully toggled keepinv on for ${player.displayName()}&a!".toChatColor())
        }
    }

    private fun sendIncorrectUsageMessage(sender: CommandSender)
    {
        var message = "${plugin.prefix} &cIncorrect usage! Usage: /keepinv [<on|off|toggle>] or /keepinv <player> [<on|off|toggle>]"
        if (sender !is Player) message = "${plugin.prefix} &cIncorrect usage! Usage: /keepinv <player> [<on|off|toggle>]"
        sender.sendMessage(message.toChatColor())
    }

    @Default
    fun onNoArguments(sender: CommandSender)
    {
        if (sender is Player)
            toggleKeepInv(sender, null)
        else
            sendIncorrectUsageMessage(sender)
    }

    @Subcommand("on")
    fun onKeepInvOnCommand(player: Player)
    {
        val keepInvPlayers = plugin.getMainConfig().getStringList("keepInvPlayers")
        plugin.getMainConfig().set("keepInvPlayers", keepInvPlayers.toSet().plus(player.uniqueId.toString()).toList())
        plugin.saveMainConfig()

        player.sendMessage("${plugin.prefix} &aSuccessfully toggled keepinv on.".toChatColor())
    }

    @Subcommand("off")
    fun onKeepInvOffCommand(player: Player)
    {
        val keepInvPlayers = plugin.getMainConfig().getStringList("keepInvPlayers")
        plugin.getMainConfig().set("keepInvPlayers", keepInvPlayers.toSet().minus(player.uniqueId.toString()).toList())
        plugin.saveMainConfig()

        player.sendMessage("${plugin.prefix} &aSuccessfully toggled keepinv &coff&a.".toChatColor())
    }

    @Subcommand("toggle")
    fun onToggleKeepInvCommand(player: Player)
    {
        toggleKeepInv(player, null)
    }

    @CatchUnknown
    @CommandCompletion("@players on|off|toggle")
    fun toggleKeepInvTarget(sender: CommandSender, @Single playerName: String?, @Optional @Single value: String?)
    {
        if (playerName == null)
        {
            sendIncorrectUsageMessage(sender)
            return
        }

        val targetPlayer: Player? = Bukkit.getPlayer(playerName)

        if (targetPlayer == null)
        {
            sender.sendMessage("${plugin.prefix} &cThat player was not found!".toChatColor())
            return
        }

        if (value == null || value.equals("toggle", true))
        {
            toggleKeepInv(targetPlayer, sender)
            return
        }

        if (value.equals("on", true))
        {
            onKeepInvOnCommand(targetPlayer)
            sender.sendMessage("${plugin.prefix} &aSuccessfully toggled keepinv on for ${targetPlayer.displayName()}&a!".toChatColor())
            return
        }

        if (value.equals("off", true))
        {
            onKeepInvOffCommand(targetPlayer)
            sender.sendMessage("${plugin.prefix} &aSuccessfully toggled keepinv &coff&a for ${targetPlayer.displayName()}&a!".toChatColor())
            return
        }
    }
}