package plus.dragons.createminingindustry.contraptions.mining.blazeminer;

import com.simibubi.create.content.contraptions.wrench.IWrenchable;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import plus.dragons.createminingindustry.entry.CmiBlockEntities;

public class MineCommandPostBlock extends HorizontalDirectionalBlock implements IWrenchable, ITE<MineCommandPostBlockEntity> {
    public MineCommandPostBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Class<MineCommandPostBlockEntity> getTileEntityClass() {
        return MineCommandPostBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MineCommandPostBlockEntity> getTileEntityType() {
        return CmiBlockEntities.MINE_COMMAND_CENTER.get();
    }
}
