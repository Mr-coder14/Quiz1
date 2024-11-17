package Models;

public class LeaderBoard {
    private String username,coin,userid;
    private int no;

    public LeaderBoard(String username, String coin, String userid, int no) {
        this.username = username;
        this.coin = coin;
        this.userid = userid;
        this.no = no;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }
}
