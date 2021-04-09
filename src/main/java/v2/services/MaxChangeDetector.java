package v2.services;

import org.apache.commons.collections4.map.HashedMap;
import v2.exceptions.TemplateNotValid;
import v2.utils.ExportData;
import v2.utils.ImportData;
import v2.utils.RowUtil;
import v2.utils.TemplateUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static v2.helpers.Values.MODIFIED_SYMBOL;
import static v2.utils.TemplateUtil.matchTemplate;

public class MaxChangeDetector {
    List<List<String>> originalTable;
    List<List<String>> modifiedTable;
    List<List<String>> deletedRows;
    List<List<String>> addedRows;
    List<List<String>> changedRows;
    List<List<String>> nonChangedRows;
    List<List<String>> finalTable;
    List<String> commonColumns;
    List<String> originalOnlyColumns;
    List<String> modifiedOnlyColumns;
    Map<String,Integer> originalOnlyMap;
    Map<String,Integer> modifiedOnlyMap;
    Map<String,Integer> commonMapInOriginal;
    Map<String,Integer> commonMapInModified;
    int originalTableId;
    int modifiedTableId;
    int commonSize;

    public MaxChangeDetector(String originFileName, String modifiedFileName) throws IOException {
        this.originalTable = ImportData.importWorkSheet(originFileName,0);
        this.modifiedTable = ImportData.importWorkSheet(modifiedFileName,0);
        boolean originalMatchingMax = TemplateUtil.matchTemplate("max.xlsx",originalTable.get(0));
        boolean modifiedMatchingMax = TemplateUtil.matchTemplate("max.xlsx",modifiedTable.get(0));

        if(!originalMatchingMax || !modifiedMatchingMax){
             throw new TemplateNotValid("Template of input doesn't match",new Throwable("header not valid"));
        }
    }


    public void addPrimaryKeys(){
        originalTableId = originalTable.get(0).size();
        modifiedTableId = modifiedTable.get(0).size();

        String MP = "Module PN";
        String CWN = "Wire Customer Name";
        String FD = "From Double Crimp. With Wire(s)";
        String TD = "To Double Crimp. With Wire(s)";

        int originalMP=0,originalCWN=0,originalFD=0,originalTD = 0;

        for(int i =0 ; i < originalTable.get(0).size() ; i++){
            if(originalTable.get(0).get(i).equals(MP)){
                originalMP = i;
            }else
            if(originalTable.get(0).get(i).equals(CWN)){
                originalCWN = i;
            }else
            if(originalTable.get(0).get(i).equals(FD)){
                originalFD = i;
            }else
            if(originalTable.get(0).get(i).equals(TD)){
                originalTD = i;
            }
        }

        int modifiedMP =0,modifiedCWN=0,modifiedFD=0,modifiedTD = 0;

        for(int i =0 ; i < modifiedTable.get(0).size() ; i++){
            if(modifiedTable.get(0).get(i).equals(MP)){
                modifiedMP = i;
            }else
            if(modifiedTable.get(0).get(i).equals(CWN)){
                modifiedCWN = i;
            }else
            if(modifiedTable.get(0).get(i).equals(FD)){
                modifiedFD = i;
            }else
            if(modifiedTable.get(0).get(i).equals(TD)){
                modifiedTD = i;
            }
        }

        originalTable.get(0).add("Primary Key");
        modifiedTable.get(0).add("Primary Key");


        for(int i = 1; i < originalTable.size() ;i++ ){
            String sortedDouble = (stringCompare(originalTable.get(i).get(originalFD),originalTable.get(i).get(originalTD))>0)?originalTable.get(i).get(originalFD)+originalTable.get(i).get(originalTD):originalTable.get(i).get(originalTD)+originalTable.get(i).get(originalFD);
            originalTable.get(i).add(originalTable.get(i).get(originalMP)+originalTable.get(i).get(originalCWN)+sortedDouble);
        }
        for(int i = 1; i < modifiedTable.size() ;i++ ){
            String sortedDouble = (stringCompare(modifiedTable.get(i).get(modifiedFD),modifiedTable.get(i).get(modifiedTD))>0)?modifiedTable.get(i).get(modifiedFD)+modifiedTable.get(i).get(modifiedTD):modifiedTable.get(i).get(modifiedTD)+modifiedTable.get(i).get(modifiedFD);
            modifiedTable.get(i).add(modifiedTable.get(i).get(modifiedMP)+modifiedTable.get(i).get(modifiedCWN)+sortedDouble);
        }

    }

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

    public void initializeData() {
        commonColumns = new ArrayList<>();

        for (String originalColumn : originalTable.get(0)) {
            for (String modifiedColumn : modifiedTable.get(0)) {
                if (originalColumn.equals(modifiedColumn) && !commonColumns.contains(originalColumn)) {
                    commonColumns.add(originalColumn);
                    break;
                }
            }
        }

        commonSize = commonColumns.size();

        commonMapInOriginal = new HashedMap<>();
        commonMapInModified = new HashedMap<>();

        for (String common : commonColumns) {
            for (int i = 0; i < originalTable.get(0).size(); i++) {
                if (common.equals(originalTable.get(0).get(i))) {
                    commonMapInOriginal.put(common, i);
                }
            }
        }

        for (String common : commonColumns) {
            for (int i = 0; i < modifiedTable.get(0).size(); i++) {
                if (common.equals(modifiedTable.get(0).get(i))) {
                    commonMapInModified.put(common, i);
                }
            }
        }

        originalOnlyColumns= new ArrayList<>();
        for(int i =0;i < originalTable.get(0).size() ; i++){
            if(!commonColumns.contains(originalTable.get(0).get(i))){
                originalOnlyColumns.add(originalTable.get(0).get(i));
            }
        }

        modifiedOnlyColumns= new ArrayList<>();
        for(int i =0;i < modifiedTable.get(0).size() ; i++){
            if(!commonColumns.contains(modifiedTable.get(0).get(i))){
                modifiedOnlyColumns.add(modifiedTable.get(0).get(i));
            }
        }

        originalOnlyMap = new HashedMap<>();

        for(int i =0 ; i< originalTable.get(0).size();i++){
            if(originalOnlyColumns.contains(originalTable.get(0).get(i))){
                originalOnlyMap.put(originalTable.get(0).get(i),i);
            }
        }

        modifiedOnlyMap = new HashedMap<>();

        for(int i =0 ; i< modifiedTable.get(0).size();i++){
            if(modifiedOnlyColumns.contains(modifiedTable.get(0).get(i))){
                modifiedOnlyMap.put(modifiedTable.get(0).get(i),i);
            }
        }

        deletedRows = new ArrayList<>();
        addedRows = new ArrayList<>();
        nonChangedRows = new ArrayList<>();
        changedRows = new ArrayList<>();


        //check if a row from the original version was deleted in the new version
        for (int i = 1; i < originalTable.size(); i++) {
            List<String> originalRow = originalTable.get(i);
            List<String> modifiedRow = RowUtil.getRowByUniqueId(modifiedTable, originalRow.get(originalTableId), modifiedTableId);
            if (modifiedRow.size() == 0) {
                deletedRows.add(RowUtil.duplicateRow(originalRow));
            }
        }

        //check for added rows in the modified version
        for (int i = 1; i < modifiedTable.size(); i++) {
            List<String> modifiedRow = modifiedTable.get(i);
            List<String> originalRow = RowUtil.getRowByUniqueId(originalTable, modifiedRow.get(modifiedTableId), originalTableId);
            if (originalRow.size() == 0) {
                addedRows.add(RowUtil.duplicateRow(modifiedRow));
            }
        }

        //for common rows
        for(int i = 1 ; i < modifiedTable.size() ; i++){
            List<String> modifiedRow = modifiedTable.get(i);
            List<String> originalRow = RowUtil.getRowByUniqueId(originalTable,modifiedRow.get(modifiedTableId),originalTableId);
            if(originalRow.size()>0){
                boolean matching = true;
                for(String common:commonColumns){
                    if(!modifiedRow.get(commonMapInModified.get(common)).equals(originalRow.get(commonMapInOriginal.get(common)))){
                        matching=false;
                        modifiedRow.set(commonMapInModified.get(common),modifiedRow.get(commonMapInModified.get(common))+MODIFIED_SYMBOL);
                    }
                }
                //non changed rows
                if(matching){
                    nonChangedRows.add(RowUtil.duplicateRow(modifiedRow));
                //changed rows
                }else{
                    changedRows.add(RowUtil.duplicateRow(modifiedRow));
                }
            }
        }
    }

    public String getPM(String primaryKey,String statue){
        return Math.random()+"::"+statue+"::PM";
    }
    public String getSK(String primaryKey,String statue){
        return Math.random()+"::"+statue+"::SK";
    }

    public void prepareFinalData(){
        // prepare the output header
        List<String> finalHeader = new ArrayList<>();

        for (String originalOnlyColumn : originalOnlyColumns) {
            finalHeader.add(originalOnlyColumn);
        }
        for (String commonColumn : commonColumns) {
            finalHeader.add(commonColumn);
        }
        for(String modifiedOnlyColumn:modifiedOnlyColumns){
            finalHeader.add(modifiedOnlyColumn);
        }

        finalHeader.add("comment");

        int finalHeaderSize = finalHeader.size();

        finalTable = new ArrayList<>();
        finalTable.add(finalHeader);




        for (List<String> deletedRow : deletedRows) {
            List<String> outputRow = RowUtil.emptyRow(finalHeaderSize);
            for(int i = 0; i < originalOnlyColumns.size() ; i++){
                outputRow.set(i,deletedRow.get(originalOnlyMap.get(originalOnlyColumns.get(i))));
            }
            for(int i = 0 ; i < commonColumns.size(); i++){
                outputRow.set(originalOnlyColumns.size() + i,deletedRow.get(commonMapInOriginal.get(commonColumns.get(i))));
            }

            outputRow.set(finalHeaderSize-1,"deleted");
            finalTable.add(outputRow);
        }

        for (List<String> addedRow : addedRows) {
            List<String> outputRow = RowUtil.emptyRow(finalHeaderSize);
            for(int i = 0 ; i < commonColumns.size() ; i++){
                outputRow.set(i + originalOnlyColumns.size() ,addedRow.get(commonMapInModified.get(commonColumns.get(i))));
            }
            for(int i = 0; i < modifiedOnlyColumns.size(); i++){
                outputRow.set(i + originalOnlyColumns.size() + commonColumns.size(),addedRow.get(modifiedOnlyMap.get(modifiedOnlyColumns.get(i))));
            }

            outputRow.set(finalHeaderSize-1,"added");
            finalTable.add(outputRow);
        }

        for (List<String> changedRow : changedRows) {
            List<String> outputRow = RowUtil.emptyRow(finalHeaderSize);
            List<String> originalChangedRow = RowUtil.getRowByUniqueId(originalTable,changedRow.get(modifiedTableId),originalTableId);
            for(int i = 0; i < originalOnlyColumns.size() ; i++){
                outputRow.set(i,originalChangedRow.get(originalOnlyMap.get(originalOnlyColumns.get(i))));
            }
            for(int i = 0 ; i < commonColumns.size() ; i++){
                outputRow.set(i + originalOnlyColumns.size() ,changedRow.get(commonMapInModified.get(commonColumns.get(i))));
            }
            for(int i = 0; i < modifiedOnlyColumns.size(); i++){
                outputRow.set(i + originalOnlyColumns.size() + commonColumns.size(),changedRow.get(modifiedOnlyMap.get(modifiedOnlyColumns.get(i))));
            }

            outputRow.set(finalHeaderSize-1,"Modified");
            finalTable.add(outputRow);
        }

        for (List<String> nonChanged : nonChangedRows) {
            List<String> outputRow = RowUtil.emptyRow(finalHeaderSize);
            List<String> originalChangedRow = RowUtil.getRowByUniqueId(originalTable,nonChanged.get(modifiedTableId),originalTableId);
            for(int i = 0; i < originalOnlyColumns.size() ; i++){
                outputRow.set(i,originalChangedRow.get(originalOnlyMap.get(originalOnlyColumns.get(i))));
            }
            for(int i = 0 ; i < commonColumns.size() ; i++){
                outputRow.set(i + originalOnlyColumns.size() ,nonChanged.get(commonMapInModified.get(commonColumns.get(i))));
            }
            for(int i = 0; i < modifiedOnlyColumns.size(); i++){
                outputRow.set(i + originalOnlyColumns.size() + commonColumns.size(),nonChanged.get(modifiedOnlyMap.get(modifiedOnlyColumns.get(i))));
            }
            outputRow.set(finalHeaderSize-1,"-");
            finalTable.add(outputRow);
        }


        if(finalHeader.contains("PM")){
            int PMPosition = finalHeader.indexOf("PM");
            int commentPosition = finalHeader.indexOf("comment");
            int primaryKeyPosition = finalHeader.indexOf("Primary Key");
            for (int i = 1 ; i < finalTable.size() ; i++){
                finalTable.get(i).set(PMPosition,getPM(finalTable.get(i).get(primaryKeyPosition),finalTable.get(i).get(commentPosition)));
            }
        }
        if(finalHeader.contains("SK")){
            int SKPosition = finalHeader.indexOf("SK");
            int commentPosition = finalHeader.indexOf("comment");
            int primaryKeyPosition = finalHeader.indexOf("Primary Key");
            for (int i = 1 ; i < finalTable.size() ; i++){
                finalTable.get(i).set(SKPosition,getSK(finalTable.get(i).get(primaryKeyPosition),finalTable.get(i).get(commentPosition)));
            }
        }


    }

    public void exportChangedLog(String filename,String sheetName) throws IOException {
        ExportData.exportTableToExcelWithModifiedCellsColored(filename,sheetName,TemplateUtil.convertToTemplate(finalTable,"maxLogOutput.xlsx"));
    }

}
