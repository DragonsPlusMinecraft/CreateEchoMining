package plus.dragons.createminingindustry.contraptions.mining.blazeminer.product;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createminingindustry.MiningIndustry;
import plus.dragons.createminingindustry.entry.CmiItems;

import java.util.List;

public class BlazeResourcePackageItem extends Item {
    public BlazeResourcePackageItem(Properties pProperties) {
        super(pProperties);
    }

    public static ItemStack ofSeed(long seed, int count){
        var ret = CmiItems.RESOURCE_PACKAGE.asStack(count);
        ret.getOrCreateTag().putLong("seed",seed);
        return ret;
    }

    public static ItemStack ofSeed(long seed){
        return ofSeed(seed,1);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        // TODO show package info
        var tag = pStack.getOrCreateTag();
        if(tag.contains("seed")){
            pTooltipComponents.add(MiningIndustry.LANG.text("Seed:"+ tag.getLong("seed")).component());
        }
    }
}
