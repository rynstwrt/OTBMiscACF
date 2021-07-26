package art.ryanstew.otbmisc.playercommands

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CatchUnknown
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Default
import org.bukkit.entity.Player

@CommandAlias("loom")
@CommandPermission("otbmisc.loom")
class LoomCommand : BaseCommand()
{
    @Default
    @CatchUnknown
    fun onCommand(player: Player)
    {
        player.openLoom(null, true)
    }
}
