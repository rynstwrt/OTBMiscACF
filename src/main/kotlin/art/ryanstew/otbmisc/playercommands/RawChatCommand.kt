package art.ryanstew.otbmisc.playercommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.bukkit.entity.Player

@CommandAlias("rawchat|rchat|sayraw")
@Description("Say a message without color formatting!")
class RawChatCommand(private val plugin: OTBMisc) : BaseCommand()
{
    @Default
    fun onNoMessage(player: Player)
    {
        player.sendMessage("${plugin.prefix} &cIncorrect usage! Usage: /rawchat <message>".toChatColor())
    }

    @CatchUnknown
    @CommandCompletion("message")
    fun onArguments(player: Player, message: String)
    {
        player.chat(message.replace("(.)".toRegex(), "&r$1"))
    }

}