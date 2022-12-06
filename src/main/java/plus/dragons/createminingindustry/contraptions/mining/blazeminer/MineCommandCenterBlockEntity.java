package plus.dragons.createminingindustry.contraptions.mining.blazeminer;

import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.SyncedTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import plus.dragons.createminingindustry.MiningIndustry;
import plus.dragons.createminingindustry.entry.CmiItems;

import javax.annotation.Nullable;
import java.util.List;

public class MineCommandCenterBlockEntity extends SmartTileEntity implements IHaveGoggleInformation {

    MineFieldTask mineFieldTask;
    CommandCenterInventory inv;
    LazyOptional<IItemHandlerModifiable> handler = LazyOptional.of(() -> inv);
    public MineCommandCenterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        mineFieldTask = null;
        inv = new CommandCenterInventory(18,this);
    }

    @Override
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {

    }

    /**
     * If mineFieldTask is null or mineFieldTask is done, return null;
     */
    @Nullable
    public MineFieldSubTask nextTask(){
        var temp = mineFieldTask.nextTaskArea();
        if(temp==null){
            notifyUpdate();
            return null;
        }
        return temp.toSubTask(mineFieldTask);
    }

    public boolean consumeToolkit(){
        for(int i=0;i<inv.getContainerSize();i++){
            if(!inv.getItem(i).isEmpty()){
                inv.setItem(i,ItemStack.EMPTY);
                notifyUpdate();
                return true;
            }
        }
        return false;
    }

    @Nullable
    public MineFieldTask setupMineField(@Nullable MineFieldTask mineFieldTask){
        var ret =  this.mineFieldTask;
        this.mineFieldTask = mineFieldTask;
        notifyUpdate();
        return ret;
    }

    public void returnTask(MineFieldSubTask task){
        mineFieldTask.returnTaskArea(task.getCachedArea());
        notifyUpdate();
    }

    @Override
    public void write(CompoundTag compoundTag, boolean clientPacket) {
        super.write(compoundTag, clientPacket);
        if(mineFieldTask!=null)
            compoundTag.put("mine_field", mineFieldTask.serializeNBT());
        compoundTag.put("inventory", inv.serializeNBT());

    }

    @Override
    protected void read(CompoundTag compoundTag, boolean clientPacket) {
        super.read(compoundTag, clientPacket);
        if(compoundTag.contains("mine_field"))
            mineFieldTask = MineFieldTask.fromNBT((CompoundTag) compoundTag.get("mine_field"));
        inv.deserializeNBT((CompoundTag) compoundTag.get("inventory"));
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        handler.invalidate();
    }

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @org.jetbrains.annotations.Nullable Direction side) {
        if (isItemHandlerCap(capability))
            return handler.cast();
        return super.getCapability(capability, side);
    }


    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        // TODO show mine field config and running status
        if(mineFieldTask!=null){
            tooltip.add(MiningIndustry.LANG.text(mineFieldTask.toString()).component());
            return true;
        } else
            return false;
    }

    public static class CommandCenterInventory extends SmartInventory{
        public CommandCenterInventory(int slots, SyncedTileEntity te) {
            super(slots, te);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if(!stack.is(CmiItems.BLAZE_MINER_TOOLKIT.get())) return stack;
            else return super.insertItem(slot,stack,simulate);
        }


        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if(!stack.is(CmiItems.BLAZE_MINER_TOOLKIT.get())) return false;
            else return super.isItemValid(slot,stack);
        }
    }
}
