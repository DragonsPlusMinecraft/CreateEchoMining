package plus.dragons.createechomining.entry;

import com.tterrag.registrate.util.entry.EntityEntry;
import net.minecraft.world.entity.MobCategory;
import plus.dragons.createechomining.contraptions.mining.portabledrill.PortableDrillEntity;

import static plus.dragons.createechomining.EchoMining.REGISTRATE;

public class CemEntityTypes {

    public static final EntityEntry<PortableDrillEntity> PORTABLE_DRILL = REGISTRATE.entity("portable_one_time_drill",
            PortableDrillEntity::new,
            () -> PortableDrillEntity.Render::new,
            MobCategory.MISC,
            4, 10, false, false,
            PortableDrillEntity::build
    ).register();


    public static void register() {}
}
