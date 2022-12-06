package plus.dragons.createminingindustry.entry;

import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.AllSections;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.BlazeMinerStationBlock;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.MineCommandCenterBlock;

import static plus.dragons.createminingindustry.MiningIndustry.REGISTRATE;


public class CmiBlocks {

    static {
        REGISTRATE.creativeModeTab(() -> Create.BASE_CREATIVE_TAB).startSection(AllSections.KINETICS);
    }

    public static final BlockEntry<BlazeMinerStationBlock> BLAZE_MINER_STATION = REGISTRATE
            .block("blaze_miner_station", BlazeMinerStationBlock::new)
            .initialProperties(SharedProperties::stone)
            .transform(AllTags.pickaxeOnly())
            .blockstate((ctx, pov) -> pov.simpleBlock(ctx.get(), AssetLookup.standardModel(ctx, pov)))
            .simpleItem()
            .register();

    public static final BlockEntry<MineCommandCenterBlock> MINE_COMMAND_CENTER = REGISTRATE
            .block("mine_command_center", MineCommandCenterBlock::new)
            .initialProperties(SharedProperties::stone)
            .transform(AllTags.pickaxeOnly())
            .blockstate((ctx, pov) -> pov.simpleBlock(ctx.get(), AssetLookup.standardModel(ctx, pov)))
            .simpleItem()
            .register();

    
    public static void register() {}
}
