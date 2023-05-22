package com.cm.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * JWT工具类
 */
public class JwtUtil {

    //有效期为
    public static final Long JWT_TTL = 24*60 * 60 *1000L;// 60 * 60 *1000  一个小时
    //设置秘钥明文:注意数据库中的密码都是用这个密钥的,换成自己的密钥后要更改密码
    public static final String JWT_KEY = "sangeng";

    public static String getUUID(){
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        return token;
    }
    
    /**
     * 生成jtw
     * @param subject token中要存放的数据（json格式）
     * @return
     */
    public static String createJWT(String subject) {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID());// 设置过期时间
        return builder.compact();
    }

    /**
     * 生成jtw
     * @param subject token中要存放的数据（json格式）
     * @param ttlMillis token超时时间
     * @return
     */
    public static String createJWT(String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, getUUID());// 设置过期时间
        return builder.compact();
    }

    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        if(ttlMillis==null){
            ttlMillis=JwtUtil.JWT_TTL;
        }
        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);
        return Jwts.builder()
                .setId(uuid)              //唯一的ID
                .setSubject(subject)   // 主题  可以是JSON数据,这里传来的是id
                .setIssuer("cm")     // 签发者
                .setIssuedAt(now)      // 签发时间
                .signWith(signatureAlgorithm, secretKey) //使用HS256对称加密算法签名, 第二个参数为秘钥
                .setExpiration(expDate);
    }

    /**
     * 创建token
     * @param id
     * @param subject
     * @param ttlMillis
     * @return
     */
    public static String createJWT(String id, String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, id);// 设置过期时间
        return builder.compact();
    }

    /**
     * 生成加密后的秘钥 secretKey
     * @return
     */
    public static SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }
    
    /**
     * 解析获得载荷部分
     *
     * @param jwt
     * @return
     * @throws Exception
     */
    public static Claims parseJWT(String jwt) throws Exception {
        SecretKey secretKey = generalKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
    }



    public static void main(String[] args) throws Exception {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI3NGMxZmFkOTVlNzg0MzlmOWQxMzU4YmJiNjY3ODVlNCIsInN1YiI6IjEiLCJpc3MiOiJjbSIsImlhdCI6MTY3NzQ5OTY1NSwiZXhwIjoxNjc3NTg2MDU1fQ.7Rm4FRwNXrm_jIC_o20TYcL6RZihin_EsoPHqq8L9dY";
        Claims claims = parseJWT(token);
        System.out.println(claims);
        System.out.println(claims.getSubject());
        System.out.println(claims.getIssuer());
        System.out.println("----------------");

        System.out.println("密钥:"+generalKey());
        System.out.println(generalKey());
        System.out.println("获得头部"+Jwts.parser().
                setSigningKey(generalKey()).
                parseClaimsJws(token).getHeader());
        System.out.println("获得载荷"+Jwts.parser().
                setSigningKey(generalKey()).
                parseClaimsJws(token).getBody());
        System.out.println("获得签名"+Jwts.parser().
                setSigningKey(generalKey()).
                parseClaimsJws(token).getSignature());
//        String jwt = JwtUtil.createJWT("1");
//        Claims claims = parseJWT(jwt);
//        System.out.println(claims.getSubject());
//        System.out.println(jwt);
    }


}