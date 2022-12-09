package plus.dragons.createminingindustry.contraptions.mining.portabledrill;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.utility.BlockHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import plus.dragons.createminingindustry.entry.CmiEntityTypes;
import plus.dragons.createminingindustry.entry.CmiItems;

import java.util.concurrent.atomic.AtomicInteger;

public class PortableDrillEntity extends Entity {

    private static final EntityDataAccessor<Boolean> IS_DRILLING = SynchedEntityData.defineId(PortableDrillEntity.class, EntityDataSerializers.BOOLEAN);
    private static final int BREAKER_ID_SHIFT = - 100000;
    private static final float BREAK_SPEED = 2.56f;
    public static final AtomicInteger NEXT_BREAKER_ID = new AtomicInteger();
    protected int ticksUntilNextProgress;
    protected int destroyProgress;
    protected int breakerId = -NEXT_BREAKER_ID.incrementAndGet() + BREAKER_ID_SHIFT;
    protected BlockPos breakingPos;
    private int durability;

    public PortableDrillEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public PortableDrillEntity(Level Level, double X, double Y, double Z, int durability) {
        this(CmiEntityTypes.PORTABLE_DRILL.get(), Level);
        this.blocksBuilding = true;
        this.durability = durability;
        this.setPos(X + 0.5, Y, Z + 0.5);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = X;
        this.yo = Y;
        this.zo = Z;
    }

    @Override
    public void tick() {
        this.move(MoverType.SELF, this.getDeltaMovement());
        if(isOnGround()){
            setDeltaMovement(Vec3.ZERO);
            setDrillingStatus(true);
            markHurt();
        } else {
            var blockPos = this.blockPosition();
            if(!level.getFluidState(blockPos).isEmpty())
                collapse();
            var others = level.getEntities(CmiEntityTypes.PORTABLE_DRILL.get(), this.getBoundingBox(), entity->true);
            others.remove(this);
            if(!others.isEmpty()){
                others.forEach(PortableDrillEntity::collapse);
                this.collapse();
                return;
            }
            else{
                setDeltaMovement(0,-0.5,0);
                setDrillingStatus(false);
                markHurt();
            }
        }
        if (level.isClientSide)
            return;
        if(isDrilling() && hasExactlyOnePlayerPassenger())
            mine();
        if(durability<=0) collapse();
        if(getY()<level.getMinBuildHeight()) collapse();
    }

    private void collapse(){
        //TODO
        this.discard();
    }

    private void mine(){
        durability--;
        breakingPos = this.blockPosition();
        BlockState stateToBreak = level.getBlockState(breakingPos);
        if(stateToBreak.isAir()){
            breakingPos = breakingPos.below();
            stateToBreak = level.getBlockState(breakingPos);
        }
        float blockHardness = stateToBreak.getDestroySpeed(level, breakingPos);

        if (blockHardness == -1) {
            if (destroyProgress != 0) {
                destroyProgress = 0;
                level.destroyBlockProgress(breakerId, breakingPos, -1);
            }
            return;
        }
        destroyProgress += Mth.clamp((int) (BREAK_SPEED / blockHardness), 1, 10 - destroyProgress);
        level.playSound(null, breakingPos, stateToBreak.getSoundType()
                .getHitSound(), SoundSource.NEUTRAL, .25f, 1);

        if (destroyProgress >= 10) {
            finishMining();
            destroyProgress = 0;
            ticksUntilNextProgress = -1;
            level.destroyBlockProgress(breakerId, breakingPos, -1);
            return;
        }

        ticksUntilNextProgress = (int) (blockHardness / BREAK_SPEED);
        level.destroyBlockProgress(breakerId, breakingPos, (int) destroyProgress);
    }

    public void finishMining() {
        BlockHelper.destroyBlock(level, breakingPos, 1f, (stack) -> {
            if (stack.isEmpty())
                return;
            if (!level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS))
                return;
            if (level.restoringBlockSnapshots)
                return;
            ItemEntity itementity = new ItemEntity(level, position().x, position().y+1, position().z, stack);
            itementity.setDefaultPickUpDelay();
            itementity.setDeltaMovement(0,0.1,0);
            level.addFreshEntity(itementity);
        });
    }

    @Override
    public void move(@NotNull MoverType pType, @NotNull Vec3 pPos) {
        if(pType!=MoverType.SELF) return;
        super.move(pType,pPos);
    }

    @Override
    public InteractionResult interact(Player pPlayer, @NotNull InteractionHand pHand) {
        var item = pPlayer.getItemInHand(pHand);
        if(AllItems.WRENCH.isIn(item)){
            if(!level.isClientSide()){
                this.getPassengers().forEach(Entity::stopRiding);
                var drop = CmiItems.PORTABLE_DRILL.asStack();
                drop.getOrCreateTag().putInt("durability",durability);
                level.addFreshEntity(new ItemEntity(level,getX(),getY(),getZ(),drop));
                this.discard();
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        } else {
            if(getPassengers().size()==0){
                if(!level.isClientSide())
                    pPlayer.startRiding(this);
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
            return InteractionResult.FAIL;
        }
    }

    @Override
    public void remove(Entity.@NotNull RemovalReason pReason) {
        if (!level.isClientSide && destroyProgress != 0)
            level.destroyBlockProgress(breakerId, breakingPos, -1);
        super.remove(pReason);
    }

    @Override
    protected boolean canRide(@NotNull Entity entity) {
        // Fake Players (tested with deployers) have a BUNCH of weird issues, don't let
        return entity instanceof Player && !(entity instanceof FakePlayer);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(IS_DRILLING, false);
    }

    private void setDrillingStatus(boolean isDrilling){
        this.entityData.set(IS_DRILLING,isDrilling);
    }

    private boolean isDrilling(){
        return this.entityData.get(IS_DRILLING);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        durability = pCompound.getInt("durability");
        setDrillingStatus(pCompound.getBoolean("is_drilling"));
        destroyProgress = pCompound.getInt("progress");
        ticksUntilNextProgress = pCompound.getInt("next_tick");
        if (pCompound.contains("breaking"))
            breakingPos = NbtUtils.readBlockPos(pCompound.getCompound("breaking"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("durability",durability);
        pCompound.putBoolean("is_drilling",isDrilling());
        pCompound.putInt("progress", destroyProgress);
        pCompound.putInt("next_tick", ticksUntilNextProgress);
        if (breakingPos != null)
            pCompound.put("breaking", NbtUtils.writeBlockPos(breakingPos));
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public static EntityType.Builder<?> build(EntityType.Builder<?> builder) {
        @SuppressWarnings("unchecked")
        EntityType.Builder<PortableDrillEntity> entityBuilder =
                (EntityType.Builder<PortableDrillEntity>) builder;
        return entityBuilder.sized(1, 1);
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    public static class Render extends EntityRenderer<PortableDrillEntity>{
        // Heavily TODO

        public Render(EntityRendererProvider.Context pContext) {
            super(pContext);
        }

        @Override
        public ResourceLocation getTextureLocation(PortableDrillEntity pEntity) {
            return null;
        }
    }
}
