package v2.utils;

import v2.models.Revision;
import v3.data.ConvertData;
import v3.data.ExportData;
import v3.data.ImportData;
import v3.utils.JavaUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RevisionsUtil {

    public static Map<String, Revision> loadRevisions(String projectName) throws IOException {
        String revisionFile = ImportData.importText("data/"+projectName+".data");
        return Convertor.convertStringToRevisions(revisionFile);
    }

    public static void addRevision(String projectName,String hash,Revision revision) throws IOException {
        String revisionText = Convertor.convertToRevisionText(hash,revision);
        ExportData.appendTextToFile("data/"+projectName+".data",revisionText);
    }

    public static int getLastRevision(Map<String,Revision> revisions,String PM){
        String customerPartInPM = RegUtil.extractCustomerPart(PM);
        List<String> currentRevisionsCode = new ArrayList<>();

        for(Revision revision:revisions.values()){
            String customerPartInRevisionPM = RegUtil.extractCustomerPart(revision.getWirePM());
            if(customerPartInPM.equals(customerPartInRevisionPM)){
                currentRevisionsCode.add(RegUtil.extractRevision(customerPartInRevisionPM));
            }
        }

        return JavaUtil.getLatestRevisionCode(currentRevisionsCode);
    }
}
