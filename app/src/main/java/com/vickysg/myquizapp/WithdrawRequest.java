package com.vickysg.myquizapp;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class WithdrawRequest {

    private  String userId;

    private String emailAddress ;

    private  String requestedBy;

    public WithdrawRequest(String userId, String emailAddress, String requestedBy) {
        this.userId = userId;
        this.emailAddress = emailAddress;
        this.requestedBy = requestedBy;
    }

    public WithdrawRequest() {
    }


    //  For Accessing Firebase Original Dates
    @ServerTimestamp
    private Date createdAt;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}
