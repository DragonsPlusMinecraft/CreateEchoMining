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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;
import plus.dragons.createminingindustry.MiningIndustry;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

import static plus.dragons.createminingindustry.MiningIndustry.REGISTRATE;

public interface CmiTags<T, P extends RegistrateTagsProvider<T>> {
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
        Arrays.stream(CmiBlockTags.values())
            .filter(CmiTags::hasDatagen)
            .forEach(tag -> REGISTRATE.addDataGenerator(ProviderType.BLOCK_TAGS, tag::datagen));
        Arrays.stream(CmiItemTags.values())
            .filter(CmiTags::hasDatagen)
            .forEach(tag -> REGISTRATE.addDataGenerator(ProviderType.ITEM_TAGS, tag::datagen));
        Arrays.stream(CmiFluidTags.values())
            .filter(CmiTags::hasDatagen)
            .forEach(tag -> REGISTRATE.addDataGenerator(ProviderType.FLUID_TAGS, tag::datagen));
    }
    
    enum CmiBlockTags implements CmiTags<Block, RegistrateTagsProvider<Block>> {
        BLAZE_SILK_TOUCH(true){
            @Override
            public void datagen(RegistrateTagsProvider<Block> pov) {
                pov.tag(tag).addTag(Tags.Blocks.ORES);
                pov.tag(tag).addTag(Tags.Blocks.GLASS);
                pov.tag(tag).addTag(Tags.Blocks.GLASS_PANES);
            }
        },
        BLAZE_RESOURCE_PACKAGE(true){
            @Override
            public void datagen(RegistrateTagsProvider<Block> pov) {
                pov.tag(tag).addTag(BlockTags.BASE_STONE_OVERWORLD);
                pov.tag(tag).addTag(BlockTags.BASE_STONE_NETHER);
                pov.tag(tag).addTag(Tags.Blocks.STONE);
                pov.tag(tag).addTag(Tags.Blocks.NETHERRACK);
                pov.tag(tag).addTag(Tags.Blocks.END_STONES);
            }
        },
        BLAZE_BURN(true){
            @Override
            public void datagen(RegistrateTagsProvider<Block> pov) {
                pov.tag(tag).addTag(BlockTags.ICE);
                pov.tag(tag).addTag(BlockTags.REPLACEABLE_PLANTS);
                pov.tag(tag).addTag(BlockTags.FLOWERS);
                pov.tag(tag).addTag(BlockTags.CROPS);
                pov.tag(tag).addTag(BlockTags.LEAVES);
                pov.tag(tag).addTag(BlockTags.SAPLINGS);
                pov.tag(tag).addTag(BlockTags.WOOL);
                pov.tag(tag).addTag(BlockTags.WOOL_CARPETS);
                pov.tag(tag).addTag(BlockTags.PLANKS);
                pov.tag(tag).addTag(BlockTags.WOODEN_FENCES);
                pov.tag(tag).addTag(BlockTags.WOODEN_BUTTONS);
                pov.tag(tag).addTag(BlockTags.WOODEN_DOORS);
                pov.tag(tag).addTag(BlockTags.WOODEN_STAIRS);
                pov.tag(tag).addTag(BlockTags.WOODEN_TRAPDOORS);
                pov.tag(tag).addTag(BlockTags.WOODEN_PRESSURE_PLATES);
                pov.tag(tag).addTag(BlockTags.SIGNS);
                pov.tag(tag).addTag(BlockTags.LOGS);
            }
        },
        BLAZE_IGNORE(true){
            @Override
            public void datagen(RegistrateTagsProvider<Block> pov) {
                pov.tag(tag).add(Blocks.BEDROCK);
            }
        };

        
        final TagKey<Block> tag;
        final boolean datagen;
        
        CmiBlockTags(String namespace, boolean datagen) {
            this.tag = BLOCK_TAGS.createTagKey(new ResourceLocation(namespace, toTagName(name())));
            this.datagen = datagen;
        }
        
        CmiBlockTags(boolean datagen) {
            this(MiningIndustry.ID, datagen);
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
    
    enum CmiItemTags implements CmiTags<Item, RegistrateItemTagsProvider> {
        UPRIGHT_ON_BELT(CREATE, true) {
            @Override
            public void datagen(RegistrateItemTagsProvider pov) {
                // TODO If no use then delete it.
                //pov.tag(tag).add(Items.EXPERIENCE_BOTTLE);
            }
        },
        RESOURCE_PACKAGE_ITEM(true){
            @Override
            public void datagen(RegistrateItemTagsProvider pov) {
                //pov.tag(tag).addTag(Tags.Blocks.ORES);
            }
        };
        
        final TagKey<Item> tag;
        final boolean datagen;
    
        CmiItemTags(String namespace, boolean datagen) {
            this.tag = ITEM_TAGS.createTagKey(new ResourceLocation(namespace, toTagName(name())));
            this.datagen = datagen;
        }
    
        CmiItemTags(boolean datagen) {
            this(MiningIndustry.ID, datagen);
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
    
    enum CmiFluidTags implements CmiTags<Fluid, RegistrateTagsProvider<Fluid>> {
        //No experience fluid tag here as different ratios is not acceptable
        BLAZE_COLLECTIBLE( true) {
            @Override
            public void datagen(RegistrateTagsProvider<Fluid> pov) {
                pov.tag(tag).add(Fluids.LAVA.getSource());
            }
        },
        RESOURCE_PACKAGE_FLUID( true) {
            @Override
            public void datagen(RegistrateTagsProvider<Fluid> pov) {
                pov.tag(tag).add(Fluids.LAVA.getSource());
            }
        };
        
        final TagKey<Fluid> tag;
        final boolean datagen;
    
        CmiFluidTags(String namespace, boolean datagen) {
            this.tag = FLUID_TAGS.createTagKey(new ResourceLocation(namespace, toTagName(name())));
            this.datagen = datagen;
        }
    
        CmiFluidTags(boolean datagen) {
            this(MiningIndustry.ID, datagen);
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
