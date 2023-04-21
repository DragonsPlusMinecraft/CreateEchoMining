package plus.dragons.createechomining;

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
import plus.dragons.createdragonlib.init.SafeRegistrate;
import plus.dragons.createdragonlib.lang.Lang;
import plus.dragons.createdragonlib.lang.LangFactory;
import plus.dragons.createechomining.entry.*;
import plus.dragons.createechomining.foundation.ponder.content.CemPonderIndex;

@Mod(EchoMining.ID)
public class EchoMining {

    public static final String NAME = "Create: Echo Mining";
    public static final String ID = "create_echo_mining";
    public static final SafeRegistrate REGISTRATE = new SafeRegistrate(ID);
    public static final Lang LANG = new Lang(ID);

    private static final LangFactory LANG_FACTORY = LangFactory.create(NAME, ID)
            .ponders(() -> {
                CemPonderIndex.register();
                CemPonderIndex.registerTags();
            })
            .tooltips()
            .ui();


    public EchoMining() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        registerEntries(modEventBus);
        modEventBus.register(this);
        modEventBus.addListener(EventPriority.LOWEST, LANG_FACTORY::datagen);
        registerForgeEvents(forgeEventBus);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> EchoMiningClient::new);
    }

    private void registerEntries(IEventBus modEventBus) {
        CemItems.register();
        CemBlocks.register();
        CemBlockEntities.register();
        CemEntityTypes.register();
        CemTags.register();
        REGISTRATE.registerEventListeners(modEventBus);
    }
    
    private void registerForgeEvents(IEventBus forgeEventBus) {
        forgeEventBus.addListener(CemItems::fillCreateItemGroup);
    }

    @SubscribeEvent
    public void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // do not convert to lambda since there may be more
            CemPackets.registerPackets();
        });
    }

    public static ResourceLocation genRL(String name) {
        return new ResourceLocation(ID, name);
    }
}
