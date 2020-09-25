package me.eric.library;

import android.os.Environment;
import android.text.TextUtils;


import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import me.eric.library.client.RetrofitGenerator;
import me.eric.library.request.HttpMethod;
import me.eric.library.response.IDownLoadSuccess;
import me.eric.library.response.IError;
import me.eric.library.response.ISuccess;
import me.eric.library.service.ApiService;
import me.eric.library.util.HttpUtils;
import me.eric.library.util.IOUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * <pre>
 *     author : eric
 *     time   : 2020/02/03
 *     desc   :
 *     version:
 * </pre>
 */
public class AndHttp {

    private String url;
    private HashMap<String, Object> mParams = new HashMap<>();
    private HashMap<String, File> mFileParams = new HashMap<>();

    private ISuccess SUCCESS;
    private IDownLoadSuccess DOWNLOAD_SUCCESS;
    private IError ERROR;

    private AndHttp() {
    }

    private static class Holder {
        private static final AndHttp INSTANCE = new AndHttp();
    }

    public static AndHttp getInstance() {
        return Holder.INSTANCE;
    }

    public AndHttp url(String url) {
        this.url = url;
        return this;
    }

    public AndHttp params(Map<String, Object> mParams) {
        this.mParams.putAll(mParams);
        return this;
    }

    public AndHttp params(String key, Object value) {
        this.mParams.put(key, value);
        return this;
    }

    public AndHttp fileParams(String key, File file) {
        mFileParams.put(key, file);
        return this;
    }


    public void get() {
        request(HttpMethod.GET);
    }

    public void post() {
        request(HttpMethod.POST);
    }

    public void postRow() {
        request(HttpMethod.POST_ROW);
    }

    public void uploadFile() {
        request(HttpMethod.UPLOAD);
    }

    public void downLoad() {
        request(HttpMethod.DOWNLOAD);
    }


    public AndHttp success(ISuccess success) {
        SUCCESS = success;
        return this;
    }

    public AndHttp success(IDownLoadSuccess success) {
        DOWNLOAD_SUCCESS = success;
        return this;
    }


    public AndHttp error(IError iError) {
        ERROR = iError;
        return this;
    }


    private void request(HttpMethod method) {
        ApiService apiService = RetrofitGenerator.getInstance().generator(ApiService.class);

        switch (method) {
            case GET:
                apiService.get(this.url, this.mParams).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            if (SUCCESS != null) {
                                SUCCESS.success(response.body());
                            }
                        } else {
                            if (ERROR != null) {
                                ERROR.error(response.code(), response.body());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (ERROR != null) {
                            ERROR.error(-1, t.getMessage());
                        }
                    }
                });
                break;

            case POST:
                apiService.post(this.url, this.mParams).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (SUCCESS != null) {
                            SUCCESS.success(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (ERROR != null) {
                            ERROR.error(-1, t.getMessage());
                        }
                    }
                });
                break;

            case POST_ROW:

                JSONObject object = new JSONObject(mParams);
                String json = String.valueOf(object);
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), json);

                apiService.postRow(url, requestBody).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (SUCCESS != null) {
                            SUCCESS.success(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        if (ERROR != null) {
                            ERROR.error(-1, t.getMessage());
                        }
                    }
                });

                break;

            case UPLOAD:

                for (Map.Entry<String, File> m : mFileParams.entrySet()) {
                    String key = m.getKey();
                    File file = m.getValue();

                    // RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), value);

                    RequestBody fileRQ = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    MultipartBody.Part part = MultipartBody.Part.createFormData(key, file.getName(), fileRQ);

                    // MultipartBody.Part part = MultipartBody.Part.create(requestBody);
                    apiService.upload(this.url, part).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (SUCCESS != null) {
                                SUCCESS.success(response.body());
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            if (ERROR != null) {
                                ERROR.error(-1, t.getMessage());
                            }
                        }
                    });

                }
                break;

            case DOWNLOAD:

                apiService.download(this.url).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (DOWNLOAD_SUCCESS != null) {
                            try {
                                DOWNLOAD_SUCCESS.success(convertResponse(response));
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();

                                if (ERROR != null) {
                                    ERROR.error(-1, throwable.getMessage());
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (ERROR != null) {
                            ERROR.error(-1, t.getMessage());
                        }
                    }
                });

                break;

            default:
                break;
        }
    }


    private static final String DM_TARGET_FOLDER = File.separator + "download" + File.separator; //下载目标文件夹
    private String folder;                  //目标文件存储的文件夹路径
    private String fileName;                //目标文件存储的文件名

    private File convertResponse(Response response) throws Throwable {
        if (TextUtils.isEmpty(folder))
            folder = Environment.getExternalStorageDirectory() + DM_TARGET_FOLDER;
        if (TextUtils.isEmpty(fileName)) fileName = HttpUtils.getUrlFileName(url);

        File dir = new File(folder);
        IOUtils.createFolder(dir);
        File file = new File(dir, fileName);
        IOUtils.delFileOrFolder(file);

        InputStream bodyStream = null;
        byte[] buffer = new byte[8192];
        FileOutputStream fileOutputStream = null;
        try {
            ResponseBody body = (ResponseBody) response.body();
            if (body == null) return null;

            bodyStream = body.byteStream();

            int len;
            fileOutputStream = new FileOutputStream(file);
            while ((len = bodyStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
            }
            fileOutputStream.flush();
            return file;
        } finally {
            IOUtils.closeQuietly(bodyStream);
            IOUtils.closeQuietly(fileOutputStream);
        }
    }


}
