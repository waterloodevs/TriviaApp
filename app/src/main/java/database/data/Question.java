package database.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

//import com.google.firebase.database.IgnoreExtraProperties;

//@IgnoreExtraProperties
public class Question implements Parcelable {

    public String video_url;
    public String question;
    public Boolean FinalQuestion;
    public long time_stamp;
    public int correct_answer;
    public List<Long> answers_count;
    public List<String> answers;
    public int prize;
    public Question NextQuestion;

    public Question(String question1, int correct_answer1, long time_stamp1, int prize1, List<String> answers1, Boolean FinalQuestion1, Question NextQuestion1) {
        question = question1;
        time_stamp = time_stamp1;
        prize = prize1;
        answers = answers1;
        FinalQuestion = FinalQuestion1;
        NextQuestion = NextQuestion1;
        correct_answer = correct_answer1;
    }

    public Question(String question1, int correct_answer1, long time_stamp1, int prize1, List<String> answers1, Boolean FinalQuestion1) {
        question = question1;
        time_stamp = time_stamp1;
        prize = prize1;
        answers = answers1;
        FinalQuestion = FinalQuestion1;
        correct_answer = correct_answer1;
    }

    public int getPrize() {
        return prize;
    }

    public String getVideoUrl() {
        return video_url;
    }

    public String getQuestion() {
        return question;
    }

    public long getTimeStamp() {
        return time_stamp;
    }

    public List<Long> getAnswersCount() {
        return answers_count;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public int getCorrectAnswer() {
        return correct_answer;
    }

    @Override
    public String toString() {
        return "Question: " + question + " TimeStamp: " + time_stamp + " Answers: " + answers.toString();
    }

    protected Question(Parcel in) {
        video_url = in.readString();
        question = in.readString();
        byte FinalQuestionVal = in.readByte();
        FinalQuestion = FinalQuestionVal == 0x02 ? null : FinalQuestionVal != 0x00;
        time_stamp = in.readLong();
        correct_answer = in.readInt();
        if (in.readByte() == 0x01) {
            answers_count = new ArrayList<Long>();
            in.readList(answers_count, Long.class.getClassLoader());
        } else {
            answers_count = null;
        }
        if (in.readByte() == 0x01) {
            answers = new ArrayList<String>();
            in.readList(answers, String.class.getClassLoader());
        } else {
            answers = null;
        }
        prize = in.readInt();
        NextQuestion = (Question) in.readValue(Question.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(video_url);
        dest.writeString(question);
        if (FinalQuestion == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (FinalQuestion ? 0x01 : 0x00));
        }
        dest.writeLong(time_stamp);
        dest.writeInt(correct_answer);
        if (answers_count == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(answers_count);
        }
        if (answers == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(answers);
        }
        dest.writeInt(prize);
        dest.writeValue(NextQuestion);
    }

    @SuppressWarnings("unused")
    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };
}