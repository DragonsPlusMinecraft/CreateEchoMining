package plus.dragons.createechomining.contraptions.mining.minefield;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createechomining.EchoMining;
import plus.dragons.createechomining.entry.CemItems;

import java.util.List;

public class SeededItem extends Item {
    public SeededItem(Properties pProperties) {
        super(pProperties);
    }

    public static ItemStack ofSeed(long seed, int count){
        var ret = CemItems.MINERAL_CLUSTER.asStack(count);
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
            pTooltipComponents.add(EchoMining.LANG.text("Seed:"+ tag.getLong("seed")).component());
        }
    }
}
