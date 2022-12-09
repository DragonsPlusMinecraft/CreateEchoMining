package plus.dragons.createminingindustry.entry;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.BlazeMinerStationBlockEntity;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.BlazeMinerStationRenderer;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.MineCommandPostBlockEntity;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.MineCommandPostRenderer;

import static plus.dragons.createminingindustry.MiningIndustry.REGISTRATE;

public class CmiBlockEntities {

    public static final BlockEntityEntry<BlazeMinerStationBlockEntity> BLAZE_MINER_STATION = REGISTRATE
            .tileEntity("blaze_miner_station", BlazeMinerStationBlockEntity::new)
            .validBlocks(CmiBlocks.BLAZE_MINER_STATION)
            .renderer(() -> BlazeMinerStationRenderer::new)
            .register();

    public static final BlockEntityEntry<MineCommandPostBlockEntity> MINE_COMMAND_CENTER = REGISTRATE
            .tileEntity("mine_command_center", MineCommandPostBlockEntity::new)
            .validBlocks(CmiBlocks.MINE_COMMAND_POST)
            .renderer(() -> MineCommandPostRenderer::new)
            .register();

    public static void register() {}
}
