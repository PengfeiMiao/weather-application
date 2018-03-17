package mg.studio.weatherappdesign;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        ImageView iv = (ImageView)findViewById(R.id.imageView2);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //android.util.Log.i("匿名内部类", "点击事件");
                btnClick(view);
            }
        });//控件响应方法two
        */
    }

    public void btnClick(View view) {
        new DownloadUpdate().execute();
        Calendar c = Calendar.getInstance();
        String tv_date = String.valueOf(c.get(Calendar.MONTH) + 1)+"/"
                +String.valueOf(c.get(Calendar.DAY_OF_MONTH))+"/"
                +String.valueOf(c.get(Calendar.YEAR));
        ((TextView) findViewById(R.id.tv_date)).setText(tv_date);
        int mWay = c.get(Calendar.DAY_OF_WEEK);// 获取当前日期的星期
        String week ="";
        switch(mWay){
            case 1:
                week = "SUNDAY";
            case 2:
                week = "MONDAY";
            case 3:
                week = "TUESDAY";
            case 4:
                week = "WEDNESDAY";
            case 5:
                week = "THUSDAY";
            case 6:
                week = "FRIDAY";
            case 7:
                week = "SATURDAY";
        }
        ((TextView) findViewById(R.id.week)).setText(week);

    }


    private class DownloadUpdate extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {

            String stringUrl = "http://www.weather.com.cn/data/sk/101042900.html";
            HttpURLConnection urlConnection = null;
            BufferedReader reader;

            try {
                URL url = new URL(stringUrl);

                // Create the request to get the information from the server, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Mainly needed for debugging
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                //The temperature
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String temperature) {
            String temp = temperature.substring(temperature.indexOf("temp")+7,temperature.indexOf("WD")-3);
            //Update the temperature displayed
            ((TextView) findViewById(R.id.temperature_of_the_day)).setText(temp);
        }
    }
}
