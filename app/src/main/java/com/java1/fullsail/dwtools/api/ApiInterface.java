package com.java1.fullsail.dwtools.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;


public interface ApiInterface {

    @GET
    @Streaming
    Call<ResponseBody> downloadFileWithDynamicUrlAsync(@Url String fileUrl);

}