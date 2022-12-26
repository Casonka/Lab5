package ru.core.base;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="patient")
public class Patient {

    public Patient() {}

    public Patient(String name, String lastname, int age, Status status) {
        this.name = name;
        this.lastname = lastname;
        this.age = age;
        this.status = status;
        this.Doctor_id = -1;
        this.id = -1;
    }
    private int id;
    private String name;
    private String lastname;
    private int age;
    private int Doctor_id;
    private Status status;
    private Health health;

    @XmlElement
    public Health getHealth() {return health;}
    @XmlElement
    public void setHealth(Health health) {this.health = health;}
    @XmlElement
    public int getId() {return id;}
    @XmlElement
    public void setId(int id) {this.id = id;}
    @XmlElement
    public String getName() {return name;}
    @XmlElement
    public void setName(String name) {this.name = name;}
    @XmlElement
    public String getLastname() {return lastname;}
    @XmlElement
    public void setLastname(String lastname) {this.lastname = lastname;}
    @XmlElement
    public int getAge() {return age;}
    @XmlElement
    public void setAge(int age) {this.age = age;}
    @XmlElement
    public int getDoctor_id() {return Doctor_id;}
    @XmlElement
    public void setDoctor_id(int doctor_id) {Doctor_id = doctor_id;}
    @XmlElement
    public Status getStatus() {return status;}
    @XmlElement
    public void setStatus(Status status) {this.status = status;}
}
