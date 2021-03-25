package v1.utils;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFRow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class HeaderGenerator {

    public static List<String> getHeaderValues(Row header){

        //initialize headerValues
        List<String> headerValues = new ArrayList<>();
        //initialize iteration over header cells
        Iterator<Cell> cells = header.cellIterator();

        //iterate
        while(cells.hasNext()){
            //getCell for this iteration
            Cell cell = cells.next();
            headerValues.add(cell.getStringCellValue());
        }

        return headerValues;
    }

    public static List<String> getMatchingValues(List<String> oldValues,List<String> newValues){
        List<String> matchingValues = new ArrayList<>();

        for (String oldValue : oldValues) {
            for (String newValue : newValues) {
                if (oldValue.equalsIgnoreCase(newValue)) {
                    matchingValues.add(oldValue.toLowerCase());
                    break;
                }
            }
        }
        return matchingValues;
    }

    public static Map<Integer,Integer> getMatchingValuesPositions(List<String> oldValues, List<String> newValues,List<String> matchingValues){
        Map<Integer,Integer> matchingValuesPositions = new HashedMap<>();

        for(String matchingValue : matchingValues){
            int oldValuesPosition = 0;
            int newValuesPosition = 0;
            for(int i =0 ; i <oldValues.size() ; i++){
                if (matchingValue.equalsIgnoreCase(oldValues.get(i))){
                    oldValuesPosition=i;
                    break;
                }
            }

            for(int i =0 ; i <newValues.size() ; i++){
                if (matchingValue.equalsIgnoreCase(newValues.get(i))){
                    newValuesPosition=i;
                    break;
                }
            }
            matchingValuesPositions.put(oldValuesPosition,newValuesPosition);
        }


        return matchingValuesPositions;
    }

    public static List<String> getAllValues(List<String> oldValues,List<String> newValues){
        List<String> allValues = new ArrayList<>();

        for(String oldValue : oldValues){
            if(!allValues.contains(oldValue.toLowerCase())){
                allValues.add(oldValue.toLowerCase());
            }
        }

        for(String newValue : newValues){
            if(!allValues.contains(newValue.toLowerCase())){
                allValues.add(newValue.toLowerCase());
            }
        }

        return allValues;
    }
}
