package com.example.myquiz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class QuestionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_question);

        Intent intent = getIntent();
        int id = intent.getIntExtra("question_id", 0);

        EditQuestion(id);
    }

    private void EditQuestion(int id) {
        final int cur_q_id = id;
        int editText_op_id[] = {R.id.editText_op1, R.id.editText_op2, R.id.editText_op3, R.id.editText_op4, R.id.editText_op5 };
        final Question_DB_Helper db_helper = new Question_DB_Helper(this);
        Question question;
        final EditText editText_q = (EditText) findViewById(R.id.editText_q);
        final EditText editText_op[] = new EditText[5];
        final EditText editText_time = (EditText) findViewById(R.id.editText_q_time);
        final EditText editText_ans = (EditText) findViewById(R.id.editText_q_ans);
        Button btu_save = (Button) findViewById(R.id.btu_edit_question);

        for(int i=0; i<5; i++)
            editText_op[i] = (EditText) findViewById(editText_op_id[i]);

        if(id > 0) {
            // Question
            question = db_helper.queryQuestion(id);
            editText_q.setText(question.GetQuestion());

            // Answer
            editText_ans.setText(Integer.toString(question.GetAns()));
            editText_time.setText(Integer.toString(question.GetTime()));

            for(int i=0; i<5; i++) {
                if(question.GetOption(i) == null)
                    editText_op[i].setText("");
                else {
                    editText_op[i].setText(question.GetOption(i));
                }
            }
        }

        btu_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast;
                int valid_options = 0;


                GeneralDialog dialog = new GeneralDialog(QuestionActivity.this, true, "Saving...", "Complete!", null);
                Question new_add_q = new Question();
                String q_string = editText_q.getText().toString();
                int q_ans =  Integer.parseInt(editText_ans.getText().toString());
                int q_time = Integer.parseInt(editText_time.getText().toString());
                String[] op_string = new String[5];

                for(int i=0; i<op_string.length; i++) {
                    op_string[i] = editText_op[i].getText().toString();
                    if(!op_string[i].isEmpty()) valid_options++;
                }

                if(q_string.isEmpty()) {
                    promptWarning("Question cannot be empty!");
                    return;
                }

                if(q_ans > valid_options || q_ans <= 0) {
                    promptWarning("Invalid Answer Number Setting");
                    return;
                }

                if(q_time > 30 || q_time < 10) {
                    promptWarning("Invalid Time Setting");
                    return;
                }

                if(cur_q_id > 0) {
                    db_helper.UpdateQuestion(cur_q_id,
                            q_string,
                            q_time,
                            q_ans,
                            op_string[0],
                            op_string[1],
                            op_string[2],
                            op_string[3],
                            op_string[4]);
                } else {
                    new_add_q.SetQuestion(q_string);
                    new_add_q.SetAns(q_ans);
                    new_add_q.SetTime(q_time);
                    for (int i=0; i<op_string.length; i++) {
                        new_add_q.SetOption(op_string[i], i+1);
                    }
                    db_helper.AddQuestion(new_add_q);
                }

                dialog.DisplayProgress();
            }
        });
    }

    private void promptWarning(String WarningMsg) {
        Toast toast;
        toast = Toast.makeText(QuestionActivity.this, WarningMsg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
        return;
    }
}
