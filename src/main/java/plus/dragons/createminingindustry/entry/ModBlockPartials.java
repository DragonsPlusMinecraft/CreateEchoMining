package plus.dragons.createminingindustry.entry;

import com.jozufozu.flywheel.core.PartialModel;
import plus.dragons.createminingindustry.MiningIndustry;

public class ModBlockPartials {
    
    /*public static final PartialModel
        COPIER_TOP = block("copier/top"),
        COPIER_MIDDLE = block("copier/middle"),
        COPIER_BOTTOM = block("copier/bottom");*/
    
    private static PartialModel block(String path) {
        return new PartialModel(MiningIndustry.genRL("block/" + path));
    }
    
    public static void register() {
    }
    
}
