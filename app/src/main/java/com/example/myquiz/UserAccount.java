package com.example.myquiz;

class UserAccount {
    private int id;
    private String user_name;
    private String password;
    private int quiz_correct;
    private int quiz_taken;
    private int quiz_rounds;
    private int login_times;
    private int[] quiz_round_perf = new int[128];

    public void SetID(int id) {
        this.id = id;
    }

    public int GetID() {
        return id;
    }

    public void SetUserName(String user) {
        user_name = user;
    }

    public String GetUserName() {
        return user_name;
    }

    public void SetPwd(String pwd) {
        password = pwd;
    }

    public String GetPwd() {
        return password;
    }

    public void AddTakenQuiz(int total) {
        quiz_taken += total;
    }

    public int GetTakenQuiz() {
        return quiz_taken;
    }

    public void SetTakenQuiz(int num) {
        quiz_taken = num;
    }

    public void AddCorrectQuiz(int total) {
        quiz_correct += total;
    }

    public int GetCorrectQuiz() {
        return quiz_correct;
    }

    public void SetCorrectQuiz(int num) {
        quiz_correct = num;
    }

    public void SetRounds(int round) {quiz_rounds = round;}

    public int GetRounds() {return quiz_rounds; }

    public void SetTimes(int round) {login_times = round;}

    public int GetTimes() {return login_times; }

    public void SetRoundPerf(int round_num, int right_quiz_num) {
        if(round_num >= 0) quiz_round_perf[round_num] = right_quiz_num;
    }

    public int GetRoundPerf(int round_num) {
        if(round_num >= 0) return quiz_round_perf[round_num];
        else
            return 0;
    }
}