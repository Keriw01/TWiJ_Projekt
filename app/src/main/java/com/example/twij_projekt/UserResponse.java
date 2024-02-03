package com.example.twij_projekt;

import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("email")
    private String email;

    @SerializedName("userId")
    private String userId;

    public String getEmail() {
        return email;
    }

    public String getUserId() {
        return userId;
    }
}
