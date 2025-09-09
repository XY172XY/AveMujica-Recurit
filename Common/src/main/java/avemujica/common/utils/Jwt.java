package avemujica.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Jwt {

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    FlowUtils flowUtils;

    //用于给Jwt令牌签名校验的秘钥
    @Value("${spring.security.jwt.key}")
    private String key;
    //令牌的过期时间，以小时为单位
    @Value("${spring.security.jwt.expire}")
    private int expire;
    //为用户生成Jwt令牌的冷却时间，防止刷接口频繁登录生成令牌，以秒为单位
    @Value("${spring.security.jwt.limit.base}")
    private int limit_base;
    //用户如果继续恶意刷令牌，更严厉的封禁时间
    @Value("${spring.security.jwt.limit.upgrade}")
    private int limit_upgrade;
    //判定用户在冷却时间内，继续恶意刷令牌的次数
    @Value("${spring.security.jwt.limit.frequency}")
    private int limit_frequency;


    //生产Jwt
    public String createJwt(UserDetails userDetails,String username,int userId) {
        if(this.frequencyCheck(userId)) {
            Algorithm algorithm = Algorithm.HMAC256(key);
            Date expireTime = this.expireTime();
            return JWT.create()
                    .withJWTId(UUID.randomUUID().toString())
                    .withClaim("id", userId)
                    .withClaim("name", username)
                    .withClaim("authorities", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                    .withExpiresAt(expireTime)
                    .withIssuedAt(new Date())
                    .sign(algorithm);
        }
        else{
            return null;
        }
    }

    //解析Jwt
    public DecodedJWT verifyJwt(String jwt) {
        String token = this.convertToken(jwt);
        if(token==null){
            return null;
        }
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try{
            DecodedJWT decodedJWT = verifier.verify(token);
            if(this.isInvalid(decodedJWT.getId())){
                return null;
            }
            if(this.inBlackList(decodedJWT.getClaim("id").asInt())){
                return null;
            }
            return new Date().after(decodedJWT.getExpiresAt()) ? null : decodedJWT;
        }
        catch (JWTVerificationException e){
            return null;
        }
    }

    //给Security使用,将Jwt中数据转为UserDetails
    public UserDetails toUserDetails(DecodedJWT jwt) {
        Map<String, Claim> claims = jwt.getClaims();
        return User
                .withUsername(claims.get("name").asString())
                .password("******")
                .authorities(claims.get("authorities").asArray(String.class))
                .build();
    }

    //给Security使用,将Jwt中数据转为Id
    public Integer toId(DecodedJWT jwt) {
        return jwt.getClaim("id").asInt();
    }

    //下面为辅助函数
    //计算过期时间，会给Security使用故置为public
    public Date expireTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, expire);
        return calendar.getTime();
    }

    //辅助限流
    private boolean frequencyCheck(int userId){
        String key = Const.JWT_FREQUENCY + userId;
        return flowUtils.limitUpgradeCheck(key,limit_frequency,limit_base,limit_upgrade);
    }

    //jwt初步处理
    private String convertToken(String jwt) {
        if(jwt==null || !jwt.startsWith("Bearer ")){
            return null;
        }
        return jwt.substring(7);
    }

    //退出登录 == 将jwt加入黑名单 时效为jwt剩余有效时间
    private boolean deleteJwt(String uuid,Date timeLast){
        if(isInvalid(uuid)){
            return false;
        }
        Date now = new Date();
        long expire = Math.max(timeLast.getTime()-now.getTime(),0);
        stringRedisTemplate.opsForValue().set(Const.JWT_BLACK_LIST + uuid,"",expire, TimeUnit.HOURS);
        return true;
    }

    private boolean inBlackList(int uid) {
        return stringRedisTemplate.hasKey(Const.USER_BLACK_LIST + uid);
    }

    private boolean isInvalid(String uuid) {
        return stringRedisTemplate.hasKey(Const.JWT_BLACK_LIST + uuid);
    }

}
