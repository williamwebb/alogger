package com.jug6ernaut.android.utilites;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
Copyright (C) 2012 Sveinung Kval Bakken, sveinung.bakken@gmail.com

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 */


public class SecurePreferences implements SharedPreferences {

    public static class SecurePreferencesException extends RuntimeException {

        public SecurePreferencesException(Throwable e) {
            super(e);
        }

    }

    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String KEY_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String SECRET_KEY_HASH_TRANSFORMATION = "SHA-256";
    private static final String CHARSET = "UTF-8";

    private final boolean encryptKeys;
    private final Cipher writer;
    private final Cipher reader;
    private final Cipher keyWriter;
    private final SharedPreferences preferences;

    /**
     * This will initialize an instance of the SecurePreferences class
     * @param context your current context.
     * @param preferenceName name of preferences file (preferenceName.xml)
     * @param secureKey the key used for encryption, finding a good key scheme is hard.
     * Hardcoding your key in the application is bad, but better than plaintext preferences. Having the user enter the key upon application launch is a safe(r) alternative, but annoying to the user.
     * @param encryptKeys settings this to false will only encrypt the values,
     * true will encrypt both values and keys. Keys can contain a lot of information about
     * the plaintext value of the value which can be used to decipher the value.
     * @throws SecurePreferencesException
     */
    public SecurePreferences(Context context, String preferenceName, String secureKey, boolean encryptKeys) throws SecurePreferencesException {
        try {
            this.writer = Cipher.getInstance(TRANSFORMATION);
            this.reader = Cipher.getInstance(TRANSFORMATION);
            this.keyWriter = Cipher.getInstance(KEY_TRANSFORMATION);

            initCiphers(secureKey);

            this.preferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);

            this.encryptKeys = encryptKeys;
        }
        catch (GeneralSecurityException e) {
            throw new SecurePreferencesException(e);
        }
        catch (UnsupportedEncodingException e) {
            throw new SecurePreferencesException(e);
        }
    }

    private void initCiphers(String secureKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidAlgorithmParameterException {
        IvParameterSpec ivSpec = getIv();
        SecretKeySpec secretKey = getSecretKey(secureKey);

        writer.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        reader.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        keyWriter.init(Cipher.ENCRYPT_MODE, secretKey);
    }

    private IvParameterSpec getIv() {
        byte[] iv = new byte[writer.getBlockSize()];
        System.arraycopy("fldsjfodasjifudslfjdsaofshaufihadsf".getBytes(), 0, iv, 0, writer.getBlockSize());
        return new IvParameterSpec(iv);
    }

    private SecretKeySpec getSecretKey(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] keyBytes = createKeyBytes(key);
        return new SecretKeySpec(keyBytes, TRANSFORMATION);
    }

    private byte[] createKeyBytes(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(SECRET_KEY_HASH_TRANSFORMATION);
        md.reset();
        byte[] keyBytes = md.digest(key.getBytes(CHARSET));
        return keyBytes;
    }

    private String get(String cypherKey,Object def){
        String key = toKey(cypherKey);
        if (preferences.contains(toKey(key))) {
            String securedEncodedValue = preferences.getString(toKey(key), "");
            return decrypt(securedEncodedValue);
        }
        return String.valueOf(def);
    }

    private void put(String key, Object value) throws SecurePreferencesException,NullPointerException {
        if(value==null)throw new NullPointerException("value can not be null");

        String secureValueEncoded = encrypt(String.valueOf(value), writer);

        preferences.edit().putString(toKey(key), secureValueEncoded).commit();
    }

    private String toKey(String key) {
        if (encryptKeys)
            return encrypt(key, keyWriter);
        else return key;
    }


    private String encrypt(String value, Cipher writer) throws SecurePreferencesException {
        byte[] secureValue;
        try {
            secureValue = convert(writer, value.getBytes(CHARSET));
        }
        catch (UnsupportedEncodingException e) {
            throw new SecurePreferencesException(e);
        }
        String secureValueEncoded = Base64.encodeToString(secureValue, Base64.NO_WRAP);
        return secureValueEncoded;
    }

    private String decrypt(String securedEncodedValue) {
        byte[] securedValue = Base64.decode(securedEncodedValue, Base64.NO_WRAP);
        byte[] value = convert(reader, securedValue);
        try {
            return new String(value, CHARSET);
        }
        catch (UnsupportedEncodingException e) {
            throw new SecurePreferencesException(e);
        }
    }

    private static byte[] convert(Cipher cipher, byte[] bs) throws SecurePreferencesException {
        try {
            return cipher.doFinal(bs);
        }
        catch (Exception e) {
            throw new SecurePreferencesException(e);
        }
    }

    @Override
    public Map<String, ?> getAll() {
        Map<String,String> newMap = new HashMap<String ,String >();
        Map<String,String> all = (Map<String, String>) preferences.getAll();
        for(Map.Entry<String, String> entry : all.entrySet()){
            newMap.put(decrypt(entry.getKey()),decrypt(entry.getValue()));
        }
        all.clear();
        return newMap;
    }

    @Override
    public String getString(String s, String s2) {
        return get(s, s2);
    }

    @Override
    public Set<String> getStringSet(String s, Set<String> strings) {
        String key = toKey(s);
        if(preferences.contains(key)){
            Set<String> set = preferences.getStringSet(key,null);
            Set<String> temp = new HashSet<String>(set.size());
            for (String str : set){
                temp.add(decrypt(str));
            }
            set.clear();
            return temp;
        }
        return null;
    }

    @Override
    public int getInt(String s, int i) {
        return Integer.valueOf(get(s,i));
    }

    @Override
    public long getLong(String s, long l) {
        return Long.valueOf(get(s,l));
    }

    @Override
    public float getFloat(String s, float v) {
        return Float.valueOf(get(s,v));
    }

    @Override
    public boolean getBoolean(String s, boolean b) {
        return Boolean.valueOf(get(s,b));
    }

    @Override
    public boolean contains(String key) {
        return preferences.contains(toKey(key));
    }

    @Override
    public Editor edit() {
        return editor;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        preferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        preferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    private Editor editor = new Editor() {
        @Override
        public Editor putString(String s, String s2) {
            put(s,s2);
            return this;
        }

        @Override
        public Editor putStringSet(String s, Set<String> strings) {
            String key = toKey(s);
            if(preferences.contains(key)){
                Set<String> temp = new HashSet<String>(strings.size());
                for (String str : strings){
                    temp.add(encrypt(str, writer));
                }
                preferences.edit().putStringSet(key,temp);
            }
            return this;
        }

        @Override
        public Editor putInt(String s, int i) {
            put(s,i);
            return this;
        }

        @Override
        public Editor putLong(String s, long l) {
            put(s,l);
            return this;
        }

        @Override
        public Editor putFloat(String s, float v) {
            put(s,v);
            return this;
        }

        @Override
        public Editor putBoolean(String s, boolean b) {
            put(s,b);
            return this;
        }

        @Override
        public Editor remove(String s) {
            return preferences.edit().remove(toKey(s));
        }

        @Override
        public Editor clear() {
            return preferences.edit().clear();
        }

        @Override
        public boolean commit() {
            return preferences.edit().commit();
        }

        @Override
        public void apply() {
            preferences.edit().apply();
        }
    };

}