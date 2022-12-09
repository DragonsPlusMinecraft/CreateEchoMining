package plus.dragons.createminingindustry.contraptions.mining.blazeminer.product.mineralcluster;

import org.jetbrains.annotations.Nullable;

public record MineralClusterDistribution(double countCoefficient, int maxCountInHeight, int spreadRange,
                                         @Nullable MineralClusterDistribution.HighAltitude highAltitudeDistribution) {
    public boolean hasPackage(int y) {
        if (y < 81)
            return maxCountInHeight + spreadRange >= y && maxCountInHeight - spreadRange <= y;
        else {
            if (highAltitudeDistribution != null) {
                return y <= 80 + highAltitudeDistribution.spreadRange;
            } else return false;
        }
    }

    public double getCount(int y) {
        if (y < 81) {
            int dif = Math.abs(maxCountInHeight - y);
            return countCoefficient * (spreadRange - dif) / spreadRange;
        } else {
            if (highAltitudeDistribution != null) {
                return highAltitudeDistribution.getCount(y);
            } else return 0;
        }
    }

    public record HighAltitude(double countCoefficient, int spreadRange){
        public double getCount(int y){
            int dif = y - 80;
            double ret = countCoefficient * (spreadRange - dif) / spreadRange;
            return ret < 0? 0: ret;
        }
    }
}
