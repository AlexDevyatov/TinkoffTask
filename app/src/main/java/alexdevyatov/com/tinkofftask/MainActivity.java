package alexdevyatov.com.tinkofftask;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import alexdevyatov.com.tinkofftask.model.Payload;
import alexdevyatov.com.tinkofftask.model.PublicationDate;
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
        final DBHelper dbHelper = new DBHelper(this);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("news", null, null, null, null, null, null);

        List<Payload> payloads = new ArrayList<>();
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.NAME);
            int textIndex = cursor.getColumnIndex(DBHelper.TEXT);
            int dateIndex = cursor.getColumnIndex(DBHelper.PUBLICATION_TIME);
            int bankIdIndex = cursor.getColumnIndex(DBHelper.BANK_INFO_TYPE_ID);
            do {
                payloads.add(new Payload(
                        cursor.getLong(idIndex),
                        cursor.getString(nameIndex),
                        cursor.getString(textIndex),
                        new PublicationDate(cursor.getLong(dateIndex)),
                        cursor.getLong(bankIdIndex)
                ));
            } while (cursor.moveToNext());

            ArrayAdapter<Payload> adapter = new ArrayAdapter<>(MainActivity.this,
                    android.R.layout.simple_list_item_1, payloads);
            listView.setAdapter(null);
            listView.setAdapter(adapter);
        }

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        DataLoadTask task = new DataLoadTask();
                        task.execute();
                        try {
                            List<Payload> payloads = task.get();

                            dbHelper.clearDatabase();

                            for (Payload payload : payloads) {
                                ContentValues cv = new ContentValues();
                                cv.put(DBHelper.ID, payload.getId());
                                cv.put(DBHelper.NAME, payload.getName());
                                cv.put(DBHelper.TEXT, payload.getText());
                                cv.put(DBHelper.PUBLICATION_TIME, payload.getPublicationDate()
                                        .getMilliseconds());
                                cv.put(DBHelper.BANK_INFO_TYPE_ID, payload.getBankInfoTypeId());
                                dbHelper.insertRecord(cv);
                            }


                            ArrayAdapter<Payload> adapter = new ArrayAdapter<>(MainActivity.this,
                                    android.R.layout.simple_list_item_1, payloads);
                            listView.setAdapter(null);
                            listView.setAdapter(adapter);
                            swipeRefreshLayout.setRefreshing(false);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                });
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Payload payload = (Payload) parent.getAdapter().getItem(position);
                        NewsLoadTask task = new NewsLoadTask();
                        task.execute(payload.getId());
                        try {
                            String content = task.get();
                            Intent intent = new Intent(MainActivity.this,
                                    ContentDisplayActivity.class);
                            intent.putExtra("content", content);
                            startActivity(intent);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    class DataLoadTask extends AsyncTask<Void, Void, List<Payload>> {

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

    class NewsLoadTask extends AsyncTask<Long, Void, String> {

        @Override
        protected String doInBackground(Long... params) {
            long payloadId = params[0];
            String newsContent = null;
            String request = "https://api.tinkoff.ru/v1/news_content?id=" + payloadId;
            try {
                URL url = new URL(request);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                Gson gson = new GsonBuilder().create();
                String response = IOUtils.toString(connection.getInputStream(),
                        Charset.forName("UTF-8"));
                JSONObject json = new JSONObject(response);
                JSONObject jsonPayload = json.getJSONObject("payload");
                newsContent = jsonPayload.getString("content");
                //newsContent = gson.fromJson(String.valueOf(jsonPayload.getJSONObject("content")), String.class);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return newsContent;
        }
    }

    class DBHelper extends SQLiteOpenHelper {

        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String TEXT = "body";
        public static final String PUBLICATION_TIME = "publication_time";
        public static final String BANK_INFO_TYPE_ID = "bank_info_type_id";

        public DBHelper(Context context) {
            super(context, "myDB", null, 1);
        }

        public void clearDatabase() {
            SQLiteDatabase db = getWritableDatabase();
            db.delete("news", null, null);
            db.close();
        }

        public void insertRecord(ContentValues cv) {
            SQLiteDatabase db = getWritableDatabase();
            db.insert("news", null, cv);
            db.close();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "--- onCreate database ---");
            db.execSQL("create table news ("
                    + "id integer primary key,"
                    + "name text,"
                    + "body text,"
                    + "publication_time integer,"
                    + "bank_info_type_id integer" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + "news");
            onCreate(db);
        }
    }
}
