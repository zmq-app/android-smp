package com.itheima.utils;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * @Subject 字符串DES对称加密,配合Base64编码
 * @Author zhangming
 * @Date 2018-09-14 15:02
 */
public class DESUtils {
    public final static String key = "ChatRoomAPP";

    /** Java/Android要使用任何加密,都需要使用Cipher这个类 **/
    public static String encryptPassword(String data) {
        String encodeStr = "";
        try {
            //获取Cipher对象,设置加密算法为对称加密DES
            Cipher cipher = Cipher.getInstance("DES");

            //使用工厂设计模式,生成Key类的子类SecretKey的对象secretKey [Key类是Java加密系统所有密码的父类]
            DESKeySpec keySpec = new DESKeySpec(key.getBytes());
            SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = factory.generateSecret(keySpec);

            //设置Cipher模式,加密or解密; 使用密钥为secretKey
            cipher.init(Cipher.ENCRYPT_MODE,secretKey);
            //加密数据data,并返回加密结果byte数组
            byte[] encodeBytes = cipher.doFinal(data.getBytes());
            //配合Base64编码
            encodeStr = Base64.encodeToString(encodeBytes,Base64.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
        }
        return encodeStr;
    }

    public static String decryptPassword(String data){
        String decodeStr = "";
        try{
            Cipher cipher = Cipher.getInstance("DES");

            //获取DES对称加密的密钥
            DESKeySpec keySpec = new DESKeySpec(key.getBytes());
            SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = factory.generateSecret(keySpec);

            //设置Cipher模式,加密or解密; 使用密钥为secretKey
            cipher.init(Cipher.DECRYPT_MODE,secretKey);
            byte[] encodeWithoutBase64 = Base64.decode(data,Base64.DEFAULT);
            byte[] decodeBytes = cipher.doFinal(encodeWithoutBase64);
            decodeStr = new String(decodeBytes);
        }catch (Exception e){
            e.printStackTrace();
        }
        return decodeStr;
    }
}
