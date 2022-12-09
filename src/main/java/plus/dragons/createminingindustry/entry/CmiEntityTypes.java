package plus.dragons.createminingindustry.entry;

import com.tterrag.registrate.util.entry.EntityEntry;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.BlazeMinerEntity;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.BlazeMinerRenderer;
import plus.dragons.createminingindustry.contraptions.mining.portabledrill.PortableDrillEntity;

import static plus.dragons.createminingindustry.MiningIndustry.REGISTRATE;

public class CmiEntityTypes {

    public static final EntityEntry<PortableDrillEntity> PORTABLE_DRILL = REGISTRATE.entity("portable_one_time_drill",
            PortableDrillEntity::new,
            () -> PortableDrillEntity.Render::new,
            MobCategory.MISC,
            4, 10, false, false,
            PortableDrillEntity::build
    ).register();

    public static final EntityEntry<BlazeMinerEntity> BLAZE_MINER = REGISTRATE.entity("blaze_miner",
            BlazeMinerEntity::new,
            () -> BlazeMinerRenderer::new,
            MobCategory.CREATURE,
            8, 3, false, true,
            BlazeMinerEntity::build
    ).register();


    public static void register() {}

    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(BLAZE_MINER.get(), BlazeMinerEntity.createAttributes().build());
    }
}
