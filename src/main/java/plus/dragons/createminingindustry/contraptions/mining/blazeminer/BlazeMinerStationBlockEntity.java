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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.SimpleContainer;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.product.BlazeFluidHolderItem;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.product.BlazeResourcePackageItem;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.product.ResourcePackageGeneration;
import plus.dragons.createminingindustry.entry.CmiTags;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class BlazeMinerStationBlockEntity extends SmartTileEntity implements IHaveGoggleInformation {

    static ItemStack SILK_TOUCH_TOOL;
    static Direction[] DIRECTIONS;

    static {
        SILK_TOUCH_TOOL = Items.NETHERITE_PICKAXE.getDefaultInstance();
        Map<Enchantment,Integer> enchantment = new HashMap<>();
        enchantment.put(Enchantments.SILK_TOUCH,1);
        EnchantmentHelper.setEnchantments(enchantment,SILK_TOUCH_TOOL);
        DIRECTIONS = new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH};
    }
    MineFieldSubTask mineFieldSubTask;
    BlazeMinerInventory blazeInv;
    SmartInventory stationInv; // 27
    @Nullable
    BlockPos commandCenterPos;
    BlockPos blazePos;
    @Nullable
    BlockPos blazeNextPos;
    double travelingPercentage;
    boolean blinkTraveling;
    Phase phase;
    BlockAction blockAction;
    FluidAction fluidAction;
    LazyOptional<IItemHandlerModifiable> handler = LazyOptional.of(() -> stationInv);
    int itemCollected;
    int idleTime;
    int progressTime;

    public BlazeMinerStationBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        blazeInv = new BlazeMinerInventory();
        stationInv = new SmartInventory(27,this).forbidInsertion();
        phase = Phase.SEARCH_COMMAND_CENTER;
        blockAction = BlockAction.NONE;
        fluidAction = FluidAction.NONE;
        itemCollected = 0;
        mineFieldSubTask = null;
        commandCenterPos = null;
        blazePos = pos;
        travelingPercentage = 0;
        blinkTraveling = false;
        idleTime = 0;
        progressTime = 0;
    }

    @Override
    public void addBehaviours(List<TileEntityBehaviour> behaviours) {

    }

    @Override
    public void tick() {
        super.tick();

        if(isVirtual())
            return;

        if(idleTime>0){
            idleTime--;
            return;
        }
        if(phase == Phase.SEARCH_COMMAND_CENTER){
            searchCommandCenter();
        }
        else if(phase == Phase.REQUEST_JOB){
            requestJob();
        }
        else if(phase == Phase.REQUEST_TASK){
            requestTask();
        }
        else if(phase == Phase.SEARCH_MINEABLE){
            searchMineable();
        }
        else if(phase == Phase.TRAVEL_TO_MINEABLE){
            travelToMineable();
        }
        else if(phase == Phase.MINE){
            mine();
        }
        else if(phase == Phase.TRAVEL_TO_STATION){
            travelToStation();
        }
        else if(phase == Phase.TRANSFER_ITEM){
            transferItem();
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
                if(mineCommandCenterBlockEntity.mineFieldTask == null || mineCommandCenterBlockEntity.mineFieldTask.done()){
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
                if(mineCommandCenterBlockEntity.mineFieldTask == null ||mineCommandCenterBlockEntity.mineFieldTask.done()){
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
            while(!validBlock(mineFieldSubTask.getTargetPos()) && !mineFieldSubTask.done() && i<16){
                mineFieldSubTask.nextPos();
                i++;
            }
            if(mineFieldSubTask.done()){
                mineFieldSubTask = null;
                setPhase(Phase.REQUEST_TASK);
                return;
            }
            if(i==16) return;
            setPhase(Phase.TRAVEL_TO_MINEABLE);
        }
    }

    private void travelToMineable() {
        // Determine travel destination & method in server and send it to client for animation.
        // Movement calculation is running on both server & client.
        // So must add some extra time for network delay.
        if(!level.isClientSide()){
            setPhase(Phase.MINE);
        }
    }

    private void mine() {
        if(!level.isClientSide()){
            if(progressTime==0){
                if(blockAction == BlockAction.NONE && fluidAction==FluidAction.NONE){
                    decideBlockMineActionType();
                    if(blockAction == BlockAction.NONE){
                        decideFluidMineActionType();
                    }
                }
                else if(fluidAction!=FluidAction.NONE){
                    finishMineFluid();
                    return;
                } else {
                    finishMineBlock();
                    return;
                }
            }
            progressTime --;
        }
    }

    private void decideBlockMineActionType(){
        var pos = mineFieldSubTask.getTargetPos();
        var blockState = level.getBlockState(pos);
        if(blockState.is(CmiTags.CmiBlockTags.BLAZE_RESOURCE_PACKAGE.tag())){
            blockAction = BlockAction.EXTRACT_RESOURCE;
        } else if(blockState.is(CmiTags.CmiBlockTags.BLAZE_SILK_TOUCH.tag())){
            blockAction = BlockAction.SILK_TOUCH;
        } else if(blockState.is(CmiTags.CmiBlockTags.BLAZE_BURN.tag())){
            blockAction = BlockAction.BURNOUT;
        } else if(!blockState.is(CmiTags.CmiBlockTags.BLAZE_IGNORE.tag()) && !blockState.getMaterial().isLiquid()){
            blockAction = BlockAction.BREAK;
        }
        progressTime = blockAction.tick;
    }

    private void decideFluidMineActionType(){
        var pos = mineFieldSubTask.getTargetPos();
        if(validFluid(pos)){
            if(validCollectibleFluid(pos)){
                fluidAction = FluidAction.EXTRACT_FLUID;
            } else fluidAction = FluidAction.DRY;
        }
        progressTime = blockAction.tick;
    }


    private void finishMineBlock(){
        var pos = mineFieldSubTask.getTargetPos();

        // Mine Block
        if(blockAction==BlockAction.EXTRACT_RESOURCE){
            var packages = ResourcePackageGeneration.getPackages((ServerLevel) level,pos,this.level.getRandom());
            for(var entry:packages.entrySet()){
                addToBlazeBackpack(BlazeResourcePackageItem.ofSeed(entry.getKey(),entry.getValue()));
            }
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        } else if(blockAction==BlockAction.SILK_TOUCH){
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
        }  else if(blockAction==BlockAction.BURNOUT){
            // TODO Burn the Block
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        } else if(blockAction==BlockAction.BREAK){
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
        blockAction = BlockAction.NONE;
        // Detect Fluid and set status
        decideFluidMineActionType();
        if(fluidAction!=FluidAction.NONE){
            notifyUpdate();
        } else {
            afterMine();
        }
    }

    private void finishMineFluid(){
        var pos = mineFieldSubTask.getTargetPos();

        var fluidState = level.getFluidState(pos);
        if(fluidAction==FluidAction.EXTRACT_FLUID){
            // Pack Fluid
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            addToBlazeBackpack(BlazeFluidHolderItem.ofFluid(fluidState.getType()));
            itemCollected+=1;
        } else {
            // absorb fluid
            removeNearbyLiquid(level,pos);
        }
        fluidAction = FluidAction.NONE;
        afterMine();
    }

    private void afterMine(){
        if(!blazeInv.backupInv.isEmpty()){
            setPhase(Phase.TRAVEL_TO_STATION);
            return;
        }
        mineFieldSubTask.nextPos();
        if(mineFieldSubTask.done()){
            mineFieldSubTask = null;
            setPhase(Phase.TRAVEL_TO_STATION);
            return;
        }
        setPhase(Phase.SEARCH_MINEABLE);
    }

    private boolean validBlock(BlockPos pos){
        var bs = level.getBlockState(pos);
        return !bs.isAir() && !bs.is(CmiTags.CmiBlockTags.BLAZE_IGNORE.tag());
    }

    // Different to validBlock.
    // This method returns turn only when fluid is valid of being collected.
    private boolean validFluid(BlockPos pos){
        var fs = level.getFluidState(pos);
        return !fs.isEmpty();
    }

    private boolean validCollectibleFluid(BlockPos pos){
        var fs = level.getFluidState(pos);
        return fs.isSource() && fs.is(CmiTags.CmiFluidTags.BLAZE_COLLECTIBLE.tag());
    }

    private void travelToStation() {
        // Determine travel destination & method in server and send it to client for animation.
        // Movement calculation is running on both server & client.
        // So must add some extra time for network delay.
        if(!level.isClientSide()){
            setPhase(Phase.TRANSFER_ITEM);
        }
    }


    private void transferItem() {
        if(!level.isClientSide()){
            for(int i=0;i<blazeInv.backupInv.getContainerSize();i++){
                var item = blazeInv.backupInv.getItem(i);
                if(!item.isEmpty()){
                    item = addToStationInventory(item);
                    blazeInv.backupInv.setItem(i,item);
                    if(!item.isEmpty()){
                        idleTime = 200;
                    }
                    notifyUpdate();
                    return;
                }
            }
            for(int i=0;i<blazeInv.getContainerSize();i++){
                var item = blazeInv.getItem(i);
                if(!item.isEmpty()){
                    item = addToStationInventory(item);
                    blazeInv.setItem(i,item);
                    if(!item.isEmpty()){
                        idleTime = 200;
                    }
                    notifyUpdate();
                    return;
                }
            }
            if(mineFieldSubTask==null){
                if(itemCollected>3456){
                    itemCollected = 0;
                    setPhase(Phase.REQUEST_JOB);
                } else {
                    setPhase(Phase.REQUEST_TASK);
                }
                return;
            }
            if(itemCollected>3456){
                itemCollected = 0;
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

    private ItemStack addToStationInventory(ItemStack itemStack){
        stationInv.allowInsertion();
        for(int i=0;i<stationInv.getContainerSize();i++){
            itemStack = stationInv.insertItem(i,itemStack,false);
            if(itemStack.isEmpty()) break;
        }
        stationInv.forbidInsertion();
        notifyUpdate();
        return itemStack;
    }

    private void removeNearbyLiquid(Level pLevel, BlockPos pPos) {
        Queue<BlockPos> queue = Lists.newLinkedList();
        queue.add(pPos);
        int i = 0;

        while(!queue.isEmpty()) {
            BlockPos blockpos = queue.poll();

            for(Direction direction : DIRECTIONS) {
                BlockPos blockpos1 = blockpos.relative(direction);
                if (validFluid(blockpos1) && !validCollectibleFluid(blockpos1)) {
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
        super.write(compoundTag, clientPacket);
        compoundTag.put("station_inventory", stationInv.serializeNBT());
        compoundTag.put("blaze_pos", NbtUtils.writeBlockPos(blazePos));
        if(blazeNextPos !=null)
            compoundTag.put("blaze_next_pos", NbtUtils.writeBlockPos(blazeNextPos));
        NBTHelper.writeEnum(compoundTag,"phase",phase);
        NBTHelper.writeEnum(compoundTag,"block_action", blockAction);
        NBTHelper.writeEnum(compoundTag,"fluid_action", fluidAction);
        compoundTag.putInt("idle_time",idleTime);
        compoundTag.putInt("progress_time",progressTime);
        compoundTag.putBoolean("blink",blinkTraveling);
        if(!clientPacket){
            if(mineFieldSubTask !=null)
                compoundTag.put("mining_task", mineFieldSubTask.serializeNBT());
            compoundTag.put("blaze_inventory", blazeInv.createTag());
            if(commandCenterPos !=null)
                compoundTag.put("center_pos", NbtUtils.writeBlockPos(commandCenterPos));
            compoundTag.putInt("collected", itemCollected);
            compoundTag.putDouble("travel_pctg",travelingPercentage);
        }
    }

    @Override
    protected void read(CompoundTag compoundTag, boolean clientPacket) {
        super.read(compoundTag, clientPacket);
        stationInv.deserializeNBT((CompoundTag) compoundTag.get("station_inventory"));
        blazePos = NbtUtils.readBlockPos((CompoundTag) compoundTag.get("blaze_pos"));
        if(compoundTag.contains("blaze_next_pos"))
            blazeNextPos = NbtUtils.readBlockPos((CompoundTag) compoundTag.get("blaze_next_pos"));
        phase = NBTHelper.readEnum(compoundTag,"phase",Phase.class);
        blockAction = NBTHelper.readEnum(compoundTag,"block_action", BlockAction.class);
        fluidAction = NBTHelper.readEnum(compoundTag,"fluid_action", FluidAction.class);
        idleTime = compoundTag.getInt("idle_time");
        progressTime = compoundTag.getInt("progress_time");
        blinkTraveling = compoundTag.getBoolean("blink");
        if(!clientPacket){
            if(compoundTag.contains("mining_task"))
                mineFieldSubTask = MineFieldSubTask.fromNBT((CompoundTag) compoundTag.get("mining_task"));
            blazeInv.fromTag((ListTag) compoundTag.get("blaze_inventory"));
            if(compoundTag.contains("center_pos"))
                commandCenterPos = NbtUtils.readBlockPos((CompoundTag) compoundTag.get("center_pos"));
            itemCollected = compoundTag.getInt("collected");
            travelingPercentage = compoundTag.getDouble("travel_pctg");
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        handler.invalidate();
    }

    @Override
    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        if (side != Direction.UP && isItemHandlerCap(capability))
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
        TRAVEL_TO_MINEABLE,
        MINE,
        TRAVEL_TO_STATION,
        TRANSFER_ITEM
    }

    enum BlockAction {
        NONE(0),
        SILK_TOUCH(20),
        EXTRACT_RESOURCE(40),
        BURNOUT(10),
        BREAK(10); // Common mining action


        public final int tick;

        BlockAction(int tick) {
            this.tick = tick;
        }
    }

    enum FluidAction{
        NONE(0),
        EXTRACT_FLUID(40),
        DRY(10);

        public final int tick;

        FluidAction(int tick) {
            this.tick = tick;
        }
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
