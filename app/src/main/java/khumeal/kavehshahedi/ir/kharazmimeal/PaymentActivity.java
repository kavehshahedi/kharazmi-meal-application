package khumeal.kavehshahedi.ir.kharazmimeal;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.http.SslError;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.wang.avi.AVLoadingIndicatorView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.text.NumberFormat;
import java.util.Locale;

import mehdi.sakout.fancybuttons.FancyButton;

public class PaymentActivity extends AppCompatActivity {

    WebView webView;

    public ConstraintLayout loadingLayout;
    public ConstraintLayout fieldsLayout;
    public ConstraintLayout checkLayout;
    public ConstraintLayout webLayout;
    public ConstraintLayout resultLayout;

    public AVLoadingIndicatorView loadingIndicator;

    public EditText moneyEditText;
    public FancyButton fieldsButton;

    public AppCompatTextView checkMoneyText;
    public AppCompatTextView checkFactorNumText;
    public FancyButton checkButton;

    public AppCompatTextView resultMessageText;
    public AppCompatTextView resultNewMoneyText;
    public FancyButton resultButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        webView = findViewById(R.id.paymentWebView);

        loadingLayout = findViewById(R.id.loadingPaymentLayout);
        fieldsLayout = findViewById(R.id.fieldsPaymentLayout);
        checkLayout = findViewById(R.id.checkPaymentLayout);
        webLayout = findViewById(R.id.webPaymentLayout);
        resultLayout = findViewById(R.id.resultPaymentLayout);

        loadingIndicator = findViewById(R.id.paymentLoadingIndicator);

        moneyEditText = findViewById(R.id.paymentMoneyEditText);
        fieldsButton = findViewById(R.id.paymentEnterMoneyButton);

        checkMoneyText = findViewById(R.id.paymentEnteredMoneyTextView);
        checkFactorNumText = findViewById(R.id.paymentFactorNumberTextView);
        checkButton = findViewById(R.id.paymentStartWebViewButton);

        resultMessageText = findViewById(R.id.paymentResultTextView);
        resultNewMoneyText = findViewById(R.id.paymentResultNewMoneyTextView);
        resultButton = findViewById(R.id.paymentResultButton);

        loadingLayout.setVisibility(View.VISIBLE);
        fieldsLayout.setVisibility(View.INVISIBLE);
        checkLayout.setVisibility(View.INVISIBLE);
        webLayout.setVisibility(View.INVISIBLE);
        resultLayout.setVisibility(View.INVISIBLE);

        webViewHandler();
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    private void webViewHandler()
    {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this,"handler");
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                    loadingLayout.setVisibility(View.VISIBLE);
                    fieldsLayout.setVisibility(View.INVISIBLE);
                    checkLayout.setVisibility(View.INVISIBLE);
                    webLayout.setVisibility(View.INVISIBLE);
                    resultLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String u = webView.getUrl();
                if (u.equals("http://meal.khu.ac.ir/Credit_CentralBank.aspx"))
                {
                    loadingLayout.setVisibility(View.INVISIBLE);
                    fieldsLayout.setVisibility(View.VISIBLE);
                    webView.loadUrl("javascript:window.handler.setFieldsLayout" + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");

                }else  if (u.equals("http://meal.khu.ac.ir/Credit_CentralBank_Went.aspx"))
                {
                    loadingLayout.setVisibility(View.INVISIBLE);
                    checkLayout.setVisibility(View.VISIBLE);
                    webView.loadUrl("javascript:window.handler.setCheckLayout" + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                }else if (u.equals("http://meal.khu.ac.ir/Credit_CentralBank_Revert.aspx"))
                {
                    loadingLayout.setVisibility(View.INVISIBLE);
                    resultLayout.setVisibility(View.VISIBLE);
                    webView.loadUrl("javascript:window.handler.setResultLayout" + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                }else if (u.contains("cbinasim"))
                {
                    loadingLayout.setVisibility(View.INVISIBLE);
                    webLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.e("Error",error.toString());
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
                Log.e("SSL",error.toString());
            }
        });
        webView.loadUrl("http://meal.khu.ac.ir/Credit_CentralBank.aspx");
    }

    @JavascriptInterface
    public void setFieldsLayout(String html)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fieldsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        if (!moneyEditText.getText().equals(""))
                        {
                            webView.post(new Runnable() {
                                @Override
                                public void run() {
                                    webView.loadUrl("javascript:var username = document.getElementById('txtAmount').value='"+moneyEditText.getText()+"';");
                                    webView.loadUrl("javascript:var button = document.getElementsByName('BtnOk')[0].click();");
                                }
                            });
                        }else
                        {
                            Snackbar snackbar = Snackbar
                                    .make(view, "مبلغ را وارد کنید", Snackbar.LENGTH_LONG);
                            TextView tv = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                            tv.setTypeface(ResourcesCompat.getFont(getApplicationContext(),R.font.isbold));
                            snackbar.show();
                        }
                    }
                });
            }
        });
    }

    @JavascriptInterface
    public void setCheckLayout(String html)
    {
        Document document = Jsoup.parse(html);
        String money = document.getElementById("txtAmount").attr("value");
        String factor = document.getElementById("txtInvoiceNo").attr("value");

        final String moneyT = "مبلغ مورد نظر : " + NumberFormat.getNumberInstance(Locale.getDefault()).format(Integer.parseInt(money)) + " ریال";
        final String factorT = "شماره فاکتور : " + factor;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                checkMoneyText.setText(moneyT);
                checkFactorNumText.setText(factorT);
                checkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        webView.post(new Runnable() {
                            @Override
                            public void run() {
                                webView.loadUrl("javascript:var buttonpay = document.getElementsByName('btnSubmit')[0].click();");
                            }
                        });
                    }
                });
            }
        });


    }

    @JavascriptInterface
    public void setResultLayout(String html)
    {
        Document document = Jsoup.parse(html);
        String type = document.getElementById("LblMessage").text();
        if (type.equals(""))
        {
            String newMoney = document.getElementById("NewEtebar").text();
            String wallet = newMoney;
            wallet = wallet.replaceAll(".*:","");
            wallet = wallet.replace("ريال","");
            wallet = wallet.replaceAll("\\s+","");

            ReserveActivity.setWallet(wallet);

            newMoney = newMoney.replace("مبلغ","");
            final String moneyT = newMoney;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    resultNewMoneyText.setText(moneyT);
                    resultMessageText.setText("تراکنش با موفیت انجام شد!");
                }
            });
        }else
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    resultNewMoneyText.setText("");
                    resultMessageText.setText("تراکنش با خطا مواجه شد!");
                }
            });
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                resultButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        finish();
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        PaymentActivity.super.onBackPressed();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog builder = new AlertDialog.Builder(this).setMessage("آیا انصراف می دهید؟").setPositiveButton("بله", dialogClickListener)
                .setNegativeButton("خیر", dialogClickListener).show();

        TextView textView = builder.findViewById(android.R.id.message);
        Button button1 = builder.getWindow().findViewById(android.R.id.button1);
        Button button2 = builder.getWindow().findViewById(android.R.id.button2);
        assert textView != null;
        textView.setTypeface(ResourcesCompat.getFont(this,R.font.isbold));
        button1.setTypeface(ResourcesCompat.getFont(this,R.font.isbold));
        button2.setTypeface(ResourcesCompat.getFont(this,R.font.isbold));
    }
}
