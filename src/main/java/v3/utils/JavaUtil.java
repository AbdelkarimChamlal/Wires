package v3.utils;

import org.bouncycastle.util.Strings;
import v3.primitiveModels.Row;
import v3.primitiveModels.Table;

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

    public static String sortAndConcat(List<String> values){
        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 0 ; i < values.size() ; i++){
            String max = values.get(0);
            int maxPosition = 0;

            for(int j = 0 ; j < values.size() ; j++){
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
            stringBuilder.append(value);
        }

        return stringBuilder.toString();
    }

    public static List<String> convertArrayToList(String[] values){
        return new ArrayList<>(Arrays.asList(values));
    }

    public static Table duplicateTable(Table table){
        Table duplicatedTable = new Table();
        for(Row row:table.getRows()){
            duplicatedTable.getRows().add(RowUtil.duplicateRow(row));
        }
        return duplicatedTable;
    }
}
