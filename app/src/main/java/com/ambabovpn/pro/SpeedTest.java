package com.ambabovpn.pro;

import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ambabovpn.pro.test.HttpDownloadTest;
import com.ambabovpn.pro.test.HttpUploadTest;
import com.ambabovpn.pro.test.PingTest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import javax.net.ssl.HttpsURLConnection;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONObject;

import com.ambabovpn.pro.util.ToastUtil;


public class SpeedTest extends AppCompatActivity {

    public String data, ip, city, state, country, isp;
    JSONObject jsonObject;
    String s;
    TextView txCity, txIp, txState, txCountry, status, txIsp, Date;

    static int position = 0;
    static int lastPosition = 0;
    GetSpeedTestHostsHandler getSpeedTestHostsHandler = null;
    HashSet<String> tempBlackList;

    ToastUtil toastutil;

    @Override
    public void onResume() {
        super.onResume();

        getSpeedTestHostsHandler = new GetSpeedTestHostsHandler();
        getSpeedTestHostsHandler.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pktsp);

        toastutil = new ToastUtil(this);
        //status.setText("Click Test");
        //IP and Locations
        tempBlackList = new HashSet<>();
        txCity = (TextView)findViewById(R.id.txcity);
        status = (TextView)findViewById(R.id.status);
        txIp = (TextView)findViewById(R.id.textView4);
        txState = (TextView)findViewById(R.id.txstate);
        txCountry = (TextView)findViewById(R.id.txcountry);
        txIsp = (TextView)findViewById(R.id.txisp);

        GetIp2 myAs=new GetIp2();
        myAs.execute();

        //Date
        Date = (TextView) findViewById(R.id.txtDate);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("E, MMM dd, yyyy", Locale.getDefault());    
        String date = df.format(c.getTime());
        Date.setText(date);

        final Button startButton = (Button) findViewById(R.id.startButton);
        final DecimalFormat dec = new DecimalFormat("#.##");
        startButton.setText("Click Test");

        tempBlackList = new HashSet<>();

        getSpeedTestHostsHandler = new GetSpeedTestHostsHandler();
        getSpeedTestHostsHandler.start();

        startButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startButton.setEnabled(false);

                    //Restart test icin eger baglanti koparsa
                    if (getSpeedTestHostsHandler == null) {
                        getSpeedTestHostsHandler = new GetSpeedTestHostsHandler();
                        getSpeedTestHostsHandler.start();
                    }


                    new Thread(new Runnable() {
                            RotateAnimation rotate;
                            ImageView barImageView1 = (ImageView) findViewById(R.id.barImageView1);
                            ImageView barImageView2 = (ImageView) findViewById(R.id.barImageView2);
                            ImageView barImageView = (ImageView) findViewById(R.id.barImageView);
                            TextView pingTextView = (TextView) findViewById(R.id.pingTextView);
                            TextView downloadTextView = (TextView) findViewById(R.id.downloadTextView);
                            TextView uploadTextView = (TextView) findViewById(R.id.uploadTextView);

                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            status.setText("Selecting best server based on ping");
                                            startButton.setText("Please Wait");
                                        }
                                    });

                                //Get egcodes.speedtest hosts
                                int timeCount = 600; //1min
                                while (!getSpeedTestHostsHandler.isFinished()) {
                                    timeCount--;
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                    }
                                    if (timeCount <= 0) {
                                        runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    toastutil.showInfoToast("Thank you for supporting the app !! 💙");
                                                    startButton.setEnabled(true);
                                                    startButton.setTextSize(16);
                                                    startButton.setText("Restart Test");
                                                }
                                            });
                                        getSpeedTestHostsHandler = null;
                                        return;
                                    }
                                }

                                //Find closest server
                                HashMap<Integer, String> mapKey = getSpeedTestHostsHandler.getMapKey();
                                HashMap<Integer, List<String>> mapValue = getSpeedTestHostsHandler.getMapValue();
                                double selfLat = getSpeedTestHostsHandler.getSelfLat();
                                double selfLon = getSpeedTestHostsHandler.getSelfLon();
                                double tmp = 19349458;
                                double dist = 0.0;
                                int findServerIndex = 0;
                                for (int index : mapKey.keySet()) {
                                    if (tempBlackList.contains(mapValue.get(index).get(5))) {
                                        continue;
                                    }

                                    Location source = new Location("Source");
                                    source.setLatitude(selfLat);
                                    source.setLongitude(selfLon);

                                    List<String> ls = mapValue.get(index);
                                    Location dest = new Location("Dest");
                                    dest.setLatitude(Double.parseDouble(ls.get(0)));
                                    dest.setLongitude(Double.parseDouble(ls.get(1)));

                                    double distance = source.distanceTo(dest);
                                    if (tmp > distance) {
                                        tmp = distance;
                                        dist = distance;
                                        findServerIndex = index;
                                    }
                                }
                                String testAddr = mapKey.get(findServerIndex).replace("http://", "https://");
                                final List<String> info = mapValue.get(findServerIndex);
                                final double distance = dist;

                                if (info == null) {
                                    runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                startButton.setTextSize(12);
                                                status.setText("There was a problem in getting Host Location. Try again later.");
                                            }
                                        });
                                    return;
                                }

                                runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            startButton.setText("Starting");
                                            startButton.setTextSize(15);
                                            status.setText(String.format("Hosted by %s (%s) [%s km]", info.get(5), info.get(3), new DecimalFormat("#.##").format(distance / 1000)));
                                        }
                                    });

                                //Init Ping graphic
                                final LinearLayout chartPing = (LinearLayout) findViewById(R.id.chartPing);
                                XYSeriesRenderer pingRenderer = new XYSeriesRenderer();
                                XYSeriesRenderer.FillOutsideLine pingFill = new XYSeriesRenderer.FillOutsideLine(XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ALL);
                                pingFill.setColor(R.color.colorPrimaryDark);
                                pingRenderer.addFillOutsideLine(pingFill);
                                pingRenderer.setDisplayChartValues(true);
                                pingRenderer.setShowLegendItem(true);
                                pingRenderer.setColor(Color.parseColor("#000000"));
                                pingRenderer.setLineWidth(5);
                                final XYMultipleSeriesRenderer multiPingRenderer = new XYMultipleSeriesRenderer();
                                multiPingRenderer.setXLabels(0);
                                multiPingRenderer.setYLabels(0);
                                multiPingRenderer.setZoomEnabled(true);
                                multiPingRenderer.setAxesColor((Color.parseColor("#647488")));
                                multiPingRenderer.setYAxisColor(Color.parseColor("#2F3C4C"));
                                multiPingRenderer.setPanEnabled(true, true);
                                multiPingRenderer.setShowAxes(true);
                                multiPingRenderer.setShowGrid(true);
                                multiPingRenderer.setGridColor(Color.YELLOW);
                                multiPingRenderer.setZoomButtonsVisible(false);
                                multiPingRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
                                multiPingRenderer.addSeriesRenderer(pingRenderer);

                                //Init Download graphic
                                final LinearLayout chartDownload = (LinearLayout) findViewById(R.id.chartDownload);
                                XYSeriesRenderer downloadRenderer = new XYSeriesRenderer();
                                XYSeriesRenderer.FillOutsideLine downloadFill = new XYSeriesRenderer.FillOutsideLine(XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ALL);
                                downloadFill.setColor(R.color.colorPrimaryDark);
                                downloadRenderer.addFillOutsideLine(downloadFill);
                                downloadRenderer.setDisplayChartValues(true);
                                downloadRenderer.setColor(Color.parseColor("#000000"));
                                downloadRenderer.setShowLegendItem(true);
                                downloadRenderer.setLineWidth(5);
                                final XYMultipleSeriesRenderer multiDownloadRenderer = new XYMultipleSeriesRenderer();
                                multiDownloadRenderer.setXLabels(0);
                                multiDownloadRenderer.setYLabels(0);
                                multiDownloadRenderer.setZoomEnabled(false);
                                multiDownloadRenderer.setXAxisColor(Color.parseColor("#647488"));
                                multiDownloadRenderer.setYAxisColor(Color.parseColor("#2F3C4C"));
                                multiDownloadRenderer.setPanEnabled(false, false);
                                multiDownloadRenderer.setShowGrid(true);
                                multiDownloadRenderer.setGridColor(Color.YELLOW);
                                multiDownloadRenderer.setZoomButtonsVisible(false);
                                multiDownloadRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
                                multiDownloadRenderer.addSeriesRenderer(downloadRenderer);

                                //Init Upload graphic
                                final LinearLayout chartUpload = (LinearLayout) findViewById(R.id.chartUpload);
                                XYSeriesRenderer uploadRenderer = new XYSeriesRenderer();
                                XYSeriesRenderer.FillOutsideLine uploadFill = new XYSeriesRenderer.FillOutsideLine(XYSeriesRenderer.FillOutsideLine.Type.BOUNDS_ALL);
                                uploadFill.setColor(R.color.colorPrimaryDark);
                                uploadRenderer.addFillOutsideLine(uploadFill);
                                uploadRenderer.setDisplayChartValues(true);
                                uploadRenderer.setColor(Color.parseColor("#000000"));
                                uploadRenderer.setShowLegendItem(true);
                                uploadRenderer.setLineWidth(5);
                                final XYMultipleSeriesRenderer multiUploadRenderer = new XYMultipleSeriesRenderer();
                                multiUploadRenderer.setXLabels(0);
                                multiUploadRenderer.setYLabels(0);
                                multiUploadRenderer.setZoomEnabled(false);
                                multiUploadRenderer.setXAxisColor(Color.parseColor("#647488"));
                                multiUploadRenderer.setYAxisColor(Color.parseColor("#2F3C4C"));
                                multiUploadRenderer.setPanEnabled(false, false);
                                multiUploadRenderer.setZoomButtonsVisible(false);
                                multiUploadRenderer.setShowGrid(true);
                                multiUploadRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
                                multiUploadRenderer.addSeriesRenderer(uploadRenderer);


                                //Reset value, graphics
                                runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pingTextView.setText("0 ms");
                                            chartPing.removeAllViews();
                                            downloadTextView.setText("0 Mbps");
                                            chartDownload.removeAllViews();
                                            uploadTextView.setText("0 Mbps");
                                            chartUpload.removeAllViews();
                                        }
                                    });
                                final List<Double> pingRateList = new ArrayList<>();
                                final List<Double> downloadRateList = new ArrayList<>();
                                final List<Double> uploadRateList = new ArrayList<>();
                                Boolean pingTestStarted = false;
                                Boolean pingTestFinished = false;
                                Boolean downloadTestStarted = false;
                                Boolean downloadTestFinished = false;
                                Boolean uploadTestStarted = false;
                                Boolean uploadTestFinished = false;

                                //Init Test
                                final PingTest pingTest = new PingTest(info.get(6).replace(":8080", ""), 3);
                                final HttpDownloadTest downloadTest = new HttpDownloadTest(testAddr.replace(testAddr.split("/")[testAddr.split("/").length - 1], ""));
                                final HttpUploadTest uploadTest = new HttpUploadTest(testAddr);


                                //Tests
                                while (true) {
                                    if (!pingTestStarted) {
                                        pingTest.start();
                                        pingTestStarted = true;
                                    }
                                    if (pingTestFinished && !downloadTestStarted) {
                                        downloadTest.start();
                                        downloadTestStarted = true;
                                    }
                                    if (downloadTestFinished && !uploadTestStarted) {
                                        uploadTest.start();
                                        uploadTestStarted = true;
                                    }


                                    //Ping Test
                                    if (pingTestFinished) {
                                        //Failure
                                        if (pingTest.getAvgRtt() == 0) {
                                            System.out.println("Ping error...");
                                        } else {
                                            //Success
                                            runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        pingTextView.setText(dec.format(pingTest.getAvgRtt()) + " ms");
                                                    }
                                                });
                                        }
                                    } else {
                                        pingRateList.add(pingTest.getInstantRtt());

                                        runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    pingTextView.setText(dec.format(pingTest.getInstantRtt()) + " ms");
                                                }
                                            });

                                        //Update chart
                                        runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    // Creating an  XYSeries for Income
                                                    XYSeries pingSeries = new XYSeries("");
                                                    pingSeries.setTitle("");

                                                    int count = 0;
                                                    List<Double> tmpLs = new ArrayList<>(pingRateList);
                                                    for (Double val : tmpLs) {
                                                        pingSeries.add(count++, val);
                                                    }

                                                    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                                                    dataset.addSeries(pingSeries);

                                                    GraphicalView chartView = ChartFactory.getLineChartView(getBaseContext(), dataset, multiPingRenderer);
                                                    chartPing.addView(chartView, 0);

                                                }
                                            });
                                    }


                                    //Download Test

                                    //Download Test
                                    if (pingTestFinished)
                                    {
                                        if (downloadTestFinished)
                                        {
                                            //Failure
                                            if (downloadTest.getFinalDownloadRate() == 0)
                                            {
                                                System.out.println("Download error...");
                                            }
                                            else
                                            {
                                                //Success
                                                runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run()
                                                        {
                                                            downloadTextView.setText(dec.format(downloadTest.getFinalDownloadRate()) + " Mbps");
                                                        }
                                                    });
                                            }
                                        }
                                        else
                                        {
                                            //Calc position
                                            double downloadRate = downloadTest.getInstantDownloadRate();
                                            downloadRateList.add(downloadRate);
                                            position = getPositionByRate(downloadRate);

                                            runOnUiThread(new Runnable() {

                                                    @Override
                                                    public void run()
                                                    {
                                                        rotate = new RotateAnimation(lastPosition, position, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                                        rotate.setInterpolator(new LinearInterpolator());
                                                        rotate.setDuration(100);
                                                        barImageView.startAnimation(rotate);
                                                        downloadTextView.setText(dec.format(downloadTest.getInstantDownloadRate()) + " Mbps");

                                                    }

                                                });
                                            lastPosition = position;

                                            //Update chart
                                            runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run()
                                                    {
                                                        // Creating an  XYSeries for Income
                                                        XYSeries downloadSeries = new XYSeries("");
                                                        downloadSeries.setTitle("");

                                                        List<Double> tmpLs = new ArrayList<>(downloadRateList);
                                                        int count = 0;
                                                        for (Double val : tmpLs)
                                                        {
                                                            downloadSeries.add(count++, val);
                                                        }

                                                        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                                                        dataset.addSeries(downloadSeries);

                                                        GraphicalView chartView = ChartFactory.getLineChartView(getBaseContext(), dataset, multiDownloadRenderer);
                                                        chartDownload.addView(chartView, 0);
                                                    }
                                                });

                                        }
                                    }


                                    //Upload Test
                                    if (downloadTestFinished)
                                    {
                                        if (uploadTestFinished)
                                        {
                                            //Failure
                                            if (uploadTest.getFinalUploadRate() == 0)
                                            {
                                                System.out.println("Upload error...");
                                            }
                                            else
                                            {
                                                //Success
                                                runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run()
                                                        {
                                                            uploadTextView.setText(dec.format(uploadTest.getFinalUploadRate()) + " Mbps");
                                                        }
                                                    });
                                            }
                                        }
                                        else
                                        {
                                            //Calc position
                                            double uploadRate = uploadTest.getInstantUploadRate();
                                            uploadRateList.add(uploadRate);
                                            position = getPositionByRate(uploadRate);

                                            runOnUiThread(new Runnable() {

                                                    @Override
                                                    public void run()
                                                    {
                                                        rotate = new RotateAnimation(lastPosition, position, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                                        rotate.setInterpolator(new LinearInterpolator());
                                                        rotate.setDuration(100);
                                                        barImageView2.startAnimation(rotate);
                                                        uploadTextView.setText(dec.format(uploadTest.getInstantUploadRate()) + " Mbps");
                                                    }

                                                });
                                            lastPosition = position;

                                            //Update chart
                                            runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run()
                                                    {
                                                        // Creating an  XYSeries for Income
                                                        XYSeries uploadSeries = new XYSeries("");
                                                        uploadSeries.setTitle("");

                                                        int count = 0;
                                                        List<Double> tmpLs = new ArrayList<>(uploadRateList);
                                                        for (Double val : tmpLs)
                                                        {
                                                            if (count == 0)
                                                            {
                                                                val = 0.0;
                                                            }
                                                            uploadSeries.add(count++, val);
                                                        }

                                                        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                                                        dataset.addSeries(uploadSeries);

                                                        GraphicalView chartView = ChartFactory.getLineChartView(getBaseContext(), dataset, multiUploadRenderer);
                                                        chartUpload.addView(chartView, 0);
                                                    }
                                                });

                                        }
                                    }

                                    //Test bitti
                                    if (pingTestFinished && downloadTestFinished && uploadTest.isFinished())
                                    {
                                        break;
                                    }

                                    if (pingTest.isFinished())
                                    {
                                        pingTestFinished = true;
                                    }
                                    if (downloadTest.isFinished())
                                    {
                                        downloadTestFinished = true;
                                    }
                                    if (uploadTest.isFinished())
                                    {
                                        uploadTestFinished = true;
                                    }

                                    if (pingTestStarted && !pingTestFinished)
                                    {
                                        try
                                        {
                                            Thread.sleep(300);
                                        }
                                        catch (InterruptedException e)
                                        {
                                        }
                                    }
                                    else
                                    {
                                        try
                                        {
                                            Thread.sleep(100);
                                        }
                                        catch (InterruptedException e)
                                        {
                                        }
                                    }
                                }

                                //Thread bitiminde button yeniden aktif ediliyor
                                runOnUiThread(new Runnable() {
                                        @Override
                                        public void run()
                                        {
                                            startButton.setEnabled(true);
                                            status.setText("Ping:"+ (dec.format(pingTest.getInstantRtt()) + " ms ") + "Download:"+ (dec.format(downloadTest.getInstantDownloadRate()) + " Mbps ") + "Upload:"+ (dec.format(uploadTest.getInstantUploadRate()) + " Mbps "));
                                            startButton.setTextSize(12);
                                            startButton.setText("Ok Test");
                                        }
                                    });


                            }
                        }).start();
                }
            });
    }


    public int getPositionByRate(double rate) {
        if (rate <= 1) {
            return (int) (rate * 30);

        } else if (rate <= 10) {
            return (int) (rate * 6) + 30;

        } else if (rate <= 30) {
            return (int) ((rate - 10) * 3) + 90;

        } else if (rate <= 50) {
            return (int) ((rate - 30) * 1.5) + 150;

        } else if (rate <= 100) {
            return (int) ((rate - 50) * 1.2) + 180;
        }

        return 0;
    }

    //Location and Ip address
    class GetIp2 extends AsyncTask<String, Void, String>
    {
        //String s = null;

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            txIp.setText(ip);
            txCity.setText(city);
            txState.setText(state);
            txCountry.setText(country);
            txIsp.setText(isp);
            // textView.setText(ip+city+state+country);
            //  mapView.animate();
        }

        @Override
        protected String doInBackground(String... params)
        {

            try
            {
                URL whatismyip = new URL("https://checkip.amazonaws.com");
                BufferedReader input = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
                ip = input.readLine();

                HttpURLConnection connection = (HttpURLConnection) new URL("http://ip-api.com/json/" + ip).openConnection();
                connection.setReadTimeout(3000);
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpsURLConnection.HTTP_OK)
                {
                    throw new IOException("HTTP error code: " + responseCode);
                }
                InputStream in = new java.io.BufferedInputStream(connection.getInputStream());
                //s=readStream(in,10000);
                s = convertStreamToString(in);
                jsonObject = new JSONObject(s);

                city = jsonObject.getString("city");
                isp = jsonObject.getString("org");
                state = jsonObject.getString("regionName");
                country = jsonObject.getString("country");

                Log.d("MainActivity", "Call reached here");

                if (in != null)
                {
                    // Converts Stream to String with max length of 500.
                    Log.d("MainActivity call 2", s);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return s;
        }

        public String convertStreamToString(InputStream is)
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            String line;
            try
            {
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line).append('\n');
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    is.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            return sb.toString();
        }

    }

}

