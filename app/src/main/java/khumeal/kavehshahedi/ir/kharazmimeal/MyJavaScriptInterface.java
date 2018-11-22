package khumeal.kavehshahedi.ir.kharazmimeal;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import static khumeal.kavehshahedi.ir.kharazmimeal.SplashActivity.webView;

public class MyJavaScriptInterface
{
    private Context ctx;
    String bodyText;
    String captcha;

    MyJavaScriptInterface(Context ctx)
    {
        this.ctx = ctx;
    }

    @JavascriptInterface
    public void showHTML(final String html)
    {
        bodyText = html;
        bodyText = bodyText.replace("\"","'");
        captcha = bodyText.substring(bodyText.indexOf("'CaptchaImage.aspx?guid=") + 1);
        captcha = "http://meal.khu.ac.ir/" + captcha.substring(0, captcha.indexOf("'"));

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
        }, 1000);
    }

    @JavascriptInterface
    public void logIn()
    {
        /*webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:var username = document.getElementById('txtusername').value='"+SplashActivity.loginActivity.getUsernameField().getEditText().getText()+"';");
                webView.loadUrl("javascript:var password = document.getElementById('txtpassword').value='"+SplashActivity.loginActivity.getPasswordField().getEditText().getText()+"';");
                webView.loadUrl("javascript:var captcha = document.getElementsByName('CaptchaControl1')[0].value='"+SplashActivity.loginActivity.getCaptchaField().getEditText().getText()+"';");
                webView.loadUrl("javascript:var buttonlogin = document.getElementsByName('btnlogin')[0].click();");
            }
        });*/
    }
}
