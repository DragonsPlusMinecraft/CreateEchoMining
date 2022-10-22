package plus.dragons.createminingindustry.entry;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import plus.dragons.createminingindustry.MiningIndustry;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.MineWorkPermitItem;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.product.BlazeFluidHolderItem;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.product.BlazeResourcePackageItem;
import plus.dragons.createminingindustry.contraptions.mining.drill.PortableDrillItem;
import plus.dragons.createminingindustry.event.FillCreateItemGroupEvent;

public class CmiItems {

    private static final CreateRegistrate REGISTRATE = MiningIndustry.registrate();

    public static final ItemEntry<PortableDrillItem> PORTABLE_DRILL = REGISTRATE.item("portable_one_time_drill", PortableDrillItem::new)
            .properties(prop -> prop.stacksTo(16))
            .register();

    public static final ItemEntry<MineWorkPermitItem> MINE_WORK_PERMIT = REGISTRATE.item("mine_work_permit", MineWorkPermitItem::new)
            .properties(prop -> prop.stacksTo(1))
            .register();

    public static final ItemEntry<BlazeFluidHolderItem> FLUID_HOLDER = REGISTRATE.item("blaze_fluid_holder", BlazeFluidHolderItem::new)
            .properties(prop -> prop.stacksTo(16))
            .register();

    public static final ItemEntry<BlazeResourcePackageItem> RESOURCE_PACKAGE = REGISTRATE.item("blaze_resource_package", BlazeResourcePackageItem::new)
            .register();

    public static void fillCreateItemGroup(FillCreateItemGroupEvent event) {
        if (event.getItemGroup() == Create.BASE_CREATIVE_TAB) {
            /*event.addInsertion(AllBlocks.ITEM_DRAIN.get(), ModBlocks.DISENCHANTER.asStack());
            event.addInsertion(AllBlocks.SPOUT.get(), ModBlocks.COPIER.asStack());
            event.addInsertion(AllBlocks.BLAZE_BURNER.get(), ENCHANTING_GUIDE.asStack());
            event.addInsertion(AllFluids.CHOCOLATE.get().getBucket(), ModFluids.INK.get().getBucket().getDefaultInstance());
            event.addInsertion(ModFluids.INK.get().getBucket(), HYPER_EXP_BOTTLE.asStack());*/
        }
    }
    
    public static void register() {
    }
}
