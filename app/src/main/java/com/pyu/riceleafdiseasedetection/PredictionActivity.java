package com.pyu.riceleafdiseasedetection;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.pyu.riceleafdiseasedetection.ml.Model;

import org.json.JSONException;
import org.json.JSONObject;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PredictionActivity extends AppCompatActivity {

    RecyclerView causesRecyclerView, recommendationRecyclerView;
    int imageSize = 180;
    AppBarLayout appBarMain;
    ImageView inputImage;
    Toolbar toolbar;
    Drawable navigationIcon;
    String jsonData, unrecognizedResponse;

    TextView diseaseDescription, causesHeading, recommendationHeading;

    public int classifyDisease(Bitmap image) {
        try {
            Model model = Model.newInstance(getApplicationContext());

            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 180, 180, 3}, DataType.FLOAT32);

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(180 * 180 * 3 * 4);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * 1.f);
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * 1.f);
                    byteBuffer.putFloat((val & 0xFF) * 1.f);
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] result = outputFeature0.getFloatArray();


            int maxIndex = 0;
            float maxValue = result[0];
            for (int i = 0; i < result.length; i++) {
                if (result[i] > maxValue) {
                    maxValue = result[i];
                    maxIndex = i;
                }
            }

            model.close();

            return maxIndex;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        appBarMain = findViewById(R.id.appBarMain);
        toolbar = findViewById(R.id.toolbar);
        inputImage = findViewById(R.id.inputImage);
        causesRecyclerView = findViewById(R.id.causesRecyclerView);
        recommendationRecyclerView = findViewById(R.id.recommendationsRecyclerView);
        diseaseDescription = findViewById(R.id.diseaseDescription);
        causesHeading = findViewById(R.id.causesHeading);
        recommendationHeading = findViewById(R.id.recommendationHeading);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            diseaseDescription.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }

        navigationIcon = AppCompatResources.getDrawable(this, R.drawable.baseline_chevron_left_24);
        toolbar.setNavigationIcon(navigationIcon);
        navigationIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        unrecognizedResponse = loadDiseaseJsonString("unrecognizedResponse.json");

        jsonData = loadDiseaseJsonString("response_bisaya.json");

        Gson gson = new Gson();

        Disease[] diseases = gson.fromJson(jsonData, Disease[].class);

        Bitmap imageBitmap = getIntent().getParcelableExtra("imageBitmap");

        inputImage.setImageBitmap(imageBitmap);

        imageBitmap = Bitmap.createScaledBitmap(imageBitmap, imageSize, imageSize, false);

        int result = classifyDisease(imageBitmap);

        String[] classes = {"Bacterial Leaf Blight", "Brown Spot", "Healthy Leaf", "Leaf Blast", "Others", "Sheath Blight", "Tungro"};

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String jsonFile = getJsonFile(item.getItemId());
                jsonData = loadDiseaseJsonString(jsonFile);
                updateUIWithJsonData(jsonData, result);
                return true;
            }
        });

        if (result == 4) {
            try {
                JSONObject response = new JSONObject(unrecognizedResponse);
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
                        .setTitle(response.getString("title"))
                        .setMessage(response.getString("message"))
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                            }
                        });
                appBarMain.setVisibility(View.INVISIBLE);
                builder.create();
                builder.show();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } else {
            getSupportActionBar().setTitle(classes[result]);
            updateUIWithJsonData(jsonData, result);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private String loadDiseaseJsonString(String filename) {
        String json = null;
        try {
            InputStream inputStream = getAssets().open(filename);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    public String getJsonFile(int itemId) {
        if (itemId == R.id.toolbar_option_translate_en) {
            return "response_english.json";
        } else if (itemId == R.id.toolbar_option_translate_ph) {
            return "response_bisaya.json";
        } else {
            return "response_bisaya.json";
        }
    }

    private void updateUIWithJsonData(String jsonData, int result) {
        Gson gson = new Gson();
        Disease[] diseases = gson.fromJson(jsonData, Disease[].class);

        for (Disease disease : diseases) {
            if (result == disease.getPrediction()) {
                diseaseDescription.setText(disease.getDescription());

                causesHeading.setText(disease.getCausesHeading());

                causesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                CausesAdapter causeAdapter = new CausesAdapter(disease.getCauses());
                causesRecyclerView.setAdapter(causeAdapter);

                recommendationHeading.setText(disease.getRecommendationHeading());

                recommendationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                RecommendationAdapter recommendationAdapter = new RecommendationAdapter(disease.getRecommendations());
                recommendationRecyclerView.setAdapter(recommendationAdapter);
            }
        }

    }

}