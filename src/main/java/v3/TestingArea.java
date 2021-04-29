package v3;

import org.apache.commons.math3.analysis.function.Max;
import v3.data.ConvertData;
import v3.data.ExportData;
import v3.data.ImportData;
import v3.data.ImportValues;
import v3.interfaces.Table;
import v3.models.*;
import v3.services.SpliceMaxWireList;
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

        SpliceMaxWireList spliceMaxWireList = new SpliceMaxWireList(maxTable);
        spliceMaxWireList.splice();
        spliceMaxWireList.exportSplicedTable("results/splicedList.xlsx","spliced");
    }



}
