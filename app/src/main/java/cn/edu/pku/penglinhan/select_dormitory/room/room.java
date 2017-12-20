package cn.edu.pku.penglinhan.select_dormitory.room;

/**
 * Created by Administrator on 2017/12/19 0019.
 */

public class room {
    private String room5,room13,room14,room8,room9;
    public String getRoom5() {return room5;}
    public String getRoom13() {return room13;}
    public String getRoom14() {return room14;}
    public String getRoom8() {return room8;}
    public String getRoom9() {return room9;}
    public void setroom5(String room5){
        this.room5=room5;
    }
    public void setroom13(String room13){
        this.room13=room13;
    }
    public void setroom14(String room14){
        this.room14=room14;
    }
    public void setroom8(String room8){
        this.room8=room8;
    }
    public void setroom9(String room9){
        this.room9=room9;
    }
    public String toString(){
        return "student{"+
                "room5='"+room5+"\'"+
                "room13='"+room13+"\'"+
                "room14='"+room14+"\'"+
                "room8='"+room8+"\'"+
                "room9='"+room9+"\'"+
                '}';
    }
}
