package plus.dragons.createminingindustry;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import plus.dragons.createminingindustry.entry.CmiBlockPartials;
import plus.dragons.createminingindustry.foundation.ponder.content.CmiPonderIndex;

public class MiningIndustryClient {

    public static void onClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        //Have to do this here because flywheel lied about the init timing ;(
        //Things won't work if you try init PartialModels in FMLClientSetupEvent
        CmiBlockPartials.register();
        modEventBus.addListener(MiningIndustryClient::clientInit);
        // forgeEventBus.addListener(InkRenderingCamera::handleInkFogColor);
    }

    public static void clientInit(final FMLClientSetupEvent event) {
        CmiPonderIndex.register();
        CmiPonderIndex.registerTags();
        // ModelBakery.UNREFERENCED_TEXTURES.add(BlazeEnchanterRenderer.BOOK_MATERIAL);
    }
}
