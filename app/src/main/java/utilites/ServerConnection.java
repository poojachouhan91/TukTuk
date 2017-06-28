package utilites;

import com.hextech.tuktukdriver.SingleTon;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by SERVER on 6/2/2017.
 */
public class ServerConnection
{

    private static Retrofit retrofit = null;
    public static Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).connectTimeout(1, TimeUnit.MINUTES).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(SingleTon.getAppUrl()+"webservice/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;
    }

    public static Retrofit doUpload()
    {


        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).connectTimeout(4, TimeUnit.MINUTES).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(SingleTon.getAppUrl()+"webservice/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return retrofit;

    }
}

