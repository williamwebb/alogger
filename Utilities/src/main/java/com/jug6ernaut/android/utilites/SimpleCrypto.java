package com.jug6ernaut.android.utilites;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * Usage:
 * <pre>
 * String crypto = SimpleCrypto.encrypt(masterpassword, cleartext)
 * ...
 * String cleartext = SimpleCrypto.decrypt(masterpassword, crypto)
 * </pre>
 * @author ferenc.hechler
 */
public class SimpleCrypto {

        public static String encrypt(String seed, String cleartext){
        	byte[] rawKey = null;
            byte[] result = null;
        	try{
                rawKey = getRawKey(seed.getBytes());
                result = encrypt(rawKey, cleartext.getBytes());
               
        	}catch(Exception e){e.printStackTrace();};
        	 return toHex(result);
        }
        
        public static String decrypt(String seed, String encrypted){
        	byte[] rawKey = null;
            byte[] enc = null;
            byte[] result = null;
            try{
            rawKey = getRawKey(seed.getBytes());
            enc = toByte(encrypted);
            result = decrypt(rawKey, enc);
            }catch(Exception e){e.printStackTrace();}
            
            if(result!=null)return new String(result);
            else return "NULL";
        }

        private static byte[] getRawKey(byte[] seed){
        	KeyGenerator kgen = null;
            SecureRandom sr = null;
            try{
            kgen = KeyGenerator.getInstance("AES");
            sr = SecureRandom.getInstance( "SHA1PRNG", "Crypto" );
            }catch(Exception e){e.printStackTrace();}
                sr.setSeed(seed);
            kgen.init(128, sr); // 192 and 256 bits may not be available
            SecretKey skey = kgen.generateKey();
            byte[] raw = skey.getEncoded();
            return raw;
        }

        
        private static byte[] encrypt(byte[] raw, byte[] clear){
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = null;
            byte[] encrypted = null;
            try{
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            encrypted = cipher.doFinal(clear);
            }catch(Exception e){e.printStackTrace();}
                return encrypted;
        }

        private static byte[] decrypt(byte[] raw, byte[] encrypted){
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = null;
            byte[] decrypted = null;
            try{
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            decrypted = cipher.doFinal(encrypted);
            }catch(Exception e){e.printStackTrace();}
                return decrypted;
        }

        public static String toHex(String txt) {
                return toHex(txt.getBytes());
        }
        public static String fromHex(String hex) {
                return new String(toByte(hex));
        }
        
        public static byte[] toByte(String hexString) {
                int len = hexString.length()/2;
                byte[] result = new byte[len];
                for (int i = 0; i < len; i++)
                        result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
                return result;
        }

        public static String toHex(byte[] buf) {
                if (buf == null)
                        return "";
                StringBuffer result = new StringBuffer(2*buf.length);
                for (int i = 0; i < buf.length; i++) {
                        appendHex(result, buf[i]);
                }
                return result.toString();
        }
        private final static String HEX = "0123456789ABCDEF";
        private static void appendHex(StringBuffer sb, byte b) {
                sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
        }
        
}