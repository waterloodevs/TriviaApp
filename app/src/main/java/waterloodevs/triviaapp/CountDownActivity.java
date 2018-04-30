package waterloodevs.triviaapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import database.FBDatabase;
import database.data.Question;
import com.google.firebase.auth.FirebaseAuth;

import java.text.DecimalFormat;

public class CountDownActivity extends AppCompatActivity implements View.OnClickListener{

    public static Intent getIntent(Context context) {
        return new Intent(context, CountDownActivity.class);
    }

    private FirebaseAuth firebaseAuth;
    DecimalFormat kinFormat = new DecimalFormat("#,###,###");
    private final static int MAX_HOURS = 100;
    private final static long MAX_HOURS_IN_MILLISECONDS = MAX_HOURS * 60 * 60 * 1000;
    private Question question;
    private TextView joinTelegram, keepMePosted;
    private TextView prize, balance, nextQuestionTitle;
    private View prizeTelegram;
    private SpannableString telegramSpannable = new SpannableString("Telegram");
    private final static String TELEGRAM_LINK = "https://t.me/kinfoundation";
    private long serverTime;
    private ClockCountDownView clockCountDownView;
    private boolean timerComplete;
    private boolean shouldAnimate = true;
    private Thread animHourGlassThread;
    private Animatable animatable;
    private Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.countdown_activity);
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(this);
        joinTelegram = findViewById(R.id.join_telegram_title);
        nextQuestionTitle = findViewById(R.id.next_question_title);
        prizeTelegram = findViewById(R.id.prize_telegram);
        prize = findViewById(R.id.prize);
        clockCountDownView = findViewById(R.id.clock_count_down);
        if (!FBDatabase.getInstance().questions.isEmpty()) {
            question = FBDatabase.getInstance().questions.get(0);
        }
        serverTime = 0;
        ImageView progressHourGlass = findViewById(R.id.timer);
        animatable = ((Animatable) progressHourGlass.getDrawable());
        updatePendingBalance();
        init();
    }

    private void updatePendingBalance() {
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        //updatePendingBalance();
//        //startThreadAnimation();
//        if (timerComplete) {
//            int nextQuestionIndex = FBDatabase.getInstance().nextQuestionIndex;
//            FBDatabase.getInstance().getQuestionAt(nextQuestionIndex + FBDatabase.MAX_QUESTIONS_IN_RAW, new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    question = dataSnapshot.getValue(Question.class);
//                    if (question == null) {
//                        Toast.makeText(CountDownActivity.this, "No More Questions for now... ", Toast.LENGTH_SHORT).show();
//                        finish();
//                    } else {
//                        init();
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                }
//            });
//        }
//    }
//
//    private void startThreadAnimation() {
//        shouldAnimate = true;
//        animHourGlassThread = new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                while (shouldAnimate) {
//                    if (!animatable.isRunning()) {
//                        startAnimation();
//                    }
//                }
//            }
//        };
//        animHourGlassThread.start();
//    }
//
//    private void startAnimation() {
//        runOnUiThread(() -> animatable.start());
//    }

    private void init() {
        if (question != null) {
            initServerTime();
        } else {
            updateKeepMePostedUi();
        }
    }

    private void initServerTime() {
        initCountDown(serverTime);
    }

    private void initCountDown(long serverTime) {
        long time = question.getTimeStamp();
        long countDownTime = time - serverTime;
        if (isValidTime(countDownTime)) {
            updateCountDownUi(countDownTime);
        } else {
            updateKeepMePostedUi();
        }
    }

    private boolean isValidTime(long countDownTime) {
        return countDownTime > 0 && countDownTime < MAX_HOURS_IN_MILLISECONDS;
    }

    private void updateCountDownUi(long countDownTime) {
        updatePrize();
        startCountDown(countDownTime);
    }

    private void startQuestion() {
        timerComplete = true;
        Intent i = new Intent(this, QuestionActivity.class);
        i.putExtra("question", question);
        startActivity(i);

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void startCountDown(long countDownTime) {
        clockCountDownView.setListener(this::startQuestion);
        clockCountDownView.startCount(countDownTime);
    }

    private void updatePrize() {
        int prize = getPrize();
        String formatPrize = formatKinAmount(prize, true);
        this.prize.setText(formatPrize);
    }

    private int getPrize() {
        return question.getPrize();
    }

    private String formatKinAmount(int totalKins, boolean useK) {
        String prizeStr = "";
        if (useK && totalKins >= 10000) {
            int k = totalKins / 1000;
            prizeStr = k + "K";
        } else {
            prizeStr = kinFormat.format(totalKins);
        }
        return prizeStr + " KIN";
    }

    private void updateKeepMePostedUi() {
        nextQuestionTitle.setText(getResources().getString(R.string.keep_me_posted));
        clockCountDownView.setVisibility(View.GONE);
        prizeTelegram.setVisibility(View.GONE);
        setJoinTelegramText();
    }

    public void openTelegramGroup(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(TELEGRAM_LINK));
        startActivity(browserIntent);
    }

    private void setJoinTelegramText() {
        joinTelegram.setText("Join our ");
        joinTelegram.append(telegramSpannable);
        joinTelegram.append(" community channel for more details about next questions and prize");
        joinTelegram.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        if(view == logout){
            firebaseAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

}
