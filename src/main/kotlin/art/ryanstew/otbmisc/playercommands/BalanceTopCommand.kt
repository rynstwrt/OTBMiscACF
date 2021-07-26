package art.ryanstew.otbmisc.playercommands

import art.ryanstew.otbmisc.OTBMisc
import art.ryanstew.otbmisc.util.MiscUtil.Util.moneyFormat
import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import com.Zrips.CMI.CMI
import org.bukkit.entity.Player
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

@CommandAlias("balancetop|worldbalancetop|baltop|worldbaltop")
@Description("See the top balances of the world you're in!")
class BalanceTopCommand(private val plugin: OTBMisc) : BaseCommand()
{
    val playerManager = CMI.getInstance().playerManager

    @Default
    @CommandCompletion("page")
    fun onBalanceTopCommand(player: Player, @Default("1") pageNumber: Int)
    {
        val worldName = player.world.name
        val moneySharedWorldSection = plugin.getMoneyConfig().getConfigurationSection("sharedWorlds")

        if (moneySharedWorldSection == null)
        {
            plugin.logger.severe("Could not load sharedWorlds from money.yml! Money based commands will most likely not work!")
            player.sendMessage("${plugin.prefix} &cThe configuration for world balances is set up incorrectly! Contact Ryan!".toChatColor())
            return
        }

        val moneySharedWorldKeys = moneySharedWorldSection.getKeys(false)

        if (!moneySharedWorldKeys.contains(worldName))
        {
            player.sendMessage("${plugin.prefix} &cBalances are disabled in this world!".toChatColor())
            return
        }

        val displayWorldName = moneySharedWorldSection.get(worldName).toString()
        val displayWorldNameSection = plugin.getMoneyConfig().getConfigurationSection("worldBalances.$displayWorldName")

        if (displayWorldNameSection == null || displayWorldNameSection.getKeys(false).isEmpty())
        {
            player.sendMessage("${plugin.prefix} &cNo balances are available in this world!".toChatColor())
            return
        }

        val worldBalances: MutableMap<String, BigDecimal> = mutableMapOf()
        val displayWorldNameSectionKeys = displayWorldNameSection.getKeys(false)

        for (uuid in displayWorldNameSectionKeys)
        {
            val balance: BigDecimal = displayWorldNameSection.get(uuid).toString().toBigDecimal()
            worldBalances[uuid] = balance
        }

        val sortedWorldBalances = worldBalances.toSortedMap(compareBy { worldBalances[it] })

        var baltopMessage = "&0\n&8------ &b&lTOP WORLD BALANCES&8 ------\n"

        val startIndex = 10 * (pageNumber - 1)

        if (startIndex > sortedWorldBalances.size)
        {
            player.sendMessage("${plugin.prefix} &cThat page does not exist!".toChatColor())
            return
        }

        for (balanceIndex in 0 until (pageNumber - 1) * 10)
        {
            if (sortedWorldBalances.isEmpty()) break
            sortedWorldBalances.remove(sortedWorldBalances.lastKey())
        }

        for (i in startIndex..(startIndex + 9))
        {
            if (sortedWorldBalances.isEmpty()) break

            val playerUUID = UUID.fromString(sortedWorldBalances.lastKey())
            val playerName = playerManager.getUser(playerUUID).displayName

            val balance = sortedWorldBalances[sortedWorldBalances.lastKey()]!!
            val formattedBalance: String = moneyFormat.format(balance.setScale(2,  RoundingMode.HALF_UP))
            baltopMessage += "&3${i + 1}) &b$playerName&7 - &a$$formattedBalance\n"
            sortedWorldBalances.remove(sortedWorldBalances.lastKey())
        }

        baltopMessage += "&8----------------------------------\n&0"

        player.sendMessage(baltopMessage.toChatColor())
    }

    @CatchUnknown
    fun onUnknownArguments(player: Player)
    {
        player.sendMessage("${plugin.prefix} &cIncorrect usage! Usage: /baltop <page>".toChatColor())
    }
}