package v2.utils;

import org.apache.commons.collections4.map.HashedMap;
import v2.models.Template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public static Template loadTemplate(String templateName,String sheetName) throws IOException {
        Template template = new Template();
        List<List<String>> templateTable = ImportData.importWorkSheet("templates/"+templateName,sheetName);
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
        for(String column:template.getColumns()){
            if(!header.contains(column)){
                return false;
            }
        }
        return true;
    }


    public static List<List<String>> convertToTemplate(List<List<String>> table,String templateName) throws IOException {

        List<List<String>> convertedTable = new ArrayList<>(table.size());

        Template template = loadTemplate(templateName);

        convertedTable.add(template.getColumns());

        // <in template,in original table>
        Map<Integer,Integer> positionsMap = new HashedMap<>();

        for(String templateColumn:template.getColumns()){
            for(int i = 0 ; i < table.get(0).size() ; i ++){
                if(templateColumn.equals(table.get(0).get(i))){
                    positionsMap.put(template.getColumnPositions().get(templateColumn),i);
                }
            }
        }

        for(int i = 1 ; i < table.size() ; i++){
            List<String> row = RowUtil.emptyRow(table.get(i).size());
            for(int j = 0 ; j < row.size() ; j++){
                if(positionsMap.containsKey(j)){
                    row.set(j,table.get(i).get(positionsMap.get(j)));
                }else{
                    row.set(j,"");
                }
            }
            convertedTable.add(row);
        }

        return convertedTable;
    }

    public static List<List<String>> convertToTemplate(List<List<String>> table,String templateName,String sheetNameInTemplate) throws IOException {
        if (table.size()==0){
            return new ArrayList<>();
        }
        List<List<String>> convertedTable = new ArrayList<>(table.size());

        Template template = loadTemplate(templateName,sheetNameInTemplate);

        // <in template,in original table>
        Map<Integer,Integer> positionsMap = new HashedMap<>();
        for(String templateColumn:template.getColumns()){
            for(int i = 0 ; i < table.get(0).size() ; i ++){
                if(templateColumn.equals(table.get(0).get(i))){
                    positionsMap.put(template.getColumnPositions().get(templateColumn),i);
                }
            }
        }

        for(int i = 1 ; i < table.size() ; i++){
            List<String> row = RowUtil.emptyRow(table.get(i).size());
            for(int j = 0 ; j < row.size() ; j++){
                if(positionsMap.containsKey(j)){
                    row.set(j,table.get(i).get(positionsMap.get(j)));
                }else{
                    row.set(j,"");
                }
            }
            convertedTable.add(row);
        }

        return convertedTable;
    }
}
