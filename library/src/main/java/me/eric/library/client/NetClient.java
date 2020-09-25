package me.eric.library.client;


import me.eric.library.interceptor.AppInterceptor;
import okhttp3.OkHttpClient;

/**
 * <pre>
 *     author : eric
 *     time   : 2020/02/03
 *     desc   :
 *     version:
 * </pre>
 */
public class NetClient {


    private NetClient() {
    }

    public static NetClient getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private final static NetClient INSTANCE = new NetClient();
    }


   public OkHttpClient getClient() {

        return new OkHttpClient.Builder()
                .addInterceptor(new AppInterceptor())
                .build();

    }

}
