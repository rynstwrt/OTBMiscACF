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

@CommandAlias("diceroll|roll")
@Description("Roll a die!")
class DiceRollCommand(private val plugin: OTBMisc) : BaseCommand()
{
    @Default
    @CatchUnknown
    fun onDiceRollCommand(sender: CommandSender)
    {
        val diceSide = Random.nextInt(1, 7)
        sender.sendMessage("${plugin.prefix} &aYou rolled a die and got &6$diceSide&a.".toChatColor())
    }
}