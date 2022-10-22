package plus.dragons.createminingindustry.foundation.ponder;

import com.simibubi.create.foundation.ponder.PonderTag;
import net.minecraft.resources.ResourceLocation;
import plus.dragons.createminingindustry.MiningIndustry;

public class CmiPonderTag extends PonderTag {

    // public static final PonderTag EXPERIENCE = create("experience").item(ModBlocks.DISENCHANTER.get(), true, false).addToIndex();

    public CmiPonderTag(ResourceLocation id) {
        super(id);
    }

    private static PonderTag create(String id) {
        return new PonderTag(MiningIndustry.genRL(id));
    }
}
