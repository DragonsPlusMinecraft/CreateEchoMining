package plus.dragons.createminingindustry.entry;

import com.simibubi.create.Create;
import com.simibubi.create.content.AllSections;

import static plus.dragons.createminingindustry.MiningIndustry.REGISTRATE;


public class CmiBlocks {

    static {
        REGISTRATE.creativeModeTab(() -> Create.BASE_CREATIVE_TAB).startSection(AllSections.KINETICS);
    }

    /*public static final BlockEntry<MineCommandPostBlock> MINE_COMMAND_POST = REGISTRATE
            .block("mine_command_post", MineCommandPostBlock::new)
            .initialProperties(SharedProperties::stone)
            .transform(AllTags.pickaxeOnly())
            .blockstate((ctx, pov) -> pov.simpleBlock(ctx.get(), AssetLookup.standardModel(ctx, pov)))
            .simpleItem()
            .register();*/

    
    public static void register() {}
}
