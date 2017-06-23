package org.example.mongodb;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Reference;

import java.util.ArrayList;
import java.util.List;

@Entity("university")
public class University {
    @Id
    @Property("Id")
    private ObjectId id;

    @Property("Department")
    private String department;

    @Reference(lazy = true)
    List<Student> studentList = new ArrayList<Student>();

    public University() {}

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<Student> getStudentList() {
        return studentList;
    }


}


