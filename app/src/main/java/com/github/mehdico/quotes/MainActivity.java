package com.github.mehdico.quotes;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

public class MainActivity extends Activity {

    private static final int BG_COLOR_DARK = 0xff000000; // in dark mode
    private static final int BG_COLOR_LIGHT = 0xffffffff; // in light mode

    private static final int TEXT_COLOR_DARK = 0xffffffff; // in dark mode
    private static final int TEXT_COLOR_LIGHT = 0xff000000; // in light mode

    private static final int TEXT2_COLOR_DARK = 0xff777777; // in dark mode
    private static final int TEXT2_COLOR_LIGHT = 0xff555555; // in light mode

    RelativeLayout rlMain;
    TextView tvQuote;
    TextView tvAuthor;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 60000 * 4;
    Random random = new Random();
//    SendHttpRequestTask sendHttpRequestTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(Consts.FONT_PATH_1)
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

//        sendHttpRequestTask = new SendHttpRequestTask();

        rlMain = new RelativeLayout(this);

        RelativeLayout.LayoutParams lpQuote = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        lpQuote.addRule(RelativeLayout.CENTER_HORIZONTAL);
//        lpQuote.setMargins(40, 200, 40, 0);


        tvQuote = new TextView(this);
        tvQuote.setTextSize(40);
        tvQuote.setPadding(40, 200, 40, 0);
//        tvQuote.setGravity(Gravity.CENTER_HORIZONTAL);
        CalligraphyUtils.applyFontToTextView(tvQuote, Consts.getFont1Bold(this));
        rlMain.addView(tvQuote, lpQuote);

        RelativeLayout.LayoutParams lpAuthor = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpAuthor.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lpAuthor.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lpAuthor.setMargins(0, 0, 0, 50);

        tvAuthor = new TextView(this);
        tvAuthor.setTextSize(25);
        CalligraphyUtils.applyFontToTextView(tvAuthor, Consts.getFont1Bold(this));
        rlMain.addView(tvAuthor, lpAuthor);

        tvAuthor.setOnClickListener(v -> reverseColors());

        rlMain.setOnClickListener(v -> {
            handler.removeCallbacks(runnable);
            scheduleQuote(true);
        });

        // styling all things
        applyColors(isLightMode());

        setContentView(rlMain);
    }

//    private class SendHttpRequestTask extends AsyncTask<String, Void, JSONObject> {
//        @Override
//        protected JSONObject doInBackground(String... params) {
//            try {
//
//                URL url = new URL("https://meme-api.herokuapp.com/gimme");
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
//                conn.connect();
//                InputStream in = conn.getInputStream();
//
//                StringBuilder stringBuilder = new StringBuilder();
//                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    stringBuilder.append(line);
//                }
//
//                return new JSONObject(stringBuilder.toString());
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(JSONObject result) {
//            Log.e("TEST",result.toString());
//        }
//    }

    private void reverseColors() {
         boolean isLightMode = !isLightMode();
        applyColors(isLightMode);

        SharedPreferences sh = this.getSharedPreferences("default", MODE_PRIVATE);
        SharedPreferences.Editor editor = sh.edit();
        editor.putBoolean("isLightMode", isLightMode);
        editor.commit();
    }

    private boolean isLightMode() {
        SharedPreferences sh = this.getSharedPreferences("default", MODE_PRIVATE);
        return sh.getBoolean("isLightMode", true);
    }

    private void applyColors(boolean lightMode) {
        rlMain.setBackgroundColor(lightMode ? BG_COLOR_LIGHT : BG_COLOR_DARK);
        tvQuote.setTextColor(lightMode ? TEXT_COLOR_LIGHT : TEXT_COLOR_DARK);
        tvAuthor.setTextColor(lightMode ? TEXT2_COLOR_LIGHT : TEXT2_COLOR_DARK);
    }

    private void setRandomLine() {
        String line = getRandomLine("persian_dic.txt");

        tvQuote.setText(line);
        tvAuthor.setText("دیکشنری");
    }

    private void setRandomQuote() {
        try {
            boolean isEnglish = random.nextBoolean();
            String strJson = loadJSONFile(isEnglish ? "english.json" : "persian.json");
            JSONArray jaQuotes = new JSONArray(strJson);
            JSONObject joQuote = getRandomObject(jaQuotes);

            if (isEnglish) {
//                tvQuote.setTextSize(40);
//                tvAuthor.setTextSize(25);
            } else { // persian

            }
            tvQuote.setText(joQuote.getString("quote"));
            tvAuthor.setText(joQuote.getString("author"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scheduleQuote(boolean alsoShowNow) {
        if (alsoShowNow)
            setRandomQuote();
        handler.postDelayed(runnable = () -> {
            handler.postDelayed(runnable, delay);
            setRandomQuote();
//            sendHttpRequestTask.execute();
//            setRandomLine();
        }, delay);
    }
    @Override
    protected void onResume() {
        super.onResume();
        scheduleQuote(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();
    }


    public String loadJSONFile(String filename) throws IOException {
        String json = null;
        InputStream inputStream = getAssets().open(filename);
        int size = inputStream.available();
        byte[] byteArray = new byte[size];
        inputStream.read(byteArray);
        inputStream.close();
        json = new String(byteArray);
        return json;
    }

    public JSONObject getRandomObject(JSONArray jsonArray) throws JSONException {
        final int min = 0;
        final int max = jsonArray.length() - 1;
        final int intRandom = random.nextInt((max - min) + 1) + min;
        return (JSONObject) jsonArray.get(intRandom);
    }


    public String getRandomLine(String filename) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open(filename)));
            List<String> lines = new ArrayList<>();
            String eachline = bufferedReader.readLine();
            while (eachline != null) {
                if (eachline.length() < 150)
                    lines.add(eachline);
                eachline = bufferedReader.readLine();
            }
            final int intRandom = random.nextInt((lines.size() - 1) + 1);
            return lines.get(intRandom);
        } catch (IOException e) {

        }
        return null;
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}