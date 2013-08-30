package org.rs2server.rs2.util;

import java.util.Random;

public class Misc {

    public static int distance(int absX, int absY, int pointX, int pointY) {
        return (int) Math.sqrt(Math.pow(absX - pointX, 2) + Math.pow(absY - pointY, 2));
    }
    // gets the direction between the two given points
    // valid directions are N:0, NE:2, E:4, SE:6, S:8, SW:10, W:12, NW:14
    // the invalid (inbetween) direction are 1,3,5,7,9,11,13,15 i.e. odd integers
    // returns -1, if src and dest are the same

    public static int direction(int srcX, int srcY, int destX, int destY) {
        int dx = destX - srcX, dy = destY - srcY;
        // a lot of cases that have to be considered here ... is there a more sophisticated (and quick!) way?
        if (dx < 0) {
            if (dy < 0) {
                if (dx < dy) {
                    return 11;
                } else if (dx > dy) {
                    return 9;
                } else {
                    return 10;        // dx == dy
                }
            } else if (dy > 0) {
                if (-dx < dy) {
                    return 15;
                } else if (-dx > dy) {
                    return 13;
                } else {
                    return 14;        // -dx == dy
                }
            } else {    // dy == 0
                return 12;
            }
        } else if (dx > 0) {
            if (dy < 0) {
                if (dx < -dy) {
                    return 7;
                } else if (dx > -dy) {
                    return 5;
                } else {
                    return 6;        // dx == -dy
                }
            } else if (dy > 0) {
                if (dx < dy) {
                    return 1;
                } else if (dx > dy) {
                    return 3;
                } else {
                    return 2;        // dx == dy
                }
            } else {    // dy == 0
                return 4;
            }
        } else {        // dx == 0
            if (dy < 0) {
                return 8;
            } else if (dy > 0) {
                return 0;
            } else {    // dy == 0
                return -1;        // src and dest are the same
            }
        }
    }

    public static String Hex(byte data[]) {
        return Hex(data, 0, data.length);
    }

    public static String Hex(byte data[], int offset, int len) {
        String temp = "";
        for (int cntr = 0; cntr < len; cntr++) {
            int num = data[offset + cntr] & 0xFF;
            String myStr;
            if (num < 16) {
                myStr = "0";
            } else {
                myStr = "";
            }
            temp += myStr + Integer.toHexString(num) + " ";
        }
        return temp.toUpperCase().trim();
    }

    public static int HexToInt(byte data[], int offset, int len) {
        int temp = 0;
        int i = 1000;
        for (int cntr = 0; cntr < len; cntr++) {
            int num = (data[offset + cntr] & 0xFF) * i;
            temp += (int) num;
            if (i > 1) {
                i = i / 1000;
            }
        }
        return temp;
    }

    public static Random random = new Random();

    public static int random(int range) {
        return (int) (java.lang.Math.random() * (range + 1));
    }

    public static int random2(int range) { //1 till range
        return (int) ((java.lang.Math.random() * range) + 1);
    }

    public static int random3(int range) { //0 till range
        return (int) (java.lang.Math.random() * range);
    }

    public static int random4(int range) { //0 till range (range INCLUDED)
        return (int) (java.lang.Math.random() * (range + 1));
    }
}