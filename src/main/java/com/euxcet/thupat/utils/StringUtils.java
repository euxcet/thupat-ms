package com.euxcet.thupat.utils;

public class StringUtils {

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static long parseLong(String str) {
        if (str == null || str.isEmpty())
            return -1;

        try {
            long value = Long.parseLong(str);
            return value;
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }

    public static int parseInt(String str) {
        if (str == null || str.isEmpty())
            return -1;

        try {
            int value = Integer.parseInt(str);
            return value;
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }

    public static float parseFloat(String str) {
        if (str == null || str.isEmpty())
            return -1;

        try {
            float value = Float.parseFloat(str);
            return value;
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String byteArray2HexString(byte[] bytes) {
        if (bytes == null || bytes.length == 0)
            return null;

        StringBuilder sb = new StringBuilder(bytes.length * 2);

        for (int i = 0; i < bytes.length; i++) {
            sb.append(convertDigit(bytes[i] >> 4))
                    .append(convertDigit((bytes[i] & 0x0f)));
        }

        return sb.toString();
    }

    private static char convertDigit(int value) {
        value &= 0x0f;
        if (value >= 10)
            return ((char) (value - 10 + 'a'));
        else
            return ((char) (value + '0'));
    }
}
