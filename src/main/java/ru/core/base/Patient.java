package ru.core.base;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="patient")
public class Patient {

    public Patient() {}

    public Patient(String name, String lastname, int age, String statistic) {
        this.name = name;
        this.lastname = lastname;
        this.age = age;
        this.status = statistic;
        this.Doctor_id = -1;
        this.id = -1;
    }
    private int id;
    private String name;
    private String lastname;
    private int age;
    private int Doctor_id;
    private String status;
    private Health health;


    public Health getHealth() {return health;}

    public void setHealth(Health health) {this.health = health;}

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getLastname() {return lastname;}

    public void setLastname(String lastname) {this.lastname = lastname;}

    public int getAge() {return age;}

    public void setAge(int age) {this.age = age;}

    public int getDoctor_id() {return Doctor_id;}

    public void setDoctor_id(int doctor_id) {Doctor_id = doctor_id;}

    public String getStatus() {return status;}

    public void setStatus(String statistic) {this.status = statistic;}
}
