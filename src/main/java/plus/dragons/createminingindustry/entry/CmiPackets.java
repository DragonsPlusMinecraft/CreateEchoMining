package plus.dragons.createminingindustry.entry;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import plus.dragons.createminingindustry.MiningIndustry;
import plus.dragons.createminingindustry.contraptions.mining.blazeminer.product.mineralcluster.MineralClusterContentSyncPacket;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public enum CmiPackets {

    // Server to Client
    RESOURCE_PACKAGE_CONTENT_SYNC(MineralClusterContentSyncPacket.class, MineralClusterContentSyncPacket::new, NetworkDirection.PLAY_TO_CLIENT);

    public static final ResourceLocation CHANNEL_NAME = MiningIndustry.genRL("main");
    public static final int NETWORK_VERSION = 1;
    public static final String NETWORK_VERSION_STR = String.valueOf(NETWORK_VERSION);
    public static SimpleChannel channel;

    private CmiPackets.LoadedPacket<?> packet;

    <T extends SimplePacketBase> CmiPackets(Class<T> type, Function<FriendlyByteBuf, T> factory,
                                            NetworkDirection direction) {
        packet = new LoadedPacket<>(type, factory, direction);
    }

    public static void registerPackets() {
        channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
                .serverAcceptedVersions(NETWORK_VERSION_STR::equals)
                .clientAcceptedVersions(NETWORK_VERSION_STR::equals)
                .networkProtocolVersion(() -> NETWORK_VERSION_STR)
                .simpleChannel();
        for (CmiPackets packet : values())
            packet.packet.register();
    }

    public static void sendToNear(Level world, BlockPos pos, int range, Object message) {
        channel.send(
                PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(pos.getX(), pos.getY(), pos.getZ(), range, world.dimension())),
                message);
    }

    private static class LoadedPacket<T extends SimplePacketBase> {
        private static int index = 0;

        private BiConsumer<T, FriendlyByteBuf> encoder;
        private Function<FriendlyByteBuf, T> decoder;
        private BiConsumer<T, Supplier<NetworkEvent.Context>> handler;
        private Class<T> type;
        private NetworkDirection direction;

        private LoadedPacket(Class<T> type, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
            encoder = T::write;
            decoder = factory;
            handler = T::handle;
            this.type = type;
            this.direction = direction;
        }

        private void register() {
            channel.messageBuilder(type, index++, direction)
                    .encoder(encoder)
                    .decoder(decoder)
                    .consumerNetworkThread(handler)
                    .add();
        }
    }
}
