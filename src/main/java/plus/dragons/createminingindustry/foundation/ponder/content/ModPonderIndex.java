package plus.dragons.createminingindustry.foundation.ponder.content;

import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import plus.dragons.createminingindustry.MiningIndustry;

public class ModPonderIndex {
    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(MiningIndustry.MOD_ID);

    public static void register() {
        //HELPER.forComponents(ModBlocks.DISENCHANTER)
        //        .addStoryBoard("disenchant",EnchantmentScenes::disenchant, ModPonderTag.EXPERIENCE);
    }

    public static void registerTags() {
        //PonderRegistry.TAGS.forTag(ModPonderTag.EXPERIENCE)
        //        .add(ModBlocks.DISENCHANTER);
    }

}
