package plus.dragons.createminingindustry.contraptions.mining.blazeminer.product;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createminingindustry.MiningIndustry;
import plus.dragons.createminingindustry.entry.CmiItems;

import java.util.List;

public class BlazeFluidHolderItem extends Item {
    public BlazeFluidHolderItem(Properties pProperties) {
        super(pProperties);
    }

    public static ItemStack ofFluid(Fluid fluid){
        var ret = CmiItems.FLUID_HOLDER.asStack();
        ret.getOrCreateTag().putString("fluid",fluid.getFluidType().toString());
        return ret;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        // TODO show fluid inside
        var tag = pStack.getOrCreateTag();
        if(tag.contains("fluid")){
            pTooltipComponents.add(MiningIndustry.LANG.text("1st Pos"+ tag.getString("fluid")).component());
        }
    }
}
