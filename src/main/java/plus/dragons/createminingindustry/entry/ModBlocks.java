package plus.dragons.createminingindustry.entry;

import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import plus.dragons.createminingindustry.MiningIndustry;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.BlazeMinerStationBlock;


public class ModBlocks {

    private static final CreateRegistrate REGISTRATE = MiningIndustry.registrate();

    public static final BlockEntry<BlazeMinerStationBlock> BLAZE_MINER_STATION = REGISTRATE
            .block("blaze_miner_station", BlazeMinerStationBlock::new)
            .initialProperties(SharedProperties::stone)
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(ModTags.pickaxeOnly())
            .blockstate((ctx, pov) -> pov.simpleBlock(ctx.get(), AssetLookup.standardModel(ctx, pov)))
            .simpleItem()
            .register();

    /*public static final BlockEntry<CopierBlock> COPIER = REGISTRATE
            .block("copier", CopierBlock::new)
            .initialProperties(SharedProperties::copperMetal)
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(ModTags.pickaxeOnly())
            .blockstate((ctx, pov) -> pov.simpleBlock(ctx.get(), AssetLookup.partialBaseModel(ctx, pov)))
            .item(AssemblyOperatorBlockItem::new)
            .model(AssetLookup::customItemModel)
            .build()
            .register();*/
    
    public static void register() {
    }
}
