package com.hollywooddream.foresight;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    static final String BASE_URL = "http://192.168.100.12:5000/";
    Button chooser;
    TextView responseTv;
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chooser = (Button) findViewById(R.id.chooser);
        responseTv = (TextView) findViewById(R.id.responseTv);

        chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openCamera(MainActivity.this, 1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
                Log.d("KJJJJWRYIFNWRUKYBNR", e.getMessage());
            }

            @Override
            public void onImagePicked(File file, EasyImage.ImageSource source, int type) {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create()))
                        .build();

                CaptionAPI captionAPI = retrofit.create(CaptionAPI.class);

                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);
                RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");
                Call<List<Caption>> call = captionAPI.getCaption(body, name);
                call.enqueue(new Callback<List<Caption>>() {
                    @Override
                    public void onResponse(Call<List<Caption>> call, Response<List<Caption>> response) {
                        final String speech = response.body().get(0).getCaption();
                        responseTv.setText(speech);

                        tts = new TextToSpeech(getBaseContext(), new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {
                                tts.setLanguage(Locale.getDefault());
                                tts.speak(speech, TextToSpeech.QUEUE_ADD, null);
                            }
                        });

                    }

                    @Override
                    public void onFailure(Call<List<Caption>> call, Throwable t) {
                        Log.d("LOOKATMENOW", t.getMessage());
                    }
                });

            }
        });
    }
}
