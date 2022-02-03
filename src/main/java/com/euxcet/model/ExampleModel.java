package com.euxcet.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExampleModel {
    private int id;
    private long time;
    private String location;

    public ExampleModel() {}

    public int getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExampleModel)) {
            return false;
        }
        final ExampleModel that = (ExampleModel) o;
        return this.id == that.id
                && this.time == that.time
                && this.location.equals(that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, time, location);
    }
}
