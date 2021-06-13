package v3.services;

import org.apache.commons.math3.analysis.function.Max;
import v3.data.ExportData;
import v3.models.*;
import v3.standards.Row;
import v3.standards.Table;
import v3.utils.JavaUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class SplicesGenerator {
    List<SpliceDiversity> spliceDiversities;
    MaxTable maxTable;
    Table spliceKSK;
    Table FMCRelations;
    Configs spliceKSKConfigs;
    Table spliceOutput;
    CrimpingTable crimpingTable;

    public SplicesGenerator(MaxTable maxTable, CrimpingTable crimpingTable, Table spliceKSK, Configs spliceKSKConfigs, Table FMCRelations) {
        this.maxTable = maxTable;
        this.FMCRelations = FMCRelations;
        this.spliceKSK = spliceKSK;
        this.crimpingTable = crimpingTable;
        this.spliceKSKConfigs = spliceKSKConfigs;
        this.spliceDiversities = extractSpliceDiversities(spliceKSK,spliceKSKConfigs);
        updateSpliceFromExternNameToInternName();
        combineJoinsForTheSameTwist();
        this.spliceOutput = prepareSpliceOutput();
    }

    public void generateDiversities(){

        int wireSKPosition = maxTable.getColumns().indexOf(maxTable.getMaxConfigs().getConfigValue("wireSK"));
        int wirePNPosition = maxTable.getColumns().indexOf(maxTable.getMaxConfigs().getConfigValue("wirePN"));
        int wireSSKPosition = maxTable.getColumns().indexOf(maxTable.getMaxConfigs().getConfigValue("sSK"));



        Map<String,List<String>> joinAllModules = getAllModulesRelatedToJoin();

        spliceDiversities.forEach(diversity ->{
            String wires;
            List<String> diversityModules = JavaUtil.convertArrayToList(diversity.getMatrix().split("/"));

            if(diversity.getWiresAtLeft().length() == 0){
                wires = diversity.getWiresAtRight();
            }else if(diversity.getWiresAtRight().length() == 0){
                wires = diversity.getWiresAtLeft();
            }else{
                wires = diversity.getWiresAtLeft() + ":" + diversity.getWiresAtRight();
            }
            List<String> wireList = JavaUtil.convertArrayToList(wires.split(":"));
            wireList.forEach(wire->{
                Row row = new Row();
                String module = getWireModule(wire,diversity.getMatrix());
                List<MaxRow> rows = getRowsForWireWithSameModule(wire, module);

                String wireSK = "-";
                String wirePN = "-";
                String doubleSK = "-";

                if(rows.size() == 1){
                    wireSK = rows.get(0).getValue(wireSKPosition);
                    wirePN = rows.get(0).getValue(wirePNPosition);
                    doubleSK = isADouble(rows.get(0))?rows.get(0).getValue(wireSSKPosition):"-";
                }else{
                    List<MaxRow> singles = new ArrayList<>();
                    List<MaxRow> doubles = new ArrayList<>();

                    for (MaxRow maxRow : rows) {
                        if (!maxRow.getToCrimpingType().equalsIgnoreCase("double") && !maxRow.getFromCrimpingType().equalsIgnoreCase("double")) {
                            singles.add(maxRow);
                        } else {
                            doubles.add(maxRow);
                        }
                    }

                    for(MaxRow doubleCrimping:doubles){
                        // check for double crimping at destination
                        if(doubleCrimping.getToCrimpingType().equalsIgnoreCase("double")){
                            String crimpedWithModule = getWireModule(doubleCrimping.getToCrimpingDouble(), diversity.getMatrix());
                            if(joinAllModules.get(diversity.getExternName()).contains(crimpedWithModule)){
                                if(diversityModules.contains(crimpedWithModule)){
                                    System.out.println("found");
                                }else{
                                    System.out.println("match not found");
                                }
                            }else{
                                System.out.println("module not related to join");
                            }
                        }



                        // check for double crimping at source
                        if(doubleCrimping.getFromCrimpingType().equalsIgnoreCase("double")){
                            String crimpedWithModule = getWireModule(doubleCrimping.getFromCrimpingDouble(), diversity.getMatrix());
                            if(joinAllModules.get(diversity.getExternName()).contains(crimpedWithModule)){
                                if(diversityModules.contains(crimpedWithModule)){
                                    System.out.println("found");
                                }else{
                                    System.out.println("match not found");
                                }
                            }else{
                                System.out.println("module not related to join");
                            }
                        }
                    }

                    System.out.println(wire + " doubles : " + doubles.size() + ", singles : " + singles.size());
                }


                String twistSK = isATwist(rows)?rows.get(0).getValue(wireSSKPosition):"-";

                row.addValue(diversity.getInternName());
                row.addValue(diversity.getInternName() + " ~ " + diversity.getMatrix());
                row.addValue(diversity.getMatrix());
                row.addValue(wire);
                row.addValue(module);
                row.addValue(wireSK);
                row.addValue(wirePN);
                row.addValue(twistSK);
                row.addValue(doubleSK);
                row.addValue((diversity.getWiresAtLeft().contains(wire))?"L":"R");

                spliceOutput.addRow(row);
            });
        });
    }

    boolean isATwist(List<MaxRow> rows){

        if(rows.size() > 0){
            int wireTypePosition = maxTable.getColumns().indexOf(maxTable.getMaxConfigs().getConfigValue("wireType"));
            return rows.get(0).getValue(wireTypePosition).equals("Twisted Wire");
        }


        return false;
    }

    boolean isADouble(MaxRow row){
        return (row.getFromCrimpingType().equalsIgnoreCase("double") || row.getToCrimpingType().equalsIgnoreCase("double"));
    }

    String getWireModule(String wire,String matrix){
        AtomicReference<String> module = new AtomicReference<>("");
        List<String> modulesInMatrix = JavaUtil.convertArrayToList(matrix.split("/"));
        maxTable.getMaxRows().forEach(maxRow ->{
            if(maxRow.getWireKey().equals(wire) && modulesInMatrix.contains(maxRow.getModuleName())) {
                module.set(maxRow.getModuleName());
            }
        });
        return module.get();
    }

    Map<String, List<String>> getAllModulesRelatedToJoin(){
        Map<String,List<String>> joinAllModules = new HashMap<>();
        spliceDiversities.forEach(spliceDiversity -> {
            if(!joinAllModules.containsKey(spliceDiversity.getExternName())){
                joinAllModules.put(spliceDiversity.getExternName(),new ArrayList<>());
            }
            List<String> joinModules = joinAllModules.get(spliceDiversity.getExternName());
            List<String> diversityModules = JavaUtil.convertArrayToList(spliceDiversity.getMatrix().split("/"));
            diversityModules.forEach(module->{
                if(!joinModules.contains(module)) joinModules.add(module);
            });
            joinAllModules.put(spliceDiversity.getExternName(),joinModules);
        });

        return joinAllModules;
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
        row.addValue("Wire Module");
        row.addValue("SK");
        row.addValue("PN");
        row.addValue("Twist SK");
        row.addValue("Double SK");
        row.addValue("Direction");
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

    List<MaxRow> getRowsForWireWithSameModule(String wire, String module){
        List<MaxRow> maxRows = new ArrayList<>();

        maxTable.getMaxRows().forEach(maxRow -> {
            if(maxRow.getWireKey().equals(wire) && maxRow.getModuleName().equals(module)){
                maxRows.add(maxRow);
            }
        });

        return maxRows;
    }
}
