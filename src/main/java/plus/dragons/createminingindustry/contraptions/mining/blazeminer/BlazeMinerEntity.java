package plus.dragons.createminingindustry.contraptions.mining.blazeminer;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public class BlazeMinerEntity extends PathfinderMob {
    public BlazeMinerEntity(EntityType<? extends BlazeMinerEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
        @SuppressWarnings("unchecked")
        EntityType.Builder<BlazeMinerEntity> entityBuilder =
                (EntityType.Builder<BlazeMinerEntity>) builder;
        return entityBuilder.sized(0.6F, 1.8F);
    }


    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.ATTACK_DAMAGE, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, (double)2F)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

}
