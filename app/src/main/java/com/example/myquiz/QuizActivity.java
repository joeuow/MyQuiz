package com.example.myquiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {
    /* quiz database variables */
    List<Question> questionList;
    Question cur_Q;
    int q_list_index;
    Question_DB_Helper db;

    /* user databse */
    UserAccount user;
    int user_id, cur_right_q, cur_taken_q, total_right_q;
    User_DB_Helper user_db_helper;

    int select_ans = 0;
    boolean answered = false;
    int q_per_round = 5;

    CountDownTimer countDownTimer;
    TextView text_q;
    TextView text_op[] = new TextView[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        user_id = intent.getIntExtra("UserID", 0);
        total_right_q = intent.getIntExtra("TotalRightQ", 0);
        cur_taken_q = intent.getIntExtra("CurTakenQ", 0);
        cur_right_q = intent.getIntExtra("CurRightQ", 0);
        q_list_index = intent.getIntExtra("CurQID", -1);
        setContentView(R.layout.quiz);

        db = new Question_DB_Helper(this);
        questionList = db.GetAllQuestions();

        if(user_id == 0)
            return;

        Generate_q_id();
        InitQuiz();
        SetButtons();
        SetUser(user_id);
        SetCountDowm(cur_Q.GetTime());
    }

    private void Generate_q_id() {  // randomly generate the q_id
       Question_DB_Helper db = new Question_DB_Helper(this);

        if (cur_taken_q > db.QuizTotalNum()) {
            Toast.makeText(QuizActivity.this, "No More Questions Left", Toast.LENGTH_LONG).show();
            finish();
        }
        if(q_list_index == -1) {// init
            Random r = new Random();
            q_list_index = r.nextInt(db.QuizTotalNum());
        } else if(q_list_index >= db.QuizTotalNum())
            q_list_index = 0;

    }

    private void SetCountDowm(int count_down) {
        final TextView textView_timer = (TextView) findViewById(R.id.textview_timer);
        Log.d("database", "count_down is " + count_down);
        countDownTimer = new CountDownTimer((count_down+1)*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                textView_timer.setText("Seconds Remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                textView_timer.setText("Time Over!");
                if(answered == false) {
                    answered = true;
                    Button btu_confirm = (Button) findViewById(R.id.btu_confirm);
                    btu_confirm.setEnabled(false);
                    answerFlash(text_op[cur_Q.GetAns() - 1], R.drawable.option_right_border, 10, 12);

                    cur_taken_q++;
                 //   user_db_helper.UpdateUser(user_id, user.GetCorrectQuiz(), user.GetTakenQuiz()+1, user.GetRounds());
                }
            }
        }.start();
    }

    private void answerFlash(TextView textView, int resource, int duration, int count) {
        textView.setBackgroundResource(resource);
        //textView.setBackgroundColor(getResources().getColor(R.color.colorOptionRight));
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(duration); //You can manage the blinking time with this parameter
        anim.setStartOffset(50);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(count);
        //anim.setBackgroundColor(getResources().getColor(R.color.colorOptionRight));
        textView.startAnimation(anim);
    }

    private void SetUser(int user_id) {
        user_db_helper = new User_DB_Helper(this);
        user = user_db_helper.QueryUser(user_id, null);
        TextView textView_user, textView_stat;
        textView_user = (TextView) findViewById(R.id.user_name);
        textView_stat = (TextView) findViewById(R.id.user_stat);

        textView_user.setText(user.GetUserName());
        textView_stat.setText(cur_right_q+total_right_q + "/" + cur_taken_q);
    }

    private void InitQuiz() {
        select_ans = 0;
        cur_Q = questionList.get(q_list_index);
        SetQuestion();
    }

    private void SetQuestion() {
        final int text_op_id[] = {R.id.op1_ans, R.id.op2_ans, R.id.op3_ans, R.id.op4_ans, R.id.op5_ans};

        text_q = (TextView)findViewById(R.id.question);
        text_q.setText(cur_Q.GetQuestion());

        for(int i=0; i<5; i++) {
            text_op[i] = (TextView) findViewById(text_op_id[i]);
            if(cur_Q.GetOption(i) == null)
                text_op[i].setText("");
            else {
                text_op[i].setText(cur_Q.GetOption(i));
                text_op[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView cur_op = (TextView) v;
                        if(answered == true)    // cannot answer the quesiton twice
                            return;

                        ResetOptionColor();
                        cur_op.setBackgroundResource(R.drawable.option_select_border);
                        switch (cur_op.getId()) {
                            case R.id.op1_ans: select_ans = 1;
                                break;
                            case R.id.op2_ans: select_ans = 2;
                                break;
                            case R.id.op3_ans: select_ans = 3;
                                break;
                            case R.id.op4_ans: select_ans = 4;
                                break;
                            case R.id.op5_ans: select_ans = 5;
                                break;
                        }
                    }
                });
            }
        }
    }
    // once Submit is pressed, cannot be re-submit any more
    private void SetButtons() {
        final Button btu_confirm = (Button) findViewById(R.id.btu_confirm);
        final Button btu_next = (Button) findViewById(R.id.btu_next_q);

        btu_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (select_ans == 0) {
                    Toast.makeText(QuizActivity.this,"Not Answer yet",Toast.LENGTH_LONG).show();
                    return;
                }

                if(answered == false) { // cannot submit the answer twice
                    answered = true;
                    btu_confirm.setEnabled(false);
                    countDownTimer.cancel();
                }

                if(select_ans == cur_Q.GetAns()) {
                    //text_op[cur_Q.GetAns() - 1].setBackgroundResource(R.drawable.option_right_border);
                    answerFlash(text_op[cur_Q.GetAns()-1], R.drawable.option_right_border, 10, 12);
                    cur_right_q++;
                    android.os.Handler handler = new android.os.Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startNextQuestion();
                        }
                    }, 1600);

                 //   user_db_helper.UpdateUser(user_id, user.GetCorrectQuiz()+1, user.GetTakenQuiz()+1, user.GetRounds());
                } else {
                    answerFlash(text_op[cur_Q.GetAns()-1], R.drawable.option_wrong_border, 10, 12);
                 //   text_op[cur_Q.GetAns()-1].setBackgroundResource(R.drawable.option_wrong_border);
                 //   user_db_helper.UpdateUser(user_id, user.GetCorrectQuiz(), user.GetTakenQuiz()+1, user.GetRounds());
                }
                cur_taken_q++;
            }
        });

        btu_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(answered == false) { // the current question needs to be answered first then move to the next
                    Toast.makeText(QuizActivity.this, "Answer Current Question Before Move To the Next", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    startNextQuestion();
                }

            }
        });
    }

    private void startNextQuestion() {
        if(cur_taken_q % q_per_round == 0) {
            user_db_helper.UpdateUser(user_id, user.GetCorrectQuiz()+cur_right_q, user.GetTakenQuiz()+q_per_round, user.GetRounds()+1);
            total_right_q += cur_right_q;
            user_db_helper.UserUpdateRoundRes(user_id, user.GetRounds(), cur_right_q);
            cur_right_q = 0;
            AlertDialog.Builder alert_builder = new AlertDialog.Builder(QuizActivity.this);
            alert_builder.setMessage("Start A New Round? ");
            alert_builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    StartNewQuestion();
                    dialog.cancel();
                }
            });
            alert_builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    finish();
                }
            });
            AlertDialog dialog = alert_builder.create();
            dialog.show();
        } else
            StartNewQuestion();
    }

    private void StartNewQuestion() {
        Intent intent = new Intent(QuizActivity.this, QuizActivity.class);
        intent.putExtra("UserID", user_id);
        intent.putExtra("CurRightQ", cur_right_q);
        intent.putExtra("CurTakenQ", cur_taken_q);
        intent.putExtra("TotalRightQ", total_right_q);
        intent.putExtra("CurQID", q_list_index+1);
        startActivity(intent);
        finish();
    }

    private void ResetOptionColor() {
        for(int i=0; i<5; i++) text_op[i].setBackgroundResource(R.color.colorOptionNormal);
    }
}
