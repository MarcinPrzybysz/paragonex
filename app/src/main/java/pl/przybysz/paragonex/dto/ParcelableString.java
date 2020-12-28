package pl.przybysz.paragonex.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableString implements Parcelable {
    private String value;

    public ParcelableString(String value) {
        this.value = value;
    }

    public ParcelableString(Parcel in) {
        value = in.readString();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static final Creator<ParcelableString> CREATOR = new Creator<ParcelableString>() {
        @Override
        public ParcelableString createFromParcel(Parcel in) {
            return new ParcelableString(in);
        }

        @Override
        public ParcelableString[] newArray(int size) {
            return new ParcelableString[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(value);
    }
}
