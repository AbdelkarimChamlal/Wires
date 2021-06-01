package v3.services;

import v3.data.ExportData;
import v3.models.Configs;
import v3.models.MaxTable;
import v3.models.SpliceDiversity;
import v3.standards.Row;
import v3.standards.Table;
import v3.utils.JavaUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SplicesGenerator {
    List<SpliceDiversity> spliceDiversities;
    MaxTable maxTable;
    Table spliceKSK;
    Table FMCRelations;
    Configs spliceKSKConfigs;
    Table spliceOutput;

    public SplicesGenerator(MaxTable maxTable, Table spliceKSK, Configs spliceKSKConfigs, Table FMCRelations) {
        this.maxTable = maxTable;
        this.FMCRelations = FMCRelations;
        this.spliceKSK = spliceKSK;
        this.spliceKSKConfigs = spliceKSKConfigs;
        this.spliceDiversities = extractSpliceDiversities(spliceKSK,spliceKSKConfigs);
        updateSpliceFromExternNameToInternName();
        combineJoinsForTheSameTwist();
        this.spliceOutput = prepareSpliceOutput();
    }

    public void generateDiversities(){
        spliceDiversities.forEach(diversity ->{
            String wires = diversity.getWiresAtLeft() + ":" + diversity.getWiresAtRight();
            List<String> wireList = JavaUtil.convertArrayToList(wires.split(":"));
            wireList.forEach(wire->{
                Row row = new Row();
                row.addValue(diversity.getInternName());
                row.addValue(diversity.getInternName() + " ~ " + diversity.getMatrix());
                row.addValue(diversity.getMatrix());
                row.addValue(wire);
                row.addValue((diversity.getWiresAtLeft().contains(wire))?"L":"R");
                row.addValue("SK");
                row.addValue("PM");
                spliceOutput.addRow(row);
            });
        });
    }

    public void exportSplicedOutput(String filePath,String sheetName) throws IOException {
        ExportData.exportTableToExcel(filePath,sheetName,this.spliceOutput);
    }

    void updateSpliceFromExternNameToInternName(){
        spliceDiversities.forEach(spliceDiversity->{
            maxTable.getRows().forEach(row->{
                if(row.getValues().get(maxTable.getColumns().indexOf(maxTable.getMaxConfigs().getConfigValue("fromExternSource"))).equals(spliceDiversity.getExternName())){
                    spliceDiversity.setInternName(row.getValue(maxTable.getColumns().indexOf(maxTable.getMaxConfigs().getConfigValue("fromSource"))));
                }
                if(row.getValues().get(maxTable.getColumns().indexOf(maxTable.getMaxConfigs().getConfigValue("toExternSource"))).equals(spliceDiversity.getExternName())){
                    spliceDiversity.setInternName(row.getValue(maxTable.getColumns().indexOf(maxTable.getMaxConfigs().getConfigValue("toSource"))));
                }
            });
        });
    }

    void combineJoinsForTheSameTwist(){
        // extract unique twists (unique wire special wires values)
        List<String> uniqueTwists = new ArrayList<>();
        maxTable.getRows().forEach(row->{
            if(row.getValues().get(maxTable.getColumns().indexOf(maxTable.getMaxConfigs().getConfigValue("wireType"))).equalsIgnoreCase("Twisted Wire")){
                String twist = row.getValues().get(maxTable.getColumns().indexOf(maxTable.getMaxConfigs().getConfigValue("wireSpecialWire")));
                if(!uniqueTwists.contains(twist))uniqueTwists.add(twist);
            }
        });

        // extract different joins that are connected to the same twist
        List<String> joinsWithSameTwist = new ArrayList<>();
        uniqueTwists.forEach(uniqueTwist->{
            List<String> joins = new ArrayList<>();
            maxTable.getRows().forEach(row->{
                String twist = row.getValues().get(maxTable.getColumns().indexOf(maxTable.getMaxConfigs().getConfigValue("wireSpecialWire")));
                if(twist.equals(uniqueTwist)){
                    String fromSource = row.getValues().get(maxTable.getColumns().indexOf(maxTable.getMaxConfigs().getConfigValue("fromSource")));
                    String toSource = row.getValues().get(maxTable.getColumns().indexOf(maxTable.getMaxConfigs().getConfigValue("toSource")));
                    if((fromSource.startsWith("J") || fromSource.startsWith("j")) && !joins.contains(fromSource) ) {
                        joins.add(fromSource);
                    }
                    if((toSource.startsWith("J") || toSource.startsWith("j")) && !joins.contains(toSource) ) {
                        joins.add(toSource);
                    }
                }

            });
            if(joins.size()>0){
                String joinsCombined = JavaUtil.sortAndConcatWithValue(joins,"-");
                if(!joinsWithSameTwist.contains(joinsCombined))joinsWithSameTwist.add(joinsCombined);
            }
        });

        // change the join name in each diversity to the combined joins
        joinsWithSameTwist.forEach(combinedJoins ->{
            spliceDiversities.forEach(spliceDiversity -> {
                if(combinedJoins.contains(spliceDiversity.getInternName())){
                    spliceDiversity.setInternName(combinedJoins);
                }
            });
        });

    }



    Table prepareSpliceOutput(){
        Table table = new Table();
        Row row = new Row();
        row.addValue("Splice Name");
        row.addValue("Splice Diversity");
        row.addValue("Matrix");
        row.addValue("Wire");
        row.addValue("Direction");
        row.addValue("SK");
        row.addValue("PM");
        table.addRow(row);
        return table;
    }

    List<SpliceDiversity> extractSpliceDiversities(Table spliceKSK,Configs spliceKSKConfigs){
        List<SpliceDiversity> spliceDiversities = new ArrayList<>();
        int spliceNamePosition = spliceKSK.getRow(0).getValues().indexOf(spliceKSKConfigs.getConfigValue("splice"));
        int matrixPosition = spliceKSK.getRow(0).getValues().indexOf(spliceKSKConfigs.getConfigValue("matrix"));
        int wiresLeftPosition = spliceKSK.getRow(0).getValues().indexOf(spliceKSKConfigs.getConfigValue("wiresLeft"));
        int wiresRightPosition = spliceKSK.getRow(0).getValues().indexOf(spliceKSKConfigs.getConfigValue("wiresRight"));
        int checkPosition = spliceKSK.getRow(0).getValues().indexOf(spliceKSKConfigs.getConfigValue("check"));
        int wiresPosition = spliceKSK.getRow(0).getValues().indexOf(spliceKSKConfigs.getConfigValue("wires"));

        for(int i = 1 ; i < spliceKSK.getRows().size() ; i++){
            SpliceDiversity spliceDiversity = new SpliceDiversity();
            Row spliceKSKRow = spliceKSK.getRow(i);
            if(spliceKSKRow.getValues().size()>0){
                spliceDiversity.setCheck(spliceKSKRow.getValue(checkPosition));
                spliceDiversity.setExternName(spliceKSKRow.getValue(spliceNamePosition));
                spliceDiversity.setMatrix(spliceKSKRow.getValue(matrixPosition));
                spliceDiversity.setWiresAtLeft(spliceKSKRow.getValue(wiresLeftPosition));
                spliceDiversity.setWiresAtRight(spliceKSKRow.getValue(wiresRightPosition));
                spliceDiversity.setWires(spliceKSKRow.getValue(wiresPosition));
                spliceDiversities.add(spliceDiversity);
            }
        }

        return spliceDiversities;
    }
}
