package me.eric.library.interceptor;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * <pre>
 *     author : eric
 *     time   : 2020/02/07
 *     desc   : 拦截器（全局注入参数，请求头，打印请求日志等等）
 *     version:
 * </pre>
 */
public class AppInterceptor implements Interceptor {

    private static final String TAG = "AndHttp";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        RequestBody requestBody = request.body();

        HttpUrl url = request.url();
        boolean https = request.isHttps();
        String method = request.method();

        Request newRequest = request.newBuilder()
                .addHeader("header1", "value1")
                .addHeader("header2", "value2")
                .build();


        String host = url.host();
        String query = url.query();
        String encodedPath = url.encodedPath();
        String encodedQuery = url.encodedQuery();

        Log.e(TAG, "----------------------网络请求begin---------------------");
        Log.e(TAG, "网络请求url--> " + url);
        Log.e(TAG, "网络请求方式--> " + method);
        Log.e(TAG, "主机地址--> " + host);
        Log.e(TAG, "encodedPath--> " + encodedPath);
        if (method.equals("POST")) {
            if (requestBody instanceof FormBody) {
                FormBody formBody = (FormBody) requestBody;
                int size = formBody.size();


                JSONObject object = new JSONObject();
                for (int i = 0; i < size; i++) {
                    try {
                        String name = formBody.encodedName(i);
                        String value = formBody.encodedValue(i);
                        object.put(name, value);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else if (requestBody instanceof MultipartBody) {

                MultipartBody multipartBody = (MultipartBody) requestBody;
                List<MultipartBody.Part> parts = multipartBody.parts();
                for (int i = 0; i < parts.size(); i++) {

                    MultipartBody.Part part = parts.get(i);
                    Log.e(TAG, "请求参数--> " + part.toString());
                }

            }

        } else {
            Log.e(TAG, "请求参数--> " + encodedQuery);

        }
        Log.e(TAG, "----------------------网络请求end---------------------");

        return chain.proceed(newRequest);
    }
}
