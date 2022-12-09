package plus.dragons.createminingindustry.contraptions.mining.blazeminer;

import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class MineCommandPostBlockEntity extends SmartTileEntity implements IHaveGoggleInformation {

    private boolean hasCommander = false;
    private

    public MineCommandPostBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {

    }

    public boolean validPost(){

    }

    /**
     * @return null if no lunchBox can be obtained.
     */
    public ItemStack obtainLunchbox(){

    }

    /**
     * Insert item into inventory of any storage below.
     * @return remaining itemStack.
     */
    public ItemStack submitItem(ItemStack itemStack){

    }

    /**
     * @return null if no task can be allocated this moment.
     */
    public BlockPos obtainTask(){

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
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        // TODO show mine field config and running status
        /*if(mineFieldTask!=null){
            tooltip.add(MiningIndustry.LANG.text(mineFieldTask.toString()).component());
            return true;
        } else
            return false;*/
    }

    public static class MineField{
        private int xs, zs, xe, ze, y;
        private BlockPos current;
        private boolean done;

        public MineField(int xs, int zs, int xe, int ze, int y) {
            this.xs = xs;
            this.zs = zs;
            this.xe = xe;
            this.ze = ze;
            this.y = y;
            this.current = new BlockPos(xs,y,zs);
            this.done = false;
        }

        private MineField(int xs, int zs, int xe, int ze, int y, BlockPos current, boolean done) {
            this.xs = xs;
            this.zs = zs;
            this.xe = xe;
            this.ze = ze;
            this.y = y;
            this.current = current;
            this.done = done;
        }

        public BlockPos nextPos(){
            var ret = current;
            if(current.getX()==xe){
                if(current.getZ()==ze){
                    if(current.getY()==-64) done = true;
                    else current = new BlockPos(xs,current.getY()-1,zs);
                } else {
                    current = new BlockPos(xs,current.getY(),current.getZ()+1);
                }
            } else {
                current = current.offset(1,0,0);
            }
            return current;
        }

        public boolean isDone() { return done; }

        public static MineField fromNBT(CompoundTag compoundTag){
            return new MineField(compoundTag.getInt("xs"), compoundTag.getInt("zs"),
                    compoundTag.getInt("xe"), compoundTag.getInt("ze"), compoundTag.getInt("y"),
                    NbtUtils.readBlockPos(compoundTag), compoundTag.getBoolean("done"));
        }

        public CompoundTag toNBT(){
            var ret = new CompoundTag();
            ret.putInt("xs",xs);
            ret.putInt("zs",zs);
            ret.putInt("xe",xe);
            ret.putInt("ze",ze);
            ret.putInt("y",y);
            NbtUtils.writeBlockPos(current);
            ret.putBoolean("done",done);
            return ret;
        }
    };
}
