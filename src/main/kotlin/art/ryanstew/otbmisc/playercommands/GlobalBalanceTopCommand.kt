package art.ryanstew.otbmisc.playercommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MiscUtil.Util.moneyFormat
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import com.Zrips.CMI.CMI
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

@CommandAlias("gbalancetop|globalbalancetop|gbaltop|globalbaltop")
@Description("See the top balances across all worlds!")
class GlobalBalanceTopCommand(private val plugin: OTBMisc) : BaseCommand()
{
    val playerManager = CMI.getInstance().playerManager

    @Default
    @CommandCompletion("page")
    fun onGlobalBalanceTop(sender: CommandSender, @Default("1") pageNumber: Int)
    {
        val worldBalancesSection = plugin.getMoneyConfig().getConfigurationSection("worldBalances")

        if (worldBalancesSection == null || worldBalancesSection.getKeys(false).isEmpty())
        {
            plugin.logger.severe("Could not load worldBalances from money.yml! Money based commands will most likely not work!")
            sender.sendMessage("${plugin.prefix} &cThe configuration for world balances is set up incorrectly! Contact Ryan!".toChatColor())
            return
        }

        val worldNames = worldBalancesSection.getKeys(false)
        val balanceMap: MutableMap<String, Pair<BigDecimal, String>> = mutableMapOf() // Map<UUID, Pair<Balance, GameMode>>

        for (worldName in worldNames)
        {
            val uuids = worldBalancesSection.getConfigurationSection(worldName)!!.getKeys(false)

            for (uuid in uuids)
            {
                val balance: BigDecimal = worldBalancesSection.get("$worldName.$uuid").toString().toBigDecimal()

                if (!balanceMap.containsKey(uuid) || balanceMap[uuid]!!.first < balance)
                {
                    val baltopGameModeKeys = plugin.getMainConfig().getConfigurationSection("baltopGameModes")?.getKeys(false)

                    if (baltopGameModeKeys == null)
                    {
                        plugin.logger.severe("Could not get baltopGameModes from config.yml!")
                        sender.sendMessage("${plugin.prefix} &cCould not get config values! Contact Ryan to fix it!".toChatColor())
                        return
                    }

                    val baltopGameModes = mutableMapOf<String, String>()
                    for (baltopGameModeKey in baltopGameModeKeys)
                    {
                        val baltopGameModeValue = plugin.getMainConfig().get("baltopGameModes.$baltopGameModeKey").toString()
                        baltopGameModes[baltopGameModeKey] = baltopGameModeValue
                    }

                    balanceMap[uuid] = Pair(balance, baltopGameModes[worldName].toString())
                }
            }
        }

        val sortedBalanceMap = balanceMap.toSortedMap(compareBy { balanceMap[it]!!.first })
        var baltopMessage = "&0\n&8------ &c&lTOP GLOBAL BALANCES&8 ------\n"

        val startIndex = 10 * (pageNumber - 1)
        if (startIndex > sortedBalanceMap.size)
        {
            sender.sendMessage("&cThat page does not exist!".toChatColor())
            return
        }

        for (dataIndex in 0 until (pageNumber - 1) * 10)
        {
            if (sortedBalanceMap.isEmpty()) break
            sortedBalanceMap.remove(sortedBalanceMap.lastKey())
        }

        for (i in startIndex..(startIndex + 9))
        {
            if (sortedBalanceMap.isEmpty()) break

            val playerUUID = plugin.server.getOfflinePlayer(UUID.fromString(sortedBalanceMap.lastKey())).uniqueId
            val playerName = playerManager.getUser(playerUUID).displayName

            val entry = sortedBalanceMap[sortedBalanceMap.lastKey()]!!
            val formattedBalance: String = moneyFormat.format(entry.first.setScale(2, RoundingMode.HALF_UP))

            baltopMessage += "&c${i + 1}) &4$playerName&8 - &7${entry.second} &8- &a$$formattedBalance\n"
            sortedBalanceMap.remove(sortedBalanceMap.lastKey())
        }

        baltopMessage += "&8-----------------------------------\n&0"
        sender.sendMessage(baltopMessage.toChatColor())
    }

    @CatchUnknown
    fun onUnknownArguments(player: Player)
    {
        player.sendMessage("${plugin.prefix} &cIncorrect usage! Usage: /gbaltop <page>".toChatColor())
    }
}