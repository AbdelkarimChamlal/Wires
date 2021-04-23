package v3;

import v3.data.ConvertData;
import v3.data.ImportData;
import v3.data.ImportValues;
import v3.interfaces.Table;
import v3.models.*;
import v3.utils.TableUtil;
import v3.utils.TemplateUtil;


public class TestingArea {
    static String maxFileName = "max.xlsx";

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
        Configs crimpingConfigs = ConvertData.convertStringToConfigs(ImportData.importText(ImportValues.CONFIG_FOLDER+crimpingConfigFileName));
        Configs plausibilityConfigs = ConvertData.convertStringToConfigs(ImportData.importText(ImportValues.CONFIG_FOLDER+plausibilityConfigsFileName));

        // load templates
        Template maxTemplate = TemplateUtil.loadTemplate(ImportValues.TEMPLATE_FOLDER+maxTemplateName,0);
        Template crimpingTemplate = TemplateUtil.loadTemplate(ImportValues.TEMPLATE_FOLDER+crimpingTemplateName,0);

        // initialize variables for Tables
        Table maxT,plausibilityT,crimpingT;

        // try to import Tables using the configurations provided in the table configuration
        maxT = TableUtil.importTableUsingConfigurations(ImportValues.UPLOAD_FOLDER+maxFileName,maxConfigs,"MAX WIRE LIST");
        crimpingT = TableUtil.importTableUsingConfigurations(ImportValues.UPLOAD_FOLDER+crimpingFileName,crimpingConfigs,"CRIMPING REPORT");
        plausibilityT = TableUtil.importTableUsingConfigurations(ImportValues.UPLOAD_FOLDER+plausibilityFileName,plausibilityConfigs,"PLAUSIBILITY");

        // initialize customized tables for each table
        MaxTable maxTable = new MaxTable(maxConfigs,maxTemplate,maxT);
        CrimpingTable crimpingTable = new CrimpingTable(crimpingConfigs,crimpingTemplate,crimpingT);

        // FIXME : for the moment being plausibility table takes some extra steps to be converted
        //  into PlausibilityTable, mainly in removing some columns and rows.
        //  those steps are done currently by PlausibilityTable by calling formatPlausibilityTable.
        //  while it works fine, but it is not dynamic for changes
        // FIXME : use configuration file to format the plausibility file before turning it into a PlausibilityTable instant
        PlausibilityTable plausibility = new PlausibilityTable(plausibilityT,plausibilityConfigs);


    }
}
