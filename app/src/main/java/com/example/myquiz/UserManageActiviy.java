package com.example.myquiz;

import android.database.Cursor;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class UserManageActiviy extends AppCompatActivity {
    User_DB_Helper user_db_helper = new User_DB_Helper(this);
    List<UserAccount> user_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_manage);

        SetUserTable();
    }

    private void SetUserTable() {
        TableLayout table_layout = (TableLayout) findViewById(R.id.tableLayout_user_account);
        int totalUser = user_db_helper.UserTotalEntry();
        user_list = user_db_helper.GetAllUsers();

        Log.d("database", "total user = " + totalUser);

        for (int index = 1; index < totalUser; index++) {// admin account should be escaped
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));
            if(index % 2 == 1) row.setBackgroundColor(getResources().getColor(R.color.colorItem1));
            else row.setBackgroundColor(getResources().getColor(R.color.colorItem2));

            SetRow(row, index);
            table_layout.addView(row);
        }
    }

    private void SetRow(TableRow row, int user_index) {
        int columns;
        int standard_col = 4; //id, username, records, rounds,
        int round_index = 0;
        UserAccount cur_user = user_list.get(user_index);

        TextView tv_reset = SetRestTv(user_index);
        row.addView(tv_reset);

        columns = standard_col + cur_user.GetRounds();
        Log.d("database", "SetRow: total columns = " + columns);

        for (int col = 0; col < columns; col++) {
            TextView tv = new TextView(this);
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(10);
            tv.setPadding(5, 5, 5, 5);

            switch (col) {
                case 0: tv.setText(Integer.toString(cur_user.GetID()-1));
                    break;
                case 1: tv.setText(cur_user.GetUserName());
                    break;
                case 2:
                    tv.setText(cur_user.GetCorrectQuiz() + "/" + cur_user.GetTakenQuiz());
                    break;
                case 3:
                    tv.setText(Integer.toString(cur_user.GetRounds()));
                    break;
                default:
                       if(cur_user.GetRounds() > 0) {
                           round_index = col-standard_col;
                           //    for(int i = 0; i < cur_user.GetRounds(); i++)
                                //tv.setText("#" + i + " " + cur_user.GetRoundPerf(i));
                           tv.setText("R.#" + (round_index+1) + " - " + cur_user.GetRoundPerf(round_index));
                        }

                        break;

            }

            row.addView(tv);
        }
    }

    private TextView SetRestTv(final int user_index) {
        TextView tv_reset = new TextView(this);
        tv_reset.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT));
        tv_reset.setGravity(Gravity.CENTER);
        tv_reset.setTextSize(10);
        tv_reset.setPadding(5, 5, 5, 5);
        tv_reset.setText("Reset");
        tv_reset.setClickable(true);
        tv_reset.setBackgroundResource(R.drawable.btu_normal);
        Log.d("database", "SetRestBtu: row_index = " + user_index);

        tv_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserAccount cur_user = user_list.get(user_index);
                User_DB_Helper db = new User_DB_Helper(UserManageActiviy.this);
                Log.d("database", "reset " + user_index);
                // update user
                db.UpdateUser(cur_user.GetID(), 0, 0, 0);
                // update user list as well
                user_list.clear();
                user_list = user_db_helper.GetAllUsers();
                // reload entry
                TableLayout table_parent = (TableLayout) findViewById(R.id.tableLayout_user_account);
                TableRow row = (TableRow) table_parent.getChildAt(user_index-1); // row_index = user_index - 1, since row index starts from 0 but user index starts from 1
                row.removeAllViews();
                SetRow(row, user_index);
            }
        });

        return tv_reset;
    }
}
