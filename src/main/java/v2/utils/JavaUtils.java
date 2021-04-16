package v2.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JavaUtils {
    public static int stringCompare(String str1, String str2) {
        int l1 = str1.length();
        int l2 = str2.length();
        int lmin = Math.min(l1, l2);
        for (int i = 0; i < lmin; i++) {
            int str1_ch = (int)str1.charAt(i);
            int str2_ch = (int)str2.charAt(i);
            if (str1_ch != str2_ch) {
                return str1_ch - str2_ch;
            }
        }
        if (l1 != l2) {
            return l1 - l2;
        } else {
            return 0;
        }
    }

    public static List<Integer> convertToInteger(List<String> integers){
        List<Integer> integerList = new ArrayList<>();
        for (String integer:integers){
            int intValue = Integer.parseInt(integer);
            integerList.add(intValue);
        }
        return integerList;
    }

    public static int maxValueOfList(List<Integer> integers){
        int max = integers.get(0);
        for (Integer intValue:integers){
            if (intValue>max)max=intValue;
        }
        return max;
    }

    public static boolean createNewDirectory(String path,String name){
        File newDirectory = new File(path+name);
        return newDirectory.mkdir();
    }

}
