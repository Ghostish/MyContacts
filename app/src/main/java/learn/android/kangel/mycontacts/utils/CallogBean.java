package learn.android.kangel.mycontacts.utils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Kangel on 2016/4/18.
 */
public class CallogBean {
    private String number;
    private String contactName;
    //private List<String> timeStrings = new LinkedList<>();
    private String location;
    private List<Integer> calltypeList = new LinkedList<>();
    private int count = 0;
    private List<Long> timeMillis = new LinkedList<>();

    public void setNumber(String number) {
        this.number = number;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

  /*  public void addTimeString(String timeString) {
        this.timeStrings.add(0, timeString);
    }*/

    public void addTimeMillis(long time) {
        timeMillis.add(time);
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public void addType(int type) {
        this.calltypeList.add(type);
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNumber() {

        return number;
    }

    public String getContactName() {
        return contactName;
    }

    /*public String getFirstTimeString() {
        return timeStrings.get(0);
    }*/

    public long getFirstTimeMillis() {
        return timeMillis.get(0);
    }
    public String getLocation() {
        return location;
    }

    public List<Integer> getTypeList() {
        return calltypeList;
    }

    public int getCount() {
        return count;
    }

    public void increamentCount() {
        count++;
    }
}
