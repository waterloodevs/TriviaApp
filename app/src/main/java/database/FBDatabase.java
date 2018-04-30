package database;

import database.data.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FBDatabase {
    public static final int MAX_QUESTIONS_IN_RAW = 3;
    private static final FBDatabase ourInstance = new FBDatabase();

    public int nextQuestionIndex, nextQuestionIndexOrigin;
    public Question nextQuestion;

    public Question question3 = new Question("3. What is your name?", 0, 5000,
            100000, Arrays.asList("Answers 1.1", "Answer 1.2", "Answer 1.3", "Answer 1.4"), true);
    public Question question2 = new Question("2. What is your name?", 0, 5000,
            100000, Arrays.asList("Answers 2.1", "Answer 2.2", "Answer 2.3", "Answer 2.4"), false, question3);
    public Question question1 = new Question("1. What is your name?", 0, 5000,
            100000, Arrays.asList("Answers 3.1", "Answer 3.2", "Answer 3.3", "Answer 3.4"), false, question2);


    public List<Question> questions = new ArrayList<Question>();

    public static FBDatabase getInstance() {
        return ourInstance;
    }

    private FBDatabase() {
        questions.add(0,question1);
        questions.add(1,question2);
        questions.add(2,question3);
    }
}
