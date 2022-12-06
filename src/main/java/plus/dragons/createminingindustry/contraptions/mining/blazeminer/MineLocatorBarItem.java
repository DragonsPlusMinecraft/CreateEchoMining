package plus.dragons.createminingindustry.contraptions.mining.blazeminer;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createminingindustry.MiningIndustry;
import plus.dragons.createminingindustry.entry.CmiItems;

import java.util.List;

public class MineLocatorBarItem extends Item {
    public MineLocatorBarItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        var player = pContext.getPlayer();
        var hand = pContext.getHand();
        var level = pContext.getLevel();
        var pos = pContext.getClickedPos();
        var itemStack = player.getItemInHand(hand);
        var tag = itemStack.getOrCreateTag();
        if(level.getBlockEntity(pos) instanceof MineCommandCenterBlockEntity mineCommandCenterBlockEntity){
            var posTag1 =  tag.get("1st_location");
            var posTag2 =  tag.get("2nd_location");
            if(posTag1!=null&&posTag2!=null){
                var pos1 = NbtUtils.readBlockPos((CompoundTag) posTag1);
                var pos2 = NbtUtils.readBlockPos((CompoundTag) posTag2);
                // Verify area first. Must not be greater than max size.
                if(Math.abs(pos1.getX()-pos2.getX())>80 || Math.abs(pos1.getZ()-pos2.getZ())>80){
                    // TODO Notify player about max size.
                    return InteractionResult.FAIL;
                } else {
                    if(!level.isClientSide()){
                        var x1 = Math.min(pos1.getX(), pos2.getX());
                        var x2 = x1==pos1.getX()?pos2.getX():pos1.getX();
                        var z1 = Math.min(pos1.getZ(), pos2.getZ());
                        var z2 = z1==pos1.getZ()?pos2.getZ():pos1.getZ();
                        var xw = x2 - x1 + 1;
                        var zw = z2 - z1 + 1;
                        var newField = new MineFieldTask(x1,z1,xw,zw);
                        var returned = mineCommandCenterBlockEntity.setupMineField(newField);
                        if(!player.getAbilities().instabuild) itemStack.shrink(1);
                        if(returned!=null){
                            var bPos1 = new BlockPos(returned.x,0,returned.z);
                            var bPos2 = new BlockPos(returned.x + returned.xWidth - 1,0,returned.z + returned.zWidth - 1);
                            var newItemStack = CmiItems.MINE_LOCATOR_BAR.asStack();
                            var tag2 = newItemStack.getOrCreateTag();
                            tag2.put("1st_location", NbtUtils.writeBlockPos(bPos1));
                            tag2.put("2nd_location", NbtUtils.writeBlockPos(bPos2));
                            if(!player.getAbilities().instabuild)
                                player.setItemInHand(hand,newItemStack);
                            else
                                player.getInventory().add(newItemStack);;
                        }
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide());
                }
            } else {
                // TODO Notify player locator not setup correctly.
                return InteractionResult.FAIL;
            }
        } else {
            if(tag.contains("1st_location") && !tag.contains("2nd_location")){
                var pos1 = NbtUtils.readBlockPos((CompoundTag) tag.get("1st_location"));
                // Verify area. Must not be greater than max size.
                if(Math.abs(pos1.getX()-pos.getX())>80 || Math.abs(pos1.getZ()-pos.getZ())>80){
                    // TODO Notify player about max size.
                    return InteractionResult.FAIL;
                } else {
                    tag.put("2nd_location", NbtUtils.writeBlockPos(pos));
                }
            } else if(tag.contains("2nd_location")){
                tag.remove("2nd_location");
                tag.put("1st_location", NbtUtils.writeBlockPos(pos));
            } else {
                tag.put("1st_location", NbtUtils.writeBlockPos(pos));
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if(pPlayer.isShiftKeyDown()){
            var tag = pPlayer.getItemInHand(pUsedHand).getOrCreateTag();
            tag.remove("1st_location");
            tag.remove("2nd_location");
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        // TODO add tutorial description & information
        var tag = pStack.getOrCreateTag();
        if(tag.contains("1st_location")){
            pTooltipComponents.add(MiningIndustry.LANG.text("1st Pos"+ NbtUtils.readBlockPos((CompoundTag) tag.get("1st_location"))).component());
            if(tag.contains("2nd_location")){
                pTooltipComponents.add(MiningIndustry.LANG.text("2nd Pos"+NbtUtils.readBlockPos((CompoundTag) tag.get("2nd_location"))).component());
            }
        }
    }
}
