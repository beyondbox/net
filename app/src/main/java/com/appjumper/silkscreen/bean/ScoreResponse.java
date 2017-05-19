package com.appjumper.silkscreen.bean;

/**
 * Created by yc on 2016/12/3.
 * 签到
 */
public class ScoreResponse extends BaseResponse{
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data{
        private String score;

        private String integral;

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }


        public String getIntegral() {
            return integral;
        }

        public void setIntegral(String integral) {
            this.integral = integral;
        }
    }


}
