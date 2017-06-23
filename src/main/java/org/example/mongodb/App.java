package org.example.mongodb;

import com.mongodb.*;


import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

public class App
{
    private Datastore datastore;
    private String department;
    private Student stud;


    public static void main( String[] args )
    {
        App app = new App();
        app.runTheApp();
    }

    public void runTheApp(){
        Morphia morphia = new Morphia();
        morphia.mapPackage("org.example.mongodb");
        datastore = morphia.createDatastore(new MongoClient(), "dbM");
        datastore.ensureIndexes();
        showMenu();
    }

    private void showMenu() {
        System.out.println("Choose 1 to add student\n" +
                "Choose 2 to delete student\n" +
                "Choose 3 to delete department\n" +
                "Choose 4 to search student\n" +
                "Choose 5 to show data base\n" +
                "Choose 6 to delete data base\n" +
                "Choose 7 to exit\n");
        switch(readTheNumb()){
            case 1:
                addStudent();
                break;
            case 2:
                deleteStudent();
                break;
            case 3:
                deleteDepartment();
                break;
            case 4:
                searchStudent();
                break;
            case 5:
                showDB();
                break;
            case 6:
                deleteDB();
                break;
            case 7:
                break;
            default:
                System.out.println("Choose between 1-6!\n");
                showMenu();
                break;
        }
    }

    private void addStudent() {
        stud = new Student();
        System.out.println("Enter student's name, surname, age and department:");
        stud.setName(readTheLine());
        stud.setSurname(readTheLine());
        stud.setAge(readTheNumb());
        department = readTheLine();
        datastore.save(stud);
        List<University> universityList = datastore.createQuery(University.class).asList();
        boolean newDep = true;
        if(universityList.size() == 0){
            createNewDep();
        } else {
            for (University un : universityList) {
                if (un.getDepartment().equals(department)) {
                    un.getStudentList().add(stud);
                    datastore.save(un);
                    newDep = false;
                    break;
                } else {
                    continue;
                }
            }
            if(newDep){
                createNewDep();
            }
        }
        System.out.println();
        showMenu();
    }

    private void createNewDep(){
        University university = new University();
        university.setDepartment(department);
        datastore.save(university);
        university.getStudentList().add(stud);
        datastore.save(university);

    }

    private void searchStudent() {
        System.out.println("Enter student's name:");
        String name = readTheLine();
        List<Student> list = datastore.createQuery(Student.class).field("Name").contains(name).asList();
        for (Student st: list) {
            System.out.println(st.getId()+" "+st.getName()+" "+st.getSurname()+" "+st.getAge());
        }
        System.out.println();
        showMenu();
    }

    private void deleteStudent() {
        System.out.println("Enter name of student to delete:");
        String name = readTheLine();
        List<University> universityList = datastore.createQuery(University.class).asList();
        for (University un : universityList) {
            List<Student> studentList = un.getStudentList();
            for (Iterator<Student> iterator = studentList.iterator(); iterator.hasNext();) {
                if(iterator.next().getName().equals(name)){
                    iterator.remove();
                    //datastore.delete(iterator);
                    datastore.save(un);
                }
            }
        }
        List<Student> studentList = datastore.createQuery(Student.class).asList();
        for (Student st: studentList) {
            if(st.getName().equals(name)){
                datastore.delete(st);
            }
        }
        System.out.println();
        showMenu();
    }

    private void deleteDepartment() {
        System.out.println("Enter department to delete:");
        String department = readTheLine();
        List<University> universityList = datastore.createQuery(University.class).asList();
        for (University un : universityList) {
            if(un.getDepartment().equals(department)){
                for (Student st: un.getStudentList()) {
                    datastore.delete(st);
                }
                datastore.delete(un);
            }
        }
        System.out.println(department + " was successfully deleted!\n");
        showMenu();
    }


    private void showDB() {
        List<University> list = datastore.createQuery(University.class).asList();
        for (University un: list) {
            System.out.println(un.getDepartment()+":");
            List<Student> studentList = un.getStudentList();
            for (Student st: studentList) {
                System.out.println(st.getId() + " " + st.getName() + " " + st.getSurname() + " " + st.getAge());
            }
            System.out.println();
        }
        System.out.println("Number of students at all departments: " + datastore.getCount(Student.class)+"\n");
        showMenu();
    }

    private void deleteDB() {
        List<University> universityList = datastore.createQuery(University.class).asList();
        for (University un : universityList) {
            datastore.delete(un);
        }
        List<Student> studentList = datastore.createQuery(Student.class).asList();
        for (Student st: studentList) {
            datastore.delete(st);
        }
        runTheApp();
    }

    private int readTheNumb(){
        Scanner sc = new Scanner(System.in);
        if (sc.hasNextInt()) {
            return sc.nextInt();
        } else {
            System.out.println("It should be a number! Try again!");
            return readTheNumb();
        }

    }

    private String readTheLine(){
        Scanner sc = new Scanner(System.in);
        String str = sc.next();
        boolean b = checkWithRegExp(str);
        if (b){
            return str;
        } else {
            System.out.println("It should be a line! Try again!");
            return readTheLine();
        }
    }

    public static boolean checkWithRegExp(String str){
        Pattern p = Pattern.compile("^[a-zA-Z]+$");
        Matcher m = p.matcher(str);
        return m.matches();
    }


}