package plus.dragons.createminingindustry.entry;

import com.tterrag.registrate.util.entry.EntityEntry;
import net.minecraft.world.entity.MobCategory;
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


    public static void register() {}
}
