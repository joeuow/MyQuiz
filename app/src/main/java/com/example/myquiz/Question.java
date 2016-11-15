package com.example.myquiz;

/**
 * Created by joewei on 2016-10-14.
 */

public class Question {
    private int q_id;
    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String option5;
    private int ans_num; // option #1 ~ option #5
    private int time_sec;   // between 10 to 30

    int GetID() {
        return q_id;
    }

    void SetID(int id) {
        q_id = id;
    }

    void SetQuestion(String question) {
        this.question = question;
    }

    String GetQuestion() {
        return question;
    }

    void SetAns(int ans_num) {
        this.ans_num = ans_num;
    }

    int GetAns() {
        return ans_num;
    }

    void SetOption(String option, int option_num) {
        switch (option_num) {
            case 0: option1 = option;
                break;
            case 1: option2 = option;
                break;
            case 2: option3 = option;
                break;
            case 3: option4 = option;
                break;
            case 4: option5 = option;
                break;
        }
    }

    String GetOption(int option_num) {
        String op_string = "";
        switch (option_num) {
            case 0: op_string = option1;
                break;
            case 1: op_string = option2;
                break;
            case 2: op_string = option3;
                break;
            case 3: op_string = option4;
                break;
            case 4: op_string = option5;
                break;
        }

        return op_string;
    }

    int GetTime() {
        return time_sec;
    }

    void SetTime(int sec) {
        time_sec = sec;
    }
}
