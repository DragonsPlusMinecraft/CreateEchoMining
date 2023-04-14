package plus.dragons.createminingindustry.entry;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fluids.FluidAttributes;

public class ModFluids {

    /*public static final ResourceLocation HYPER_EXPERIENCE_STILL_RL = EnchantmentIndustry.genRL("fluid/hyper_experience_still");
    public static final ResourceLocation HYPER_EXPERIENCE_FLOW_RL = EnchantmentIndustry.genRL("fluid/hyper_experience_flow");

    public static final FluidEntry<VirtualFluid> HYPER_EXPERIENCE =
            EnchantmentIndustry.registrate().virtualFluid("hyper_experience", HYPER_EXPERIENCE_STILL_RL, HYPER_EXPERIENCE_FLOW_RL)
                    .lang("Liquid Hyper Experience")
                    .attributes(builder -> builder.luminosity(15))
                    .register();
    
    public static final ResourceLocation INK_STILL_RL = EnchantmentIndustry.genRL("fluid/ink_still");
    public static final ResourceLocation INK_FLOW_RL = EnchantmentIndustry.genRL("fluid/ink_flow");
    
    public static final FluidEntry<ForgeFlowingFluid.Flowing> INK =
            EnchantmentIndustry.registrate().fluid("ink", INK_STILL_RL, INK_FLOW_RL, NoColorFluidAttributes::new)
                    .attributes(b -> b.viscosity(1000)
                            .density(1000))
                    .properties(p -> p.levelDecreasePerBlock(2)
                            .tickRate(25)
                            .slopeFindDistance(4)
                            .explosionResistance(100f))
                    .source(ForgeFlowingFluid.Source::new) // TODO: remove when Registrate fixes FluidBuilder
                    .tag(ModTags.ModFluidTags.INK.tag)
                    .bucket()
                    .build()
                    .register();*/

    public static void register() {
    }
    
    /**
     * Removing alpha from tint prevents optifine from forcibly applying biome
     * colors to modded fluids (Makes translucent fluids disappear)
     */
    private static class NoColorFluidAttributes extends FluidAttributes {

        protected NoColorFluidAttributes(Builder builder, Fluid fluid) {
            super(builder, fluid);
        }

        @Override
        public int getColor(BlockAndTintGetter world, BlockPos pos) {
            return 0x00ffffff;
        }

    }
}
