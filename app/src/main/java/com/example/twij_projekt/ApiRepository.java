package com.example.twij_projekt;

import android.content.Context;

import androidx.annotation.NonNull;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiRepository {
    private static final String BASE_URL = "http://audiobookhsetvo.mooo.com/api/twij.php/";

    public static class AuthRepository {
        public interface AuthCallback {
            void onTokensReceived(String accessToken, String refreshToken);
            void onTokensError(String errorMessage);
        }
        public void login(String email, String password, AuthRepository.AuthCallback callback) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService = retrofit.create(ApiService.class);

            Call<TokenResponse> call = apiService.login(email, password);

            call.enqueue(new Callback<TokenResponse>() {
                @Override
                public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        TokenResponse tokenResponse = response.body();
                        String newAccessToken = tokenResponse.getAccessToken();
                        String newRefreshToken = tokenResponse.getRefreshToken();

                        callback.onTokensReceived(newAccessToken, newRefreshToken);
                    } else {
                        handleErrorResponse(response, callback);
                    }
                }
                @Override
                public void onFailure(Call<TokenResponse> call, Throwable t) {
                    handleFailure(t, callback);
                }
            });
        }

        public void register(String email, String password, AuthRepository.AuthCallback callback) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService = retrofit.create(ApiService.class);

            Call<TokenResponse> call = apiService.register(email, password);

            call.enqueue(new Callback<TokenResponse>() {
                @Override
                public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        TokenResponse tokenResponse = response.body();
                        String newAccessToken = tokenResponse.getAccessToken();
                        String newRefreshToken = tokenResponse.getRefreshToken();

                        callback.onTokensReceived(newAccessToken, newRefreshToken);
                    } else {
                        handleErrorResponse(response, callback);
                    }
                }
                @Override
                public void onFailure(Call<TokenResponse> call, Throwable t) {
                    handleFailure(t, callback);
                }
            });
        }

        public void refreshTokens(String refreshToken, String userId, AuthRepository.AuthCallback callback) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService = retrofit.create(ApiService.class);

            Call<TokenResponse> call = apiService.refreshToken(refreshToken, userId);

            call.enqueue(new Callback<TokenResponse>() {
                @Override
                public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        TokenResponse tokenResponse = response.body();
                        String newAccessToken = tokenResponse.getAccessToken();
                        String newRefreshToken = tokenResponse.getRefreshToken();

                        callback.onTokensReceived(newAccessToken, newRefreshToken);
                    } else {
                        handleErrorResponse(response, callback);
                    }
                }
                @Override
                public void onFailure(Call<TokenResponse> call, Throwable t) {
                    handleFailure(t, callback);
                }
            });
        }
        private void handleErrorResponse(Response<TokenResponse> response, AuthRepository.AuthCallback callback) {
            if (response.code() == 401) {
                callback.onTokensError("Internal credentials");
            } else if (response.code() == 403) {
                callback.onTokensError("Unauthorized");
            } else if (response.code() == 500) {
                callback.onTokensError("Internal server error");
            } else {
                callback.onTokensError("Error: " + response.message());
            }
        }

        private void handleFailure(Throwable t, AuthRepository.AuthCallback callback) {

            callback.onTokensError("Communication error: " + t.getMessage());
        }
    }

    public static class UserRepository {
        public interface UserDataCallback {
            void onUserReceived(String userEmail, String userId);
            void onUserError(String errorMessage);
        }

        public void getUser(String refreshToken, UserDataCallback callback) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService = retrofit.create(ApiService.class);
            Call<UserResponse> call = apiService.getUser(refreshToken);

            call.enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        UserResponse userResponse = response.body();
                        String userEmail = userResponse.getEmail();
                        String userId = userResponse.getUserId();

                        callback.onUserReceived(userEmail, userId);
                    } else {
                        handleErrorResponse(response, callback);
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    handleFailure(t, callback);
                }
            });
        }

        private void handleErrorResponse(Response<UserResponse> response, UserDataCallback callback) {
            if (response.code() == 401) {
                callback.onUserError("Refresh token is missing or invalid");
            } else if (response.code() == 403) {
                callback.onUserError("User not authorized");
            } else if (response.code() == 404) {
                callback.onUserError("User not found");
            } else if (response.code() == 500) {
                callback.onUserError("Internal server error");
            } else {
                callback.onUserError("Error: " + response.message());
            }
        }

        private void handleFailure(Throwable t, UserDataCallback callback) {
            callback.onUserError("Communication error: " + t.getMessage());
        }
    }


    public static class UserUpdateRepository {
        public interface UserUpdateCallback {
            void onUserReceived(String message);
            void onUserError(String errorMessage);
        }

        public void changePassword(String actualPassword, String newPassword, String userId, UserUpdateCallback callback) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService apiService = retrofit.create(ApiService.class);
            Call<ResponseBody> call = apiService.updatePassword(actualPassword, newPassword, userId);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        callback.onUserReceived("Zaktulizowano hasło");
                    } else {
                        handleErrorResponse(response, callback);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    handleFailure(t, callback);
                }
            });
        }

        private void handleErrorResponse(Response<ResponseBody> response, UserUpdateCallback callback) {
            if (response.code() == 401) {
                callback.onUserError("Invalid current password");
            } else if (response.code() == 404) {
                callback.onUserError("User not found");
            } else if (response.code() == 500) {
                callback.onUserError("Internal server error");
            } else {
                callback.onUserError("Error: " + response.message());
            }
        }

        private void handleFailure(Throwable t, UserUpdateCallback callback) {
            callback.onUserError("Communication error: " + t.getMessage());
        }
    }
}

