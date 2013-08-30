package org.rs2server.rs2.util;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;

public class Encryption {
    public static int randomNumber(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    public static String encrypt(String encryption, String toencrypt) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if (encryption.equalsIgnoreCase("md5")) {
            return md5(toencrypt);
        } else if (encryption.equalsIgnoreCase("sha1") || encryption.equalsIgnoreCase("sha-1")) {
            return SHA1(toencrypt);
        } else if (encryption.equalsIgnoreCase("sha256") || encryption.equalsIgnoreCase("sha-256")) {
            return SHA256(toencrypt);
        } else if (encryption.equalsIgnoreCase("sha512") || encryption.equalsIgnoreCase("sha2")
                || encryption.equalsIgnoreCase("sha-512") || encryption.equalsIgnoreCase("sha-2")) {
            return SHA512(toencrypt);
        } else if (encryption.equalsIgnoreCase("whirlpool")) {
            return whirlpool(toencrypt);
        } else if (encryption.equalsIgnoreCase("xauth")) {
            String salt = whirlpool(UUID.randomUUID().toString()).substring(0, 12);
            String hash = whirlpool(salt + toencrypt);
            int saltPos = (toencrypt.length() >= hash.length() ? hash.length() - 1 : toencrypt.length());
            return hash.substring(0, saltPos) + salt + hash.substring(saltPos);
        }
        return md5(toencrypt);
    }

    public static String hash(int length, String charset, int RangeFrom, int RangeTo) {
        if (charset.equalsIgnoreCase("none")) {
            StringBuffer salt = new StringBuffer();
            for (int i = 0; i < length; i++) {
                salt.append((char) (randomNumber(RangeFrom, RangeTo)));
            }
            return salt.toString();
        } else {
            Random rand = new Random(System.currentTimeMillis());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < length; i++) {
                int pos = rand.nextInt(charset.length());
                sb.append(charset.charAt(pos));
            }
            return sb.toString();
        }
    }

    public static String whirlpool(String toencrypt) {
        Whirlpool w = new Whirlpool();
        byte[] digest = new byte[Whirlpool.DIGESTBYTES];
        w.NESSIEinit();
        w.NESSIEadd(toencrypt);
        w.NESSIEfinalize(digest);
        return Whirlpool.display(digest);
    }

    public static String bytes2hex(byte[] bytes) {
        StringBuffer r = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String x = Integer.toHexString(bytes[i] & 0xff);
            if (x.length() < 2) {
                r.append("0");
            }
            r.append(x);
        }
        return r.toString();
    }


    public static String md5(String data) {
        try {
            byte[] bytes = data.getBytes("ISO-8859-1");
            MessageDigest md5er = MessageDigest.getInstance("MD5");
            byte[] hash = md5er.digest(bytes);
            return bytes2hex(hash);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String convertToHexx(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int twoHalfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            }
            while (twoHalfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md;
        md = MessageDigest.getInstance("SHA-1");
        byte[] sha1hash;
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        sha1hash = md.digest();
        return convertToHexx(sha1hash);
    }

    public static String SHA256(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes());
            byte byteData[] = md.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1) hexString.append('0');
                {
                    hexString.append(hex);
                }
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            //    Util.logging.StackTrace(e.getStackTrace(), Thread.currentThread().getStackTrace()[1].getMethodName(), Thread.currentThread().getStackTrace()[1].getLineNumber(), Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getFileName());
        }
        return text;
    }

    public static String SHA512(String text) {
        byte[] sha1hash = new byte[40];
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(text.getBytes("UTF-8"), 0, text.length());
            sha1hash = md.digest();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertToHex(sha1hash);
    }

    public static int hexToInt(char ch) {
        if (ch >= '0' && ch <= '9') {
            return ch - '0';
        }
        ch = Character.toUpperCase(ch);
        if (ch >= 'A' && ch <= 'F') {
            return ch - 'A' + 0xA;
        }
        throw new IllegalArgumentException("Not a hex character: " + ch);
    }

    public static String pack(String hex) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < hex.length(); i += 2) {
            char c1 = hex.charAt(i);
            char c2 = hex.charAt(i + 1);
            char packed = (char) (hexToInt(c1) * 16 + hexToInt(c2));
            buf.append(packed);
        }
        return buf.toString();
    }

    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            }
            while (two_halfs++ < 1);
        }
        return buf.toString();
    }
}