package plus.dragons.createminingindustry.contraptions.mining.blazeminer;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

// 16 x 16 block is area of a single mineTask, and height is 16;
// max fleid area 1s 5 x 5 unit;
public class MineFieldTask {
    static final int MAX_HEIGHT = 319, VERTICAL_SLICE = 24;
    public int x,z,xWidth, zWidth;
    SubTaskArea distributed = new SubTaskArea(0,0,0);
    Set<SubTaskArea> extraAvailable = new HashSet<>();
    boolean done = false;

    public MineFieldTask(int x, int z, int xWidth, int zWidth) {
        this.x = x;
        this.z = z;
        this.xWidth = xWidth;
        this.zWidth = zWidth;
    }

    private MineFieldTask() {}

    @Nullable
    public SubTaskArea nextTaskArea(){
        if(done) return null;
        if(!extraAvailable.isEmpty()){
            var ret = extraAvailable.iterator().next();
            extraAvailable.remove(ret);
            return ret;
        }
        if((distributed.x+1)*16>=xWidth){
            if((distributed.z+1)*16>=zWidth){
                if(distributed.y==VERTICAL_SLICE-1){
                    done = true;
                    return null;
                }
                else distributed = new SubTaskArea(0, distributed.y+1,0);
            }
            else distributed = new SubTaskArea(0, distributed.y, distributed.z+1);
        }
        else distributed = new SubTaskArea(distributed.x+1, distributed.y, distributed.z);
        return distributed;
    }

    public boolean done(){
        return done;
    }

    public void returnTaskArea(SubTaskArea subTaskArea){
        extraAvailable.add(subTaskArea);
        done = false;
    }

    public CompoundTag serializeNBT() {
        var ret = new CompoundTag();
        ret.putInt("x",x);
        ret.putInt("z",z);
        ret.putInt("x_width",xWidth);
        ret.putInt("z_width",zWidth);
        ret.put("distributed", distributed.serializeNBT());
        if(!extraAvailable.isEmpty()){
            ret.putByte("has_extra", (byte) 1);
            ret.putInt("extra_size", extraAvailable.size());
            var list = new ListTag();
            for(var task:extraAvailable){
                list.add(task.serializeNBT());
            }
            ret.put("extra_list",list);
        }
        return ret;
    }

    public static MineFieldTask fromNBT(CompoundTag nbt) {
        var ret = new MineFieldTask();
        ret.done = nbt.getBoolean("done");
        ret.x = nbt.getInt("x");
        ret.z = nbt.getInt("z");
        ret.xWidth = nbt.getInt("x_width");
        ret.zWidth = nbt.getInt("z_width");
        ret.distributed = SubTaskArea.deserializeNBT((CompoundTag) nbt.get("distributed"));
        if(nbt.contains("has_extra")){
            var size = nbt.getInt("extra_size");
            ListTag list = (ListTag) nbt.get("extra_list");
            for(int i=0;i<size;i++){
                ret.extraAvailable.add(SubTaskArea.deserializeNBT((CompoundTag) list.get(i)));
            }
        }
        return ret;
    }

    @Override
    public String toString() {
        return "MineFieldTask{" +
                "x=" + x +
                ", z=" + z +
                ", xWidth=" + xWidth +
                ", zWidth=" + zWidth +
                ", done=" + done +
                '}';
    }

    public record SubTaskArea(int x, int y, int z){

        public MineFieldSubTask toSubTask(MineFieldTask inField){
            return new MineFieldSubTask(this.toAABB(inField),this,this.startPoint(inField));
        }

        private BlockPos.MutableBlockPos startPoint(MineFieldTask inField){
            var x1 = inField.x + x * 16;
            var z1 = inField.z + z * 16;
            var y1 = MAX_HEIGHT -  y * 16;
            return new BlockPos.MutableBlockPos(x1,y1,z1);
        }

        // order -> x, z, y
        private AABB toAABB(MineFieldTask inField){
            var x1 = inField.x + x * 16;
            var z1 = inField.z + z * 16;
            var x2 = (x + 1) * 16 > inField.xWidth? inField.x + inField.xWidth - 1: x1 + 15;
            var z2 = (z + 1) * 16 > inField.zWidth? inField.z + inField.zWidth - 1: z1 + 15;
            var y1 = MAX_HEIGHT -  y * 16;
            var y2 = y1 - 15;
            return new AABB(x1,y1,z1,x2,y2,z2);
        }


        public CompoundTag serializeNBT() {
            var ret = new CompoundTag();
            ret.putInt("x",x);
            ret.putInt("y",y);
            ret.putInt("z",z);
            return ret;
        }

        public static SubTaskArea deserializeNBT(CompoundTag nbt) {
            var x = nbt.getInt("x");
            var y = nbt.getInt("x");
            var z = nbt.getInt("x");
            return new SubTaskArea(x,y,z);
        }
    }
}
