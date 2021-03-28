package v2.utils;


import org.apache.commons.collections4.map.HashedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Handle all row manipulations and simplify the code.
 */
public class RowUtil {

    /**
     * get rows that intersect in multiple columns.
     *
     * @param table 2D java list
     * @param ids position in order of each column
     * @param values values in order of each column
     * @return 2D java list of rows that much in all the values on level of columnPosition
     */
    public static List<List<String>> query(List<List<String>> table, int[] ids, String... values){
        List<List<String>> queryResult = table;
        int i=0;
        while(i<ids.length){
            queryResult = getRowsByColumnValue(queryResult,values[i],ids[i]);
            i++;
        }
        return queryResult;
    }

    /**
     * get subTable of a table where all rows have the same value in a specific column.
     *
     * @param table 2D java list
     * @param value string value for which we want to collect all rows with this value
     * @param valueColumn position of the column we are checking
     * @return a subTable of the main table where all rows have the value in the column specified
     */
    public static List<List<String>> getRowsByColumnValue(List<List<String>> table,String value,int valueColumn){
        List<List<String>> valueTable = new ArrayList<>();
        for(int i =1;i<table.size();i++){
            if(table.get(i).get(valueColumn).equals(value)){
                valueTable.add(table.get(i));
            }
        }
        return valueTable;
    }

    /**
     * return a row from a table using a unique id.
     *
     * @param table 2D java list
     * @param id the value of id we want to get its row
     * @param idPosition the column position for the ids
     * @return a row oko 1D java list
     */
    public static List<String> getRowByUniqueId(List<List<String>> table,String id,int idPosition){
        for(int i =1; i <table.size() ; i++){
            if(table.get(i).get(idPosition).equalsIgnoreCase(id)){
                return table.get(i);
            }
        }
        return new ArrayList<>();
    }

    /**
     * add increasing unique key to the table.
     *
     * @param table java 2D list
     * @param idPosition where the id will be placed preferred to be at the end of columns
     */
    public static void addId(List<List<String>> table,int idPosition){
        for(int i =0 ; i< table.size();i++){
            table.get(i).add(String.valueOf(i));
        }
    }

    /**
     * Remove a column from java 2D list.
     *
     * @param javaTable 2D list object
     * @param columnPosition the position of the wanted to be removed column
     */
    public static void removeColumn(List<List<String>> javaTable,int columnPosition){
        for(List<String> row:javaTable){
            //remove the column
            row.remove(columnPosition);
        }
    }

    /**
     * Remove a column from java 2D list.
     *
     * @param javaTable 2D list object
     * @param rowPosition the position of the wanted to be removed row
     */
    public static void removeRow(List<List<String>> javaTable,int rowPosition){
        javaTable.remove(rowPosition);
    }

    /**
     * takes a list of strings and duplicate them to new list with the same order.
     *
     * @param originalRow the list which we want to duplicate
     * @return a new list matches the original in values but different in reference
     */
    public static List<String> duplicateRow(List<String> originalRow){
        return new ArrayList<>(originalRow);
    }

    /**
     * get all string values from a specific column in a table.
     *
     * @param table 2D table using List
     * @param columnPosition the column position in the table
     * @return list of string values in the same order as the table starting from row 0 to the last row in the table
     */
    public static List<String> getColumn(List<List<String>> table,int columnPosition){
        List<String> column = new ArrayList<>();
        for(List<String> row:table){
            column.add(row.get(columnPosition));
        }
        return column;
    }

    /**
     * create a map for two list with the same size using increasing index.
     *
     * @param key the list of keys in the map
     * @param value the list of values in the map
     * @return map object which for each key(i) we get value(i)
     */
    public static Map<String,String> generateMap(List<String> key,List<String> value){
        Map<String,String> KEY_VALUE = new HashedMap<>();

        for(int i = 1; i<key.size() ; i++){
            if(!KEY_VALUE.containsKey(key.get(i))){
                KEY_VALUE.put(key.get(i),value.get(i));
            }
        }

        return KEY_VALUE;
    }

    /**
     * generates a list of unique values only in a specific column.
     *
     * @param table  java 2D table
     * @param column specific position for the column in the table.
     * @return list of unique values at the column specified
     */
    public static List<String> uniqueValues(List<List<String>> table,int column){
        List<String> uniques = new ArrayList<>();
        for(List<String> row:table){
            if(!uniques.contains(row.get(column))){
                uniques.add(row.get(column));
            }
        }
        return uniques;
    }

    /**
     * create an empty row with a specific cells number
     *
     * @param size cells number in the new row
     * @return new row of data in form of 1D list
     */
    public static List<String> emptyRow(int size){
        List<String> newRow = new ArrayList<>();
        for (int i =0 ; i<size ;i++){
            newRow.add("");
        }
        return newRow;
    }
}

