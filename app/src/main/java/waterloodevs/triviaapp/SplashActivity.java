package waterloodevs.triviaapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moveToCountDown();
    }
    private void moveToCountDown() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }, 1000);
    }
}
