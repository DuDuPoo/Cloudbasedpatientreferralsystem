package com.nbn.cloudbasedpatientreferralsystem.pojo;

import java.io.Serializable;

/**
 * Created by dudupoo on 25/1/18.
 */

public class DoctorProfile implements Serializable
{
    private String name;
    private String contactNo;
    private String email;
    private String dob;
    private String firebaseId;
    private String photoURL;
    private String gender;
    private String specialization;

    public DoctorProfile() {}

    public DoctorProfile(DoctorProfile d) {
        this.name = d.getName();
        this.contactNo = d.getContactNo();
        this.email = d.getEmail();
        this.dob = d.getDob();
        this.firebaseId = d.getFirebaseId();
        this.photoURL = d.getPhotoURL();
        this.gender = d.getGender();
        this.specialization = d.getSpecialization();
    }

    @Override
    public String toString()
    {
        return "DoctorProfile{" +
                "name='" + name + '\'' +
                ", contactNo='" + contactNo + '\'' +
                ", email='" + email + '\'' +
                ", dob='" + dob + '\'' +
                ", firebaseId='" + firebaseId + '\'' +
                ", photoURL='" + photoURL + '\'' +
                ", gender='" + gender + '\'' +
                ", specialization='" + specialization + '\'' +
                '}';
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getContactNo()
    {
        return contactNo;
    }

    public void setContactNo(String contactNo)
    {
        this.contactNo = contactNo;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getDob()
    {
        return dob;
    }

    public void setDob(String dob)
    {
        this.dob = dob;
    }

    public String getFirebaseId()
    {
        return firebaseId;
    }

    public void setFirebaseId(String firebaseId)
    {
        this.firebaseId = firebaseId;
    }

    public String getPhotoURL()
    {
        return photoURL;
    }

    public void setPhotoURL(String photoURL)
    {
        this.photoURL = photoURL;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public String getSpecialization()
    {
        return specialization;
    }

    public void setSpecialization(String specialization)
    {
        this.specialization = specialization;
    }
}
