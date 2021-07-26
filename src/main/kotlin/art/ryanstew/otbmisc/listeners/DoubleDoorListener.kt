package art.ryanstew.otbmisc.listeners

import art.ryanstew.otbmisc.OTBMisc
import org.bukkit.Location
import org.bukkit.Tag
import org.bukkit.block.Block
import org.bukkit.block.data.type.Door
import org.bukkit.event.Event.Result
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

class DoubleDoorListener(private val plugin: OTBMisc) : Listener
{
    @EventHandler(priority = EventPriority.HIGHEST)
    fun onDoorOpen(e: PlayerInteractEvent)
    {
        // return if the block was not:
        //      - in a location where the user can build
        //      - a wooden door
        //      - right clicked
        if (e.clickedBlock == null
            || e.useInteractedBlock() == Result.DENY
            || e.action != Action.RIGHT_CLICK_BLOCK
            || !Tag.WOODEN_DOORS.isTagged(e.clickedBlock!!.type)
            || !plugin.getMainConfig().getStringList("doubleDoorPlayers").contains(e.player.uniqueId.toString())) return

        // get a nearby door that is:
        //      - a wooden door
        //      - oppositely hinged
        // otherwise return
        val nearbyWoodenDoorBlock: Block = getNearbyOppositeHingedWoodenDoor(e.clickedBlock!!) ?: return

        // flip the nearby door
        val nearbyWoodenDoorState = nearbyWoodenDoorBlock.state
        val nearbyWoodenDoor: Door = nearbyWoodenDoorState.blockData as Door
        nearbyWoodenDoor.isOpen = !nearbyWoodenDoor.isOpen
        nearbyWoodenDoorState.blockData = nearbyWoodenDoor
        nearbyWoodenDoorState.update()
    }

    // return all wooden doors that are oppositely hinged
    private fun getNearbyOppositeHingedWoodenDoor(block: Block): Block?
    {
        val locations: List<Location> = listOf(
            block.location.add(1.0, 0.0, 0.0),
            block.location.subtract(1.0, 0.0, 0.0),
            block.location.add(0.0, 0.0, 1.0),
            block.location.subtract(0.0, 0.0, 1.0)
        )

        val door: Door = block.state.blockData as Door

        for (location in locations)
        {
            val currentBlock = block.world.getBlockAt(location)
            if (Tag.WOODEN_DOORS.isTagged(currentBlock.type))
            {
                val currentDoor: Door = currentBlock.state.blockData as Door

                if (currentDoor.hinge != door.hinge) return currentBlock
            }
        }

        return null
    }
}