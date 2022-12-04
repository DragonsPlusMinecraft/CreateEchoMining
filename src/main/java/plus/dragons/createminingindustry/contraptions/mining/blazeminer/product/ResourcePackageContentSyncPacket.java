package plus.dragons.createminingindustry.contraptions.mining.blazeminer.product;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ResourcePackageContentSyncPacket extends SimplePacketBase {

    private final List<ItemStack> itemStacks;

    public ResourcePackageContentSyncPacket(List<ItemStack> itemStacks) {
        this.itemStacks = itemStacks;
    }

    public ResourcePackageContentSyncPacket(FriendlyByteBuf buffer) {
        var size = buffer.readInt();
        this.itemStacks = new ArrayList<>();
        for(int i=0;i<size;i++){
            this.itemStacks.add(buffer.readItem());
        }
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(itemStacks.size());
        itemStacks.forEach(itemStack -> buffer.writeItemStack(itemStack,false));
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ResourcePackageContentGeneration.CONTENTS = itemStacks);
        context.get().setPacketHandled(true);
    }
}
