package com.greensoft.secondlife.mobile_device;

/**
 * Created by zebul on 6/24/17.
 */

public class MobileDevice {

    public String Id;
    public String Name;
    public String ModelName;

    public MobileDevice(String id, String name, String modelName) {
        this.Id = id;
        this.Name = name;
        this.ModelName = modelName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MobileDevice that = (MobileDevice) o;

        if (!Id.equals(that.Id)) return false;
        if (!Name.equals(that.Name)) return false;
        return ModelName.equals(that.ModelName);

    }

    @Override
    public int hashCode() {
        int result = Id.hashCode();
        result = 31 * result + Name.hashCode();
        result = 31 * result + ModelName.hashCode();
        return result;
    }
}
