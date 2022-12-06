package plus.dragons.createminingindustry.contraptions.mining.blazeminer.product;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import plus.dragons.createminingindustry.entry.CmiPackets;
import plus.dragons.createminingindustry.entry.CmiTags;

import java.util.ArrayList;
import java.util.List;

public class ResourcePackageContentGeneration {
    static List<ItemStack> CONTENTS = new ArrayList<>();

    public static void registerResourcePackage(FMLCommonSetupEvent event){
        // Collect all Items from tags
        event.enqueueWork(()->{
            for(var item:ForgeRegistries.ITEMS.getValues()){
                var i = item.getDefaultInstance();
                if(i.is(CmiTags.CmiItemTags.RESOURCE_PACKAGE_ITEM.tag()))
                    CONTENTS.add(i);
            }
            for(var fluid:ForgeRegistries.FLUIDS.getValues()){
                if(fluid.defaultFluidState().is(CmiTags.CmiFluidTags.RESOURCE_PACKAGE_FLUID.tag()))
                    CONTENTS.add(BlazeFluidHolderItem.ofFluid(fluid));
            }
        });
    }

    public static void syncResourcePackageToClient(PlayerEvent.PlayerLoggedInEvent event){
        CmiPackets.channel.send(PacketDistributor.PLAYER.with(()-> (ServerPlayer) event.getEntity()),
                new ResourcePackageContentSyncPacket(CONTENTS));
    }

}
