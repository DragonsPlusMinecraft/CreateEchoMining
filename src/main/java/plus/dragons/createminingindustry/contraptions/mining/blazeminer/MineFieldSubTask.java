package plus.dragons.createminingindustry.contraptions.mining.blazeminer;

import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.phys.AABB;

public class MineFieldSubTask {

    AABB dutyAABB;
    MineFieldTask.SubTaskArea cachedArea;
    BlockPos.MutableBlockPos targetPos;
    Boolean done;

    public MineFieldSubTask(AABB dutyAABB, MineFieldTask.SubTaskArea cachedArea, BlockPos.MutableBlockPos targetPos) {
        this.cachedArea = cachedArea;
        this.targetPos = targetPos;
        this.dutyAABB = dutyAABB;
        this.done = false;
    }

    private MineFieldSubTask() {}

    public void nextPos(){
        if(targetPos.getX() >= dutyAABB.maxX){
            if(targetPos.getZ() >= dutyAABB.maxZ){
                if(targetPos.getY() <= dutyAABB.minY){
                    done = true;
                }
                else {
                    targetPos.move(0,-1,0);
                    targetPos.setX((int) dutyAABB.minX);
                    targetPos.setZ((int) dutyAABB.minZ);
                }
            }
            else {
                targetPos.move(0,0,1);
                targetPos.setX((int) dutyAABB.minX);
            }
        }
        else {
            targetPos.move(1,0,0);
        }
    }

    public boolean done(){
        return done;
    }

    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        tag.put("duty",NBTHelper.writeAABB(dutyAABB));
        tag.put("target",NbtUtils.writeBlockPos(targetPos));
        tag.put("cached_area",cachedArea.serializeNBT());
        return tag;
    }

    public static MineFieldSubTask fromNBT(CompoundTag nbt) {
        var ret = new MineFieldSubTask();
        ret.dutyAABB = NBTHelper.readAABB((ListTag) nbt.get("duty"));
        var pos = NbtUtils.readBlockPos((CompoundTag) nbt.get("target"));
        ret.targetPos = new BlockPos.MutableBlockPos(pos.getX(),pos.getY(),pos.getZ());
        ret.cachedArea = MineFieldTask.SubTaskArea.deserializeNBT((CompoundTag) nbt.get("cached_area"));
        return ret;
    }

    public MineFieldTask.SubTaskArea getCachedArea() {
        return cachedArea;
    }

    public BlockPos getTargetPos() {
        return targetPos.immutable();
    }

    @Override
    public String toString() {
        return "MineFieldSubTask{" +
                "dutyAABB=" + dutyAABB +
                ", cachedArea=" + cachedArea +
                '}';
    }
}
