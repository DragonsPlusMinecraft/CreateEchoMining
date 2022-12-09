package plus.dragons.createminingindustry.contraptions.mining.blazeminer.product.mineralcluster;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MineralClusterContentSyncPacket extends SimplePacketBase {
    private final List<ItemStack> itemStacks;

    public MineralClusterContentSyncPacket(List<ItemStack> itemStacks) {
        this.itemStacks = itemStacks;
    }

    public MineralClusterContentSyncPacket(FriendlyByteBuf buffer) {
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
        context.get().enqueueWork(() -> MineralClusterContentGeneration.CONTENTS = itemStacks);
        context.get().setPacketHandled(true);
    }
}
