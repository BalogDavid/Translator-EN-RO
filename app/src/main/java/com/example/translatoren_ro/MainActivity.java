package com.example.translatoren_ro;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String API_URL = "https://google-translate1.p.rapidapi.com/language/translate/v2";
    private static final String API_KEY = "77563d34fcmsh8758496c62e982fp1c72afjsn0ede55d30472";
    private EditText inputText;
    private TextView translatedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText = findViewById(R.id.inputText);
        translatedText = findViewById(R.id.translatedText);
        Button translateButton = findViewById(R.id.translateButton);

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textToTranslate = inputText.getText().toString().trim();
                if (!textToTranslate.isEmpty()) {
                    translateText(textToTranslate);
                } else {
                    translatedText.setText("Introduceți un text.");
                }
            }
        });
    }

    private void translateText(String text) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "q=" + text + "&target=ro&source=en");

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("X-RapidAPI-Key", API_KEY)
                .addHeader("X-RapidAPI-Host", "google-translate1.p.rapidapi.com")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> translatedText.setText("Eroare de rețea."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String jsonData = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonData);
                        String translated = jsonObject
                                .getJSONObject("data")
                                .getJSONArray("translations")
                                .getJSONObject(0)
                                .getString("translatedText");

                        runOnUiThread(() -> translatedText.setText(translated));
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> translatedText.setText("Eroare la procesarea răspunsului."));
                    }
                } else {
                    runOnUiThread(() -> translatedText.setText("Eroare API."));
                }
            }
        });
    }
}