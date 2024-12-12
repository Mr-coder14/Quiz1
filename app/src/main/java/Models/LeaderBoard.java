package Models;

public class LeaderBoard {
    private String username,userid;

    private int no,coin;
    private String profile;
    public LeaderBoard(String username,String userid, int coin,  int no,String profile) {
        this.username = username;
        this.coin = coin;
        this.userid = userid;
        this.profile=profile;
        this.no = no;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public String getUserid() {
        return userid;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
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
