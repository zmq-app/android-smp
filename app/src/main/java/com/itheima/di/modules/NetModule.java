package com.itheima.di.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.itheima.smp.BuildConfig;
import com.itheima.presenter.ApiService;

import org.joda.time.DateTime;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


@Module
public class NetModule {
    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
        OkHttpClient okhttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20,TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();
        return okhttpClient;
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(OkHttpClient okhttpClient) {
        Gson gson = new GsonBuilder().registerTypeAdapter(DateTime.class, new DateTimeTypeAdapter()).create();
        Retrofit retrofit = new Retrofit.Builder()
                .client(okhttpClient)
                .baseUrl("http://static.owspace.com/")
                .addConverterFactory(StringConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit;
    }

    @Provides
    @Singleton
    public ApiService provideApiService(Retrofit retrofit){
        return retrofit.create(ApiService.class);
    }

    public static class StringConverterFactory extends Converter.Factory {
        public static StringConverterFactory create(){
            return new StringConverterFactory();
        }

        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            if (type == String.class) {
                return new StringConverter();
            }
            //其它类型我们不处理,返回null就行
            return null;
        }
    }

    public static class StringConverter implements Converter<ResponseBody, String> {
        @Override
        public String convert(ResponseBody value) throws IOException {
            return value.string();
        }
    }


    public class DateTimeTypeAdapter implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {
        @Override
        public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }

        @Override
        public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new DateTime(json.getAsString());
        }

    }
}
