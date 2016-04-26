package learn.android.kangel.mycontacts.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kangel on 2016/4/18.
 */
public class CallogBean implements Parcelable {
    private String number;
    private String contactName;
    //private List<String> timeStrings = new LinkedList<>();
    private String location;
    private int count = 0;
    private List<Long> timeMillis = new ArrayList<>();
    private List<Integer> calltypeList = new ArrayList<>();
    private List<Integer> durationList = new ArrayList<>();

    public CallogBean() {

    }

    protected CallogBean(Parcel in) {
        number = in.readString();
        contactName = in.readString();
        location = in.readString();
        count = in.readInt();
        timeMillis = in.readArrayList(null);
        calltypeList = in.readArrayList(null);
        durationList = in.readArrayList(null);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(number);
        dest.writeString(contactName);
        dest.writeString(location);
        dest.writeInt(count);
        dest.writeList(timeMillis);
        dest.writeList(calltypeList);
        dest.writeList(durationList);
    }

    public static final Creator<CallogBean> CREATOR = new Creator<CallogBean>() {
        @Override
        public CallogBean createFromParcel(Parcel in) {
            return new CallogBean(in);
        }

        @Override
        public CallogBean[] newArray(int size) {
            return new CallogBean[size];
        }
    };

    public void setNumber(String number) {
        this.number = number;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /*  public void addTimeString(String timeString) {
          this.timeStrings.add(0, timeString);
      }*/
    private void addTimeMillis(long time) {
        timeMillis.add(time);
    }

    private void addType(int type) {
        this.calltypeList.add(type);
    }

    private void addDuration(int duration) {
        this.durationList.add(duration);
    }

    public void addDetail(long time, int type, int duration) {
        addTimeMillis(time);
        addType(type);
        addDuration(duration);
    }

    public int getCallTypeAtPosition(int position) {
        return calltypeList.get(position);
    }

    public int getDurationAtPosition(int position) {
        return durationList.get(position);
    }

    public long getTimeMillisAtPosition(int position) {
        return timeMillis.get(position);
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

    @Override
    public int describeContents() {
        return 0;
    }


}
