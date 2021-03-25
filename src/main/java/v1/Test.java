package v1;

import v1.utils.ImportExcel;

import java.util.List;

public class Test {

    static int error_code = 0;
    public static void main(String[] args) {

        List<List<String>> values = null;

        try{
            values = ImportExcel.importWorkSheet("/max.xlsx",1);
        }catch (Exception e){
            error_code = 1;
        }

        if(values != null){
            //values available
        }

    }
}
