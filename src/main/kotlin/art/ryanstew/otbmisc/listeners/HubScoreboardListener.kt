package art.ryanstew.otbmisc.listeners

import art.ryanstew.otbmisc.OTBMisc
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.scoreboard.DisplaySlot

class HubScoreboardListener(private val plugin: OTBMisc) : Listener
{
    @EventHandler
    fun onPlayerWorldChange(e: PlayerChangedWorldEvent)
    {
        val hubWorld = plugin.getMainConfig().getString("hubWorld")!!
        if (!hubWorld.equals(e.player.world.name, true)) return

        val scoreboardManager = Bukkit.getScoreboardManager()
        val board = scoreboardManager.newScoreboard

        val team = board.registerNewTeam("hub")
        team.addEntry(e.player.name)

        val obj = board.registerNewObjective("name", "criteria", Component.text("displayname"))
        obj.displaySlot = DisplaySlot.SIDEBAR
        obj.getScore(e.player.name).score = 17

        Bukkit.broadcast(Component.text("asdf"))
        e.player.scoreboard = board
    }
}