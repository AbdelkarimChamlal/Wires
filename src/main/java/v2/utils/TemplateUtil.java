package v2.utils;

import org.apache.commons.collections4.map.HashedMap;
import v2.models.Template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TemplateUtil {




    public static Template loadTemplate(String templateName) throws IOException {
        Template template = new Template();
        List<List<String>> templateTable = ImportData.importWorkSheet("templates/"+templateName,0);
        List<String> templateValues = templateTable.get(0);
        template.setColumns(new ArrayList<>());
        template.setColumnPositions(new HashedMap<>());

        for(int i = 0 ; i < templateValues.size() ; i++){
            template.getColumns().add(templateValues.get(i));
            template.getColumnPositions().put(templateValues.get(i),i);
        }
        return template;
    }

    public static boolean matchTemplate(String templateName,List<String> header) throws IOException {
        Template template = loadTemplate(templateName);
        if (header.size()<template.getColumns().size()) return false;
        for(String column:header){
            if(!template.getColumns().contains(column)){
                return false;
            }
        }
        return true;
    }
}
