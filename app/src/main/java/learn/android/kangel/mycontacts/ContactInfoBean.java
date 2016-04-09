package learn.android.kangel.mycontacts;

/**
 * Created by Kangel on 2016/4/5.
 */
public class ContactInfoBean {
    public final static int TYPE_INSERT = 11;
    public final static int TYPE_UPDATE = 12;
    public final static int TYPE_DELETE = 13;
    private boolean isDirty = false;

    private int dataRowId = -1;

    private String dataMIMETYPE;
    private int updateType;
    private String value;
    private String label;
    public ContactInfoBean(String dataMIMETYPE) {
        this.dataMIMETYPE = dataMIMETYPE;
        this.updateType = TYPE_INSERT;
    }

    public String getDataMIMETYPE() {
        return dataMIMETYPE;
    }

    public int getUpdateType() {
        return updateType;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public ContactInfoBean(String dataMIMETYPE, int updateType, String value, String label) {
        this.dataMIMETYPE = dataMIMETYPE;
        this.updateType = updateType;
        this.value = value;
        this.label = label;
    }

    public ContactInfoBean(int dataRowId, String dataMIMETYPE, int updateType, String value, String label) {

        this.dataRowId = dataRowId;
        this.dataMIMETYPE = dataMIMETYPE;
        this.updateType = updateType;
        this.value = value;
        this.label = label;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setValue(String value) {

        this.value = value;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setDirty(boolean dirty) {

        isDirty = dirty;
    }

    public void setUpdateType(int updateType) {
        this.updateType = updateType;
    }

    public int getDataRowId() {
        return dataRowId;
    }
}
