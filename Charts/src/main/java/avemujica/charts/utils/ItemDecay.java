package avemujica.charts.utils;

import org.springframework.stereotype.Component;

@Component
public class ItemDecay {
    public static class Params {
        public double k0 = 20.0;     // 热度拐点,答题人数=k0时分数下降到一半
        public double alpha = 1.0;    // 热度斜率,衰减得更狠还是更平缓
        public double wSigma = 0.5;   // 难度中 σ 权重 决定标准差影响大小
        public double wMu = 0.5;      // 难度中 (1-μ) 权重 决定平均分影响大小
        public double gamma = 0.5;    // R 与 H 的权重 更看人气还是更看难度
        public double dMin = 0.2;     // 衰减下限
        public double sigmaMax = 0.5; // 分数已归一化到[0,1]时的最大σ
    }

    public static double decay(double mu, double sigma, int k, Params p) {
        //人气因子 R(k)
        double R = 1.0 / (1.0 + Math.pow(k / Math.max(1.0, p.k0), p.alpha));
        R = clamp(R, 0.0, 1.0);

        //难度因子 H(mu, sigma)
        double s = clamp(sigma / p.sigmaMax, 0.0, 1.0);
        double H = clamp(p.wSigma * s + p.wMu * (1.0 - mu), 0.0, 1.0);

        //合并为保留率 K（几何混合）
        double K = Math.pow(R, p.gamma) * Math.pow(H, 1.0 - p.gamma);

        //衰减系数
        return p.dMin + (1.0 - p.dMin) * K;
    }

    private static double clamp(double x, double lo, double hi) {
        return Math.max(lo, Math.min(hi, x));
    }
}

