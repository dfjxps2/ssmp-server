package com.seaboxdata.auth.server.axis.utils;

public class CaEncryptUtils {

    /**
     * flag: true加密
     * flag: false解密
     */
    public static String ssoEncrypt(String str, boolean flag) {
        char A[] = new char[62];
        A[0] = 'U';
        A[1] = 'o';
        A[2] = 'g';
        A[3] = '8';
        A[4] = 'X';
        A[5] = '3';
        A[6] = 'D';
        A[7] = 'J';
        A[8] = 'l';
        A[9] = '5';
        A[10] = 't';
        A[11] = 'I';
        A[12] = 'd';
        A[13] = 'v';
        A[14] = 'x';
        A[15] = 'm';
        A[16] = 'W';
        A[17] = 'A';
        A[18] = 'f';
        A[19] = 'C';
        A[20] = 'u';
        A[21] = 'L';
        A[22] = 'Y';
        A[23] = 'P';
        A[24] = 'H';
        A[25] = '4';
        A[26] = 'c';
        A[27] = 'Q';
        A[28] = 'b';
        A[29] = 'z';
        A[30] = 'M';
        A[31] = '6';
        A[32] = 'B';
        A[33] = 'V';
        A[34] = 'k';
        A[35] = 'h';
        A[36] = '2';
        A[37] = 'T';
        A[38] = 'O';
        A[39] = 'R';
        A[40] = '7';
        A[41] = 'E';
        A[42] = 'N';
        A[43] = 'n';
        A[44] = 'K';
        A[45] = 's';
        A[46] = 'p';
        A[47] = 'a';
        A[48] = 'i';
        A[49] = 'w';
        A[50] = '1';
        A[51] = 'r';
        A[52] = 'G';
        A[53] = 'j';
        A[54] = 'F';
        A[55] = '9';
        A[56] = 'S';
        A[57] = '0';
        A[58] = 'q';
        A[59] = 'y';
        A[60] = 'Z';
        A[61] = 'e';
        String result = "";
        for (int i = 0; i < str.length(); i++)
            if (flag) {
                if (str.charAt(i) <= '9' && str.charAt(i) >= '0')
                    result = (new StringBuilder(String.valueOf(result)))
                            .append(A[Character.getNumericValue(str.charAt(i))])
                            .toString();
                if (str.charAt(i) <= 'z' && str.charAt(i) >= 'a')
                    result = (new StringBuilder(String.valueOf(result)))
                            .append(A[(new Integer(str.charAt(i) - 97))
                                    .intValue() + 10]).toString();
                if (str.charAt(i) <= 'Z' && str.charAt(i) >= 'A')
                    result = (new StringBuilder(String.valueOf(result)))
                            .append(A[(new Integer(str.charAt(i) - 65))
                                    .intValue() + 36]).toString();
            } else {
                int j = 0;
                for (j = 0; j < 62; j++)
                    if (str.charAt(i) == A[j])
                        break;

                if (j <= 9 && j >= 0)
                    result = (new StringBuilder(String.valueOf(result)))
                            .append(Integer.valueOf(j).toString()).toString();
                if (j < 36 && j >= 10)
                    result = (new StringBuilder(String.valueOf(result)))
                            .append((char) Integer.valueOf((j - 10) + 97)
                                    .intValue()).toString();
                if (j < 62 && j >= 36)
                    result = (new StringBuilder(String.valueOf(result)))
                            .append((char) Integer.valueOf((j - 36) + 65)
                                    .intValue()).toString();
            }

        return result;
    }

}
