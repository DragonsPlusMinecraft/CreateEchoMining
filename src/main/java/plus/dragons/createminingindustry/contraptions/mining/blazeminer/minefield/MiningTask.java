package plus.dragons.createminingindustry.contraptions.mining.blazeminer.minefield;

import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.INBTSerializable;

public class MiningTask implements INBTSerializable<CompoundTag> {

    AABB dutyAABB;
    MineFieldInfo.MineTaskArea cachedArea;
    BlockPos.MutableBlockPos targetPos;
    Boolean done;

    public MiningTask(AABB dutyAABB, MineFieldInfo.MineTaskArea cachedArea, BlockPos.MutableBlockPos targetPos) {
        this.cachedArea = cachedArea;
        this.targetPos = targetPos;
        this.dutyAABB = dutyAABB;
        this.done = false;
    }

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

    @Override
    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        tag.put("duty",NBTHelper.writeAABB(dutyAABB));
        tag.put("target",NbtUtils.writeBlockPos(targetPos));
        tag.put("cached_area",cachedArea.serializeNBT());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.dutyAABB = NBTHelper.readAABB((ListTag) nbt.get("duty"));
        var pos = NbtUtils.readBlockPos((CompoundTag) nbt.get("target"));
        this.targetPos = new BlockPos.MutableBlockPos(pos.getX(),pos.getY(),pos.getZ());
        this.cachedArea = MineFieldInfo.MineTaskArea.deserializeNBT((CompoundTag) nbt.get("cached_area"));
    }

    public MineFieldInfo.MineTaskArea getCachedArea() {
        return cachedArea;
    }

    public BlockPos getTargetPos() {
        return targetPos.immutable();
    }
}
