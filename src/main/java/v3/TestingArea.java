package v3;

import org.apache.commons.math3.analysis.function.Max;
import v3.data.ConvertData;
import v3.data.ExportData;
import v3.data.ImportData;
import v3.data.ImportValues;
import v3.interfaces.Table;
import v3.models.*;
import v3.standards.Row;
import v3.utils.JavaUtil;
import v3.utils.TableUtil;
import v3.utils.TemplateUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TestingArea {
    static String maxFileName = "splite.xlsx";

    static String crimpingFileName = "crimping.xlsx";
    static String crimpingConfigFileName = "crimping.conf";
    static String maxConfigFileName = "maxWireList.conf";
    static String maxTemplateName = "maxTemplate.xlsx";
    static String crimpingTemplateName = "crimping.xlsx";
    static String plausibilityFileName = "plausibility.xlsm";
    static String plausibilityConfigsFileName = "plausibility.conf";
    static String resultName = "maxResult.xlsx";

    public static void main(String[] args) throws Exception {

        // first step is to load the configs
        Configs maxConfigs = ConvertData.convertStringToConfigs(ImportData.importText(ImportValues.CONFIG_FOLDER+maxConfigFileName));
//        Configs crimpingConfigs = ConvertData.convertStringToConfigs(ImportData.importText(ImportValues.CONFIG_FOLDER+crimpingConfigFileName));
//        Configs plausibilityConfigs = ConvertData.convertStringToConfigs(ImportData.importText(ImportValues.CONFIG_FOLDER+plausibilityConfigsFileName));

        // load templates
        Template maxTemplate = TemplateUtil.loadTemplate(ImportValues.TEMPLATE_FOLDER+maxTemplateName,0);
//        Template crimpingTemplate = TemplateUtil.loadTemplate(ImportValues.TEMPLATE_FOLDER+crimpingTemplateName,0);

        // initialize variables for Tables
        Table maxT,plausibilityT,crimpingT;

        // try to import Tables using the configurations provided in the table configuration
        maxT = TableUtil.importTableUsingConfigurations(ImportValues.UPLOAD_FOLDER+maxFileName,maxConfigs,"MAX WIRE LIST");
//        crimpingT = TableUtil.importTableUsingConfigurations(ImportValues.UPLOAD_FOLDER+crimpingFileName,crimpingConfigs,"CRIMPING REPORT");
//        plausibilityT = TableUtil.importTableUsingConfigurations(ImportValues.UPLOAD_FOLDER+plausibilityFileName,plausibilityConfigs,"PLAUSIBILITY");

        // initialize customized tables for each table
        MaxTable maxTable = new MaxTable(maxConfigs,maxTemplate,maxT);
//        CrimpingTable crimpingTable = new CrimpingTable(crimpingConfigs,crimpingTemplate,crimpingT);
//
//        // FIXME : for the moment being plausibility table takes some extra steps to be converted
//        //  into PlausibilityTable, mainly in removing some columns and rows.
//        //  those steps are done currently by PlausibilityTable by calling formatPlausibilityTable.
//        //  while it works fine, but it is not dynamic for changes
//        // FIXME : use configuration file to format the plausibility file before turning it into a PlausibilityTable instant
//        PlausibilityTable plausibility = new PlausibilityTable(plausibilityT,plausibilityConfigs);
//
//        List<String> connectorsFoundInMax = new ArrayList<>();
//        Map<String, List<String>> cavities = new HashMap<>();
//
//        for(MaxRow maxRow:maxTable.getMaxRows()){
//            if(maxRow.getToSource().startsWith("C")){
//                if(!connectorsFoundInMax.contains(maxRow.getToSource())){
//                    connectorsFoundInMax.add(maxRow.getToSource());
//                    cavities.put(maxRow.getToSource(),new ArrayList<>());
//                    cavities.get(maxRow.getToSource()).add(maxRow.getToCavity());
//                }else{
//                    if(!cavities.get(maxRow.getToSource()).contains(maxRow.getToCavity())){
//                       cavities.get(maxRow.getToSource()).add(maxRow.getToCavity());
//                    }
//                }
//            }
//            if(maxRow.getFromSource().startsWith("C")){
//                if(!connectorsFoundInMax.contains(maxRow.getFromSource())){
//                    connectorsFoundInMax.add(maxRow.getFromSource());
//                    cavities.put(maxRow.getFromSource(),new ArrayList<>());
//                    cavities.get(maxRow.getFromSource()).add(maxRow.getFromCavity());
//                }else{
//                    if(!cavities.get(maxRow.getFromSource()).contains(maxRow.getFromCavity())){
//                        cavities.get(maxRow.getFromSource()).add(maxRow.getFromCavity());
//                    }
//                }
//            }
//        }

        // collect unique joins
        List<String> joins = new ArrayList<>();
        for(MaxRow maxRow:maxTable.getMaxRows()){
            if(maxRow.getFromSource().startsWith("J")){
                if(!joins.contains(maxRow.getFromSource())){
                    joins.add(maxRow.getFromSource());
                }
            }
            if(maxRow.getToSource().startsWith("J")){
                if(!joins.contains(maxRow.getToSource())){
                    joins.add(maxRow.getToSource());
                }
            }
        }

        // prepare output table
        v3.standards.Table table = new v3.standards.Table();
        // add the header to it
        table.addRow(maxT.getRows().get(0));

        // get the last essential column which separates wire data from PNs
        int lastColumnPosition = maxTable.getColumns().indexOf(maxConfigs.getConfigValue("lastEssentialValue"));

        // generate all joins diversities
        joins.forEach(s ->{
            // initialize diversities list for this join
            List<String> diversities = new ArrayList<>();
            // extract rows from max wires list connecting to this join on both ends
            List<MaxRow> maxRows = maxTable.rowsWithSameSourceInBothDirections(s);
            // fill the diversities list with all different diversities
            for(int i = lastColumnPosition + 1 ; i < maxTable.getColumns().size() ; i++){
                String diversity = getDiversity(maxRows,i);
                if(!diversities.contains(diversity)) diversities.add(diversity);
            }
            // duplicate rows for each diversity
            diversities.forEach(d ->{
                // extract wireKeys which make this diversity
                List<String> wiresInDiversity = JavaUtil.convertArrayToList(d.split("::"));
                // make duplicated rows for those wires
                List<Row> rows = duplicateRows(maxRows,wiresInDiversity);
                // if a reference calls this diversity fill it with Xs for all duplicated rows if not leave it empty
                for(int i = lastColumnPosition +1 ; i < maxTable.getColumns().size() ; i++){
                    if(d.equals(getDiversity(maxRows,i))){
                        fillColumnWithValue(rows,i,"X");
                    }else{
                        fillColumnWithValue(rows,i,"");
                    }
                }

                // add this diversity rows to the output table
                rows.forEach(row ->{
                    row.addValue(s+"-"+d);
                    table.addRow(row);
                });
            });
        });

        // add the wires which are not connected to joins
        maxTable.getMaxRows().forEach(maxRow -> {
            // check if the source and destination are not connected to a Joint
            if(!maxRow.getFromSource().startsWith("J") && !maxRow.getToSource().startsWith("J")){
                // create a new row
                Row row = new Row();
                // give it the same values as this maxRow
                row.setValues(maxRow.getValues());
                // set the value of the joint diversity to -
                row.addValue("-");
                // add row the final table
                table.addRow(row);
            }
        });

        table.getRow(0).addValue("Join Diversity");
        // export the spliced table
        ExportData.exportTableToExcel("results/spliced.xlsx","spliced",table);
    }

    public static void fillColumnWithValue(List<Row> rows,int columnPosition,String value){
        rows.forEach(row->{
            row.getValues().set(columnPosition,value);
        });
    }

    public static String getDiversity(List<MaxRow> maxRows,int referencePosition){
        StringBuilder diversity = new StringBuilder();
        for(MaxRow maxRow:maxRows){
            diversity.append((maxRow.getValue(referencePosition).equals("X"))? maxRow.getWireKey()+"::":"");
        }
        if(diversity.lastIndexOf("::")>0) diversity.delete(diversity.lastIndexOf("::"),diversity.length());
        return diversity.toString();
    }

    public static List<Row> duplicateRows(List<MaxRow> maxRows,List<String> rowsKey){
        List<Row> rows = new ArrayList<>();
        maxRows.forEach(maxRow -> {
            if(rowsKey.contains(maxRow.getWireKey())){
                Row row = new Row();
                row.setValues(JavaUtil.duplicateList(maxRow.getValues()));
                rows.add(row);
            }
        });
        return rows;
    }
}
