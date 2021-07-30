package art.ryanstew.otbmisc.placeholders

import art.ryanstew.otbmisc.OTBMisc
import com.massivecraft.factions.FPlayer
import com.massivecraft.factions.FPlayers
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer

class FactionNamePlaceholder(private val plugin: OTBMisc) : PlaceholderExpansion()
{
    private val factionsWorlds = listOf("factions", "factions_nether", "factions_the_end")

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
        return "otbfactions"
    }

    override fun getRequiredPlugin(): String
    {
        return "Factions"
    }

    override fun getVersion(): String
    {
        return "1.0.0"
    }

    override fun onRequest(offlinePlayer: OfflinePlayer?, identifier: String): String
    {
        val player = offlinePlayer?.player ?: return ""

        if (!identifier.equals("factionname", true)) return ""

        if (!factionsWorlds.contains(player.world.name)
            || plugin.getMainConfig().getStringList("noFactionsPrefixPlayers").contains(player.uniqueId.toString())) return ""

        val fPlayer: FPlayer = FPlayers.getInstance().getByPlayer(player.player)

        if (!fPlayer.hasFaction()) return ""

        return "&8&l[&a${fPlayer.rolePrefix}${fPlayer.faction.tag}&8&l]&r "
    }
}