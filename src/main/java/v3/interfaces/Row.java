package v3.interfaces;

import java.util.List;


public interface Row {
    String getValue(int columnPosition);
    void addValue(String column);
    boolean containsValue(String value);
    List<String> getValues();
}
