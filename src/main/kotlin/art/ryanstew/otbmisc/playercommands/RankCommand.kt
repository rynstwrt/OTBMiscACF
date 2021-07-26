package art.ryanstew.otbmisc.playercommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import com.Zrips.CMI.CMI
import me.armar.plugins.autorank.Autorank
import me.armar.plugins.autorank.api.API
import me.armar.plugins.autorank.pathbuilder.Path
import me.armar.plugins.autorank.storage.TimeType
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.TimeUnit

@CommandAlias("rank")
@Description("See yours or another's rank progress!")
class RankCommand(private val plugin: OTBMisc) : BaseCommand()
{
    private val playerManager = CMI.getInstance().playerManager
    private val autoRank: API = (plugin.server.pluginManager.getPlugin("Autorank") as Autorank).api

    @Default
    fun onRankCommand(player: Player)
    {
        sendRankInfo(player, player.uniqueId)
    }

    @CatchUnknown
    @CommandCompletion("@players")
    fun onRankCommandSpecific(sender: CommandSender, @Name("user") @Single playerName: String)
    {
        val user = playerManager.getUser(playerName)

        if (user == null)
        {
            sender.sendMessage("${plugin.prefix} &cThat player could not be found!".toChatColor())
            return
        }

        sendRankInfo(sender, user.uniqueId)
    }

    private fun sendRankInfo(sender: CommandSender, targetPlayerUUID: UUID)
    {
        val activePaths: List<Path> = autoRank.getActivePaths(targetPlayerUUID)
        val currentRank: String
        val nextRank: String
        val requirement: String
        val ranks = plugin.getMainConfig().getStringList("ranks")

        if (activePaths.isEmpty())
        {
            nextRank = "None!"
            currentRank = ranks.last()
            requirement = "None!"
        }
        else
        {
            nextRank = activePaths[0].displayName
            currentRank = ranks[ranks.indexOf(nextRank) - 1]
            requirement = activePaths[0].requirements[0].description
        }

        val timePlayedFuture = autoRank.getPlayTime(TimeType.TOTAL_TIME, targetPlayerUUID, TimeUnit.MILLISECONDS)
        timePlayedFuture.thenRun {
            try
            {
                val timePlayed: Long = timePlayedFuture.get()
                val playedDays: Long = TimeUnit.MILLISECONDS.toDays(timePlayed)
                val playedHours: Long = TimeUnit.MILLISECONDS.toHours(timePlayed) - TimeUnit.DAYS.toHours(playedDays)
                val playedMinutes: Long = TimeUnit.MILLISECONDS.toMinutes(timePlayed) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timePlayed))
                val timePlayedMessage = "${playedDays}d ${playedHours}h ${playedMinutes}m   "

                var percent = "100"
                if (!nextRank.equals("None!", true))
                    percent = DecimalFormat("0.##").format(autoRank.getPath(nextRank).getProgress(targetPlayerUUID) * 100)

                val message = "&0\n&8------------&b&lRANK DATA&8------------\n"
                    .plus("&3Current rank&7: &a$currentRank\n")
                    .plus("&3Next rank&7: &a$nextRank\n")
                    .plus("&3Requirement&7: &a$requirement\n")
                    .plus("&3Time played&7: &a$timePlayedMessage\n")
                    .plus("&3Total Progress&7: &a$percent%\n")
                    .plus("&8----------------------------------\n&0")

                sender.sendMessage(message.toChatColor())
            }
            catch (e: Exception)
            {
                e.printStackTrace()
                sender.sendMessage("${plugin.prefix} &cCould not load rank data!".toChatColor())
            }
        }
    }
}