package plus.dragons.createminingindustry.contraptions.mining.blazeminer;

import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import plus.dragons.createminingindustry.entry.CmiBlockEntities;

public class MineCommandCenterBlock extends HorizontalDirectionalBlock implements IWrenchable, ITE<MineCommandCenterBlockEntity> {
    public MineCommandCenterBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Class<MineCommandCenterBlockEntity> getTileEntityClass() {
        return MineCommandCenterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MineCommandCenterBlockEntity> getTileEntityType() {
        return CmiBlockEntities.MINE_COMMAND_CENTER.get();
    }
}
