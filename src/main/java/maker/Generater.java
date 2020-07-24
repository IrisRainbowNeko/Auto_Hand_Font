package maker;

import maker.utils.Range;
import maker.utils.Rect;
import maker.utils.Size;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Generater {

    String text="";
    Size page_size=new Size(1080,1920); //纸张大小
    Rect page_pad=new Rect(40,80,40,80); //页边距

    FontDatabase fdb;

    Range ofx_range=new Range(-8,8),ofy_range=new Range(-5,5); //字符自身偏移范围
    Range rot_range=new Range(-6,6); //旋转范围
    Range sc_range=new Range(0.9f,1.2f); //缩放范围

    Range chsp_range=new Range(-3,3); //字间距偏移范围
    Range linsp_range=new Range(5,10); //行间距偏移范围

    //加载配置文件
    public void loadProperties(Properties properties) throws IOException {

        String stmp=properties.getProperty("ofx_range",null);
        if(stmp!=null){
            String[] tmp=stmp.split(",");
            ofx_range.set(Float.parseFloat(tmp[0]),Float.parseFloat(tmp[1]));
        }

        stmp=properties.getProperty("ofy_range",null);
        if(stmp!=null){
            String[] tmp=stmp.split(",");
            ofy_range.set(Float.parseFloat(tmp[0]),Float.parseFloat(tmp[1]));
        }

        stmp=properties.getProperty("rot_range",null);
        if(stmp!=null){
            String[] tmp=stmp.split(",");
            rot_range.set(Float.parseFloat(tmp[0]),Float.parseFloat(tmp[1]));
        }

        stmp=properties.getProperty("sc_range",null);
        if(stmp!=null){
            String[] tmp=stmp.split(",");
            sc_range.set(Float.parseFloat(tmp[0]),Float.parseFloat(tmp[1]));
        }

        stmp=properties.getProperty("chsp_range",null);
        if(stmp!=null){
            String[] tmp=stmp.split(",");
            chsp_range.set(Float.parseFloat(tmp[0]),Float.parseFloat(tmp[1]));
        }

        stmp=properties.getProperty("linsp_range",null);
        if(stmp!=null){
            String[] tmp=stmp.split(",");
            linsp_range.set(Float.parseFloat(tmp[0]),Float.parseFloat(tmp[1]));
        }

        stmp=properties.getProperty("page_size",null);
        if(stmp!=null){
            String[] tmp=stmp.split(",");
            page_size.set(Float.parseFloat(tmp[0]),Float.parseFloat(tmp[1]));
        }

        stmp=properties.getProperty("page_pad",null);
        if(stmp!=null){
            String[] tmp=stmp.split(",");
            page_pad.set(Float.parseFloat(tmp[0]),Float.parseFloat(tmp[1]),Float.parseFloat(tmp[2]),Float.parseFloat(tmp[3]));
        }
    }

    //生成一页
    public BufferedImage geneOnePage(){
        BufferedImage bi = new BufferedImage((int)page_size.w, (int)page_size.h, BufferedImage.TYPE_INT_ARGB); //创建图片
        Graphics2D g = bi.createGraphics(); //图片作为画布

        //绘制白色背景
        g.setColor(Color.WHITE);
        g.fillRect(0,0,(int)page_size.w,(int)page_size.h);
        g.setColor(Color.BLACK);

        char[] chs=text.toCharArray(); //文本转字符数组(效率优化)

        float px=page_pad.left,py=page_pad.top; //当前绘制文本的x,y坐标
        for (int i = 0; i < chs.length; i++) { //循环直到结尾
            if(chs[i]=='\n'){ //换行
                px=page_pad.left; //x移至最左端
                py += (fdb.dh + Utils.rand_range(linsp_range)); //y移至下一行
                if(py+fdb.dh/2>=page_size.h-page_pad.bottom){ //超出本页范围
                    this.text=text.substring(i); //删除已绘制的文本
                    return bi;
                }
                continue;
            }

            drawChar(g,chs[i],px,py);
            if(Utils.isnum_or_alpha(chs[i]))
                px+=(fdb.dw+Utils.rand_range(chsp_range))*0.6f;
            else
                px+=(fdb.dw+Utils.rand_range(chsp_range)); //横向移至下一个字符
            if(px+fdb.dw/2>=page_size.w-page_pad.right) { //超出行宽度，该换行了
                px=page_pad.left; //x移至最左端
                py += (fdb.dh + Utils.rand_range(linsp_range)); //y移至下一行
                if(py+fdb.dh/2>=page_size.h-page_pad.bottom){ //超出本页范围
                    this.text=text.substring(i);
                    return bi;
                }
            }
        }
        this.text="";
        return bi;
    }

    public void drawChar(Graphics2D g,char ch,float x,float y){ //在x,y出绘制字符ch

        AffineTransform trans=new AffineTransform();
        trans.translate(x,y);

        //平移
        trans.translate(Utils.rand_range(ofx_range),Utils.rand_range(ofy_range));

        //旋转
        trans.rotate(Math.toRadians(Utils.rand_range(rot_range)));

        //缩放
        trans.scale(Utils.rand_range(sc_range),Utils.rand_range(sc_range));

        g.drawImage(fdb.getCharRandom(ch),trans,null);
    }

    public boolean isOver(){ //绘制结束
        return text==null || text.isEmpty();
    }

    public void setText(String text) { //设置要绘制的文本
        this.text = text;
    }

    public void setFdb(FontDatabase fdb) { //设置字体库
        this.fdb = fdb;
    }

    public void setPage_size(Size page_size) { //设置页面大小
        this.page_size = page_size;
    }
}
