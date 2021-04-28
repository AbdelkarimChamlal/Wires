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
    static String maxFileName = "splice.xlsx";

    static String crimpingFileName = "crimping.xlsx";
    static String crimpingConfigFileName = "crimping.conf";
    static String maxConfigFileName = "maxWireList.conf";
    static String maxTemplateName = "maxTemplate.xlsx";
    static String crimpingTemplateName = "crimping.xlsx";
    static String plausibilityFileName = "plausibility.xlsm";
    static String plausibilityConfigsFileName = "plausibility.conf";
    static String resultName = "maxResult.xlsx";
    static String separateValue = " <-> ";

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
                    row.addValue(s+separateValue+d);
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

        // check if a twisted wire goes into different joins
        // in this case the join diversity should be called something like join1-join2-wires

        // important index
        int wireTypePosition = table.getRow(0).getValues().indexOf(maxConfigs.getConfigValue("wireType"));
        int wireKeyPosition = table.getRow(0).getValues().indexOf(maxConfigs.getConfigValue("wireKey"));
        int wireSpecialWiresPosition = table.getRow(0).getValues().indexOf(maxConfigs.getConfigValue("wireSpecialWire"));
        int toSourcePosition = table.getRow(0).getValues().indexOf(maxConfigs.getConfigValue("toSource"));
        int fromSourcePosition = table.getRow(0).getValues().indexOf(maxConfigs.getConfigValue("fromSource"));
        int fromCavityPosition = table.getRow(0).getValues().indexOf(maxConfigs.getConfigValue("fromCavity"));
        int fromCrimpingTypePosition = table.getRow(0).getValues().indexOf(maxConfigs.getConfigValue("fromCrimpingType"));
        int fromCrimpingDoublePosition = table.getRow(0).getValues().indexOf(maxConfigs.getConfigValue("fromCrimpingDouble"));
        int toCavityPosition = table.getRow(0).getValues().indexOf(maxConfigs.getConfigValue("toCavity"));
        int toCrimpingTypePosition = table.getRow(0).getValues().indexOf(maxConfigs.getConfigValue("toCrimpingType"));
        int toCrimpingDoublePosition = table.getRow(0).getValues().indexOf(maxConfigs.getConfigValue("toCrimpingDouble"));


        int joinDiversity = table.getRow(0).getValues().indexOf("Join Diversity");



        // combine joins that are connected to each other
        List<String> combinedJoins = new ArrayList<>();

        // finalCombination
        List<String> finalCombinations = new ArrayList<>();

        joins.forEach(join ->{
            StringBuilder combinedJoin = new StringBuilder(join);
            table.getRows().forEach(row ->{
                if(combinedJoin.toString().contains(row.getValue(toSourcePosition)) && row.getValue(fromSourcePosition).startsWith("J")){
                    if(!combinedJoin.toString().contains(row.getValue(fromSourcePosition))){
                        combinedJoin.append("&").append(row.getValue(fromSourcePosition));
                    }
                }
                if(combinedJoin.toString().contains(row.getValue(fromSourcePosition)) && row.getValue(toSourcePosition).startsWith("J")){
                    if(!combinedJoin.toString().contains(row.getValue(toSourcePosition))){
                        combinedJoin.append("&").append(row.getValue(toSourcePosition));
                    }
                }
            });
            String finalCombination = JavaUtil.sortAndConcatWithValue(JavaUtil.convertArrayToList(combinedJoin.toString().split("&"))," - ");
            if (!combinedJoins.contains(finalCombination))combinedJoins.add(finalCombination);
        });



        table.getRows().forEach(row -> {
            if(!row.getValue(joinDiversity).equals("-")){
                String[] diversityParts = row.getValue(joinDiversity).split(separateValue);
                combinedJoins.forEach(combinedJoin->{
                    if(combinedJoin.contains(diversityParts[0])){
                        row.getValues().set(joinDiversity,combinedJoin +separateValue+diversityParts[1]);
                    }
                });
            }
        });

        // extract twisted wires
        List<Row> twistedWires = getTwistedWires(table.getRows(),wireTypePosition);
        // collect unique twists
        List<String> uniqueTwists = getUniqueTwists(table.getRows(),wireSpecialWiresPosition);

        // for each unique twist
        uniqueTwists.forEach( twist ->{
            // get rows with this twist
            List<Row> twistRows = getTwistsByWireSpecialWires(twistedWires,twist,wireSpecialWiresPosition);
            // collect all joins connected to this twist
            List<String> twistJoins = extractTwistJoins(twistRows,toSourcePosition,fromSourcePosition);
            // get the combination of joins for those joins
            List<String> twistJoinsCombined = new ArrayList<>();
            twistJoins.forEach(twistJoin ->{
                combinedJoins.forEach(combinedJoin->{
                    if(combinedJoin.contains(twistJoin) && !twistJoinsCombined.contains(combinedJoin))twistJoinsCombined.add(combinedJoin);
                });
            });
            // if there are joins connected to this twist
            if(twistJoinsCombined.size()>0){
                // update the value of joinDiversity to the sortedAndConcatenated Value of all joins
                String joinsSortedAndConcatenated = JavaUtil.sortAndConcatWithValue(twistJoinsCombined," / ");
                twistRows.forEach(twistRow ->{
                    String[] diversityParts = twistRow.getValue(joinDiversity).split(separateValue);
                    String newDiv = joinsSortedAndConcatenated +separateValue+diversityParts[1];
                    if(!finalCombinations.contains(joinsSortedAndConcatenated))finalCombinations.add(joinsSortedAndConcatenated);
                    twistRow.getValues().set(joinDiversity,newDiv);
                });
            }
        });





        table.getRows().forEach(row ->{
            if(!row.getValue(joinDiversity).equals("-")){
                String comb = row.getValue(joinDiversity).split(separateValue)[0];
                finalCombinations.forEach(finalCombination ->{
                    if(finalCombination.contains(comb))row.getValues().set(joinDiversity,finalCombination+separateValue+row.getValue(joinDiversity).split(separateValue)[1]);
                });
            }
        });

        // export the spliced table
        ExportData.exportTableToExcel("results/spliced.xlsx","spliced",table);
    }



    public static List<String> extractTwistJoins(List<Row> rows,int toSource,int fromSource){
        List<String> twistJoins = new ArrayList<>();
        rows.forEach(row->{
            if(row.getValue(toSource).startsWith("J") && !twistJoins.contains(row.getValue(toSource))){
                twistJoins.add(row.getValue(toSource));
            }else if(row.getValue(fromSource).startsWith("J") && !twistJoins.contains(row.getValue(fromSource))){
                twistJoins.add(row.getValue(fromSource));
            }
        });
        return twistJoins;
    }

    public static List<Row> getTwistsByWireSpecialWires(List<Row> rows,String wireSpecialWiresValue,int wireSpecialWiresPosition){
        List<Row> twist = new ArrayList<>();
        rows.forEach( row ->{
            if(row.getValue(wireSpecialWiresPosition).equals(wireSpecialWiresValue))twist.add(row);
        });
        return twist;
    }

    public static List<String> getUniqueTwists(List<Row> twistedRows,int wireSpecialWiresPosition){
        List<String> uniqueTwists = new ArrayList<>();
        twistedRows.forEach(twistedRow ->{
            if(!uniqueTwists.contains(twistedRow.getValue(wireSpecialWiresPosition)))uniqueTwists.add(twistedRow.getValue(wireSpecialWiresPosition));
        });
        return uniqueTwists;
    }

    public static List<Row> getTwistedWires(List<Row> rows,int wireTypePosition){
        List<Row> twistedWires = new ArrayList<>();
        rows.forEach(row ->{
            if(row.getValue(wireTypePosition).equals("Twisted Wire")) twistedWires.add(row);
        });
        return twistedWires;
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
