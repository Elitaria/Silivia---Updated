package me.meowcher.silivia.utils.player

import me.meowcher.silivia.utils.misc.UMinecraft.Companion.minecraft
import me.meowcher.silivia.utils.world.UBlock
import net.minecraft.item.Item
import net.minecraft.network.Packet
import net.minecraft.network.packet.c2s.play.*
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d

class UInteract
{
    companion object
    {
        fun doPacketSend(Packet : Packet<*>)
        {
            minecraft.networkHandler?.sendPacket(Packet)
        }
        private fun doSwingHand()
        {
            minecraft.player?.swingHand(Hand.MAIN_HAND)
        }
        fun doUse()
        {
            doPacketSend(PlayerInteractItemC2SPacket(Hand.MAIN_HAND))
        }
        private fun doStartDestroyBlock(destroyPosition : BlockPos?)
        {
            doPacketSend(PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, destroyPosition, Direction.UP))
        }
        fun doAbortDestroyBlock(destroyPosition : BlockPos)
        {
            doPacketSend(PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, destroyPosition, Direction.UP))
        }
        fun doInteractBlock(Position : BlockPos, Direction : Direction, Hand : Hand)
        {
            val result = BlockHitResult(Vec3d.of(Position), Direction, Position, false)
            minecraft.player!!.networkHandler.sendPacket(PlayerInteractBlockC2SPacket(Hand, result))
        }
        fun doPlace(Item : Item, Position : BlockPos, Direction : Direction, swapBack : Boolean)
        {
            var selectSlotDone = false
            val oldSlot = UInventory.getSlot()
            val itemSlot = UInventory.getItemSlot(Item, false)
            if (!UBlock.isAir(Position)) return
            if (itemSlot != oldSlot)
            {
                UInventory.doSelectSlot(itemSlot)
                selectSlotDone = true
            }
            doInteractBlock(Position, Direction, Hand.MAIN_HAND)
            if (selectSlotDone && swapBack) UInventory.doSelectSlot(oldSlot)
            doStartDestroyBlock(Position)
            doSwingHand()
        }
    }
}
