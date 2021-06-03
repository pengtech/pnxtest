/*
 *  Copyright (c) 2020-2021
 *  This file is part of PnxTest framework.
 *
 *  PnxTest is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero Public License version 3 as
 *  published by the Free Software Foundation
 *
 *  PnxTest is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero Public License for more details.
 *
 *  You should have received a copy of the GNU Affero Public License
 *  along with PnxTest.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  For more information, please contact the author at this address:
 *  chen.baker@gmail.com
 *
 */

package com.pnxtest.core.util;

import java.util.stream.Collectors;

public class StringUtil {
    private StringUtil(){}

    public static String escapeHtml(String source){
        return source.codePoints().mapToObj(c -> c > 127 || "\"'<>&".indexOf(c) != -1 ?
                "&#" + c + ";" : new String(Character.toChars(c)))
                .collect(Collectors.joining());
    }

    public static String escapeHtmlAndReplaceNewLine(String source){
        return escapeHtml(source).replaceAll("(\\r\\n|\\r|\\n)+", "<br/>");
    }

    public static String replaceNewLine(String source){
        return source.replaceAll("(\\r\\n|\\r|\\n)+", "<br/>");
    }
    public static String removeNewLine(String source) {
        return source.replaceAll("\\\\n", "");
    }


    public static String truncate(String source, int maxLength){
        if(source == null) return null;
        int slen = source.length();
        int slenWithDoubleBytes = source.codePointCount(0, slen);
        if(slenWithDoubleBytes<=maxLength){
            return source;
        }

        return source.substring(0, source.offsetByCodePoints(0, maxLength)).concat("...");
    }


    public static  boolean isEmpty(String source){
        return source == null || source.length()==0;
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for(int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }

        }
        return true;
    }


    public static String removeHtmlTags(String str){
        str = str.replaceAll("<.*?>", "");

        str = str.replace("&nbsp;", ""); //replace &nbsp
        str = str.replace("&amp;", "&");//replace &amp; with &
        str = str.replaceAll("&.*?;", "");//OR remove all HTML entities
        str = str.replaceAll("\n", " ");

        return str;
    }


    public static String capitalize(String str) {
        int strLen;
        if (str != null && (strLen = str.length()) != 0) {
            int firstCodepoint = str.codePointAt(0);
            int newCodePoint = Character.toTitleCase(firstCodepoint);
            if (firstCodepoint == newCodePoint) {
                return str;
            } else {
                int[] newCodePoints = new int[strLen];
                int outOffset = 0;
                newCodePoints[outOffset] = newCodePoint;

                int codepoint;
                for(int inOffset = Character.charCount(firstCodepoint); inOffset < strLen; inOffset += Character.charCount(codepoint)) {
                    codepoint = str.codePointAt(inOffset);
                    newCodePoints[++outOffset] = codepoint;
                }

                return new String(newCodePoints, 0, (outOffset+1));
            }
        } else {
            return str;
        }
    }

    public static String lowerCase(String str) {
        return str == null ? null : str.toLowerCase();
    }

    public static boolean isAsciiAlphaUpper(char ch) {
        return ch >= 'A' && ch <= 'Z';
    }

    public static boolean isAsciiAlphaLower(char ch) {
        return ch >= 'a' && ch <= 'z';
    }


    public static String repeat(char ch, int repeat) {
        if (repeat <= 0) {
            return "";
        } else {
            char[] buf = new char[repeat];

            for(int i = repeat - 1; i >= 0; --i) {
                buf[i] = ch;
            }

            return new String(buf);
        }
    }

    public static String rightPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        } else {
            int pads = size - str.length();
            if (pads <= 0) {
                return str;
            } else {
                return pads > 8192 ? rightPad(str, size, String.valueOf(padChar)) : str.concat(repeat(padChar, pads));
            }
        }
    }

    public static String rightPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        } else {
            if (isEmpty(padStr)) {
                padStr = " ";
            }

            int padLen = padStr.length();
            int strLen = str.length();
            int pads = size - strLen;
            if (pads <= 0) {
                return str;
            } else if (padLen == 1 && pads <= 8192) {
                return rightPad(str, size, padStr.charAt(0));
            } else if (pads == padLen) {
                return str.concat(padStr);
            } else if (pads < padLen) {
                return str.concat(padStr.substring(0, pads));
            } else {
                char[] padding = new char[pads];
                char[] padChars = padStr.toCharArray();

                for(int i = 0; i < pads; ++i) {
                    padding[i] = padChars[i % padLen];
                }

                return str.concat(new String(padding));
            }
        }
    }

}
