package v3.interfaces;

import java.util.List;
import java.util.Map;

public interface Row {
    public String getValue(int columnPosition);
    public void addValue(String column);
    public boolean containsValue(String value);
    public List<String> getValues();
}
