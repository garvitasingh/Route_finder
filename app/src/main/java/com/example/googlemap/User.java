package com.example.googlemap;

public class User {
    public String fullName,age,email,prof,gender,aadharno,carplateno,dlno;
    public User(){

    }

//    public User(String fullName, String age, String email,String prof,String gender,
//                String aadharno,String carplateno,String dlno)
    public User(String fullName, String age, String email,String gender){
        this.fullName=fullName;
        this.age=age;
        this.email=email;
//        this.prof=prof;
        this.gender=gender;
//        this.aadharno=aadharno;
//        this.carplateno=carplateno;
//        this.dlno=dlno;
    }
}
