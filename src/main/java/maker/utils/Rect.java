package maker.utils;

public class Rect {
    public float left,top,right,bottom;

    public Rect(float left,float top,float right,float bottom){
        this.left=left;
        this.top=top;
        this.right=right;
        this.bottom=bottom;
    }


    public void set(float left,float top,float right,float bottom){
        this.left=left;
        this.top=top;
        this.right=right;
        this.bottom=bottom;
    }
}
