package plus.dragons.createechomining.foundation.ponder;

import com.simibubi.create.foundation.ponder.PonderTag;
import net.minecraft.resources.ResourceLocation;
import plus.dragons.createechomining.EchoMining;

public class CemPonderTag extends PonderTag {

    // public static final PonderTag EXPERIENCE = create("experience").item(ModBlocks.DISENCHANTER.get(), true, false).addToIndex();

    public CemPonderTag(ResourceLocation id) {
        super(id);
    }

    private static PonderTag create(String id) {
        return new PonderTag(EchoMining.genRL(id));
    }
}
