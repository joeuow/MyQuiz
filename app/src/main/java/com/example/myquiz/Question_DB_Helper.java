package com.example.myquiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.myquiz.Question;

import java.util.ArrayList;
import java.util.List;


public class Question_DB_Helper extends SQLiteOpenHelper {
    private static final int db_version = 1;

    //DB name
    private static final String db_name = "quiz";

    // table name
    private static final String db_table = "questions";

    // column name
    private static final String key_id = "q_id";
    private static final String key_Q = "question";
    private static final String KEY_A_TIME = "answering_time";
    private static final String key_A_num = "answer_num";
    private static final String key_op1 = "option1";
    private static final String key_op2 = "option2";
    private static final String key_op3 = "option3";
    private static final String key_op4 = "option4";
    private static final String key_op5 = "option5";


    public Question_DB_Helper(Context context) {
        super(context, db_name, null, db_version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        Log.d("database", "onCreate");

        String sql = "create table if not exists questions "
                + "(q_id integer primary key autoincrement, "
                + "question text, "
                + "answering_time integer, "
                + "answer_num integer, "
                + "option1 text, "
                + "option2 text, "
                + "option3 text, "
                + "option4 text, "
                + "option5 text);";
        db.execSQL(sql);

        //AddInitQuestions();
        values.put(key_Q, "What does Confederation mean?");
        values.put(KEY_A_TIME, 20);
        values.put(key_A_num, 2);
        values.put(key_op1,"The joining of provinces to become a new country.");
        values.put(key_op2,"The United States Confederate army came to settle in Canada.");
        values.put(key_op3,"The combination of neighborhood to build a larger community.\"");
        values.put(key_op4,"The merger of colonies to form a province.");
        db.insert(db_table,null,values);

        values.put(key_Q, "Which province is the biggest producer of metals in Canada?");
        values.put(KEY_A_TIME, 30);
        values.put(key_A_num, 3);
        values.put(key_op1,"Ontario.");
        values.put(key_op2,"Northwest Territories.");
        values.put(key_op3,"Yukon.");
        values.put(key_op4,"Alberta.");
        db.insert(db_table,null,values);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        Log.d("database", "onUpgrade");
        //db.execSQL("DROP TABLE IF EXISTS " + db_table);
        onCreate(db);
    }

    // add a question to the database
    public void AddQuestion(Question new_q) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(key_Q, new_q.GetQuestion());
        values.put(KEY_A_TIME, new_q.GetTime());
        values.put(key_A_num, new_q.GetAns());
        values.put(key_op1,new_q.GetOption(1));
        values.put(key_op2,new_q.GetOption(2));
        values.put(key_op3,new_q.GetOption(3));
        values.put(key_op4,new_q.GetOption(4));
        values.put(key_op5,new_q.GetOption(5));

        db.insert(db_table,null,values);
        db.close();
    }

    public Question queryQuestion(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Question question;

        if(id == 0)
            return null;
        else
            cursor = db.query(db_table, null, "q_id = ?", new String[] {Integer.toString(id)}, null, null, null);

        if (cursor.getCount() == 0)
            return null;

        question = new Question();

        cursor.moveToFirst();
        question.SetID(cursor.getInt(0));
        question.SetQuestion(cursor.getString(1));
        question.SetTime(cursor.getInt(2));
        question.SetAns(cursor.getInt(3));
        for(int i=0; i<5; i++) {
            question.SetOption(cursor.getString(4+i), i);
        }

        db.close();

        return question;
    }

    // read questions from the database
    public List<Question> GetAllQuestions() {
        Cursor cursor;
        List<Question> q_list = new ArrayList<Question>();
        String sql_select_all = "select * from " + db_table;
        SQLiteDatabase db = this.getReadableDatabase();

        cursor = db.rawQuery(sql_select_all, null);
        if(cursor.moveToFirst()) {
            do {
                Question question = new Question();
                question.SetID(cursor.getInt(0));
                question.SetQuestion(cursor.getString(1));
                question.SetTime(cursor.getInt(2));
                question.SetAns(cursor.getInt(3));
                for(int i=0; i<5; i++) {
                    question.SetOption(cursor.getString(4+i), i);
                }
                q_list.add(question);
            }while (cursor.moveToNext());
        }

        db.close();
        return q_list;
    }

    public int QuizTotalNum() {
        int count;
        SQLiteDatabase db = this.getReadableDatabase();
        count = (int) DatabaseUtils.queryNumEntries(db, db_table);
        db.close();

        return count;
    }

    public void DelQuiz(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(db_table, "q_id = ? ", new String[] {Integer.toString(id)} );
        db.close();
    }

    public void UpdateQuestion(int id, String question, int time, int ans, String op1, String op2, String op3, String op4, String op5) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if(!question.isEmpty())
            values.put(key_Q, question);
        if(ans > 0)
            values.put(key_A_num, ans);
        if(time > 0) {
            values.put(KEY_A_TIME, time);
        }
        if(!op1.isEmpty()) values.put(key_op1, op1);
        if(!op2.isEmpty()) values.put(key_op2, op2);
        if(!op3.isEmpty()) values.put(key_op3, op3);
        if(!op4.isEmpty()) values.put(key_op4, op4);
        if(!op5.isEmpty()) values.put(key_op5, op5);

        db.update(db_table, values, "q_id = ? ", new String[] { Integer.toString(id) } );
        db.close();
    }
}