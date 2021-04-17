package v3.models;

import java.util.HashMap;
import java.util.Map;

/**
 * handles the configuration of each input
 */
public class Configs {
    Map<String,String> values;

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }

    public Configs(){
        this.values = new HashMap<>();
    }
}
