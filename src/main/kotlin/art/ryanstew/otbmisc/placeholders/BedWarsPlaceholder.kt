package art.ryanstew.otbmisc.placeholders

import art.ryanstew.otbmisc.util.MiscUtil.Util.toChatColor
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.screamingsandals.bedwars.api.BedwarsAPI
import org.screamingsandals.bedwars.api.TeamColor

class BedWarsPlaceholder() : PlaceholderExpansion()
{
    private val api = BedwarsAPI.getInstance()
    private val teamColorChatColorMap =  mapOf(
        TeamColor.BLACK to ChatColor.BLACK,
        TeamColor.BLUE to ChatColor.DARK_AQUA,
        TeamColor.GREEN to ChatColor.DARK_GREEN,
        TeamColor.RED to ChatColor.RED,
        TeamColor.MAGENTA to ChatColor.DARK_PURPLE,
        TeamColor.ORANGE to ChatColor.GOLD,
        TeamColor.LIGHT_GRAY to ChatColor.GRAY,
        TeamColor.GRAY to ChatColor.DARK_GRAY,
        TeamColor.LIGHT_BLUE to ChatColor.BLUE,
        TeamColor.LIME to ChatColor.GREEN,
        TeamColor.CYAN to ChatColor.AQUA,
        TeamColor.PINK to ChatColor.LIGHT_PURPLE,
        TeamColor.YELLOW to ChatColor.YELLOW,
        TeamColor.WHITE to ChatColor.WHITE,
        TeamColor.BROWN to ChatColor.GOLD
    )

    override fun canRegister(): Boolean
    {
        return true
    }

    override fun getAuthor(): String
    {
        return "RuntimeRyan"
    }

    override fun getIdentifier(): String
    {
        return "otbbedwars"
    }

    override fun getRequiredPlugin(): String
    {
        return "BedWars"
    }

    override fun getVersion(): String
    {
        return "1.0.0"
    }

    override fun onRequest(offlinePlayer: OfflinePlayer?, identifier: String): String
    {
        val player = offlinePlayer?.player ?: return ""

        if (!identifier.equals("prefix", true)
            || !api.isPlayerPlayingAnyGame(player)
            || !api.getGameOfPlayer(player)!!.isPlayerInAnyTeam(player)) return ""


        val allTeams = api.getGameOfPlayer(player)!!.runningTeams
        val playerTeam = allTeams.firstOrNull { it.isPlayerInTeam(player) } ?: return ""

        val color = teamColorChatColorMap[playerTeam.color]
        return "&8&l[${color}&lTEAM ${playerTeam.name.uppercase()}&8&l]&r ".toChatColor()
    }
}