package plus.dragons.createminingindustry.entry;

import com.tterrag.registrate.util.entry.BlockEntityEntry;
import plus.dragons.createminingindustry.MiningIndustry;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.BlazeMinerStationBlockEntity;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.BlazeMinerStationRenderer;

public class CmiBlockEntities {

    public static final BlockEntityEntry<BlazeMinerStationBlockEntity> BLAZE_MINER_STATION = MiningIndustry.registrate()
            .tileEntity("disenchanter", BlazeMinerStationBlockEntity::new)
            .validBlocks(CmiBlocks.BLAZE_MINER_STATION)
            .renderer(() -> BlazeMinerStationRenderer::new)
            .register();

    public static void register() {
    }
}
