package plus.dragons.createminingindustry;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import plus.dragons.createminingindustry.entry.*;
import plus.dragons.createminingindustry.foundation.data.advancement.ModAdvancements;
import plus.dragons.createminingindustry.foundation.data.advancement.ModTriggers;
import plus.dragons.createminingindustry.foundation.data.lang.LangMerger;

@Mod(MiningIndustry.MOD_ID)
public class MiningIndustry {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "create_mining_industry";
    private static final NonNullSupplier<CreateRegistrate> REGISTRATE = CreateRegistrate.lazy(MOD_ID);

    public MiningIndustry() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        initAllEntries();

        addForgeEventListeners(forgeEventBus);
        modEventBus.addListener(MiningIndustry::init);
        modEventBus.addListener(MiningIndustry::datagen);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MiningIndustryClient.onClient(modEventBus, forgeEventBus));
    }

    private void initAllEntries() {
        ModItems.register();
        ModBlocks.register();
        ModBlockEntities.register();
        ModEntityTypes.register();
        ModFluids.register();
        ModContainerTypes.register();
        ModTags.register();

    }
    
    private void addForgeEventListeners(IEventBus forgeEventBus) {
        forgeEventBus.addListener(ModItems::fillCreateItemGroup);
        forgeEventBus.addListener(ModFluids::handleInkEffect);
    }

    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModPackets.registerPackets();
            ModAdvancements.register();
            ModTriggers.register();
        });
    }
    
    public static void datagen(final GatherDataEvent event) {
        DataGenerator datagen = event.getGenerator();
        datagen.addProvider(new LangMerger(datagen));
        datagen.addProvider(new ModAdvancements(datagen));
    }

    public static ResourceLocation genRL(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    public static CreateRegistrate registrate() {
        return REGISTRATE.get();
    }
}
