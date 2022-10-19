package plus.dragons.createminingindustry.contraptions.mining.blazeminer;

import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import plus.dragons.createminingindustry.entry.ModBlockEntities;

public class BlazeMinerStationBlock extends HorizontalDirectionalBlock implements IWrenchable, ITE<BlazeMinerStationBlockEntity> {
    public BlazeMinerStationBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Class<BlazeMinerStationBlockEntity> getTileEntityClass() {
        return BlazeMinerStationBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BlazeMinerStationBlockEntity> getTileEntityType() {
        return ModBlockEntities.BLAZE_MINER_STATION.get();
    }
}
