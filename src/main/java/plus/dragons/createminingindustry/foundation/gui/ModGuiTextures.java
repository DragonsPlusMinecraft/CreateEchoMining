package plus.dragons.createminingindustry.foundation.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.gui.UIRenderHelper;
import com.simibubi.create.foundation.gui.element.ScreenElement;
import com.simibubi.create.foundation.utility.Color;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import plus.dragons.createminingindustry.MiningIndustry;

public enum ModGuiTextures implements ScreenElement {
    EXAMPLE("example", 185, 48);
    
    public final ResourceLocation location;
    public final int width, height;
    public final int startX, startY;
    
    ModGuiTextures(String namespace, String location, int startX, int startY, int width, int height) {
        this.location = new ResourceLocation(namespace, "textures/gui/" + location + ".png");
        this.width = width;
        this.height = height;
        this.startX = startX;
        this.startY = startY;
    }
    
    ModGuiTextures(String location, int startX, int startY, int width, int height) {
        this(MiningIndustry.MOD_ID, location, startX, startY, width, height);
    }
    
    ModGuiTextures(String location, int width, int height) {
        this(location, 0, 0, width, height);
    }
    
    @OnlyIn(Dist.CLIENT)
    public void bind() {
        RenderSystem.setShaderTexture(0, location);
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(PoseStack ms, int x, int y) {
        bind();
        GuiComponent.blit(ms, x, y, 0, startX, startY, width, height, 256, 256);
    }
    
    @OnlyIn(Dist.CLIENT)
    public void render(PoseStack ms, int x, int y, GuiComponent component) {
        bind();
        component.blit(ms, x, y, startX, startY, width, height);
    }
    
    @OnlyIn(Dist.CLIENT)
    public void render(PoseStack ms, int x, int y, Color c) {
        bind();
        UIRenderHelper.drawColoredTexture(ms, c, x, y, startX, startY, width, height);
    }
    
}
