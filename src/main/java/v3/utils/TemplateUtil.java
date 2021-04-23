package v3.utils;

import v3.data.ConvertData;
import v3.data.ImportData;
import v3.interfaces.Table;
import v3.models.Template;
import v3.standards.Row;

import java.io.IOException;

public class TemplateUtil {

    public static Template loadTemplate(String templateName,int templatePosition) throws IOException {
        Template template = new Template();
        Table table = ConvertData.convertSheetIntoTable(ImportData.importSheet(templateName,templatePosition));

        for(int i = 0 ; i < table.getRows().get(0).getValues().size() ; i++){
            template.addColumn(table.getRows().get(0).getValue(i));
            template.putColumnPosition(table.getRows().get(0).getValue(i),i);
        }

        return template;
    }

    public static Template loadTemplate(String templateName,String templateSheetName) throws IOException {
        Template template = new Template();
        Table table = ConvertData.convertSheetIntoTable(ImportData.importSheet(templateName,templateSheetName));

        for(int i = 0 ; i < table.getRows().get(0).getValues().size() ; i++){
            template.addColumn(table.getRows().get(0).getValue(i));
            template.putColumnPosition(table.getRows().get(0).getValue(i),i);
        }

        return template;
    }

    public static boolean matchTableToTemplate(Row tableHeader,Template template){
        for(String templateColumn:template.getColumns()){
            if(!tableHeader.containsValue(templateColumn))return false;
        }
        return true;
    }

    public static v3.standards.Table setTemplate(Table table, Template template){
        v3.standards.Table convertedTable = new v3.standards.Table();
        Row header = new Row();
        header.setValues(template.getColumns());
        convertedTable.getRows().add(header);
        for(int i = 1 ; i < table.getRows().size() ; i++){
            Row row = table.getRows().get(i);
            Row convertedRow = RowUtil.emptyRow(header.getValues().size());
            for(int j = 0 ; j < row.getValues().size() ; j++){
                if(template.containsColumn(table.getRows().get(0).getValues().get(j))){
                    convertedRow.getValues().set(template.getColumnPosition(table.getRows().get(0).getValues().get(j)),row.getValue(j));
                }
            }
            convertedTable.getRows().add(convertedRow);
        }
        return convertedTable;
    }
}
