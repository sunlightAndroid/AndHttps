package me.eric.library.client;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * <pre>
 *     author : eric
 *     time   : 2020/02/03
 *     desc   :
 *     version:
 * </pre>
 */
public class RetrofitGenerator {

    private RetrofitGenerator() {
    }

    private final static class Holder {
        private static final RetrofitGenerator INSTANCE = new RetrofitGenerator();
    }

    public static RetrofitGenerator getInstance() {
        return Holder.INSTANCE;
    }

    public <T> T generator(Class<T> service) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.10:8088/api/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(NetClient.getInstance().getClient())
                .build();

        return retrofit.create(service);

    }


}
