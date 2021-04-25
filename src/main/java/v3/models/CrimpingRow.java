package v3.models;

import v3.interfaces.Row;
import v3.utils.JavaUtil;

import java.util.List;

public class CrimpingRow implements Row {
    List<String> values;
    List<String> columns;
    String connectorName;
    String cavity;
    String wireCustomerName;
    String wireFM;
    String crimpingType;
    String crimpingDouble;
    String comment;


    public String getPrimaryKey(){
        return JavaUtil.hashString(this.toString());
    }

    @Override
    public String toString(){
        return "connectorName="+connectorName+",cavity="+cavity+",wireCustomerName="+wireCustomerName+",wireFM="+wireFM+",crimpingType="+crimpingType+",crimpingDouble="+crimpingDouble;
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

    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }

    public String getCavity() {
        return cavity;
    }

    public void setCavity(String cavity) {
        this.cavity = cavity;
    }

    public String getWireCustomerName() {
        return wireCustomerName;
    }

    public void setWireCustomerName(String wireCustomerName) {
        this.wireCustomerName = wireCustomerName;
    }

    public String getWireFM() {
        return wireFM;
    }

    public void setWireFM(String wireFM) {
        this.wireFM = wireFM;
    }

    public String getCrimpingType() {
        return crimpingType;
    }

    public void setCrimpingType(String crimpingType) {
        this.crimpingType = crimpingType;
    }

    public String getCrimpingDouble() {
        return crimpingDouble;
    }

    public void setCrimpingDouble(String crimpingDouble) {
        this.crimpingDouble = crimpingDouble;
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
        return values;
    }
}
