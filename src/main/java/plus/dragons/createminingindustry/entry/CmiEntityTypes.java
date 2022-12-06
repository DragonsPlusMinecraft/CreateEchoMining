package plus.dragons.createminingindustry.entry;

import com.simibubi.create.foundation.data.CreateEntityBuilder;
import com.simibubi.create.foundation.utility.Lang;
import com.tterrag.registrate.util.entry.EntityEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import plus.dragons.createminingindustry.MiningIndustry;
import plus.dragons.createminingindustry.contraptions.mining.drill.PortableDrillEntity;

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
