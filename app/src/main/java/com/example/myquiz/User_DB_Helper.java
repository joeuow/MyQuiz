package com.example.myquiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class User_DB_Helper extends SQLiteOpenHelper {
    private static final String db_name = "account.db";
    private static final String db_table = "user";
    private static final String column_id = "id";
    private static final String column_user_name = "user_name";
    private static final String column_password = "password";
    private static final String column_correct_quiz = "correct_quiz";
    private static final String column_quiz_taken = "quiz_taken";
    private static final String COLUMN_QUIZ_ROUNDS = "rounds_taken";

    SQLiteDatabase db;
    public User_DB_Helper(Context context) {
        super(context, db_name, null, 1006);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("database", "onCreate");
        String sql = "create table if not exists user "
                + "(id integer primary key autoincrement, "
                + "user_name text, "
                + "password text, "
                + "correct_quiz integer, "
                + "quiz_taken integer,"
                + "rounds_taken integer);";
        db.execSQL(sql);

        /* init admin value */
        ContentValues values = new ContentValues();
        values.put(column_user_name, "admin");
        values.put(column_password, "admin");
        values.put(column_correct_quiz, 0);
        values.put(column_quiz_taken, 0);
        values.put(COLUMN_QUIZ_ROUNDS, 0);
        db.insert(db_table, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //onCreate(db);
    }

    public void AddUser(String user, String pwd) {
        Log.d("database", "AddUser");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(column_user_name, user);
        values.put(column_password, pwd);
        values.put(column_correct_quiz, 0);
        values.put(column_quiz_taken, 0);
        values.put(COLUMN_QUIZ_ROUNDS, 0);
        db.insert(db_table, null, values);
        db.close();
    }

    public UserAccount QueryUser (int id, String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        UserAccount user;

        if(id > 0)
            cursor = db.query(db_table, null, "id = ?", new String[] {Integer.toString(id)}, null, null, null);
        else {
            if(!name.isEmpty())
                cursor = db.query(db_table, null, "user_name = ?", new String[] {name}, null, null, null);
        }

        if (cursor.getCount() == 0)
            return null;

        user = new UserAccount();

        cursor.moveToFirst();
        user.SetID(cursor.getInt(0));
        user.SetUserName(cursor.getString(1));
        user.SetPwd(cursor.getString(2));
        user.SetCorrectQuiz(cursor.getInt(3));
        user.SetTakenQuiz(cursor.getInt(4));
        user.SetRounds(cursor.getInt(5));
        cursor.close();
        db.close();

        return user;
    }

    public void UpdateUser(int id, int correct_quiz, int quiz_taken, int rounds_taken) {
        UserAccount cur_user = QueryUser(id, null);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(column_correct_quiz, correct_quiz);
        values.put(column_quiz_taken, quiz_taken);
        values.put(COLUMN_QUIZ_ROUNDS, rounds_taken);

        // reset
        if(rounds_taken == 0) {
            for (int i = 0; i < cur_user.GetRounds(); i++) {
                values.put("Round" + i, 0);
            }
        }

        db.update(db_table, values, "id = ? ", new String[] { Integer.toString(id) } );
        db.close();
    }

    public void DelUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(db_table, "id = ? ", new String[] {Integer.toString(id)} );
        db.close();
    }

    public List<UserAccount> GetAllUsers() {
        Cursor cursor;
        SQLiteDatabase db = this.getReadableDatabase();
        List<UserAccount> q_list = new ArrayList<UserAccount>();
        String sql_cmd = "select * from " + db_table;

        cursor = db.rawQuery(sql_cmd, null);
        if(cursor.moveToFirst()) {
            do {
                UserAccount user = new UserAccount();
                user.SetID(cursor.getInt(0));
                user.SetUserName(cursor.getString(1));
                user.SetPwd(cursor.getString(2));
                user.SetCorrectQuiz(cursor.getInt(3));
                user.SetTakenQuiz(cursor.getInt(4));
                user.SetRounds(cursor.getInt(5));
                for(int i = 0; i < cursor.getInt(5); i++) {
                    user.SetRoundPerf(i, cursor.getInt(6+i));
                }
                q_list.add(user);
            }while (cursor.moveToNext());
        }

        db.close();
        return q_list;
    }

    public int UserTotalEntry() {
        int count;
        SQLiteDatabase db = this.getReadableDatabase();
        count = (int) DatabaseUtils.queryNumEntries(db, db_table);
        return count;
    }

    public void UserUpdateRoundRes (int id, int round_num, int round_res) {
        int std_colns = 6;
        int max_colns = 0;
        Log.d("user_db_helper", "user id "+id+": round "+round_num + " = " + round_res);
        SQLiteDatabase db = this.getWritableDatabase();
        String sql_cmd = "select * from " + db_table;
        Cursor cursor = db.rawQuery(sql_cmd, null);

        if(cursor.moveToFirst()) {
            do {
                if(max_colns < cursor.getColumnCount())
                    max_colns = cursor.getColumnCount();
            }while (cursor.moveToNext());
        }

        Log.d("user_db_helper", "max_colns = " + max_colns);
        if(max_colns - std_colns <= round_num) { // check if the round coln is existed
            db.setVersion(round_num + 1);
            String sql_add_col = "ALTER TABLE user ADD COLUMN Round" + round_num + " INTEGER;";
            db.execSQL(sql_add_col);
        }
        String sql_update_round_res = "update user set round" + round_num + " = " + round_res + " where id = " + id;
        db.execSQL(sql_update_round_res);


        db.close();
    }
}