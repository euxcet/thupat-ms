package com.euxcet.thupat.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceModel {

    public enum ServiceType {
        STUDY, SPORT, EAT, RELAX, REST, TRIP, INOUT
    }

    private int id;
    private String name;
    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServiceModel)) {
            return false;
        }
        final ServiceModel that = (ServiceModel) o;
        return this.id == that.id
                && this.name == that.name
                && this.type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type);
    }
}
