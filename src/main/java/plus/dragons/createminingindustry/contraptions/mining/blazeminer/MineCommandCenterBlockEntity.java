package plus.dragons.createminingindustry.contraptions.mining.blazeminer;

import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.minefield.MineFieldInfo;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.minefield.MiningTask;

import java.util.List;

public class MineCommandCenterBlockEntity extends SmartTileEntity implements IHaveGoggleInformation {

    MineFieldInfo mineFieldInfo;
    public MineCommandCenterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        mineFieldInfo = new MineFieldInfo(pos,48,48);
    }

    @Override
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {

    }

    public MiningTask nextTask(){
        var temp = mineFieldInfo.nextTaskArea();
        return new MiningTask(temp.toAABB(mineFieldInfo),temp,temp.startPoint(mineFieldInfo));
    }

    public void returnTask(MiningTask task){
        mineFieldInfo.returnTaskArea(task.getCachedArea());
    }

    @Override
    public void write(CompoundTag compoundTag, boolean clientPacket) {
        super.write(compoundTag, clientPacket);
        compoundTag.put("mine_field",mineFieldInfo.serializeNBT());
        // TODO
    }

    @Override
    protected void read(CompoundTag compoundTag, boolean clientPacket) {
        super.read(compoundTag, clientPacket);
        mineFieldInfo.deserializeNBT((CompoundTag) compoundTag.get("mine_field"));
        // TODO
    }
}
