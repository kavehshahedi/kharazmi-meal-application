package khumeal.kavehshahedi.ir.kharazmimeal;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import khumeal.kavehshahedi.ir.kharazmimeal.Services.AlarmBootReceiver;
import khumeal.kavehshahedi.ir.kharazmimeal.Services.NotificationReceiver;
import top.wefor.circularanim.CircularAnim;

public class SplashActivity extends AppCompatActivity {

    //A Comment To Check Git

    public static WebView webView;

    AVLoadingIndicatorView loading;

    public static String currentPage = null;

    public ImageView img;

    CircularProgressButton cpb;

    public EditText usernameField,passwordField,captchaField;

    public AppCompatTextView splashErrorText;

    public ConstraintLayout splashLoadingLayout;
    public ConstraintLayout splashLoginLayout;

    public TextView loadingTextView;

    final Timer t = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Configuration configuration = getResources().getConfiguration();
        configuration.setLayoutDirection(Locale.ENGLISH);
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        splashLoadingLayout = findViewById(R.id.splashLoadingLayout);
        splashLoginLayout = findViewById(R.id.splashLoginLayout);

        splashErrorText = findViewById(R.id.loginErrorTextView);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                splashLoadingLayout.setVisibility(View.VISIBLE);
                splashLoginLayout.setVisibility(View.INVISIBLE);
                splashErrorText.setText("");
            }
        });

        loading = findViewById(R.id.loadingIndicatorID);
        loadingTextView = findViewById(R.id.textView);

        img = findViewById(R.id.captchaImage);

        cpb = findViewById(R.id.loginButtonID);

        usernameField = findViewById(R.id.usernameFieldID);
        passwordField = findViewById(R.id.passwordFieldID);
        captchaField = findViewById(R.id.captchaFieldID);

        webView = findViewById(R.id.wvID);
        webView.setVisibility(View.INVISIBLE);
        webView.setClickable(false);


        t.schedule(new TimerTask() {
            @Override
            public void run() {
                if((currentPage != null) && currentPage.equals("Reserve"))
                {
                    startReservePage();
                    t.cancel();
                }
            }
        }, 0, 500);

        cpb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (!usernameField.getText().toString().equals("") && !passwordField.getText().toString().equals("") && !captchaField.getText().toString().equals(""))
                {
                    cpb.startAnimation();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            splashErrorText.setText("");
                        }
                    });

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            webView.loadUrl("javascript:window.handler.submit();");
                        }
                    }, 1);
                }else
                {
                    splashErrorText.setText("همه ی ورودی ها را بررسی کنید");
                }
            }
        });

        loadWebsite();
    }

    public void loadWebsite()
    {
        loading.show();
        loadingTextView.setVisibility(View.VISIBLE);
        if (isInternetAvailable(20000)) startWeb();
        else
        {
            loading.hide();
            loadingTextView.setVisibility(View.INVISIBLE);
            Snackbar snackbar = Snackbar
                    .make(splashLoadingLayout, "عدم اتصال به اینترنت", 10000000)
                    .setAction("دوباره", new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            loadWebsite();
                        }
                    });
            TextView tv = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
            TextView tvb = snackbar.getView().findViewById(android.support.design.R.id.snackbar_action);
            tv.setTypeface(ResourcesCompat.getFont(this,R.font.isbold));
            tvb.setTypeface(ResourcesCompat.getFont(this,R.font.isbold));
            tvb.setTextColor(Color.RED);
            snackbar.show();
        }
    }

    public boolean isInternetAvailable(int timeOut){
        InetAddress inetAddress = null;
        try {
            Future<InetAddress> future = Executors.newSingleThreadExecutor().submit(new Callable<InetAddress>() {
                @Override
                public InetAddress call() {
                    try {
                        return InetAddress.getByName("google.com");
                    } catch (UnknownHostException e) {
                        return null;
                    }
                }
            });
            inetAddress = future.get(timeOut, TimeUnit.MILLISECONDS);
            future.cancel(true);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (TimeoutException e) {
        }
        return inetAddress!=null && !inetAddress.equals("");
    }

    public void startReservePage()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                cpb.doneLoadingAnimation(Color.GREEN, BitmapFactory.decodeResource(getResources(), R.drawable.checked));
                CircularAnim.fullActivity(SplashActivity.this, cpb)
                        .colorOrImageRes(R.color.greenCircularButton)
                        .deployStartAnimator(new CircularAnim.OnAnimatorDeployListener()
                        {
                            @Override
                            public void deployAnimator(Animator animator)
                            {
                                animator.setDuration(400);
                            }
                        })
                        .deployReturnAnimator(new CircularAnim.OnAnimatorDeployListener()
                        {
                            @Override
                            public void deployAnimator(Animator animator)
                            {
                                animator.setDuration(250);
                            }
                        })
                        .go(new CircularAnim.OnAnimationEndListener()
                        {
                            @Override
                            public void onAnimationEnd()
                            {
                                startActivity(new Intent(SplashActivity.this, ReserveActivity.class).putExtra("reserveHTML",reserveHTML));
                            }
                        });
            }
        });
    }

    public void setCaptcha(String captcha)
    {
        Picasso.get().load(captcha).into(img);
    }

    @SuppressLint({"JavascriptInterface", "AddJavascriptInterface", "SetJavaScriptEnabled"})
    public void startWeb()
    {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this,"handler");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String u = webView.getUrl();
                if(u.equalsIgnoreCase("http://meal.khu.ac.ir/login.aspx"))
                {
                    webView.loadUrl("javascript:window.handler.showHTML" + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                }
                if(u.equalsIgnoreCase("http://meal.khu.ac.ir/Reserve.aspx"))
                {
                    //Toast.makeText(getApplicationContext(),"This Is Login",Toast.LENGTH_SHORT).show();
                    webView.loadUrl("javascript:window.handler.getReserveHTML" + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                    currentPage = "Reserve";
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        webView.loadUrl("http://meal.khu.ac.ir/login.aspx");
    }

    String bodyText,captcha;

    @JavascriptInterface
    public void showHTML(String html)
    {
        Document document = Jsoup.parse(html);
        String message = document.getElementById("lblmessage").text();

        switch (message)
        {
            case("نام کاربری و کلمه عبور خود را وارد نمائید") :
            {
                bodyText = html;
                bodyText = bodyText.replace("\"","'");
                captcha = bodyText.substring(bodyText.indexOf("'CaptchaImage.aspx?guid=") + 1);
                captcha = "http://meal.khu.ac.ir/" + captcha.substring(0, captcha.indexOf("'"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        splashLoadingLayout.setVisibility(View.INVISIBLE);
                        splashLoginLayout.setVisibility(View.VISIBLE);
                        setCaptcha(captcha);
                    }
                });
                break;
            }

            case("کد امنیتی اشتباه است") :
            {

                bodyText = html;
                bodyText = bodyText.replace("\"","'");
                captcha = bodyText.substring(bodyText.indexOf("'CaptchaImage.aspx?guid=") + 1);
                captcha = "http://meal.khu.ac.ir/" + captcha.substring(0, captcha.indexOf("'"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cpb.revertAnimation();
                        usernameField.setText("");
                        passwordField.setText("");
                        captchaField.setText("");

                        splashErrorText.setText("کد امنیتی اشتباه است");

                        splashLoadingLayout.setVisibility(View.INVISIBLE);
                        splashLoginLayout.setVisibility(View.VISIBLE);
                        setCaptcha(captcha);
                    }
                });
                break;
            }

            case("شماره کارت یا کلمه عبور اشتباه است") :
            {

                bodyText = html;
                bodyText = bodyText.replace("\"","'");
                captcha = bodyText.substring(bodyText.indexOf("'CaptchaImage.aspx?guid=") + 1);
                captcha = "http://meal.khu.ac.ir/" + captcha.substring(0, captcha.indexOf("'"));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cpb.revertAnimation();

                        usernameField.setText("");
                        passwordField.setText("");
                        captchaField.setText("");

                        splashErrorText.setText("شماره کارت یا کلمه عبور اشتباه است");

                        splashLoadingLayout.setVisibility(View.INVISIBLE);
                        splashLoginLayout.setVisibility(View.VISIBLE);
                        setCaptcha(captcha);
                    }
                });
                break;
            }
            default:break;
        }
        //startActivity(new Intent(SplashActivity.this, LoginActivity.class).putExtra("captcha", getCaptcha()));
    }

    public static String reserveHTML;
    @JavascriptInterface
    public void getReserveHTML(String html)
    {
        reserveHTML = html;
    }

    public String getCaptcha()
    {
        return  captcha;
    }

    @JavascriptInterface
    public void submit()
    {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                logIn();
            }
        }, 1);
    }

    @JavascriptInterface
    public void logIn()
    {
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:var username = document.getElementById('txtusername').value='"+usernameField.getText()+"';");
                webView.loadUrl("javascript:var password = document.getElementById('txtpassword').value='"+passwordField.getText()+"';");
                webView.loadUrl("javascript:var captcha = document.getElementsByName('CaptchaControl1')[0].value='"+captchaField.getText()+"';");
                webView.loadUrl("javascript:var buttonlogin = document.getElementsByName('btnlogin')[0].click();");
            }
        });
    }

    public static void setupNotification(Context context)
    {
        Calendar currentTime = Calendar.getInstance();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE,30);
        calendar.set(Calendar.SECOND,0);

        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        assert alarmManager != null;
        if (currentTime.getTimeInMillis() <  calendar.getTimeInMillis()) {
        } else {
            int dayDiffBetweenClosestFriday = (7 + calendar.get(Calendar.DAY_OF_WEEK) - calendar.get(Calendar.DAY_OF_WEEK)) % 7;

            if (dayDiffBetweenClosestFriday == 0)
            {
                dayDiffBetweenClosestFriday = 7;
            }

            calendar.add(Calendar.DAY_OF_MONTH, dayDiffBetweenClosestFriday);
        }

        int interval = 1000 * 60 * 60 * 24 * 7;

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);


        ComponentName receiver = new ComponentName(context, NotificationReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
}