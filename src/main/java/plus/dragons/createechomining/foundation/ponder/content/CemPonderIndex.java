package plus.dragons.createechomining.foundation.ponder.content;

import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import plus.dragons.createechomining.EchoMining;

public class CemPonderIndex {
    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(EchoMining.ID);

    public static void register() {
        //HELPER.forComponents(ModBlocks.DISENCHANTER)
        //        .addStoryBoard("disenchant",EnchantmentScenes::disenchant, ModPonderTag.EXPERIENCE);
    }

    public static void registerTags() {
        //PonderRegistry.TAGS.forTag(ModPonderTag.EXPERIENCE)
        //        .add(ModBlocks.DISENCHANTER);
    }

}
