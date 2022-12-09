package plus.dragons.createminingindustry.contraptions.mining.blazeminer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.product.BlazeMinerModel;

public class BlazeMinerRenderer extends MobRenderer<BlazeMinerEntity, BlazeMinerModel> {
    private static final BlazeMinerModel model = new BlazeMinerModel();
    public BlazeMinerRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, model, 0.5F);
    }

    // Heavily TODO
    @Override
    public ResourceLocation getTextureLocation(BlazeMinerEntity pEntity) {
        return null;
    }
}
