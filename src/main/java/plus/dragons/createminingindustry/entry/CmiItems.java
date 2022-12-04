package plus.dragons.createminingindustry.entry;

import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import plus.dragons.createminingindustry.MiningIndustry;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.MineLocatorBarItem;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.product.BlazeFluidHolderItem;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.product.BlazeResourcePackageItem;
import plus.dragons.createminingindustry.contraptions.mining.drill.PortableDrillItem;
import plus.dragons.createminingindustry.event.FillCreateItemGroupEvent;

public class CmiItems {

    private static final CreateRegistrate REGISTRATE = MiningIndustry.registrate();

    public static final ItemEntry<PortableDrillItem> PORTABLE_DRILL = REGISTRATE.item("portable_one_time_drill", PortableDrillItem::new)
            .properties(prop -> prop.stacksTo(16))
            .register();

    public static final ItemEntry<MineLocatorBarItem> MINE_LOCATOR_BAR = REGISTRATE.item("mine_locator_bar", MineLocatorBarItem::new)
            .properties(prop -> prop.stacksTo(1))
            .register();

    public static final ItemEntry<Item> BLAZE_MINER_TOOLKIT = REGISTRATE.item("blaze_miner_toolkit", Item::new)
            .properties(prop -> prop.stacksTo(1))
            .register();

    public static final ItemEntry<BlazeFluidHolderItem> FLUID_HOLDER = REGISTRATE.item("blaze_fluid_holder", BlazeFluidHolderItem::new)
            .properties(prop -> prop.stacksTo(16))
            .register();

    public static final ItemEntry<BlazeResourcePackageItem> RESOURCE_PACKAGE = REGISTRATE.item("blaze_resource_package", BlazeResourcePackageItem::new)
            .properties(prop -> prop.stacksTo(16))
            .register();

    public static void fillCreateItemGroup(FillCreateItemGroupEvent event) {
        if (event.getItemGroup() == Create.BASE_CREATIVE_TAB) {
            event.addInsertion(AllItems.WRENCH.get(), PORTABLE_DRILL.asStack());
            event.addInsertion(AllItems.WRENCH.get(), MINE_LOCATOR_BAR.asStack());
            event.addInsertion(AllItems.WRENCH.get(), BLAZE_MINER_TOOLKIT.asStack());
            event.addInsertion(AllItems.WRENCH.get(), CmiBlocks.MINE_COMMAND_CENTER.asStack());
            event.addInsertion(AllItems.WRENCH.get(), CmiBlocks.BLAZE_MINER_STATION.asStack());
        }
    }
    
    public static void register() {
    }
}
