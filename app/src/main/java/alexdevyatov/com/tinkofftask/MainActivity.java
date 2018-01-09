package alexdevyatov.com.tinkofftask;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ExecutionException;

import alexdevyatov.com.tinkofftask.model.Payload;
import alexdevyatov.com.tinkofftask.model.Response;

public class MainActivity extends AppCompatActivity {

    private final String REQUEST = "https://api.tinkoff.ru/v1/news";
    private final String TAG = "MainActivity";

    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.lv);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        JsonParseTask parser = new JsonParseTask();
                        parser.execute();
                        try {
                            List<Payload> payloads = parser.get();
                            ArrayAdapter<Payload> adapter = new ArrayAdapter<>(MainActivity.this,
                                    android.R.layout.simple_list_item_1, payloads);
                            listView.setAdapter(adapter);
                            swipeRefreshLayout.setRefreshing(false);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    class JsonParseTask extends AsyncTask<Void, Integer, List<Payload>> {

        @Override
        protected List<Payload> doInBackground(Void... params) {
            List<Payload> payloads = null;
            try {
                URL url = new URL(REQUEST);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                Gson gson = new GsonBuilder().create();
                String json = IOUtils.toString(connection.getInputStream(),
                        Charset.forName("UTF-8"));
                Response response = gson.fromJson(json, Response.class);
                if (response.getResultCode().equals("OK")) {
                    payloads = response.getPayloads();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e(TAG, "MalformedURLException");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "IOException");
            }
            return payloads;
        }
    }
}
