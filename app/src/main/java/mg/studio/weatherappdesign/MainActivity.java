package mg.studio.weatherappdesign;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    public static String address = "";
    public static String temp = "";
    public static String week ="";
    public static String Cdate = "";
    public static int mWay = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkNetwork(this);
        refreshDate();
        new DownloadUpdate().execute();
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new TemUpdate().execute();
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

    public void btnRefresh(View view) {
        checkNetwork(this);
        new DownloadUpdate().execute();
        new TemUpdate().execute();
        refreshDate();
    }

    public void refreshDate(){
        Calendar c = Calendar.getInstance();
        Cdate = String.valueOf(c.get(Calendar.MONTH) + 1)+"/"
                +String.valueOf(c.get(Calendar.DAY_OF_MONTH))+"/"
                +String.valueOf(c.get(Calendar.YEAR));
        ((TextView) findViewById(R.id.tv_date)).setText(Cdate);

        mWay = c.get(Calendar.DAY_OF_WEEK);// 获取当前日期的星期
        switch(mWay){
            case 1:
                week = "SUNDAY";
                break;
            case 2:
                week = "MONDAY";
                break;
            case 3:
                week = "TUESDAY";
                break;
            case 4:
                week = "WEDNESDAY";
                break;
            case 5:
                week = "THUSDAY";
                break;
            case 6:
                week = "FRIDAY";
                break;
            case 7:
                week = "SATURDAY";
                break;
        }
        ((TextView) findViewById(R.id.week)).setText(week);
        c.clear();
    }

    public void btnCalendar(View view) {
        gotoCalendarApp(this);
    }

    public static void gotoCalendarApp(Context cnt) {
        try {

            Intent t_intent = new Intent(Intent.ACTION_VIEW);
            t_intent.addCategory(Intent.CATEGORY_DEFAULT);
            t_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            t_intent.setDataAndType(Uri.parse("content://com.android.calendar/"), "time/epoch");
            cnt.startActivity(t_intent);

        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(cnt, "GoToCalendar-Failed", Toast.LENGTH_SHORT).show();

        }
    }

    private class TemUpdate extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = "https://www.sojson.com/open/api/weather/json.shtml?city="+address;
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
                if (buffer.toString() != null) {

                    JSONObject response = new JSONObject(buffer.toString());
                    if (response.optInt("status") == 200) {
                        temp = response.getJSONObject("data").optString("wendu");
                    } else if (response.optInt("status") == 400) {
                        temp = "err";
                    }
                    return temp;
                }
            }catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                urlConnection.disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String tempeture) {
            ((TextView) findViewById(R.id.temperature_of_the_day)).setText(tempeture);
        }
    }


    private class DownloadUpdate extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = "http://ip.chinaz.com/getip.aspx";
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
                JSONObject jsonObject =  new JSONObject(buffer.toString());
                address = jsonObject.optString("address");
                if(address.indexOf("市")!=-1){
                    if(address.indexOf("区")!=-1){
                        address = address.substring(address.indexOf("市")+1,address.indexOf("区"));
                    }else
                    address = address.substring(address.indexOf("省")+1,address.indexOf("市"));
                }
                if(address.indexOf("未知")!=-1){address = "未知";}
                return address;

            }catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                urlConnection.disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String address) {
            //Update the temperature displayed
            //Gson temp = new Gson();
            //Weather weather = temp.fromJson(temperature,Weather.class);
            //Data data = (Data)weather.getData();
            ((TextView) findViewById(R.id.tv_location)).setText(address);
        }
    }

    public static boolean isNetworkAvalible(Context context) {
        // 获得网络状态管理器
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 建立网络数组
            NetworkInfo[] net_info = connectivityManager.getAllNetworkInfo();

            if (net_info != null) {
                for (int i = 0; i < net_info.length; i++) {
                    // 判断获得的网络状态是否是处于连接状态
                    if (net_info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // 如果没有网络，则弹出网络设置对话框
    public static void checkNetwork(final Activity activity) {
        if (!MainActivity.isNetworkAvalible(activity)) {
            TextView msg = new TextView(activity);
            msg.setText("--No Network Or Internet For Service！");
            new AlertDialog.Builder(activity)
                    .setIcon(R.drawable.blue_bg)
                    .setTitle("Network Warning")
                    .setView(msg)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    // 跳转到设置界面
                                    activity.startActivityForResult(new Intent(
                                                    Settings.ACTION_WIRELESS_SETTINGS),
                                            0);
                                }
                            }).create().show();
        }
        return;
    }
}
