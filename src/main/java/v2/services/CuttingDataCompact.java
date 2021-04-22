package v2.services;

import org.apache.commons.codec.digest.DigestUtils;
import v2.exceptions.TemplateNotValid;
import v2.models.Revision;
import v2.utils.*;
import v3.utils.JavaUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CuttingDataCompact {
    List<List<String>> maxWireList;
    List<List<String>> maxWireListOriginal;

    public CuttingDataCompact(String maxWireListPath) throws IOException {
        this.maxWireList = ImportData.importWorkSheet(maxWireListPath,0);
        boolean matchingTemplate = TemplateUtil.matchTemplate("max.xlsx",maxWireList.get(0));
        if(!matchingTemplate){
            throw new TemplateNotValid("Template of input doesn't match",new Throwable("header not valid"));
        }
    }


    public void initializeData() throws IOException {
        //simply add those columns to the table
        maxWireList.get(0).add("DOUBLE PM");
        maxWireList.get(0).add("DOUBLE SK");
        maxWireList.get(0).add("TWIST PM");
        maxWireList.get(0).add("TWIST SK");
        maxWireListOriginal = TemplateUtil.convertToTemplate(maxWireList,"max.xlsx");

        for(int i = 1 ; i < maxWireList.size() ; i++){
            maxWireList.get(i).add("");
            maxWireList.get(i).add("");
            maxWireList.get(i).add("");
            maxWireList.get(i).add("");
        }

        int wireTypePosition = maxWireList.get(0).indexOf("Wire Type");
        int fromCrimpingType = maxWireList.get(0).indexOf("From Crimping Type");
        int fromCrimpingDouble = maxWireList.get(0).indexOf("From Double Crimp. With Wire(s)");
        int toCrimpingType = maxWireList.get(0).indexOf("To Crimping Type");
        int toCrimpingDouble = maxWireList.get(0).indexOf("To Double Crimp. With Wire(s)");
        int wireCostumerName = maxWireList.get(0).indexOf("Wire Customer Name");
        int wireSpecialWires = maxWireList.get(0).indexOf("Wire Special Wires");
        int wirePM = maxWireList.get(0).indexOf("PM");
        int wireSK = maxWireList.get(0).indexOf("SK");

        int doublePM = maxWireList.get(0).indexOf("DOUBLE PM");
        int doubleSK = maxWireList.get(0).indexOf("DOUBLE SK");
        int twistPM = maxWireList.get(0).indexOf("TWIST PM");
        int twistSK = maxWireList.get(0).indexOf("TWIST SK");

        int fromStart = maxWireListOriginal.get(0).indexOf("From Connector (long name)");
        int toStart = maxWireListOriginal.get(0).indexOf("To Connector (long name)");



        Map<String,Revision> revisionMap = RevisionsUtil.loadRevisions("projectNameRevisions");


        for(int i = 1 ; i < maxWireList.size() ; i++){
            boolean finalPMUpdated = false;

            List<String> wireInf = maxWireListOriginal.get(i).subList(0,fromStart);
            List<String> from = maxWireListOriginal.get(i).subList(fromStart,toStart);
            List<String> to = maxWireListOriginal.get(i).subList(toStart,maxWireListOriginal.get(0).size());

            String concatWireInf = JavaUtil.concat(wireInf);
            String concatFrom = JavaUtil.concat(from);
            String concatTo = JavaUtil.concat(to);

            List<String> temp = new ArrayList<>();
            temp.add(concatFrom);
            temp.add(concatTo);
            String hashText = concatWireInf + JavaUtil.sortAndConcat(temp);

            String hash = DigestUtils.sha256Hex(hashText);

            if(!revisionMap.containsKey(hash)){

                if(maxWireList.get(i).get(wireTypePosition).equalsIgnoreCase("Twisted Wire")){
                    maxWireList.get(i).set(twistPM,"540"+maxWireList.get(i).get(wireSpecialWires)+"_1A");
                    finalPMUpdated = true;
                    maxWireList.get(i).set(wirePM,"541"+maxWireList.get(i).get(wireCostumerName)+"_1A");
                }

                if(maxWireList.get(i).get(fromCrimpingType).equalsIgnoreCase("double")){
                    String sortAndConcat = JavaUtil.sortAndConcat(JavaUtil.convertArrayToList(new String[] {
                            maxWireList.get(i).get(wireCostumerName),
                            maxWireList.get(i).get(fromCrimpingDouble)
                    }));
                    maxWireList.get(i).set(doublePM,"520"+sortAndConcat+"_1A");
                    if(!finalPMUpdated){
                        finalPMUpdated = true;
                        maxWireList.get(i).set(wirePM,"521"+maxWireList.get(i).get(wireCostumerName)+"_1A");
                    }
                }

                if(maxWireList.get(i).get(toCrimpingType).equalsIgnoreCase("double")){
                    String sortAndConcat = JavaUtil.sortAndConcat(JavaUtil.convertArrayToList(new String[] {
                            maxWireList.get(i).get(wireCostumerName),
                            maxWireList.get(i).get(toCrimpingDouble)
                    }));
                    maxWireList.get(i).set(doublePM,"520"+sortAndConcat+"_1A");
                    if(!finalPMUpdated){
                        finalPMUpdated = true;
                        maxWireList.get(i).set(wirePM,"521"+maxWireList.get(i).get(wireCostumerName)+"_1A");
                    }
                }



                int lastRevisionCode = RevisionsUtil.getLastRevision(revisionMap,maxWireList.get(i).get(wirePM));
                lastRevisionCode++;

                String currentPM = maxWireList.get(i).get(wirePM);
                String customerPart = RegUtil.extractCustomerPart(currentPM);
                //TODO this needs more logic in case it doesn't end with A
                String newPM = customerPart + "_" + lastRevisionCode +"A";

                maxWireList.get(i).set(wirePM,newPM);

                Revision revision = new Revision();
                revision.setWirePM(maxWireList.get(i).get(wirePM));
                revision.setWireSK(maxWireList.get(i).get(wireSK));
                revision.setDoublePM(maxWireList.get(i).get(doublePM));
                revision.setDoubleSK(maxWireList.get(i).get(doubleSK));
                revision.setTwistPM(maxWireList.get(i).get(twistPM));
                revision.setTwistSK(maxWireList.get(i).get(twistSK));
                revisionMap.put(hash,revision);
                RevisionsUtil.addRevision("projectNameRevisions",hash,revision);

            }else{
                Revision revision = revisionMap.get(hash);

                maxWireList.get(i).set(twistPM,revision.getTwistPM());
                maxWireList.get(i).set(twistSK,revision.getTwistSK());
                maxWireList.get(i).set(wirePM,revision.getWirePM());
                maxWireList.get(i).set(wireSK,revision.getWireSK());
                maxWireList.get(i).set(doublePM,revision.getDoublePM());
                maxWireList.get(i).set(doubleSK,revision.getDoubleSK());

            }
        }
    }



    public void exportData(String filePath,String sheetName) throws IOException {
        ExportData.exportTableUsingTemplate(filePath,maxWireList,"cuttingDataCompacted.xlsx");
    }


}