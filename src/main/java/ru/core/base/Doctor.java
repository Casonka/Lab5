package ru.core.base;

import java.util.List;

public class Doctor {
    public Doctor(int id, String name, String lastname, String location) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.location = location;
        this.Patients_counts = 0;
    }
    private int id;
    private String name;
    private String lastname;
    private String location;
    private int Patients_counts;

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getLastname() {return lastname;}
    public void setLastname(String lastname) {this.lastname = lastname;}
    public String getLocation() {return location;}
    public void setLocation(String location) {this.location = location;}
    public int getPatients_counts() {return Patients_counts;}
    public void setPatients_counts(int patients_counts) {Patients_counts = patients_counts;}
}
