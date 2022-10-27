package plus.dragons.createminingindustry.contraptions.mining.blazeminer;

import com.google.common.collect.Lists;
import com.simibubi.create.content.contraptions.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.product.BlazeFluidHolderItem;
import plus.dragons.createminingindustry.entry.CmiTags;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class BlazeMinerStationBlockEntity extends SmartTileEntity implements IHaveGoggleInformation {

    static ItemStack SILK_TOUCH_TOOL;

    static {
        SILK_TOUCH_TOOL = Items.NETHERITE_PICKAXE.getDefaultInstance();
        Map<Enchantment,Integer> enchantment = new HashMap<>();
        enchantment.put(Enchantments.SILK_TOUCH,1);
        EnchantmentHelper.setEnchantments(enchantment,SILK_TOUCH_TOOL);
    }
    MineFieldSubTask mineFieldSubTask;
    BlazeMinerInventory blazeInv;
    SmartInventory stationInv; // 27
    @Nullable
    BlockPos commandCenterPos;
    Phase phase;
    LazyOptional<IItemHandlerModifiable> handler = LazyOptional.of(() -> stationInv);
    int itemCollected;
    int idleTime;

    public BlazeMinerStationBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        blazeInv = new BlazeMinerInventory();
        stationInv = new SmartInventory(27,this);
        phase = Phase.SEARCH_COMMAND_CENTER;
        itemCollected = 0;
        mineFieldSubTask = null;
        commandCenterPos = null;
        idleTime = 0;
    }

    @Override
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {

    }

    @Override
    public void tick() {
        super.tick();
        if(idleTime>0){
            idleTime--;
            return;
        }
        if(phase == Phase.SEARCH_COMMAND_CENTER){
            if(!level.isClientSide()){
                searchCommandCenter();
            }
        }
        else if(phase == Phase.REQUEST_JOB){
            if(!level.isClientSide()){
                requestJob();
            }
        }
        else if(phase == Phase.REQUEST_TASK){
            if(!level.isClientSide()){
                requestTask();
            }
        }
        else if(phase == Phase.SEARCH_MINEABLE){
            if(!level.isClientSide()){
                searchMineable();
            }
        }
        else if(phase == Phase.BLINK_TO_MINEABLE){
            if(!level.isClientSide()){
                blinkToMineable();
            }
        }
        else if(phase == Phase.MINE){
            if(!level.isClientSide()){
                mine();
            }
        }
        else if(phase == Phase.BLINK_TO_STATION){
            if(!level.isClientSide()){
                blinkToStation();
            }
        }
        else if(phase == Phase.TRANSFER_ITEM){
            if(!level.isClientSide()){
                transferItem();
            }
        }
    }


    private void searchCommandCenter() {
        if(!level.isClientSide()){
            for(int i=-4;i<=4;i++){
                for(int j=-4;j<=4;j++){
                    for(int k=-4;k<=4;k++){
                        var pos = getBlockPos().offset(i,j,k);
                        if(level.getBlockEntity(pos) instanceof MineCommandCenterBlockEntity){
                            if(commandCenterPos!=null){
                                if(getBlockPos().distToLowCornerSqr(pos.getX(),pos.getY(),pos.getZ()) <
                                        getBlockPos().distToLowCornerSqr(commandCenterPos.getX(),commandCenterPos.getY(),commandCenterPos.getZ()))
                                    commandCenterPos = pos;
                            } else {
                                commandCenterPos = pos;
                            }
                        }
                    }
                }
            }
            if(commandCenterPos==null) {
                idleTime = 200;
            } else {
                setPhase(Phase.REQUEST_JOB);
            }
        }
    }


    private void requestJob() {
        if(!level.isClientSide()){
            var blockEntity = level.getBlockEntity(commandCenterPos);
            if(blockEntity instanceof MineCommandCenterBlockEntity mineCommandCenterBlockEntity){
                if(mineCommandCenterBlockEntity.mineFieldTask.done()){
                    idleTime = 200;
                    notifyUpdate();
                } else {
                    var result = mineCommandCenterBlockEntity.consumeToolkit();
                    if(result){
                        setPhase(Phase.REQUEST_TASK);
                    } else {
                        idleTime = 200;
                        notifyUpdate();
                    }
                }
            } else {
                commandCenterPos = null;
                setPhase(Phase.SEARCH_COMMAND_CENTER);
            }
        }
    }

    private void requestTask() {
        if(!level.isClientSide()){
            var blockEntity = level.getBlockEntity(commandCenterPos);
            if(blockEntity instanceof MineCommandCenterBlockEntity mineCommandCenterBlockEntity){
                if(mineCommandCenterBlockEntity.mineFieldTask.done()){
                    idleTime = 200;
                    notifyUpdate();
                } else {
                    mineFieldSubTask = mineCommandCenterBlockEntity.nextTask();
                    System.out.println("Obtain Sub Task:" + mineFieldSubTask);
                    if(mineFieldSubTask==null){
                        idleTime = 200;
                        notifyUpdate();
                    } else {
                        setPhase(Phase.SEARCH_MINEABLE);
                    }
                }
            } else {
                commandCenterPos = null;
                setPhase(Phase.SEARCH_COMMAND_CENTER);
            }
        }
    }


    private void searchMineable() {
        if(!level.isClientSide()){
            // Temp: Remove high area for test
            if(mineFieldSubTask.getTargetPos().getY()>100){
                setPhase(Phase.REQUEST_TASK);
                return;
            }
            var i = 0;
            while(!validMineLocation(mineFieldSubTask.getTargetPos()) && !mineFieldSubTask.done() && i<16){
                mineFieldSubTask.nextPos();
                i++;
            }
            if(mineFieldSubTask.done()){
                mineFieldSubTask = null;
                setPhase(Phase.REQUEST_TASK);
                return;
            }
            if(i==16) return;
            setPhase(Phase.BLINK_TO_MINEABLE);
        }
    }

    private boolean validMineLocation(BlockPos pos){
        var bs = level.getBlockState(pos);
        return !bs.isAir() && !bs.is(CmiTags.CmiBlockTags.BLAZE_IGNORE.tag());
    }

    private void blinkToMineable() {
        if(!level.isClientSide()){
            setPhase(Phase.MINE);
        }
    }

    private void mine() {
        if(!level.isClientSide()){
            var pos = mineFieldSubTask.getTargetPos();

            // Mine Block
            var blockState = level.getBlockState(pos);
            if(blockState.is(CmiTags.CmiBlockTags.BLAZE_RESOURCE_PACKAGE.tag())){
                // Generate Blaze Resource Package
                // TODO
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            } else if(blockState.is(CmiTags.CmiBlockTags.BLAZE_SILK_TOUCH.tag())){
                BlockHelper.destroyBlockAs(level, pos, null,SILK_TOUCH_TOOL,1f, (stack) -> {
                    if (stack.isEmpty())
                        return;
                    if (!level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS))
                        return;
                    if (level.restoringBlockSnapshots)
                        return;
                    itemCollected+=stack.getCount();
                    addToBlazeBackpack(stack);
                });
            }  else if(blockState.is(CmiTags.CmiBlockTags.BLAZE_BURN.tag())){
                // Burn the Block
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            } else if(!blockState.is(CmiTags.CmiBlockTags.BLAZE_IGNORE.tag())){
                BlockHelper.destroyBlock(level, pos, 1f, (stack) -> {
                    if (stack.isEmpty())
                        return;
                    if (!level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS))
                        return;
                    if (level.restoringBlockSnapshots)
                        return;
                    itemCollected+=stack.getCount();
                    addToBlazeBackpack(stack);
                });
            }

            // Mine Liquid
            var fluidState = level.getFluidState(pos);
            if(!fluidState.isEmpty()){
                if(fluidState.isSource() && fluidState.is(CmiTags.CmiFluidTags.BLAZE_COLLECTABLE.tag())){
                    // Pack Fluid
                    level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    addToBlazeBackpack(BlazeFluidHolderItem.ofFluid(fluidState.getType()));
                    itemCollected+=1;
                } else {
                    // absorb fluid
                    removeNearbyLiquid(level,pos);
                }
            }

            if(!blazeInv.backupInv.isEmpty()){
                setPhase(Phase.BLINK_TO_STATION);
                return;
            }
            setPhase(Phase.SEARCH_MINEABLE);
        }
    }

    private void blinkToStation() {
        if(!level.isClientSide()){
            setPhase(Phase.TRANSFER_ITEM);
        }
    }


    private void transferItem() {
        if(!level.isClientSide()){
            var allItems = blazeInv.removeAllItems();
            allItems.addAll(blazeInv.backupInv.removeAllItems());
            for(var item: allItems){
                // TODO should saving into station inv
                // Temporary solution for test only
                ItemEntity itementity = new ItemEntity(level, getBlockPos().getX(), getBlockPos().getY()+1, getBlockPos().getZ(), item);
                itementity.setDeltaMovement(0.5,0.5,0.5);
                level.addFreshEntity(itementity);
            }
            if(itemCollected>3456){
                if (!mineFieldSubTask.done()){
                    var blockEntity = level.getBlockEntity(commandCenterPos);
                    if(blockEntity instanceof MineCommandCenterBlockEntity mineCommandCenterBlockEntity){
                        mineCommandCenterBlockEntity.returnTask(mineFieldSubTask);
                        mineFieldSubTask = null;
                        setPhase(Phase.REQUEST_JOB);
                    } else {
                        // if cannot find commandCenter, just give up return sub task
                        setPhase(Phase.SEARCH_COMMAND_CENTER);
                    }
                }
                itemCollected = 0;
                return;
            }
            setPhase(Phase.SEARCH_MINEABLE);
        }
    }

    private void setPhase(Phase phase){
        this.phase = phase;
        notifyUpdate();
    }

    private void addToBlazeBackpack(ItemStack itemStack){
        var left = blazeInv.addItem(itemStack);
        if(!left.isEmpty())
            blazeInv.backupInv.addItem(left);
    }

    private void removeNearbyLiquid(Level pLevel, BlockPos pPos) {
        Queue<BlockPos> queue = Lists.newLinkedList();
        queue.add(pPos);
        int i = 0;

        while(!queue.isEmpty()) {
            BlockPos blockpos = queue.poll();

            for(Direction direction : Direction.values()) {
                BlockPos blockpos1 = blockpos.relative(direction);
                FluidState fluidstate = pLevel.getFluidState(blockpos1);
                if (!fluidstate.isSource() || !fluidstate.is(CmiTags.CmiFluidTags.BLAZE_COLLECTABLE.tag())) {
                    pLevel.setBlockAndUpdate(blockpos1, Blocks.AIR.defaultBlockState());
                    i++;
                    queue.add(blockpos1);
                }
            }

            if (i > 64) {
                break;
            }
        }
    }

    @Override
    public void write(CompoundTag compoundTag, boolean clientPacket) {
        // TODO
        super.write(compoundTag, clientPacket);
        if(mineFieldSubTask !=null)
            compoundTag.put("mining_task", mineFieldSubTask.serializeNBT());
        compoundTag.put("blaze_inventory", blazeInv.createTag());
        compoundTag.put("station_inventory", stationInv.serializeNBT());
        if(commandCenterPos!=null)
            compoundTag.put("center_pos", NbtUtils.writeBlockPos(commandCenterPos));
        compoundTag.putInt("collected", itemCollected);
        NBTHelper.writeEnum(compoundTag,"phase",phase);
        compoundTag.putInt("idle_time",idleTime);
    }

    @Override
    protected void read(CompoundTag compoundTag, boolean clientPacket) {
        // TODO
        super.read(compoundTag, clientPacket);
        if(compoundTag.contains("mining_task"))
            mineFieldSubTask = MineFieldSubTask.fromNBT((CompoundTag) compoundTag.get("mining_task"));
        blazeInv.fromTag((ListTag) compoundTag.get("blaze_inventory"));
        stationInv.deserializeNBT((CompoundTag) compoundTag.get("station_inventory"));
        if(compoundTag.contains("center_pos"))
            commandCenterPos = NbtUtils.readBlockPos((CompoundTag) compoundTag.get("center_pos"));
        itemCollected = compoundTag.getInt("collected");
        phase = NBTHelper.readEnum(compoundTag,"phase",Phase.class);
        idleTime = compoundTag.getInt("idle_time");
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
        SEARCH_COMMAND_CENTER,
        REQUEST_JOB,
        REQUEST_TASK,
        SEARCH_MINEABLE,
        BLINK_TO_MINEABLE,
        MINE,
        BLINK_TO_STATION,
        TRANSFER_ITEM
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
