package v3;

import v3.data.ConvertData;
import v3.data.ImportData;
import v3.models.Configs;
import v3.models.Table;

import java.io.IOException;

public class TestingArea {

    public static String filePath = "uploads/";
    public static String configsPath = "configs/";
    public static void main(String[] args) throws IOException {
        Table table = ConvertData.convertSheetIntoTable(ImportData.importSheet(filePath+"max.xlsx",0));
        Configs configs = ConvertData.convertStringToConfigs(ImportData.importText(configsPath+"maxWireList.conf"));
        System.out.println(configs.getValues());
    }
}
