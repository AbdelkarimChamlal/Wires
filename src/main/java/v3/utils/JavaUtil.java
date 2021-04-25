package v3.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaUtil {
    public static int stringCompare(String str1, String str2) {
        int l1 = str1.length();
        int l2 = str2.length();
        int lmin = Math.min(l1, l2);
        for (int i = 0; i < lmin; i++) {
            int str1_ch = str1.charAt(i);
            int str2_ch = str2.charAt(i);
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

    public static List<Integer> convertListToIntegers(List<String> integers){
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

    public static String hashString(String string){
        return DigestUtils.sha256Hex(string);
    }

    public static String sortAndConcat(List<String> values){
        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0 ; i < values.size() ; i++){
            if(values.get(i).equals("")){
                values.set(i,"-");
            }
            String max = values.get(0);
            int maxPosition = 0;

            for(int j = 0 ; j < values.size() ; j++){
                if(values.get(j).equals("")){
                    values.set(j,"-");
                }
                if(stringCompare(max,values.get(j))<0){
                    max = values.get(j);
                    maxPosition = j;
                }
            }

            stringBuilder.append(max);
            values.remove(maxPosition);
            i--;
        }
        return stringBuilder.toString();
    }

    public static String concat(List<String> values){
        StringBuilder stringBuilder = new StringBuilder();

        for(String value:values){
            if(value.equals("")){
                value="-";
            }
            stringBuilder.append(value);
        }

        return stringBuilder.toString();
    }

    public static List<String> convertArrayToList(String[] values){
        return new ArrayList<>(Arrays.asList(values));
    }

    public static int getLatestRevisionCode(List<String> revisionsCode){
        List<Integer> revisionsOrder = new ArrayList<>();

        for(String revisionCode:revisionsCode){
            StringBuilder stringBuilder = new StringBuilder();
            for(int i = 0 ; i < revisionCode.length() ; i ++){
                if(revisionCode.charAt(i)<48 || revisionCode.charAt(i)>57){
                    break;
                }else{
                    stringBuilder.append(revisionCode.charAt(i));
                }
            }
            if(stringBuilder.length()>0)
            revisionsOrder.add(Integer.parseInt(stringBuilder.toString()));
        }

        int result = 0;
        for(Integer revisionOrder:revisionsOrder){
            if(revisionOrder>result) result = revisionOrder;
        }

        return result;
    }

}
