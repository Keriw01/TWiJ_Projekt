package com.example.twij_projekt;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    @FormUrlEncoded
    @POST("login")
    Call<TokenResponse> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("registration")
    Call<TokenResponse> register(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("refresh-token")
    Call<TokenResponse> refreshToken(
            @Field("refresh_token") String refreshToken,
            @Field("user_id") String userId
    );

    @FormUrlEncoded
    @POST("update-password")
    Call<ResponseBody> updatePassword(
            @Field("actual_password") String actualPassword,
            @Field("new_password") String newPassword,
            @Field("user_id") String userId
    );

    @GET("user")
    Call<UserResponse> getUser(@Header("Authorization") String refreshToken);
}
