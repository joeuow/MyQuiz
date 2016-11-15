package com.example.myquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class ManageQuizActivity extends AppCompatActivity {
    Question_DB_Helper question_db_helper = new Question_DB_Helper(this);
    List<Question> quiz_list;
    ;

    @Override
    public void onRestart(){
        super.onResume();
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_manage);
        Log.d("database", "ManageQuizActivity onCreate");

        SetUserTable();
    }

    private void SetUserTable() {
        Question cur_quiz;
        Button btu_add = new Button(this);
        int totalNum = question_db_helper.QuizTotalNum();

        final LinearLayout parent_layout = (LinearLayout) findViewById(R.id.ll_quiz);

        quiz_list = question_db_helper.GetAllQuestions();
        for (int index = 0; index < totalNum; index++) {// admin account should be escaped
            final int q_id;
            final int id_ll;
            final LinearLayout new_row;
            Button btu_del, btu_edit;
            final TextView new_question;

            cur_quiz = quiz_list.get(index);

            new_row = new LinearLayout(this);
            id_ll = View.generateViewId();
            new_row.setId(id_ll);
            LinearLayout.LayoutParams params_ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params_ll.setMargins(15, 0, 0, 5);
            new_row.setLayoutParams(params_ll);
            if(index % 2 == 1) new_row.setBackgroundColor(getResources().getColor(R.color.colorItem1));
            else new_row.setBackgroundColor(getResources().getColor(R.color.colorItem2));
            new_row.setOrientation(LinearLayout.HORIZONTAL);

            parent_layout.addView(new_row);

            new_question = new TextView(this);
            LinearLayout.LayoutParams params_q = new LinearLayout.LayoutParams(380, LinearLayout.LayoutParams.WRAP_CONTENT);
            params_q.gravity = Gravity.FILL_VERTICAL;
            //new_question.setLayoutParams(new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT));
            new_question.setLayoutParams(params_q);
            new_question.setText(cur_quiz.GetQuestion());
            new_question.setTextSize(10f);
            new_question.setSingleLine(false);
            new_row.addView(new_question);

            btu_edit = new Button(this);
            btu_edit.setLayoutParams(new LinearLayout.LayoutParams(22, 22));
            btu_edit.setBackgroundResource(R.drawable.btu_edit);
            new_row.addView(btu_edit);

            btu_del = new Button(this);
            btu_del.setLayoutParams(new LinearLayout.LayoutParams(22, 22));
            btu_del.setBackgroundResource(R.drawable.btu_del);
            new_row.addView(btu_del);

            q_id = cur_quiz.GetID();

            btu_del.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Question tmp_quiz;

                    Log.d("database", "del " + q_id);
                    question_db_helper.DelQuiz(q_id);
                    parent_layout.removeView(new_row);
                }
            });

            btu_edit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ManageQuizActivity.this, QuestionActivity.class);
                    intent.putExtra("question_id", q_id);
                    startActivity(intent);
                }
            });

            //new_row.
        }

        LinearLayout bot_line = new LinearLayout(this);
        LinearLayout.LayoutParams params_bot = new LinearLayout.LayoutParams(40, 40);
        params_bot.gravity = Gravity.RIGHT;
        params_bot.setMargins(0, 0, 0, 30);
        bot_line.setLayoutParams(params_bot);
        parent_layout.addView(bot_line);
        // button for adding
        btu_add = (Button)findViewById(R.id.btu_add_q);
    /*    LinearLayout.LayoutParams params_btu_add = new LinearLayout.LayoutParams(40, 40);
        params_btu_add.gravity = Gravity.RIGHT;
        params_btu_add.setMargins(0, 0, 0, 30);
        btu_add.setLayoutParams(params_btu_add);
        btu_add.setBackgroundResource(R.drawable.btu_add);
        parent_layout.addView(btu_add);
*/
        btu_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManageQuizActivity.this, QuestionActivity.class);
                startActivity(intent);
            }
        });
    }
}
