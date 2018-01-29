package com.nbn.cloudbasedpatientreferralsystem.pojo;

import java.io.Serializable;

/**
 * Created by dudupoo on 27/1/18.
 */

public class DocumentInfo implements Serializable
{
    private String docName;
    private String imageUrl;
    private PatientProfile patientProfile;

    public DocumentInfo() {}

    @Override
    public String toString()
    {
        return "DocumentInfo{" +
                "docName='" + docName + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", patientProfile=" + patientProfile +
                '}';
    }

    public String getDocName()
    {
        return docName;
    }

    public void setDocName(String docName)
    {
        this.docName = docName;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    public PatientProfile getPatientProfile()
    {
        return patientProfile;
    }

    public void setPatientProfile(PatientProfile patientProfile)
    {
        this.patientProfile = patientProfile;
    }
}
