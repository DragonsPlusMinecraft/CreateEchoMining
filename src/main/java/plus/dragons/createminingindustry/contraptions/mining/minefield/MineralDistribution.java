package plus.dragons.createminingindustry.contraptions.mining.minefield;

import org.jetbrains.annotations.Nullable;

public record MineralDistribution(double countCoefficient, int maxCountInHeight, int spreadRange,
                                  @Nullable MineralDistribution.InYMoreThan80 inYMoreThan80) {
    public boolean hasPackage(int y) {
        if (y < 81)
            return maxCountInHeight + spreadRange >= y && maxCountInHeight - spreadRange <= y;
        else {
            if (inYMoreThan80 != null) {
                return y <= 80 + inYMoreThan80.spreadRange;
            } else return false;
        }
    }

    public double getCount(int y) {
        if (y < 81) {
            int dif = Math.abs(maxCountInHeight - y);
            return countCoefficient * (spreadRange - dif) / spreadRange;
        } else {
            if (inYMoreThan80 != null) {
                return inYMoreThan80.getCount(y);
            } else return 0;
        }
    }

    public record InYMoreThan80(double countCoefficient, int spreadRange){
        public double getCount(int y){
            int dif = y - 80;
            double ret = countCoefficient * (spreadRange - dif) / spreadRange;
            return ret < 0? 0: ret;
        }
    }
}
