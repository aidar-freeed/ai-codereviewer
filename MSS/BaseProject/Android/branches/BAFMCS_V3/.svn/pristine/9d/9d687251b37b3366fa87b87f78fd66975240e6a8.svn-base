package com.adins.mss.coll.networks.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by angga.permadi on 6/6/2016.
 */
public class ClosingTaskEntity implements Parcelable{
    private String noKontrak;
    private String customerName;
    private String noLkp;

    public ClosingTaskEntity() {

    }

    protected ClosingTaskEntity(Parcel in) {
        readFromParcel(in);
    }

    public String getNoKontrak() {
        return noKontrak;
    }

    public void setNoKontrak(String noKontrak) {
        this.noKontrak = noKontrak;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getNoLkp() {
        return noLkp;
    }

    public void setNoLkp(String noLkp) {
        this.noLkp = noLkp;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void readFromParcel(Parcel in) {
        noKontrak = in.readString();
        customerName = in.readString();
        noLkp = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(noKontrak);
        dest.writeString(customerName);
        dest.writeString(noLkp);
    }

    public static final Creator<ClosingTaskEntity> CREATOR = new Creator<ClosingTaskEntity>() {
        @Override
        public ClosingTaskEntity createFromParcel(Parcel in) {
            return new ClosingTaskEntity(in);
        }

        @Override
        public ClosingTaskEntity[] newArray(int size) {
            return new ClosingTaskEntity[size];
        }
    };
}
