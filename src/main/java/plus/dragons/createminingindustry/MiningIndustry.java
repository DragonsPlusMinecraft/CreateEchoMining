package plus.dragons.createminingindustry;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plus.dragons.createdragonlib.init.SafeRegistrate;
import plus.dragons.createdragonlib.lang.Lang;
import plus.dragons.createdragonlib.lang.LangFactory;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.product.mineralcluster.MineralClusterContentGeneration;
import plus.dragons.createminingindustry.entry.*;
import plus.dragons.createminingindustry.foundation.ponder.content.CmiPonderIndex;

@Mod(MiningIndustry.ID)
public class MiningIndustry {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String NAME = "Create: Mining Industry";
    public static final String ID = "create_mining_industry";
    public static final SafeRegistrate REGISTRATE = new SafeRegistrate(ID);
    public static final Lang LANG = new Lang(ID);

    private static final LangFactory LANG_FACTORY = LangFactory.create(NAME, ID)
            .ponders(() -> {
                CmiPonderIndex.register();
                CmiPonderIndex.registerTags();
            })
            .tooltips()
            .ui();


    public MiningIndustry() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        registerEntries(modEventBus);
        modEventBus.register(this);
        modEventBus.addListener(EventPriority.LOWEST, LANG_FACTORY::datagen);
        modEventBus.addListener(MineralClusterContentGeneration::registerResourcePackage);
        modEventBus.addListener(CmiEntityTypes::registerEntityAttributes);
        registerForgeEvents(forgeEventBus);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> MiningIndustryClient::new);
    }

    private void registerEntries(IEventBus modEventBus) {
        CmiItems.register();
        CmiBlocks.register();
        CmiBlockEntities.register();
        CmiEntityTypes.register();
        CmiTags.register();
        REGISTRATE.registerEventListeners(modEventBus);
    }
    
    private void registerForgeEvents(IEventBus forgeEventBus) {
        forgeEventBus.addListener(CmiItems::fillCreateItemGroup);
        forgeEventBus.addListener(MineralClusterContentGeneration::syncResourcePackageToClient);
    }

    @SubscribeEvent
    public void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // do not convert to lambda since there may be more
            CmiPackets.registerPackets();
        });
    }

    public static ResourceLocation genRL(String name) {
        return new ResourceLocation(ID, name);
    }
}
