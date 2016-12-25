package flutterwave.com.rave.service;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Shittu on 19/12/2016.
 */

public class RaveRestClient {
    private static final String BASE_URL = "http://flw-pms-dev.eu-west-1.elasticbeanstalk.com/flwv3-pug/getpaid/api";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static OkHttpClient client = new OkHttpClient();

    public static Response post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(BASE_URL + url)
                .post(body)
                .build();
        return client.newCall(request).execute();
    }

    public static Response get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        return client.newCall(request).execute();
    }

}
