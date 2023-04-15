package plus.dragons.createminingindustry.entry;

import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.AllSections;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import plus.dragons.createdragonlib.init.FillCreateItemGroupEvent;
import plus.dragons.createminingindustry.contraptions.mining.minefield.SeededItem;
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

    public static final ItemEntry<Item> BLAZE_MINER_SUIT = REGISTRATE.item("blaze_miner_suit", Item::new)
            .properties(prop -> prop.stacksTo(16))
            .register();

    public static final ItemEntry<Item> EMPTY_BLAZE_MINER_TRANSIT_BOX = REGISTRATE.item("empty_blaze_miner_transit_box", Item::new)
            .properties(prop -> prop.stacksTo(16))
            .register();

    public static final ItemEntry<SeededItem> MINERAL_CLUSTER = REGISTRATE.item("mineral_cluster", SeededItem::new)
            .properties(prop -> prop.stacksTo(16))
            .register();

    public static void fillCreateItemGroup(FillCreateItemGroupEvent event) {
        if (event.getItemGroup() == Create.BASE_CREATIVE_TAB) {
            // TODO arrange them later
            event.addInsertion(AllItems.WRENCH.get(), PORTABLE_DRILL.asStack());
            event.addInsertion(AllItems.WRENCH.get(), BLAZE_MINER_SUIT.asStack());
            event.addInsertion(AllItems.WRENCH.get(), EMPTY_BLAZE_MINER_TRANSIT_BOX.asStack());
        }
    }
    
    public static void register() {
    }
}
