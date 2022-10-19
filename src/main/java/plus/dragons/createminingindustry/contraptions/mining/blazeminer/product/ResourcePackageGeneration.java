package plus.dragons.createminingindustry.contraptions.mining.blazeminer.product;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


@Mod.EventBusSubscriber()
class ResourcePackageGeneration {
    private static final LoadingCache<Tile, TileWorleyNoisePointInfo> WORLEY_NOISE_TILE_POS_CACHE = CacheBuilder.newBuilder()
            .initialCapacity(10)
            .maximumSize(500)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<>() {
                        @Override
                        public @NotNull TileWorleyNoisePointInfo load(@NotNull Tile tile) {
                            return computeWorleyPosForTile(tile);
                        }
                    });

    private static final LoadingCache<Long, List<Long>> PACKAGE_SEEDS_CACHE = CacheBuilder.newBuilder()
            .initialCapacity(10)
            .maximumSize(500)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<>() {
                        @Override
                        public @NotNull List<Long> load(@NotNull Long areaSeed) {
                            return computeOreSeed(areaSeed);
                        }
                    });

    private static final LoadingCache<Long, PackageDistribution> PACKAGE_DISTRIBUTION_INFO_CACHE = CacheBuilder.newBuilder()
            .initialCapacity(10)
            .maximumSize(500)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<>() {
                        @Override
                        public @NotNull PackageDistribution load(@NotNull Long packageSeed) {
                            return computePackageDistribution(packageSeed);
                        }
                    });

    public static List<Long> getAreaPackageSeeds(ServerLevel level, BlockPos blockPos) {
        var areaSeed = getAreaSeedForPos(level,blockPos);
        try{
            return PACKAGE_SEEDS_CACHE.get(areaSeed);
        } catch(ExecutionException ignored){
            // I don't believe there'll be exception. it's impossible.
        }
        return null;
    }

    public static PackageDistribution getPackageDistribution(long packageSeed) {
        try{
            return PACKAGE_DISTRIBUTION_INFO_CACHE.get(packageSeed);
        } catch(ExecutionException ignored){
            // I don't believe there'll be exception. it's impossible.
        }
        return null;
    }

    private static long getAreaSeedForPos(ServerLevel level, BlockPos blockPos) {
        var tiles = Tile.all9(Tile.of(level.dimension().getRegistryName(), level.getSeed(), XYChunk.ofPos(blockPos)));
        var pointInfos = new ArrayList<TileWorleyNoisePointInfo>();
        try{
            for(var tile:tiles){
                pointInfos.add(WORLEY_NOISE_TILE_POS_CACHE.get(tile));
            }
        } catch(ExecutionException ignored){
            // I don't believe there'll be exception. it's impossible.
        }
        var result = pointInfos.stream().max((p1,p2)-> {
            var diff = Math.sqrt(Math.pow((blockPos.getX()-p1.x),2) + Math.pow((blockPos.getY()-p1.y),2) - Math.sqrt(Math.pow((blockPos.getX()-p2.x),2) + Math.pow((blockPos.getY()-p2.y),2)));
            if(diff<0) return (int) Math.floor(diff);
            else return (int) Math.ceil(diff);
        }).get();
        return result.tileSeed;
    }

    // TODO test only
    @SubscribeEvent
    public static void testMethod(TickEvent.PlayerTickEvent event) {
        /*if(!event.player.level.isClientSide() && event.phase == TickEvent.Phase.START && event.player.level.getDayTime() % 20 == 0){
            var level = (ServerLevel) event.player.level;
            var pos = event.player.blockPosition();
            System.out.println("Area Seed: " + getAreaSeedForPos(level,pos) + ". This area has " + getAreaOreSeeds(level,pos).oreSeeds.size() + " kind of ore.");
            System.out.println("Ore Seeds: " + getAreaOreSeeds(level,pos));
        }*/
    }

    private static TileWorleyNoisePointInfo computeWorleyPosForTile(Tile tile){
        String seed = tile.dimensionId + tile.levelSeed + tile.x + tile.y;
        long longSeed = UUID.nameUUIDFromBytes(seed.getBytes(StandardCharsets.UTF_8)).getMostSignificantBits() & Long.MAX_VALUE;
        var random = new Random(longSeed);
        var x = random.nextInt(80) + tile.x * 80;
        var y = random.nextInt(80) + tile.y * 80;
        return new TileWorleyNoisePointInfo(x,y,longSeed);
    }

    private static List<Long> computeOreSeed(long areaSeed) {
        var random = new Random(areaSeed);
        var count =  (int) Math.floor(Math.abs(random.nextGaussian(0,3))) + 1;
        var ret = new ArrayList<Long>();
        for(int i = 0; i < count; i++){
            var s = String.valueOf(areaSeed) + i;
            long oreSeed = UUID.nameUUIDFromBytes(s.getBytes(StandardCharsets.UTF_8)).getMostSignificantBits() & Long.MAX_VALUE;
            ret.add(oreSeed);
        }
        return ret;
    }

    private static PackageDistribution computePackageDistribution(long packageSeed) {
        var random = new Random(packageSeed);
        int peak = (int) (random.nextDouble() * 112 - 64);
        double co = 1 + random.nextDouble() * 9;
        int spread = (int) (random.nextDouble() * 36 + 12);
        PackageHighAltitudeDistribution highAltitudeDistribution = null;
        if(peak+spread>=80){
            int spread2 = (int) (random.nextDouble() * 80 + 40);
            int dif = 80 - peak;
            double co2 = co / spread * (spread - dif);
            highAltitudeDistribution = new PackageHighAltitudeDistribution(co2,spread2);
        }
        return new PackageDistribution(co,peak,spread,highAltitudeDistribution);
    }

    record XYChunk(int x, int y){
        public static XYChunk ofPos(BlockPos blockPos) {
            return new XYChunk((int) Math.floor(blockPos.getX()/16.0), (int) Math.floor(blockPos.getY()/16.0));
        }
    };

    record Tile(int x, int y, String dimensionId, long levelSeed){
        public static Tile of(ResourceLocation dimensionId, long levelSeed, XYChunk xyChunk) {
            return new Tile((int) Math.floor(xyChunk.x/5.0), (int) Math.floor(xyChunk.y/5.0),dimensionId.getNamespace(),levelSeed);
        }

        public static List<Tile> all9(Tile tile) {
            var ret = new ArrayList<Tile>();
            for(int i=-1;i<2;i++)
                for(int j=-1;j<2;j++)
                    ret.add(new Tile(tile.x+i,tile.y+j,tile.dimensionId,tile.levelSeed));
            return ret;
        }
    }

    record TileWorleyNoisePointInfo(int x, int y, long tileSeed){ }

    public record PackageDistribution(double countCoefficient, int maxCountInHeight, int spreadRange, @Nullable PackageHighAltitudeDistribution highAltitudeDistribution){
        public boolean hasPackage(int y){
            if(y<81)
                return maxCountInHeight + spreadRange >= y && maxCountInHeight - spreadRange <= y;
            else{
                if(highAltitudeDistribution!=null){
                    return y <= 80 + highAltitudeDistribution.spreadRange;
                }
                else return false;
            }
        }

        public double getCount(int y){
            if(y<81) {
                int dif = Math.abs(maxCountInHeight - y);
                return countCoefficient * (spreadRange - dif) / spreadRange;
            }
            else{
                if(highAltitudeDistribution!=null){
                    return highAltitudeDistribution.getCount(y);
                }
                else return 0;
            }
        }
    }

    public record PackageHighAltitudeDistribution(double countCoefficient, int spreadRange){
        public double getCount(int y){
            int dif = y - 80;
            double ret = countCoefficient * (spreadRange - dif) / spreadRange;
            return ret < 0? 0: ret;
        }
    }



}
