package khumeal.kavehshahedi.ir.kharazmimeal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import khumeal.kavehshahedi.ir.kharazmimeal.Adapters.DayItemsRecyclerViewAdapter;
import khumeal.kavehshahedi.ir.kharazmimeal.Adapters.DietItemsRecyclerViewAdapter;
import khumeal.kavehshahedi.ir.kharazmimeal.Models.DietModel;
import khumeal.kavehshahedi.ir.kharazmimeal.Models.SelfDataModel;
import mehdi.sakout.fancybuttons.FancyButton;

public class ReserveActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static String reserveHTML;
    private static String wallet = "0";

    Bundle extras;

    RecyclerView recyclerView;

    public ConstraintLayout reserveLoadingLayout;
    public ConstraintLayout reserveMainLayout;

    public NavigationView navigationView;
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle toggle;

    public AppCompatTextView nameText;
    public AppCompatTextView cardText;
    public AppCompatTextView walletText;

    Toolbar toolbar;

    String currentPage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);

        reserveLoadingLayout = findViewById(R.id.reserveLoadingLayout);
        reserveMainLayout = findViewById(R.id.reserveMainLayout);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                reserveLoadingLayout.setVisibility(View.INVISIBLE);
                reserveMainLayout.setVisibility(View.VISIBLE);
            }
        });

        toolbar = findViewById(R.id.reserveLayoutToolbar);
        setSupportActionBar(toolbar);

        extras = getIntent().getExtras();


        assert extras != null;
        reserveHTML = extras.getString("reserveHTML");

        recyclerView = findViewById(R.id.daysRecyclerView);

        navigationView = findViewById(R.id.navigationDrawer);
        drawerLayout = findViewById(R.id.drawerLayoutID);
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.drawerOpen,R.string.drawerClose);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                } else {
                    drawerLayout.openDrawer(Gravity.RIGHT);
                }
            }
        });

        View header = navigationView.getHeaderView(0);
        nameText = header.findViewById(R.id.navigationHeaderNameText);
        cardText = header.findViewById(R.id.navigationHeaderCardText);
        walletText = header.findViewById(R.id.navigationHeaderWalletText);

        changeNavigationDrawerFont();

        changeReserveWeek(0,0);
        currentPage = "CurrentWeek";

        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        final String wallet = "اعتبار : " + NumberFormat.getNumberInstance(Locale.getDefault()).format(Integer.parseInt(getWallet().replace(",",""))) + " ریال";
                        walletText.setText(wallet);
                    }
                });
            }
        }, 0, 1000);

    }

    List<SelfDataModel> selfDataModels = new ArrayList<>();
    public void setupSite(String html)
    {
        selfDataModels.clear();

        //Edit HTML Worlds
        html = html.replace("lblsat","lbl1");
        html = html.replace("lblSat","lbl1");
        html = html.replace("lblsun","lbl2");
        html = html.replace("lblSun","lbl2");
        html = html.replace("lblmon","lbl3");
        html = html.replace("lblMon","lbl3");
        html = html.replace("lblthr","lbl4");
        html = html.replace("lblThr","lbl4");
        html = html.replace("lblwed","lbl5");
        html = html.replace("lblWed","lbl5");
        html = html.replace("lblTur","lbl6");
        html = html.replace("lbltur","lbl6");
        html = html.replace("lblFri","lbl7");
        html = html.replace("lblfri","lbl7");

        Document document = Jsoup.parse(html);

        //Parse Names and Dates
        List<String> dayNames = new ArrayList<>();
        List<String> dayDates = new ArrayList<>();
        Elements dateElements = document.getElementsByClass("tarikh");
        for (int i = 1; i <= dateElements.size(); i++)
        {
            Element dayName = document.getElementById("Name" + i);
            Element dayDate = document.getElementById("D" + i);

            dayNames.add(dayName.text());
            dayDates.add(dayDate.text());
        }

        //Can Reserve Or Not
        List<String> canBreakfasts = new ArrayList<>();
        List<String> canLunches = new ArrayList<>();
        List<String> canDinners = new ArrayList<>();

        for (int i = 1; i <= 7; i++)
        {
            String canBreakfast = document.getElementById("GhazaC" + i).attr("class");
            String canLunch = document.getElementById("GhazaN" + i).attr("class");
            String canDinner = document.getElementById("GhazaS" + i).attr("class");

            canBreakfasts.add(canBreakfast);
            canLunches.add(canLunch);
            canDinners.add(canDinner);
        }

        //Parse Foods Name
        List<String> breakfastNames = new ArrayList<>();
        List<String> lunchNames = new ArrayList<>();
        List<String> dinnerNames = new ArrayList<>();

        for (int i = 1; i <= 7; i++)
        {
            String breakfastName = document.getElementById("lbl" + i + "_ghazaC" + i).text();
            String lunchName = document.getElementById("lbl" + i + "_ghazaN" + i).text();
            String dinnerName = document.getElementById("lbl" + i + "_ghazaS" + i).text();

            breakfastNames.add(breakfastName);
            lunchNames.add(lunchName);
            dinnerNames.add(dinnerName);
        }

        //Parse Reserve Links
        List<String> breakfastLinks = new ArrayList<>();
        List<String> lunchLinks = new ArrayList<>();
        List<String> dinnerLinks = new ArrayList<>();

        for (int i = 1; i <= 7; i++)
        {
            if (canBreakfasts.get(i - 1).equals("enable"))
            {
                String breakfastLink = document.getElementById("linkC" + i).attr("onclick");
                breakfastLinks.add(i - 1, breakfastLink);
            }else
            {
                breakfastLinks.add(i - 1, "null");
            }
        }
        for (int i = 1; i <= 7; i++)
        {
            if (canLunches.get(i - 1).equals("enable"))
            {
                String lunchLink = document.getElementById("link" + i).attr("onclick");
                lunchLinks.add(i - 1, lunchLink);
            }else
            {
                lunchLinks.add(i - 1,"null");
            }
        }
        for (int i = 1; i <= 7; i++)
        {
            if (canDinners.get(i - 1).equals("enable"))
            {
                String dinnerLink = document.getElementById("linkS" + i).attr("onclick");
                dinnerLinks.add(i - 1,dinnerLink);
            }else
            {
                dinnerLinks.add(i - 1,"null");
            }
        }

        //Parse Type,Number,Self
        List<String> breakfastNumbers = new ArrayList<>();
        List<String> breakfastSelves = new ArrayList<>();
        List<String> breakfastTypes = new ArrayList<>();

        List<String> lunchNumbers = new ArrayList<>();
        List<String> lunchSelves = new ArrayList<>();
        List<String> lunchTypes = new ArrayList<>();

        List<String> dinnerNumbers = new ArrayList<>();
        List<String> dinnerSelves = new ArrayList<>();
        List<String> dinnerTypes = new ArrayList<>();

        for (int i = 1; i <= 7; i++)
        {
            String breakfastNumber = "txtc_numGhaza" + i;
            String breakfastSelf = "EditC" + i;
            String breakfastType = "GhazaC" + i;

            String lunchNumber = "txtn_numGhaza" + i;
            String lunchSelf = "EditN" + i;
            String lunchType = "GhazaN" + i;

            String dinnerNumber = "txts_numGhaza" + i;
            String dinnerSelf = "EditS" + i;
            String dinnerType = "GhazaS" + i;

            breakfastNumbers.add(breakfastNumber);
            breakfastSelves.add(breakfastSelf);
            breakfastTypes.add(breakfastType);

            lunchNumbers.add(lunchNumber);
            lunchSelves.add(lunchSelf);
            lunchTypes.add(lunchType);

            dinnerNumbers.add(dinnerNumber);
            dinnerSelves.add(dinnerSelf);
            dinnerTypes.add(dinnerType);
        }


        //Create List of Days With Data
        for (int i = 0; i < 7; i++)
        {
            SelfDataModel model = new SelfDataModel(dayNames.get(i),dayDates.get(i),breakfastNames.get(i),canBreakfasts.get(i),breakfastLinks.get(i),breakfastNumbers.get(i),
                    breakfastSelves.get(i),breakfastTypes.get(i),lunchNames.get(i),canLunches.get(i),lunchLinks.get(i),lunchNumbers.get(i),lunchSelves.get(i),lunchTypes.get(i),
                    dinnerNames.get(i),canDinners.get(i),dinnerLinks.get(i),dinnerNumbers.get(i),dinnerSelves.get(i),dinnerTypes.get(i));
            selfDataModels.add(model);
        }

        //Get User Data
        wallet = document.getElementById("lbEtebar").text();
        final String name = document.getElementById("LbFName").text();
        final String card = "شماره کارت : " + document.getElementById("lbnumber").text();
        final String wallet = "اعتبار : " + document.getElementById("lbEtebar").text() + " ریال";

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                nameText.setText(name);
                cardText.setText(card);
                walletText.setText(wallet);
            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setupUI(selfDataModels);
            }
        });
    }

    public void setupUI(List<SelfDataModel> models)
    {
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        DayItemsRecyclerViewAdapter adapter = new DayItemsRecyclerViewAdapter(this,models);
        recyclerView.setAdapter(adapter);
    }

    public static String getWallet()
    {
        return wallet;
    }

    public static void setWallet(String w)
    {
        wallet = w;
    }

    boolean loadedOnce = false;
    boolean webHasLoaded = false;
    public static WebView web;
    @SuppressLint("SetJavaScriptEnabled")
    public void changeReserveWeek(final int week, final int mode)
    {
        //WEEK : 0 = Current Week  &  1 = Upcoming Week
        //MODE : 0 = Change Week  &  2 = Reserve Food

        if (web == null)
        {
            web = new WebView(this);
            web.getSettings().setJavaScriptEnabled(true);
            web.getSettings().setDomStorageEnabled(true);
            web.addJavascriptInterface(this,"handler");
            web.setWebViewClient(new WebViewClient()
            {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon)
                {
                    super.onPageStarted(view, url, favicon);
                    if (mode == 0)
                    {
                        reserveLoadingLayout.setVisibility(View.VISIBLE);
                        reserveMainLayout.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    //Toast.makeText(getApplicationContext(), mode + "\n" + week,Toast.LENGTH_LONG).show();
                    if (loadedOnce)
                    {
                        if (mode == 0)
                        {
                            switch (week)
                            {
                                case 0 :
                                {
                                    web.loadUrl("javascript:window.handler.updateReserveHandler" + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                                    break;
                                }
                                case 1 :
                                {
                                    web.loadUrl("javascript:window.handler.updateReserveHandler" + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                                    break;
                                }
                                case 3 :
                                {
                                    //Dast Nazani Be ... Mire
                                    //web.loadUrl("javascript:window.handler.updateReserveHandler" + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                                    break;
                                }
                                default:
                                {
                                    //web.loadUrl("javascript:window.handler.updateReserveHandler" + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                                    break;
                                }
                            }
                        }else
                        {

                        }
                    }
                    else
                    {
                        loadedOnce = true;
                        web.loadUrl("javascript:window.handler.updateReserveHandler" + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                        //changeReserveWeek(week,mode);
                    }
                }

                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    handler.proceed();
                }
            });
        }

        loadedOnce = false;
        if (!webHasLoaded)
        {
            webHasLoaded = true;
            web.loadUrl("http://meal.khu.ac.ir/Reserve.aspx");
        }
        else
        {
            if (mode == 0)
            {
                switch(week)
                {
                    case 0 :
                    {
                        currentPage = "CurrentWeek";
                        web.loadUrl("javascript:var x = __doPostBack('btncurrweek1','');");
                        break;
                    }

                    case 1 :
                    {
                        currentPage = "NextWeek";
                        web.loadUrl("javascript:__doPostBack('btnnextweek1','');");
                        break;
                    }

                    case 3 :
                    {
                        break;
                    }

                    default:break;
                }
            }
        }
    }

    @JavascriptInterface
    public void updateReserveHandler(String html)
    {
        reserveHTML = html;
        setupSite(html);
        Document document = Jsoup.parse(html);
        String msg = document.getElementById("LbMsg").text();
        //Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                reserveLoadingLayout.setVisibility(View.INVISIBLE);
                reserveMainLayout.setVisibility(View.VISIBLE);
            }
        });

        if (msg.contains("غیر مجاز") || msg.contains("غیرمجاز"))
        {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            finishAffinity();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog builder = new AlertDialog.Builder(this).setMessage("هنگام رزرو خطایی رخ داد. مجددا وارد شوید").setPositiveButton("خروج", dialogClickListener)
                    .setCancelable(false).show();

            TextView textView = builder.findViewById(android.R.id.message);
            Button button1 = builder.getWindow().findViewById(android.R.id.button1);
            assert textView != null;
            textView.setTypeface(ResourcesCompat.getFont(getApplicationContext(),R.font.isbold));
            button1.setTypeface(ResourcesCompat.getFont(getApplicationContext(),R.font.isbold));
        }
    }

    public void changeNavigationDrawerFont()
    {
        Menu m = navigationView.getMenu();
        for (int i=0;i<m.size();i++)
        {
            MenuItem mi = m.getItem(i);

            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 )
            {
                for (int j=0; j <subMenu.size();j++)
                {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
            applyFontToMenuItem(mi);
        }
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = ResourcesCompat.getFont(this,R.font.isbold);
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            this.drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            finishAffinity();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog builder = new AlertDialog.Builder(this).setMessage("آیا میخواهید خارج شوید؟").setPositiveButton("بله", dialogClickListener)
                    .setNegativeButton("خیر", dialogClickListener).show();

            TextView textView = builder.findViewById(android.R.id.message);
            Button button1 = builder.getWindow().findViewById(android.R.id.button1);
            Button button2 = builder.getWindow().findViewById(android.R.id.button2);
            assert textView != null;
            textView.setTypeface(ResourcesCompat.getFont(getApplicationContext(),R.font.isbold));
            button1.setTypeface(ResourcesCompat.getFont(getApplicationContext(),R.font.isbold));
            button2.setTypeface(ResourcesCompat.getFont(getApplicationContext(),R.font.isbold));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case(R.id.passwordMenuOption) :
            {
                openPasswordPopup();
                break;
            }
            case(R.id.walletMenuOption) :
            {
                startActivity(new Intent(ReserveActivity.this,PaymentActivity.class));
                break;
            }
            case (R.id.currentWeekMenuOption) :
            {
                if (currentPage != "CurrentWeek") changeReserveWeek(0,0);
                break;
            }
            case (R.id.nextWeekMenuOption) :
            {
                if (currentPage != "NextWeek") changeReserveWeek(1,0);
                break;
            }

            case (R.id.foodScheduleMenuOption) :
            {
                openDietPopup();
                break;
            }

            case (R.id.infoMenuOption) :
            {
                startActivity(new Intent(this,AboutUsActivity.class));
                break;
            }
            default:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }

    public Dialog passwordPopup;
    public ConstraintLayout loadingLayout;
    public ConstraintLayout passLayout;
    public WebView passWebView;
    public EditText passwordPopupCurrentField;
    public EditText passwordPopupNewField;
    public EditText passwordPopupNewAgainField;
    public FancyButton passwordPopupButton;
    public AppCompatTextView passwordErrorText;
    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    public void openPasswordPopup()
    {
        passwordPopup = new Dialog(this);
        passwordPopup.setContentView(R.layout.password_page);
        passwordPopup.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        if(Build.VERSION.SDK_INT <= 18)
        {
            int dividerID = passwordPopup.getContext().getResources()
                    .getIdentifier("android:id/titleDivider", null, null);
            View divider = passwordPopup.findViewById(dividerID);
            divider.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }

        passwordPopupCurrentField = passwordPopup.findViewById(R.id.passwordPopupCurrentPasswordField);
        passwordPopupNewField = passwordPopup.findViewById(R.id.passwordPopupNewPasswordField);
        passwordPopupNewAgainField = passwordPopup.findViewById(R.id.passwordPopupNewPasswordAgainField);
        passwordPopupButton = passwordPopup.findViewById(R.id.changePasswordButton);
        passwordErrorText = passwordPopup.findViewById(R.id.passwordErrorTextView);
        passLayout = passwordPopup.findViewById(R.id.passwordPopupMenu);
        loadingLayout = passwordPopup.findViewById(R.id.passwordPopupLoadingView);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                passwordErrorText.setText("");
            }
        });

        passWebView = new WebView(this);
        passWebView.getSettings().setJavaScriptEnabled(true);
        passWebView.addJavascriptInterface(this,"handler");
        passWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                loadingLayout.setVisibility(View.VISIBLE);
                passLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadingLayout.setVisibility(View.INVISIBLE);
                passLayout.setVisibility(View.VISIBLE);
                passWebView.loadUrl("javascript:window.handler.setPassLayout" + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        passWebView.loadUrl("http://meal.khu.ac.ir/ChangePass.aspx");

        passwordPopup.show();
    }

    @JavascriptInterface
    public void setPassLayout(String html)
    {
        Document document = Jsoup.parse(html);
        String message = document.getElementById("LbMsg").text();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                passwordErrorText.setText("");
                passwordPopupCurrentField.setText("");
                passwordPopupNewField.setText("");
                passwordPopupNewAgainField.setText("");
            }
        });

        //Toast.makeText(this,message,Toast.LENGTH_SHORT).show();

        switch (message)
        {
            case "در این صفحه می توانید نام کاربری و کلمه عبور خود را تغییر دهید" :
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        passwordPopupButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                if (!passwordPopupCurrentField.getText().toString().equals("") && !passwordPopupNewField.getText().toString().equals("")
                                        && !passwordPopupNewAgainField.getText().toString().equals(""))
                                {
                                    if(passwordPopupNewField.getText().toString().equals(passwordPopupNewAgainField.getText().toString()))
                                    {
                                        passWebView.loadUrl("javascript:var oldpass = document.getElementById('txtpassold').value='"+passwordPopupCurrentField.getText().toString()+"';");
                                        passWebView.loadUrl("javascript:var newpass = document.getElementById('txtnewpass').value='"+passwordPopupNewField.getText().toString()+"';");
                                        passWebView.loadUrl("javascript:var newpassagain = document.getElementById('txtrepPass').value='"+passwordPopupNewAgainField.getText().toString()+"';");
                                        passWebView.loadUrl("javascript:var buttonlogin = document.getElementsByName('btnlogin')[0].click();");
                                    }else
                                    {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                passwordErrorText.setText("رمز جدید مطابقت ندارد!");
                                            }
                                        });
                                    }
                                }else
                                {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            passwordErrorText.setText("همه ی فیلد ها را پر کنید!");
                                        }
                                    });
                                }
                            }
                        });
                    }
                });

                break;
            }
            case "تغییر رمز با موفقیت انجام شد" :
            {
                passwordPopup.dismiss();
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog builder = new AlertDialog.Builder(this).setMessage("تغییر رمز با موفقیت انجام شد!").setPositiveButton("باشد", dialogClickListener).show();

                TextView textView = builder.findViewById(android.R.id.message);
                Button button1 = builder.getWindow().findViewById(android.R.id.button1);
                assert textView != null;
                textView.setTypeface(ResourcesCompat.getFont(this,R.font.isbold));
                button1.setTypeface(ResourcesCompat.getFont(this,R.font.isbold));
                break;
            }
            case "رمز عبور قبل اشتباه می باشد" :
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        passwordErrorText.setText("رمز عبور قبل اشتباه می باشد");
                        passwordPopupCurrentField.setText("");
                        passwordPopupNewField.setText("");
                        passwordPopupNewAgainField.setText("");

                        passwordPopupButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                if (!passwordPopupCurrentField.getText().toString().equals("") && !passwordPopupNewField.getText().toString().equals("")
                                        && !passwordPopupNewAgainField.getText().toString().equals(""))
                                {
                                    if(passwordPopupNewField.getText().toString().equals(passwordPopupNewAgainField.getText().toString()))
                                    {
                                        passWebView.loadUrl("javascript:var oldpass = document.getElementById('txtpassold').value='"+passwordPopupCurrentField.getText().toString()+"';");
                                        passWebView.loadUrl("javascript:var newpass = document.getElementById('txtnewpass').value='"+passwordPopupNewField.getText().toString()+"';");
                                        passWebView.loadUrl("javascript:var newpassagain = document.getElementById('txtrepPass').value='"+passwordPopupNewAgainField.getText().toString()+"';");
                                        passWebView.loadUrl("javascript:var buttonlogin = document.getElementsByName('btnlogin')[0].click();");
                                    }else
                                    {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                passwordErrorText.setText("رمز جدید مطابقت ندارد!");
                                            }
                                        });
                                    }
                                }else
                                {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            passwordErrorText.setText("همه ی فیلد ها را پر کنید!");
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
                break;
            }
            default:break;
        }
    }

    public Dialog dietPopup;
    public WebView dietWeb;
    public ConstraintLayout dietPopupLoadingLayout;
    public ConstraintLayout dietPopupMainLayout;
    public RecyclerView dietPopupRecyclerView;

    @SuppressLint("SetJavaScriptEnabled")
    public void openDietPopup()
    {
        dietPopup = new Dialog(this);
        dietPopup.setContentView(R.layout.diet_popup);
        dietPopup.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        if (Build.VERSION.SDK_INT <= 18)
        {
            int dividerID = dietPopup.getContext().getResources()
                    .getIdentifier("android:id/titleDivider", null, null);
            View divider = dietPopup.findViewById(dividerID);
            divider.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dietPopupRecyclerView = dietPopup.findViewById(R.id.dietPopupRecyclerView);
                dietPopupLoadingLayout = dietPopup.findViewById(R.id.dietPopupLoadingView);
                dietPopupMainLayout = dietPopup.findViewById(R.id.dietPopupMainLayout);
            }
        });

        dietPopup.show();

        String html = reserveHTML;

        Document document = Jsoup.parse(html);
        Element td = document.getElementById("MenuGhaza").select("td").first();

        String s = td.html();s = s.replaceAll(".*window.open","");
        s = s.replaceAll(";.*","");
        s = s.substring(2,24);

        String url = "http://meal.khu.ac.ir/" + s;


        dietWeb = new WebView(this);
        dietWeb.getSettings().setJavaScriptEnabled(true);
        dietWeb.addJavascriptInterface(this,"handler");
        dietWeb.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                dietPopupMainLayout.setVisibility(View.INVISIBLE);
                dietPopupLoadingLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                dietPopupMainLayout.setVisibility(View.VISIBLE);
                dietPopupLoadingLayout.setVisibility(View.INVISIBLE);
                dietWeb.loadUrl("javascript:window.handler.setDietLayout" + "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {

            }
        });
        dietWeb.loadUrl(url);
    }


    List<DietModel> dietModels = new ArrayList<>();
    @JavascriptInterface
    public void setDietLayout(String html)
    {
        dietModels.clear();
        Document document = Jsoup.parse(html);

        Element table = document.getElementById("TABLE1");
        Elements rows = table.select("tr");

        try
        {
            for (int i = 1; i < rows.size(); i++)
            {
                Element row = rows.get(i);
                Elements cols = row.select("td");

                List<String> breakfasts = new ArrayList<>();
                List<String> lunches = new ArrayList<>();
                List<String> dinners = new ArrayList<>();

                for (int j = 0; j < cols.size(); j++)
                {
                    switch (j)
                    {
                        case 1 :
                        {
                            breakfasts.clear();
                            Element col = cols.get(j);
                            Elements ols = col.select("ol");

                            if (ols.size() > 0)
                            {
                                for (int k = 0; k < ols.size(); k++)
                                {
                                    Element ol = ols.get(k);
                                    Elements ils = ol.select("li");

                                    for (int w = 0; w < ils.size(); w++)
                                    {
                                        breakfasts.add(ils.get(w).text());
                                    }
                                }
                            }
                            else
                            {
                                breakfasts.add("null");
                            }
                            break;
                        }

                        case 2 :
                        {
                            lunches.clear();
                            Element col = cols.get(j);
                            Elements ols = col.select("ol");

                            if (ols.size() > 0)
                            {
                                for (int k = 0; k < ols.size(); k++)
                                {
                                    Element ol = ols.get(k);
                                    Elements ils = ol.select("li");

                                    for (int w = 0; w < ils.size(); w++)
                                    {
                                        lunches.add(ils.get(w).text());
                                    }
                                }
                            }else lunches.add("null");
                            break;
                        }

                        case 3 :
                        {
                            dinners.clear();
                            Element col = cols.get(j);
                            Elements ols = col.select("ol");

                            if (ols.size() > 0)
                            {
                                for (int k = 0; k < ols.size(); k++)
                                {
                                    Element ol = ols.get(k);
                                    Elements ils = ol.select("li");

                                    for (int w = 0; w < ils.size(); w++)
                                    {
                                        dinners.add(ils.get(w).text());
                                    }
                                }
                            }else dinners.add("null");
                            break;
                        }
                    }

                }

                DietModel model = new DietModel(cols.first().text(),breakfasts,lunches,dinners);
                dietModels.add(model);
            }
        }catch (Exception e){}

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dietPopupRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
                DietItemsRecyclerViewAdapter adapter = new DietItemsRecyclerViewAdapter(getApplicationContext(),dietModels);
                dietPopupRecyclerView.setAdapter(adapter);
            }
        });

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public Bitmap fastBlur(Bitmap sentBitmap, int radius) {
        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = ( 0xff000000 & pix[yi] ) | ( dv[rsum] << 16 ) | ( dv[gsum] << 8 ) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }

    private static Bitmap takeScreenShot(Activity activity)
    {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();

        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height  - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }


}
