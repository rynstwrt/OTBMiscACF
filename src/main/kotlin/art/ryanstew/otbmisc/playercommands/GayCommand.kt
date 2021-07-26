package art.ryanstew.otbmisc.playercommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CatchUnknown
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.LocalDate

@CommandAlias("gay|lgbtq")
@Description("✧･ﾟ: *✧･ﾟ･✧*:･ﾟ✧")
class GayCommand(private val plugin: OTBMisc) : BaseCommand()
{
    @Default
    @CatchUnknown
    fun onGayCommand(sender: CommandSender)
    {
        val gayMessage = plugin.getMainConfig().get("gay.message").toString()
        val gayMessagePrideMonth = plugin.getMainConfig().get("gay.messagePrideMonth").toString()
        val gayMessageSound = plugin.getMainConfig().get("gay.messageSound").toString()

        var message = "&0\n${gayMessage}"
        if (LocalDate.now().monthValue == 6) message += "\n${gayMessagePrideMonth}"
        message += "\n&0"

        sender.sendMessage(message.toChatColor())

        if (sender !is Player) return

        try
        {
            val gaySound = Sound.valueOf(gayMessageSound)
            sender.playSound(sender.location, gaySound, 1f, 1f)
        }
        catch (e: IllegalArgumentException)
        {
            Bukkit.getLogger().warning("&cThe gay.messageSound sound set in the config does not exist!".toChatColor())
            return
        }
    }
}