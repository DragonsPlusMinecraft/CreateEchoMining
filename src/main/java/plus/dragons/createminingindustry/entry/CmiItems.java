package plus.dragons.createminingindustry.entry;

import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.AllSections;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import plus.dragons.createdragonlib.init.FillCreateItemGroupEvent;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.BlazeMinerLunchboxItem;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.BlazeMinerTransitBoxItem;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.MineLocatorBarItem;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.product.fluidpackage.BlazeFluidContainerItem;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.product.mineralcluster.MineralClusterItem;
import plus.dragons.createminingindustry.contraptions.mining.portabledrill.PortableDrillItem;

import static plus.dragons.createminingindustry.MiningIndustry.REGISTRATE;

public class CmiItems {

    static {
        REGISTRATE.creativeModeTab(() -> Create.BASE_CREATIVE_TAB).startSection(AllSections.KINETICS);
    }

    // Placeholder

    static {
        REGISTRATE.startSection(AllSections.MATERIALS);
    }

    public static final ItemEntry<PortableDrillItem> PORTABLE_DRILL = REGISTRATE.item("portable_one_time_drill", PortableDrillItem::new)
            .properties(prop -> prop.stacksTo(1))
            .register();

    public static final ItemEntry<MineLocatorBarItem> MINE_LOCATOR_BAR = REGISTRATE.item("mine_locator_bar", MineLocatorBarItem::new)
            .properties(prop -> prop.stacksTo(1))
            .register();

    public static final ItemEntry<Item> BLAZE_MINER_SUIT = REGISTRATE.item("blaze_miner_suit", Item::new)
            .properties(prop -> prop.stacksTo(16))
            .register();

    public static final ItemEntry<Item> EMPTY_BLAZE_MINER_TRANSIT_BOX = REGISTRATE.item("empty_blaze_miner_transit_box", Item::new)
            .properties(prop -> prop.stacksTo(16))
            .register();

    public static final ItemEntry<BlazeMinerTransitBoxItem> BLAZE_MINER_TRANSIT_BOX = REGISTRATE.item("blaze_miner_transit_box", BlazeMinerTransitBoxItem::new)
            .properties(prop -> prop.stacksTo(16))
            .register();

    // TODO do more design and variety
    public static final ItemEntry<BlazeMinerLunchboxItem> BLAZE_MINER_LUNCHBOX_SAMPLE = REGISTRATE.item("blaze_miner_lunchbox_sample", BlazeMinerLunchboxItem::new)
            .properties(prop -> prop.durability(100)) // This make lunchbox stack to 1.
            .register();


    public static final ItemEntry<BlazeFluidContainerItem> BLAZE_FLUID_CONTAINER = REGISTRATE.item("blaze_fluid_container", BlazeFluidContainerItem::new)
            .properties(prop -> prop.stacksTo(16))
            .register();

    public static final ItemEntry<MineralClusterItem> MINERAL_CLUSTER = REGISTRATE.item("mineral_cluster", MineralClusterItem::new)
            .properties(prop -> prop.stacksTo(16))
            .register();

    public static void fillCreateItemGroup(FillCreateItemGroupEvent event) {
        if (event.getItemGroup() == Create.BASE_CREATIVE_TAB) {
            // TODO arrange them later
            event.addInsertion(AllItems.WRENCH.get(), PORTABLE_DRILL.asStack());
            event.addInsertion(AllItems.WRENCH.get(), MINE_LOCATOR_BAR.asStack());
            event.addInsertion(AllItems.WRENCH.get(), BLAZE_MINER_SUIT.asStack());
            event.addInsertion(AllItems.WRENCH.get(), EMPTY_BLAZE_MINER_TRANSIT_BOX.asStack());
            event.addInsertion(AllItems.WRENCH.get(), BLAZE_MINER_TRANSIT_BOX.asStack());
            event.addInsertion(AllItems.WRENCH.get(), BLAZE_MINER_LUNCHBOX_SAMPLE.asStack());
            event.addInsertion(AllItems.WRENCH.get(), CmiBlocks.MINE_COMMAND_POST.asStack());
        }
    }
    
    public static void register() {
    }
}
