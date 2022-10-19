package plus.dragons.createminingindustry.contraptions.mining.blazeminer;

import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlazeMinerStationBlockEntity extends SmartTileEntity implements IHaveGoggleInformation {

    MiningTask miningTask;
    BlazeMinerInventory blazeInv;
    SmartInventory stationInv; // 27
    BlockPos commandCenterPos;
    Phase phase;
    LazyOptional<IItemHandlerModifiable> handler = LazyOptional.of(() -> stationInv);


    public BlazeMinerStationBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        blazeInv = new BlazeMinerInventory();
        stationInv = new SmartInventory(27,this);
        phase = Phase.IDLE;
    }

    @Override
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {}

    @Override
    public void tick() {
        super.tick();

        if(phase == Phase.IDLE){
            // Communicate with Command Center to apply for job.

            return;
        }
        if(phase == Phase.PREPARE){
            // Make sure everything is set, blazeInv is emptied.

            return;
        }
        if(phase == Phase.BLINK_TO_LOCATION){

            return;
        }
        if(phase == Phase.MINE){

            return;
        }
        if(phase == Phase.TRANSPORT_ITEM){

            return;
        }
        // Detect Command Center
    }

    @Override
    public void write(CompoundTag compoundTag, boolean clientPacket) {
        super.write(compoundTag, clientPacket);
        // TODO
    }

    @Override
    protected void read(CompoundTag compoundTag, boolean clientPacket) {
        super.read(compoundTag, clientPacket);
        // TODO
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        handler.invalidate();
    }

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        if (side != null && side != Direction.UP && isItemHandlerCap(capability))
            return handler.cast();
        return super.getCapability(capability, side);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        // TODO
        /*ModLang.translate("gui.goggles.blaze_enchanter").forGoggles(tooltip);
        Pair<Enchantment, Integer> ei;
        if (targetItem != null && (ei = EnchantingGuideItem.getEnchantment(targetItem)) != null) {
            tooltip.add(Components.literal("     ").append(ei.getFirst().getFullname(ei.getSecond() + (hyper()? 1 : 0))));
        }
        containedFluidTooltip(tooltip, isPlayerSneaking, getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY));*/
        return true;
    }

    enum Phase{
        IDLE,
        PREPARE,
        BLINK_TO_LOCATION,
        MINE,
        TRANSPORT_ITEM
    }

    static class BlazeMinerInventory extends SimpleContainer{
        SimpleContainer backupInv;

        public BlazeMinerInventory() {
            super(9);
            backupInv = new SimpleContainer(9);
        }

        @Override
        public void fromTag(@NotNull ListTag pContainerNbt) {
            var listTag = pContainerNbt.get(0);
            var listTag2 = pContainerNbt.get(1);
            super.fromTag((ListTag) listTag);
            backupInv.fromTag((ListTag) listTag2);
        }

        @Override
        public ListTag createTag() {
            var listTag = super.createTag();
            var listTag2 = backupInv.createTag();
            var ret = new ListTag();
            ret.add(listTag);
            ret.add(listTag2);
            return ret;
        }
    }


}
