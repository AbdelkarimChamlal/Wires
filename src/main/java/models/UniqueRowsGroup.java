package models;

import org.apache.poi.ss.usermodel.Row;

import java.util.List;

public class UniqueRowsGroup {
    String key;
    List<Row> group;

    public String getKey() {
        return key;
    }

    public UniqueRowsGroup(String key, List<Row> group) {
        this.key = key;
        this.group = group;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<Row> getGroup() {
        return group;
    }

    public void setGroup(List<Row> group) {
        this.group = group;
    }
}
