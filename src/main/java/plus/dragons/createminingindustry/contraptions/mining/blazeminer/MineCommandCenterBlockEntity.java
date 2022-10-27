package plus.dragons.createminingindustry.contraptions.mining.blazeminer;

import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import plus.dragons.createminingindustry.foundation.utility.CmiLang;

import javax.annotation.Nullable;
import java.util.List;

public class MineCommandCenterBlockEntity extends SmartTileEntity implements IHaveGoggleInformation {

    MineFieldTask mineFieldTask = null;
    public MineCommandCenterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
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
        //TODO
        notifyUpdate();
        return true;
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
    }

    @Override
    protected void read(CompoundTag compoundTag, boolean clientPacket) {
        super.read(compoundTag, clientPacket);
        var mineField = compoundTag.get("mine_field");
        if(mineField!=null){
            mineFieldTask = MineFieldTask.fromNBT((CompoundTag) mineField);
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        // TODO show mine field config and running status
        if(mineFieldTask!=null){
            tooltip.add(CmiLang.text(mineFieldTask.toString()).component());
            return true;
        } else
            return false;
    }
}
