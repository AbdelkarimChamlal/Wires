package v3.utils;

import v3.data.ConvertData;
import v3.data.ExportData;
import v3.data.ImportData;
import v3.models.Revision;

import java.io.IOException;
import java.util.Map;

public class RevisionUtil {
    public static Map<String, Revision> loadRevisions(String projectName) throws IOException {
        String revisionFile = ImportData.importText("data/"+projectName+".data");
        return ConvertData.convertStringToRevisions(revisionFile);
    }

    public static void addRevision(String projectName,String hash,Revision revision) throws IOException {
        String revisionText = ConvertData.convertToRevisionText(hash,revision);
        ExportData.appendTextToFile("data/"+projectName+".data",revisionText);
    }
}
