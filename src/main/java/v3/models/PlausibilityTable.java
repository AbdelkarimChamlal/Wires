package v3.models;

import v3.interfaces.Table;
import v3.utils.JavaUtil;
import v3.utils.RowUtil;
import v3.utils.TableUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlausibilityTable {
    Table plausibilityTable;
    Map<String,Plausibility> plausibilityItemsMap;

    public PlausibilityTable(v3.interfaces.Table table){
        this.plausibilityTable = table;
        formatPlausibilityTable();
        this.plausibilityItemsMap = new HashMap<>();
        convertTableRowsIntoPlausibilityItems();

    }

    public void formatPlausibilityTable(){
        // remove unwanted rows
        TableUtil.removeRow(plausibilityTable,2);
        TableUtil.removeRow(plausibilityTable,0);

        // remove unwanted columns
        TableUtil.removeColumn(plausibilityTable,10);
        TableUtil.removeColumn(plausibilityTable,9);
        TableUtil.removeColumn(plausibilityTable,8);
        TableUtil.removeColumn(plausibilityTable,7);
        TableUtil.removeColumn(plausibilityTable,6);
        TableUtil.removeColumn(plausibilityTable,5);
        TableUtil.removeColumn(plausibilityTable,4);
        TableUtil.removeColumn(plausibilityTable,3);
        TableUtil.removeColumn(plausibilityTable,2);
        TableUtil.removeColumn(plausibilityTable,1);

    }

    public void convertTableRowsIntoPlausibilityItems(){
        for(int i = 1 ; i < plausibilityTable.getRows().size() ; i++){
            Plausibility plausibility = new Plausibility();
            plausibility.setFM(plausibilityTable.getRows().get(i).getValue(0));
            Map<String, List<String>> plausibilityMap = new HashMap<>();

            for(int j = 1 ; j < plausibilityTable.getRows().get(0).getValues().size(); j++){

                if (!plausibilityMap.containsKey(plausibilityTable.getRows().get(i).getValue(j))) {
                    plausibilityMap.put(plausibilityTable.getRows().get(i).getValue(j), new ArrayList<>());
                }
                plausibilityMap.get(plausibilityTable.getRows().get(i).getValue(j)).add(plausibilityTable.getRows().get(0).getValue(j));

            }

            plausibility.setPlausibilityMap(plausibilityMap);
            plausibilityItemsMap.put(plausibility.getFM(),plausibility);
            System.out.println(plausibility.getPlausibilityMap());
        }
    }
}
