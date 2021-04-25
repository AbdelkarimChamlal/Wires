package v3.models;

import v3.interfaces.Row;
import v3.utils.JavaUtil;

import java.util.List;

public class MaxRow implements Row {
    String moduleName;
    String modulePIN;
    String wireKey;
    String fromSource;
    String fromCavity;
    String fromCrimpingType;
    String fromCrimpingDouble;
    String toSource;
    String toCavity;
    String toCrimpingType;
    String toCrimpingDouble;
    List<String> values;
    List<String> columns;

    // TODO override the clone method


    @Override
    public String toString(){
        return "moduleName="+moduleName+",modulePIN="+modulePIN+",wireKey="+wireKey+",fromSource="+fromSource+",fromCavity="+fromCavity+",fromCrimpingType="+fromCrimpingType+",fromCrimpingDouble="+fromCrimpingDouble+",toSource="+toSource+",toCavity="+toCavity+",toCrimpingType="+toCrimpingType+",toCrimpingDouble="+toCrimpingDouble;
    }

    public String getPrimaryKey(){
        return JavaUtil.hashString(this.toString());
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModulePIN() {
        return modulePIN;
    }

    public void setModulePIN(String modulePIN) {
        this.modulePIN = modulePIN;
    }

    public String getWireKey() {
        return wireKey;
    }

    public void setWireKey(String wireKey) {
        this.wireKey = wireKey;
    }

    public String getFromSource() {
        return fromSource;
    }

    public void setFromSource(String fromSource) {
        this.fromSource = fromSource;
    }

    public String getFromCavity() {
        return fromCavity;
    }

    public void setFromCavity(String fromCavity) {
        this.fromCavity = fromCavity;
    }

    public String getFromCrimpingType() {
        return fromCrimpingType;
    }

    public void setFromCrimpingType(String fromCrimpingType) {
        this.fromCrimpingType = fromCrimpingType;
    }

    public String getFromCrimpingDouble() {
        return fromCrimpingDouble;
    }

    public void setFromCrimpingDouble(String fromCrimpingDouble) {
        this.fromCrimpingDouble = fromCrimpingDouble;
    }

    public String getToSource() {
        return toSource;
    }

    public void setToSource(String toSource) {
        this.toSource = toSource;
    }

    public String getToCavity() {
        return toCavity;
    }

    public void setToCavity(String toCavity) {
        this.toCavity = toCavity;
    }

    public String getToCrimpingType() {
        return toCrimpingType;
    }

    public void setToCrimpingType(String toCrimpingType) {
        this.toCrimpingType = toCrimpingType;
    }

    public String getToCrimpingDouble() {
        return toCrimpingDouble;
    }

    public void setToCrimpingDouble(String toCrimpingDouble) {
        this.toCrimpingDouble = toCrimpingDouble;
    }

    @Override
    public String getValue(int columnPosition) {
        return values.get(columnPosition);
    }

    @Override
    public void addValue(String column) {
        values.add(column);
    }

    @Override
    public boolean containsValue(String value) {
        return values.contains(value);
    }

    @Override
    public List<String> getValues() {
        return this.values;
    }
}
