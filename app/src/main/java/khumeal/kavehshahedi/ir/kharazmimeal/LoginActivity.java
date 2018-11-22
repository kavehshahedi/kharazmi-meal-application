package khumeal.kavehshahedi.ir.kharazmimeal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LoginActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Bundle extras = getIntent().getExtras();
        //setCaptcha(extras.getString("captcha"));

        //SplashActivity.setFields(usernameField,passwordField,captchaField);
    }
}
