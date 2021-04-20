package v3.models;

import java.util.List;
import java.util.Map;

public class Plausibility {
    String FM;
    Map<String, List<String>> plausibilityMap;

    public String getFM() {
        return FM;
    }

    public void setFM(String FM) {
        this.FM = FM;
    }

    public Map<String, List<String>> getPlausibilityMap() {
        return plausibilityMap;
    }

    public void setPlausibilityMap(Map<String, List<String>> plausibilityMap) {
        this.plausibilityMap = plausibilityMap;
    }
}
