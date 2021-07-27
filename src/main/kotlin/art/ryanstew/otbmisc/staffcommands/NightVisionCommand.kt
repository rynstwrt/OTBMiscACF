package art.ryanstew.otbmisc.staffcommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

@CommandAlias("nightvision|nv")
@CommandPermission("otbmisc.nightvision")
@Description("Toggle night vision!")
class NightVisionCommand(private val plugin: OTBMisc) : BaseCommand()
{
    private fun setOnForPlayer(player: Player, sender: CommandSender?)
    {
        player.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, Int.MAX_VALUE, Int.MAX_VALUE, false, false, false))

        player.sendMessage("${plugin.prefix} &aToggled nightvision on.".toChatColor())
        sender?.sendMessage("${plugin.prefix} &aToggled nightvision on for ${player.displayName()}&a.".toChatColor())
    }

    private fun setOffForPlayer(player: Player, sender: CommandSender?)
    {
        player.removePotionEffect(PotionEffectType.NIGHT_VISION)

        player.sendMessage("${plugin.prefix} &aToggled nightvision &coff&a.".toChatColor())
        sender?.sendMessage("${plugin.prefix} &aToggled nightvision &coff&a for ${player.displayName()}&a.".toChatColor())
    }

    private fun toggleForPlayer(player: Player, sender: CommandSender?)
    {
        if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION))
            setOffForPlayer(player, sender)
        else
            setOnForPlayer(player, sender)
    }

    private fun sendIncorrectUsageMessage(sender: CommandSender)
    {
        var message = "&cIncorrect usage! Usage: /nv <player> [<on|off|toggle>]"
        if (sender is Player) message = "&cIncorrect usage! Usage: /nv [<on|off|toggle>] or /nv <player> [<on|off|toggle>]"
        sender.sendMessage("${plugin.prefix} $message".toChatColor())
    }

    @Default
    fun onBaseCommand(sender: CommandSender)
    {
        if (sender is Player)
            toggleForPlayer(sender, null)
        else
            sendIncorrectUsageMessage(sender)
    }

    @Subcommand("on")
    fun onSelfOnCommand(player: Player)
    {
        setOnForPlayer(player, null)
    }

    @Subcommand("off")
    fun onSelfOffCommand(player: Player)
    {
        setOffForPlayer(player, null)
    }

    @Subcommand("toggle")
    fun onSelfToggleCommand(player: Player)
    {
        toggleForPlayer(player, null)
    }

    @CatchUnknown
    @CommandCompletion("@players on|off|toggle")
    fun onOtherCommand(sender: CommandSender, playerName: String, @Optional value: String?)
    {
        val player: Player? = Bukkit.getPlayer(playerName)

        if (player == null)
        {
            sender.sendMessage("${plugin.prefix} &cThat player was not found!")
            return
        }

        if (value == null || value.equals("toggle", true))
            toggleForPlayer(player, sender)
        else if (value.equals("on", true))
            setOnForPlayer(player, sender)
        else if (value.equals("off", true))
            setOffForPlayer(player, sender)
        else
            sendIncorrectUsageMessage(sender)
    }

}