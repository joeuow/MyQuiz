package com.example.myquiz;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {
    /* user database variables */
    User_DB_Helper user_db_helper;
    //List<UserAccount> user_list;
    int user_id, org_right_q, org_taken_q;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        user_db_helper = new User_DB_Helper(this);
        //user_list = user_db_helper.GetAllUsers();

        SetSignup();
        SetLogin();
    }

    private void SetLogin() {
        Button btu_login = (Button) findViewById(R.id.btu_login);
        btu_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                GeneralDialog dialog_login;
                boolean login = false;
                final UserAccount cur_user;

                EditText login_user = (EditText) findViewById(R.id.editText_login_user);
                EditText login_pwd = (EditText) findViewById(R.id.editText_login_pwd);
                final String user = login_user.getText().toString();
                String pwd = login_pwd.getText().toString();

                if (user.isEmpty())
                    Toast.makeText(MainActivity.this, "Input \"User\" Please", Toast.LENGTH_SHORT).show();
                else {
                    if (pwd.isEmpty())
                        Toast.makeText(MainActivity.this, "Input \"Password\" Please", Toast.LENGTH_SHORT).show();
                    else {
                        cur_user = user_db_helper.QueryUser(0, user);
                        if(cur_user != null) {
                            if ((cur_user.GetUserName().compareTo(user) == 0) && (cur_user.GetPwd().compareTo(pwd) == 0)) {
                                login = true;
                                user_id = cur_user.GetID();
                                org_right_q = cur_user.GetCorrectQuiz();
                                org_taken_q = cur_user.GetTakenQuiz();
                            }
                        }
                    }
                }
                dialog_login = new GeneralDialog(MainActivity.this, login, "Authentication...", "Login Success", "Login Fail");
                dialog_login.DisplayProgress();
                // redirect to Activities according
                if (login) {
                    android.os.Handler handler = new android.os.Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (user.compareTo("admin") == 0) { // QM user
                                GotoAdmin();
                            } else { // QT user
                                GotoQuiz();
                            }
                        }
                    }, 3000);
                }

            }
        });
    }

    private void GotoQuiz() {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("UserID", user_id);

        startActivity(intent);
    }

    private void GotoAdmin() {
        final Dialog dialog_admin = new Dialog(MainActivity.this);
        dialog_admin.setContentView(R.layout.admin);
        dialog_admin.setTitle("Administrator");
        dialog_admin.show();

        Button btu_admin_user = (Button) dialog_admin.findViewById(R.id.btu_admin_user);
        Button btu_admin_quiz = (Button) dialog_admin.findViewById(R.id.btu_admin_quiz);

        // set Button of user manage
        btu_admin_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserManageActiviy.class);
                dialog_admin.dismiss();
                startActivity(intent);
            }
        });

        // set Button of quiz manage
        btu_admin_quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ManageQuizActivity.class);
                dialog_admin.dismiss();
                startActivity(intent);
            }
        });
    }

    private void SetSignup() {
        Button btu_signup = (Button) findViewById(R.id.btu_signup);

        btu_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateSignupDialog();
            }
        });
    }

    private void CreateSignupDialog() {
        Button btu_create;
        final EditText editText_user, editText_pwd, editText_cfm_pwd;
        final Dialog dialog_sigup = new Dialog(MainActivity.this);

        dialog_sigup.setContentView(R.layout.signup);
        dialog_sigup.setTitle("Sign UP");
        dialog_sigup.show();

        editText_user = (EditText) dialog_sigup.findViewById(R.id.editText_signup_user);
        editText_pwd = (EditText) dialog_sigup.findViewById(R.id.editText_signup_pwd);
        editText_cfm_pwd = (EditText) dialog_sigup.findViewById(R.id.editText_signup_confirm_pwd);


        btu_create = (Button) dialog_sigup.findViewById(R.id.btu_signup_create);

        btu_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String signup_user, signup_pwd, signup_cfm_pwd;
                signup_user = editText_user.getText().toString();
                signup_pwd = editText_pwd.getText().toString();
                signup_cfm_pwd = editText_cfm_pwd.getText().toString();
                GeneralDialog progress_dialog;

                if(signup_user.isEmpty())
                    Toast.makeText(MainActivity.this, "Input \"User name\" Please", Toast.LENGTH_SHORT).show();
                else {
                    if(signup_pwd.isEmpty() || signup_cfm_pwd.isEmpty())
                        Toast.makeText(MainActivity.this, "Input \"Password\" Please", Toast.LENGTH_SHORT).show();
                    else {
                        if(user_db_helper.QueryUser(0, signup_user) != null) {
                            progress_dialog = new GeneralDialog(MainActivity.this, false, "Creating...", null, "Creat Fail. User Exists Already");
                            progress_dialog.DisplayProgress();
                        } else {
                            if(signup_cfm_pwd.compareTo(signup_pwd) == 0) {
                                progress_dialog = new GeneralDialog(MainActivity.this, true, "Creating...", "Create User Account Success", null);
                                progress_dialog.DisplayProgress();
                                user_db_helper.AddUser(signup_user, signup_pwd);
                                dialog_sigup.dismiss();
                            } else {
                                progress_dialog = new GeneralDialog(MainActivity.this, false, "Creating...", null, "Create Fail. Password Does NOT Match.");
                                progress_dialog.DisplayProgress();
                            }
                        }
                    }
                }
            }
        });
    }
/*

*/
}
