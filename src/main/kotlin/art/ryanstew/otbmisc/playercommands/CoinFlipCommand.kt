package art.ryanstew.otbmisc.playercommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CatchUnknown
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Default
import co.aikar.commands.annotation.Description
import org.bukkit.command.CommandSender
import kotlin.random.Random

@CommandAlias("coinflip|flip")
@Description("Flip a coin!")
class CoinFlipCommand(private val plugin: OTBMisc) : BaseCommand()
{
    @Default
    @CatchUnknown
    fun onCoinFlipCommand(sender: CommandSender)
    {
        var face = "tails"
        if (Random.nextBoolean()) face = "heads"

        sender.sendMessage("${plugin.prefix} &aYou flipped a coin and got &6$face&a.".toChatColor())
    }
}