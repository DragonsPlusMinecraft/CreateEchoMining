package plus.dragons.createminingindustry.entry;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import plus.dragons.createminingindustry.MiningIndustry;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.BlazeMinerStationBlockEntity;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.BlazeMinerStationRenderer;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.MineCommandCenterBlockEntity;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.MineCommandCenterRenderer;

public class CmiBlockEntities {

    public static final BlockEntityEntry<BlazeMinerStationBlockEntity> BLAZE_MINER_STATION = MiningIndustry.registrate()
            .tileEntity("blaze_miner_station", BlazeMinerStationBlockEntity::new)
            .validBlocks(CmiBlocks.BLAZE_MINER_STATION)
            .renderer(() -> BlazeMinerStationRenderer::new)
            .register();

    public static final BlockEntityEntry<MineCommandCenterBlockEntity> MINE_COMMAND_CENTER = MiningIndustry.registrate()
            .tileEntity("mine_command_center", MineCommandCenterBlockEntity::new)
            .validBlocks(CmiBlocks.MINE_COMMAND_CENTER)
            .renderer(() -> MineCommandCenterRenderer::new)
            .register();

    public static void register() {
    }
}
