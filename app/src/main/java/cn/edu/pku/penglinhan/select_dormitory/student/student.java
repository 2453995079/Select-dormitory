package cn.edu.pku.penglinhan.select_dormitory.student;

/**
 * Created by Administrator on 2017/12/19 0019.
 */

public class student {
    private String studentid,name,gender,vcode,room,building,location,grade;
    public String getstudentid() {return studentid;}
    public String getname() {return name;}
    public String getgender() {return gender;}
    public String getvcode() {return vcode;}
    public String getroom() {return room;}
    public String getbuilding() {return building;}
    public String getlocation() {return location;}
    public String getgrade() {return grade;}
    public void setstudentid(String studentid){
        this.studentid=studentid;
    }
    public void setname(String name){
        this.name=name;
    }
    public void setgender(String gender){
        this.gender=gender;
    }
    public void setvcode(String vcode){
        this.vcode=vcode;
    }
    public void setroom(String room){
        this.room=room;
    }
    public void setbuilding(String building){
        this.building=building;
    }
    public void setlocation(String location){
        this.location=location;
    }
    public void setgrade(String grade){
        this.grade=grade;
    }
    public String toString(){
        return "student{"+
                "studentid='"+studentid+"\'"+
                "name='"+name+"\'"+
                "gender='"+gender+"\'"+
                "vcode='"+vcode+"\'"+
                "room='"+room+"\'"+
                "building='"+building+"\'"+
                "location='"+location+"\'"+
                "grade='"+grade+"\'"+
                '}';
    }
}
