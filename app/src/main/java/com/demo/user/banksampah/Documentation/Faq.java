package com.demo.user.banksampah.Documentation;

import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.user.banksampah.Adapter.CustomProgress;
import com.demo.user.banksampah.Adapter.RestProcess;
import com.demo.user.banksampah.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.MySSLSocketFactory;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class Faq extends AppCompatActivity {

    //private ProgressDialog progressDialog;
    private CustomProgress customProgress;

    private RestProcess rest_class;
    protected HashMap<String,String> apiData;

    protected ConnectivityManager conMgr;

    protected WebView wb_Faq;
    protected TextView tvFaq_Hide;

    //private ArrayList<HashMap<String, String>> var_faq_detail = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        /*progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Menampilkan Informasi Frequently Asked Information, Harap Menunggu...");
*/
        customProgress = CustomProgress.getInstance();
        rest_class = new RestProcess();
        apiData = rest_class.apiErecycle();

        tvFaq_Hide = findViewById(R.id.tvFaq_Hide);

        wb_Faq = findViewById(R.id.tvFaq);
        wb_Faq.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        wb_Faq.setLongClickable(false);
        wb_Faq.setHapticFeedbackEnabled(false);
        //wb_Faq.setVerticalScrollBarEnabled(false);

        conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isConnected()) {
                getFaq();
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }

    }

    private void getFaq() {
        customProgress.showProgress(this, "", false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(MySSLSocketFactory.getFixedSocketFactory());
        //RequestParams params = new RequestParams();
        String base_url;

        base_url = apiData.get("str_url_address") + "/resource/Master%20FAQ/Master%20FAQ";
        client.addHeader(apiData.get("str_header"), apiData.get("str_token_value"));
        client.get(base_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                customProgress.hideProgress();
                String str = null;
                String[] field_name = {"data", "faq"};
                try {
                    str = new String(responseBody, "UTF-8");
                    Log.e("tag", str);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject jsonObject = new JSONObject(str);
                    JSONObject getData = jsonObject.getJSONObject(field_name[0]);

                    String faq = getData.getString(field_name[1]);
                    String change = faq.replace("\n", "<br>");
                    //String test = change.replace("\\t", "&emsp;");
                    //tvFaq_Hide.setText(faq);

                    wb_Faq.loadData(
                            "<p style=\'font-size:14px;text-align:justify;color:black\'>"
                                    + change +
                                    "</p>", "text/html", "UTF-8"
                    );

                } catch (Throwable t) {
                    Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_409) + " 1 : " + getString(R.string.MSG_CHECK_DATA), Toast.LENGTH_LONG).show();
                    Log.e("tag", " 1 :" + String.valueOf(t));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                customProgress.hideProgress();
                Toasty.error(getApplicationContext(), getString(R.string.MSG_CODE_500) + " 1 : " + getString(R.string.MSG_CHECK_CONN), Toast.LENGTH_LONG).show();
                Log.e("Tag", " 1: " + String.valueOf(error));
            }
        });

    }
}
