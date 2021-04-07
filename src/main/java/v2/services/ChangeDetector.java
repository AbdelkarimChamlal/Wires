package v2.services;

import org.apache.commons.collections4.map.HashedMap;
import v2.utils.ExportData;
import v2.utils.ImportData;
import v2.utils.RowUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Detect changes that happened to an excel file by comparing two instants of the same file
 */
public class ChangeDetector {
    List<List<String>> originalTable;
    List<List<String>> modifiedTable;
    List<List<String>> deletedRows;
    List<List<String>> addedRows;
    List<List<String>> changedRows;
    List<List<String>> nonChangedRows;
    List<List<String>> finalTable;
    List<Integer> uniqueKeys;
    int commonSize;

    public ChangeDetector(String originFileName, String modifiedFileName, List<Integer> uniqueKeys) throws IOException {
        this.originalTable = ImportData.importWorkSheet(originFileName,0);
        this.modifiedTable = ImportData.importWorkSheet(modifiedFileName,0);
        this.uniqueKeys = uniqueKeys;
    }

    public void initializeData(){
        List<String> commonColumns = new ArrayList<>();

        for(String originalColumn:originalTable.get(0)){
            for(String modifiedColumn:modifiedTable.get(0)){
                if(originalColumn.equals(modifiedColumn) && !commonColumns.contains(originalColumn)){
                    commonColumns.add(originalColumn);
                }
            }
        }

        commonSize = commonColumns.size();

        Map<String,Integer> commonMapInOriginal = new HashedMap<>();
        Map<String,Integer> commonMapInModified = new HashedMap<>();

        for(String common:commonColumns){
            for(int i = 0 ; i < originalTable.get(0).size() ; i++ ){
                if(common.equals(originalTable.get(0).get(i))){
                    commonMapInOriginal.put(common,i);
                }
            }
        }

        for(String common:commonColumns){
            for(int i = 0 ; i < modifiedTable.get(0).size() ; i++ ){
                if(common.equals(modifiedTable.get(0).get(i))){
                    commonMapInModified.put(common,i);
                }
            }
        }

        deletedRows = new ArrayList<>();
        addedRows = new ArrayList<>();
        nonChangedRows = new ArrayList<>();
        changedRows = new ArrayList<>();


        //check if a row from the original version was deleted in the new version
        for(int i = 1 ; i < originalTable.size() ; i++){
            List<String> originalRow = originalTable.get(i);
            boolean foundMatching = false;
            for(int j = 1 ; j < modifiedTable.size() ; j++){
                List<String> modifiedRow = modifiedTable.get(j);

                boolean matching = true;
                for(Integer unique:uniqueKeys){
                    if(!originalRow.get(unique).equals(modifiedRow.get(unique))){
                        matching=false;
                        break;
                    }
                }

                if(matching){
                    foundMatching=true;
                    break;
                }
            }

            if(!foundMatching){
                deletedRows.add(RowUtil.duplicateRow(originalRow));
            }
        }

        //check for added rows in the modified version
        for(int i = 1 ; i < modifiedTable.size() ; i++){
            List<String> modifiedRow = modifiedTable.get(i);
            boolean foundMatching = false;
            for(int j = 1 ; j < originalTable.size() ; j++){
                List<String> originalRow = originalTable.get(j);

                boolean matching = true;
                for(Integer unique:uniqueKeys){
                    if(!originalRow.get(unique).equals(modifiedRow.get(unique))){
                        matching=false;
                        break;
                    }
                }
                if(matching){
                    foundMatching=true;
                    break;
                }
            }

            if(!foundMatching){
                addedRows.add(RowUtil.duplicateRow(modifiedRow));
            }
        }

        //get all not changed rows
        for(int i = 1 ; i < originalTable.size() ; i++){
            List<String> originalRow = originalTable.get(i);

            boolean notModified = false;
            for(int j = 1 ; j < modifiedTable.size() ; j++){
                List<String> modifiedRow = modifiedTable.get(j);

                boolean foundMatch = true;

                for(String common:commonColumns){
                    if(!originalRow.get(commonMapInOriginal.get(common)).equals(modifiedRow.get(commonMapInModified.get(common)))){
                        foundMatch = false;
                        break;
                    }
                }

                if(foundMatch && commonColumns.size()>0){
                    notModified = true;
                    break;
                }
            }

            if(notModified){
                nonChangedRows.add(RowUtil.duplicateRow(originalRow));
            }
        }

        //get changed rows
        for(int i = 1 ; i < originalTable.size() ; i++){
            List<String> originalRow = originalTable.get(i);
            for(int j = 1 ; j < modifiedTable.size() ; j++){
                List<String> modifiedRow = modifiedTable.get(j);

                boolean foundMatch = true;

                for(Integer uniqueKey:uniqueKeys){
                    if(!originalRow.get(uniqueKey).equals(modifiedRow.get(uniqueKey))){
                        foundMatch = false;
                        break;
                    }
                }

                if(foundMatch){
                    boolean modified = false;
                    for(String common:commonColumns){
                        if(!originalRow.get(commonMapInOriginal.get(common)).equals(modifiedRow.get(commonMapInModified.get(common)))){
                            modified = true;
                            break;
                        }
                    }
                    if(modified){
                        changedRows.add(RowUtil.duplicateRow(modifiedRow));
                    }
                }
            }
        }
    }

    public void prepareFinalData(){
        // prepare the output header
        // TODO: check if the common < original.size || modified.size
        int finalHeaderSize = originalTable.get(0).size() - commonSize + modifiedTable.get(0).size() ;



        finalTable = new ArrayList<>();
        finalTable.add(originalTable.get(0));
        finalTable.get(0).add("comment");

        for (List<String> nonChangedRow : nonChangedRows) {
            nonChangedRow.add("not Modified");
            finalTable.add(nonChangedRow);
        }

        for (List<String> changedRow : changedRows) {
            changedRow.add("Modified");
            finalTable.add(changedRow);
        }

        for (List<String> addedRow : addedRows) {
            addedRow.add("Added");
            finalTable.add(addedRow);
        }

        for (List<String> deletedRow : deletedRows) {
            deletedRow.add("Deleted");
            finalTable.add(deletedRow);
        }
    }

    public void exportChangedLog(String filename,String sheetName) throws IOException {
        ExportData.exportTableToExcel(filename,sheetName,finalTable);
    }

}
