package plus.dragons.createminingindustry.contraptions.mining.portabledrill;


import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.AABB;

public class PortableDrillItem extends Item {

    private static final int DEFAULT_DURABILITY = 300;
    public PortableDrillItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        var pos = pContext.getClickedPos().relative(pContext.getClickedFace());
        var level =  pContext.getLevel();
        var blockState = level.getBlockState(pos);
        if((blockState.isAir() || (blockState.getMaterial().isReplaceable() && !blockState.getMaterial().isLiquid())) && level.getEntities(null,new AABB(pos)).isEmpty()){
            var drillItem = pContext.getItemInHand();
            if(!level.isClientSide()){
                var tag = drillItem.getOrCreateTag();
                var durability = tag.contains("durability")? tag.getInt("durability"): DEFAULT_DURABILITY;
                var drill = new PortableDrillEntity(level,pos.getX(),pos.getY(),pos.getZ(),durability);
                level.addFreshEntity(drill);
            }
            if(!pContext.getPlayer().getAbilities().instabuild)
                drillItem.shrink(1);
            return InteractionResult.sidedSuccess(!level.isClientSide());
        }
        return InteractionResult.FAIL;
    }
}
