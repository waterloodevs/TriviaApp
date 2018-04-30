package waterloodevs.triviaapp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import database.data.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionActivity extends AppCompatActivity {

    private static final int DURATION_SECONDS = 10;
    private static final float UNSELECTED_ALPHA = 0.7f;
    private static final float SELECTED_ALPHA = 1f;
    private static final long WAIT_TIME_BETWEEN_QUESTIONS = 10000;

    private Question question;
    private View root;
    private MediaPlayer mediaPlayer;

    private List<AnswerView> answerViewList = new ArrayList<>(4);
    private boolean isWinner;

    private View.OnClickListener answerClickListener = view -> {
        AnswerView answerView = (AnswerView) view;
        //answerView.setActivated(true);
        disableClicks();
        int answerIndex = Integer.parseInt((String) view.getTag());
        updateSelection(answerIndex);
        isWinner = (answerIndex == question.getCorrectAnswer());
        //sendAnswer(answerIndex);
        //FBAnalytics.getInstance().answeredTapped(view.getContext(), isWinner, answerIndex);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_activity);
        root = findViewById(R.id.root);
        question = getIntent().getExtras().getParcelable("question");
        initViews();
        startCountDown();
    }

    private void onCountDownComplete() {
        disableClicks();
        deselectAnswers();
        updateSelection(question.getCorrectAnswer());
//        updateAnswersColors();
        updateLayout();
//        animateVoting();
        boolean hasNextQuestion = upateNextQuestion(isWinner);
        waitForNextScreen(hasNextQuestion);
    }

    public boolean upateNextQuestion(boolean isWinner) {
        boolean hasNextQuestion = false;
        if (isWinner && !question.FinalQuestion) {
            hasNextQuestion = true;
        }
        return hasNextQuestion;
    }

    public void waitForNextScreen(boolean hasNextQuestion) {
        if (isWinner && hasNextQuestion) {
            findViewById(R.id.next_question_countdown_layout).setVisibility(View.VISIBLE);
        }
        ClockCountDownView countDownView = findViewById(R.id.next_question_count_down);
        countDownView.startCount(WAIT_TIME_BETWEEN_QUESTIONS);
        countDownView.setListener(() -> {
            Intent i;
            if (hasNextQuestion) {
                i = new Intent(this, QuestionActivity.class);
                i.putExtra("question", question.NextQuestion);
            }else{
                i = new Intent(this, CountDownActivity.class);
            }
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });
    }

    private void startCountDown() {
        ClockCountDownView countDownView = findViewById(R.id.clock_count_down);
        countDownView.setListener(this::onCountDownComplete);
        countDownView.startCount(DURATION_SECONDS * 1000);
    }

    private void disableClicks() {
        for (AnswerView answerView : answerViewList) {
            answerView.setOnClickListener(null);
        }
    }

    private void deselectAnswers() {
        for (AnswerView answerView : answerViewList) {
            answerView.setSelected(false);
            //answerView.setActivated(false);
        }
    }

    private void updateAnswersColors() {
        for (int i = 0; i < answerViewList.size(); i++) {
            if (i == question.getCorrectAnswer()) {
                answerViewList.get(i).markCorrect();
            } else {
                answerViewList.get(i).markRatio();
            }
        }
    }

    private void updateSelection(int index) {
        for (int i = 0; i < answerViewList.size(); i++) {
            if (i != index) {
                answerViewList.get(i).setAlpha(UNSELECTED_ALPHA);
            } else {
                answerViewList.get(i).setAlpha(SELECTED_ALPHA);
            }
        }
    }

    private void playAudio(boolean isWin) {
        mediaPlayer = MediaPlayer.create(getApplicationContext(), isWin ? R.raw.win : R.raw.fail);
        mediaPlayer.setOnPreparedListener(MediaPlayer::start);
        mediaPlayer.setOnCompletionListener(mp -> releaseMediaPlayer());
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    private void updateWinnerLayout() {
        findViewById(R.id.question_layout).setVisibility(View.GONE);
        View bg = findViewById(R.id.winner_bg);
        bg.setAlpha(0);
        bg.setY(bg.getY() - 200);
        findViewById(R.id.winner_layout).setVisibility(View.VISIBLE);
        playAudio(true);
        bg.animate().alpha(1).translationYBy(200).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(4000).start();
    }

    private void updateLoserLayout() {
        findViewById(R.id.question_layout).setVisibility(View.GONE);
        View view = findViewById(R.id.try_next_time_text);
        view.setAlpha(0);
        findViewById(R.id.loser_layout).setVisibility(View.VISIBLE);
        playAudio(false);
        view.animate().alpha(1).setDuration(1000).setStartDelay(1000).start();
    }

    private void updateLayout() {
        if (isWinner) {
            updateWinnerLayout();
        } else {
            updateLoserLayout();
        }
    }

    private void initViews() {
        answerViewList.add((AnswerView) findViewById(R.id.answer0));
        answerViewList.add((AnswerView) findViewById(R.id.answer1));
        answerViewList.add((AnswerView) findViewById(R.id.answer2));
        answerViewList.add((AnswerView) findViewById(R.id.answer3));
        ((TextView) findViewById(R.id.question)).setText(question.getQuestion());

        List<String> answersStrArray = question.getAnswers();
        for (int i = 0; i < answersStrArray.size(); i++) {
            AnswerView answerView = answerViewList.get(i);
            answerView.setAnswer(answersStrArray.get(i));
            answerView.setOnClickListener(answerClickListener);
        }
    }
}