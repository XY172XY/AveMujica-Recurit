package avemujica.common.utils;

import jakarta.annotation.Resource;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
public class BloomFilterUtils {

    @Resource
    private RedissonClient redissionClient;



//    public void testBooleanFilter() {
//        try {
//            String filterName = "myBloomFilter";
//            long expectedInsertions = 100000L; // 预期元素数量
//            double falseProbability = 0.01; // 容错率，也就是误报率
//
//            // 创建布隆过滤器，参数分别为：过滤器名称，预期元素数量，误差率
//            RBloomFilter<Object> bloomFilter = redissionClient.getBloomFilter(filterName);
//            bloomFilter.tryInit(expectedInsertions, falseProbability);
//
//            // 添加元素
//            for (int i = 1; i <= 50; i++) {
//                bloomFilter.add(String.format("element%02d", i));
//            }
//
//            // 检查元素是否存在
//            System.out.println("bloomFilter.contains(\"element01\") = " + bloomFilter.contains("element01"));
//            System.out.println("bloomFilter.contains(\"element51\") = " + bloomFilter.contains("element51"));
//        } finally {
//            // 关闭 Redission 客户端
//            redissionClient.shutdown();
//        }
//    }

}

