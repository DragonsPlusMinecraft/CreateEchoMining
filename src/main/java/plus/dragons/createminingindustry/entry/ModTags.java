package plus.dragons.createminingindustry.entry;

import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateItemTagsProvider;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;
import plus.dragons.createminingindustry.MiningIndustry;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public interface ModTags<T, P extends RegistrateTagsProvider<T>> {
    ITagManager<Block> BLOCK_TAGS = Objects.requireNonNull(ForgeRegistries.BLOCKS.tags());
    ITagManager<Item> ITEM_TAGS = Objects.requireNonNull(ForgeRegistries.ITEMS.tags());
    ITagManager<Fluid> FLUID_TAGS = Objects.requireNonNull(ForgeRegistries.FLUIDS.tags());
    String FORGE = "forge";
    String CREATE = "create";
    
    TagKey<T> tag();
    
    boolean hasDatagen();
    
    default void datagen(P pov) {
        //NO-OP
    }
    
    static String toTagName(String enumName) {
        return enumName.replace('$', '/').toLowerCase(Locale.ROOT);
    }
    
    static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> axeOrPickaxe() {
        return b -> b.tag(BlockTags.MINEABLE_WITH_AXE).tag(BlockTags.MINEABLE_WITH_PICKAXE);
    }
    
    static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> axeOnly() {
        return b -> b.tag(BlockTags.MINEABLE_WITH_AXE);
    }
    
    static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, BlockBuilder<T, P>> pickaxeOnly() {
        return b -> b.tag(BlockTags.MINEABLE_WITH_PICKAXE);
    }
    
    static <T extends Block, P> NonNullFunction<BlockBuilder<T, P>, ItemBuilder<BlockItem, BlockBuilder<T, P>>> tagBlockAndItem(String namespace, String... paths) {
        return block -> {
            ItemBuilder<BlockItem, BlockBuilder<T, P>> item = block.item();
            for (String path : paths) {
                block.tag(BLOCK_TAGS.createTagKey(new ResourceLocation(namespace, path)));
                item.tag(ITEM_TAGS.createTagKey(new ResourceLocation(namespace, path)));
            }
            return item;
        };
    }
    
    static void register() {
        CreateRegistrate registrate = MiningIndustry.registrate();
        Arrays.stream(ModBlockTags.values())
            .filter(ModTags::hasDatagen)
            .forEach(tag -> registrate.addDataGenerator(ProviderType.BLOCK_TAGS, tag::datagen));
        Arrays.stream(ModItemTags.values())
            .filter(ModTags::hasDatagen)
            .forEach(tag -> registrate.addDataGenerator(ProviderType.ITEM_TAGS, tag::datagen));
        Arrays.stream(ModFluidTags.values())
            .filter(ModTags::hasDatagen)
            .forEach(tag -> registrate.addDataGenerator(ProviderType.FLUID_TAGS, tag::datagen));
    }
    
    enum ModBlockTags implements ModTags<Block, RegistrateTagsProvider<Block>> {
        BLAZE_MINE_SILK_TOUCH(true){
            @Override
            public void datagen(RegistrateTagsProvider<Block> pov) {
                //pov.tag(tag).add();
            }
        },
        BLAZE_MINE_RESOURCE_PACKAGE(true){
            @Override
            public void datagen(RegistrateTagsProvider<Block> pov) {
                //pov.tag(tag).add();
            }
        },
        BLAZE_BURN(true){
            @Override
            public void datagen(RegistrateTagsProvider<Block> pov) {
                //pov.tag(tag).add();
            }
        };

        
        final TagKey<Block> tag;
        final boolean datagen;
        
        ModBlockTags(String namespace, boolean datagen) {
            this.tag = BLOCK_TAGS.createTagKey(new ResourceLocation(namespace, toTagName(name())));
            this.datagen = datagen;
        }
        
        ModBlockTags(boolean datagen) {
            this(MiningIndustry.MOD_ID, datagen);
        }
    
        @Override
        public TagKey<Block> tag() {
            return tag;
        }
    
        @Override
        public boolean hasDatagen() {
            return datagen;
        }
    }
    
    enum ModItemTags implements ModTags<Item, RegistrateItemTagsProvider> {
        UPRIGHT_ON_BELT(CREATE, true) {
            @Override
            public void datagen(RegistrateItemTagsProvider pov) {
                // TODO If no use then delete it.
                //pov.tag(tag).add(Items.EXPERIENCE_BOTTLE);
            }
        };
        
        final TagKey<Item> tag;
        final boolean datagen;
    
        ModItemTags(String namespace, boolean datagen) {
            this.tag = ITEM_TAGS.createTagKey(new ResourceLocation(namespace, toTagName(name())));
            this.datagen = datagen;
        }
    
        ModItemTags(boolean datagen) {
            this(MiningIndustry.MOD_ID, datagen);
        }
    
        @Override
        public TagKey<Item> tag() {
            return tag;
        }
    
        @Override
        public boolean hasDatagen() {
            return datagen;
        }
    }
    
    enum ModFluidTags implements ModTags<Fluid, RegistrateTagsProvider<Fluid>> {
        //No experience fluid tag here as different ratios is not acceptable
        BLAZE_COLLECTABLE( false);
        
        final TagKey<Fluid> tag;
        final boolean datagen;
    
        ModFluidTags(String namespace, boolean datagen) {
            this.tag = FLUID_TAGS.createTagKey(new ResourceLocation(namespace, toTagName(name())));
            this.datagen = datagen;
        }
    
        ModFluidTags(boolean datagen) {
            this(MiningIndustry.MOD_ID, datagen);
        }
    
        @Override
        public TagKey<Fluid> tag() {
            return tag;
        }
    
        @Override
        public boolean hasDatagen() {
            return datagen;
        }
    }
    
}
