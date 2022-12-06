package plus.dragons.createminingindustry;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import plus.dragons.createminingindustry.foundation.ponder.content.CmiPonderIndex;

public class MiningIndustryClient {

    public MiningIndustryClient() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        //Have to do this here because flywheel lied about the init timing ;(
        //Things won't work if you try init PartialModels in FMLClientSetupEvent
        // CmiBlockPartials.register();
        modEventBus.register(this);
        registerForgeEvents(forgeEventBus);
        // forgeEventBus.addListener(InkRenderingCamera::handleInkFogColor);
    }

    private void registerForgeEvents(IEventBus forgeEventBus) {
        // Just leave it here for future
    }

    @SubscribeEvent
    public static void setup(final FMLClientSetupEvent event) {
        CmiPonderIndex.register();
        CmiPonderIndex.registerTags();
    }
}
