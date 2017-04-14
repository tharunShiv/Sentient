package com.hollywooddream.foresight;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CaptionAPI {

    @Multipart
    @POST("upload")
    Call<List<Caption>> getCaption(@Part MultipartBody.Part file, @Part("name") RequestBody name);
}