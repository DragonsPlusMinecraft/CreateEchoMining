# Mineral System

# 分布 Distribution

## 水平分布

由 Worley noise 生成的区域划分，每个区域随机生成一个long作为后续随机的种子。

> 设 Level Seed 为 $Seed_{level}$ ，从坐标 $(0,0)$ 开始，每 3 x 3 Chunks 作为一个 Tile，根据 $Seed_{level} + ResourceLocation_{dimension} + Chunk_x / 3 + Chunky_y / 3$ 得出一个 String，再根据这个 String 得到一个 UUID，最后通过`UUID.*fromString*(seed).getMostSignificantBits() & Long.*MAX_VALUE*`得到一个long值，这个值便是这个Area对应的$Seed_{Area}$，用$Seed_{Area}$随机出一个坐标 $(x,y)$，该坐标即为Worley Noise在该Area内的参考坐标。
> 

## 区域内可产出矿物种类

每个区域的随机矿物种类符合 1 + 随机数，该随机数符合标准差为3的正态分布。

> $Seed_{Area} + index$ 得出一个 String，再根据这个 String 得到一个 UUID，最后通过`UUID.*fromString*(seed).getMostSignificantBits() & Long.*MAX_VALUE*`得到一个long值这个值便是这个随机矿石对应的$Seed_{Ore}$
> 

## 垂直分布与储量

### $y ≤ 80$ 矿物开采数量计算 & 储量计算方式

矿物密度随高度呈线性变化，先升高至顶点再降低至0。其峰值$y_p$出现在$-64≤y≤48$，是一个随机数，当值为n时，即代表挖掘1块岩石，有可能获得n个矿物，斜率的绝对值$m$不小于$n/48$，不大于$n/12$。$1≤n≤10$。

也就是说，某高度矿物开采量 per 方块理论上为: $f(y) = n - \lvert y_p-y \rvert * m$。

实际开采时，整数部分矿物将直接获取，小数部分将`Random.nextDouble()`对比后决定是否获取。

理论上区块最大矿物总储量为 122880，最小矿物总储量为1536。

| 区块矿物总储量 | 称为 |
| --- | --- |
| ≤5k | 储量极少 |
| >5k | 储量较少 |
| >25k | 储量一般 |
| >100k | 储量丰富 |
| >500k | 储量极多 |

### $y > 80$ 矿物开采数量计算

当$y=80$且$矿物密度≥0$时，80以上高度将被视为存在这种矿物，但其矿物密度将随高度上升极快速减少。(当$y=80$时，矿物密度最大值为$n/3$)

矿物密度随高度呈线性变化，当$y=81$，矿物最大值为$f(80)/10$，斜率的绝对值$m_{>80}$不小于$n/120$，不大于$n/40$。

也就是说，某高度矿物开采量 per 方块理论上为: $f_{>80}(y) = f(80)/10 - (y-80) * m_{>80}$。

实际开采时，将`Random.nextDouble()`对比后决定是否获取。

# 处理 Processing

 TODO