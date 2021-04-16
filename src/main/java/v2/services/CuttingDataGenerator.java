package v2.services;

import v2.exceptions.TemplateNotValid;
import v2.utils.ExportData;
import v2.utils.ImportData;
import v2.utils.RowUtil;
import v2.utils.TemplateUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CuttingDataGenerator {
    List<List<String>> twists;
    List<List<String>> doubles;
    List<List<String>> wireList;
    List<List<String>> splices;
    List<List<String>> maxWireList;

    public CuttingDataGenerator(String maxWireListPath) throws IOException {
        this.maxWireList = ImportData.importWorkSheet(maxWireListPath,0);
        boolean matchingTemplate = TemplateUtil.matchTemplate("max.xlsx",maxWireList.get(0));
        if(!matchingTemplate){
            throw new TemplateNotValid("Template of input doesn't match",new Throwable("header not valid"));
        }
    }


    public void initializeData(){
        int wireTypePosition = maxWireList.get(0).indexOf("Wire Type");
        int fromCrimpingType = maxWireList.get(0).indexOf("From Crimping Type");
        int toCrimpingType = maxWireList.get(0).indexOf("To Crimping Type");

        splices = new ArrayList<>();
        wireList = new ArrayList<>();
        twists = new ArrayList<>();
        doubles = new ArrayList<>();

        splices.add(maxWireList.get(0));
        wireList.add(maxWireList.get(0));
        twists.add(maxWireList.get(0));
        doubles.add(maxWireList.get(0));

        for(int i = 1 ; i < maxWireList.size() ; i++){
            List<String> row = maxWireList.get(i);
            wireList.add(RowUtil.duplicateRow(row));

            if(row.get(wireTypePosition).equalsIgnoreCase("Twisted Wire")){
                twists.add(RowUtil.duplicateRow(row));
            }

            if(row.get(fromCrimpingType).equalsIgnoreCase("double") || row.get(toCrimpingType).equalsIgnoreCase("double")) {
                doubles.add(RowUtil.duplicateRow(row));
            }

        }
    }
    public void prepareFinalData() throws IOException {
        wireList = TemplateUtil.convertToTemplate(wireList,"cuttingData.xlsx","wire list");
        doubles = TemplateUtil.convertToTemplate(doubles,"cuttingData.xlsx","doubles");
        splices = TemplateUtil.convertToTemplate(splices,"cuttingData.xlsx","splices");
        twists = TemplateUtil.convertToTemplate(twists,"cuttingData.xlsx","twists");
    }

    public void exportCuttingData(String filename) throws IOException {
        ExportData.exportCuttingTable(filename,wireList,doubles,twists,splices);
    }



}
