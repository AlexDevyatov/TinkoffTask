package alexdevyatov.com.tinkofftask;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ContentDisplayActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_display);

        textView = (TextView) findViewById(R.id.content_text_view);

        Intent intent = getIntent();

        textView.setText(intent.getStringExtra("content"));
    }
}
