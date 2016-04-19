package tw.kits.voicein.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Henry on 2016/4/12.
 */
public class RecordList implements Serializable {
    private List<Record> record;

    public List<Record> getRecords() {
        return record;
    }

    public void setRecords(List<Record> record) {
        this.record = record;
    }
}
