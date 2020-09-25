package me.eric.library.service;


import java.util.Map;

import me.eric.library.model.RequestModel;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * <pre>
 *     author : eric
 *     time   : 2020/02/03
 *     desc   :  测试接口 https://api.github.com/users/basil2style
 * </pre>
 */
public interface ApiService {

    @GET()
    Call<String> get(@Url String url, @QueryMap Map<String, Object> params);

    @FormUrlEncoded
    @POST
    Call<String> post(@Url String url, @FieldMap Map<String, Object> params);

    @POST
    Call<String> postRow(@Url String url, @Body RequestBody body);

    @POST
    Call<String> postJson(@Url String url, @Body RequestModel model);

    @Multipart
    @POST
    Call<String> upload(@Url String url, @Part MultipartBody.Part file);

    @GET
    Call<ResponseBody> download(@Url String url);
}
