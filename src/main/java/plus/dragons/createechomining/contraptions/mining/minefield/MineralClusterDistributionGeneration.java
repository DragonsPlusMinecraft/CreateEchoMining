package plus.dragons.createechomining.contraptions.mining.minefield;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class MineralClusterDistributionGeneration {
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

    private static final LoadingCache<Long, List<Long>> MINERAL_SEEDS_CACHE = CacheBuilder.newBuilder()
            .initialCapacity(10)
            .maximumSize(500)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<>() {
                        @Override
                        public @NotNull List<Long> load(@NotNull Long areaSeed) {
                            return computeMineralSeed(areaSeed);
                        }
                    });

    private static final LoadingCache<Long, MineralDistribution> MINERAL_DISTRIBUTION_CACHE = CacheBuilder.newBuilder()
            .initialCapacity(10)
            .maximumSize(500)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<>() {
                        @Override
                        public @NotNull MineralDistribution load(@NotNull Long mineralSeed) {
                            return computeDistribution(mineralSeed);
                        }
                    });

    @SuppressWarnings("all")
    public static List<Long> getMineralSeeds(ServerLevel level, BlockPos blockPos) {
        var areaSeed = computeAreaSeedForPos(level,blockPos);
        try{
            return MINERAL_SEEDS_CACHE.get(areaSeed);
        } catch(ExecutionException ignored){
            // I don't believe there'll be exception. it's impossible.
        }
        return null;
    }

    @SuppressWarnings("all")
    public static MineralDistribution getDistribution(long mineralSeed) {
        try{
            return MINERAL_DISTRIBUTION_CACHE.get(mineralSeed);
        } catch(ExecutionException ignored){
            // I don't believe there'll be exception. it's impossible.
        }
        return null;
    }

    public static Map<Long,Integer> genAmountsOfAllMineralCluster(ServerLevel level, BlockPos blockPos, RandomSource random){
        Map<Long,Integer> ret = new HashMap<>();
        var packageSeeds = getMineralSeeds(level,blockPos);
        var y = blockPos.getY();
        for(var p:packageSeeds){
            var dist = getDistribution(p);
            if(dist.hasPackage(y)){
                var count = dist.getCount(y);
                int a = (int) Math.floor(count);
                double b = count - a;
                boolean c = random.nextDouble() < b;
                if(a!=0||c){
                    ret.put(p,c?a+1:a);
                }
            }
        }
        return ret;
    }

    private static long computeAreaSeedForPos(ServerLevel level, BlockPos blockPos) {
        var tiles = Tile.all9(Tile.of(level.dimension().registry(), level.getSeed(), XYChunk.ofPos(blockPos)));
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

    private static TileWorleyNoisePointInfo computeWorleyPosForTile(Tile tile){
        String seed = tile.dimensionId + tile.levelSeed + tile.x + tile.y;
        long longSeed = UUID.nameUUIDFromBytes(seed.getBytes(StandardCharsets.UTF_8)).getMostSignificantBits() & Long.MAX_VALUE;
        var random = new Random(longSeed);
        var x = random.nextInt(80) + tile.x * 80;
        var y = random.nextInt(80) + tile.y * 80;
        return new TileWorleyNoisePointInfo(x,y,longSeed);
    }

    private static List<Long> computeMineralSeed(long areaSeed) {
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

    private static MineralDistribution computeDistribution(long mineralSeed) {
        var random = new Random(mineralSeed);
        int peak = (int) (random.nextDouble() * 112 - 64);
        double co = 1 + random.nextDouble() * 9;
        int spread = (int) (random.nextDouble() * 36 + 12);
        MineralDistribution.InYMoreThan80 InYMoreThan80Distribution = null;
        if(peak+spread>=80){
            int spread2 = (int) (random.nextDouble() * 80 + 40);
            int dif = 80 - peak;
            double co2 = co / spread * (spread - dif);
            InYMoreThan80Distribution = new MineralDistribution.InYMoreThan80(co2,spread2);
        }
        return new MineralDistribution(co,peak,spread, InYMoreThan80Distribution);
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


}
