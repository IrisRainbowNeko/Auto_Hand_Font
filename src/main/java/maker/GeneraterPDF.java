package maker;

import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import com.spire.pdf.general.find.PdfTextFind;
import com.spire.pdf.general.find.PdfTextFindCollection;
import maker.utils.Range;
import maker.utils.Rect;
import maker.utils.Size;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Properties;

public class GeneraterPDF {

    float page_sc_rate=3;

    FontDatabase fdb;

    Range ofx_range=new Range(-8,8),ofy_range=new Range(-5,5); //字符自身偏移范围
    Range rot_range=new Range(-6,6); //旋转范围
    Range sc_range=new Range(0.9f,1.2f); //缩放范围

    Range chsp_range=new Range(-3,3); //字间距偏移范围
    Range linsp_range=new Range(5,10); //行间距偏移范围

    PdfDocument pdf;

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

        stmp=properties.getProperty("page_sc_rate","3");
        if(stmp!=null){
            page_sc_rate=Float.parseFloat(stmp);
        }
    }

    //生成一页
    public BufferedImage geneOnePage(int pageid){
        PdfPageBase page=pdf.getPages().get(pageid);
        PdfTextFind[] texts=page.findAllText().getFinds();
        Dimension2D page_size_raw=page.getSize(); //纸张大小

        BufferedImage bi = new BufferedImage((int)(page_size_raw.getWidth()*page_sc_rate), (int)(page_size_raw.getHeight()*page_sc_rate), BufferedImage.TYPE_INT_ARGB); //创建图片
        Graphics2D g = bi.createGraphics(); //图片作为画布

        //绘制白色背景
        g.setColor(Color.WHITE);
        g.fillRect(0,0,(int)(page_size_raw.getWidth()*page_sc_rate), (int)(page_size_raw.getHeight()*page_sc_rate));
        g.setColor(Color.BLACK);

        for(PdfTextFind text:texts) {
            char[] chs=text.getSearchText().toCharArray();
            Rectangle2D bounds;

            float px=(float)text.getPosition().getX()*page_sc_rate,py=(float)text.getPosition().getY()*page_sc_rate;
            float dsize=(float) text.getSize().getHeight()*page_sc_rate;
            //float dsize=(float) (text.getSize().getWidth()/chs.length)*page_sc_rate;
            float draw_size=dsize*1.2f;

            //System.out.println(text.getSearchText()+text.getBounds()+dsize);

            py+=Utils.rand_range(linsp_range);
            for(char ch:chs){
                drawChar(g,ch,px-draw_size/2,py-draw_size/2,draw_size,draw_size);
                //px+=(dsize+Utils.rand_range(chsp_range));
                if(Utils.isnum(ch))
                    px+=(dsize+Utils.rand_range(chsp_range))*0.6f;
                else if(Utils.isnum_or_alpha(ch))
                    px+=(dsize+Utils.rand_range(chsp_range))*0.5f;
                else
                    px+=(dsize+Utils.rand_range(chsp_range));
            }

            /*System.out.println(text.getSearchText());
            System.out.println(text.getSize());
            System.out.println(text.getBounds());*/

        }

        return bi;
    }

    public void drawChar(Graphics2D g,char ch,float x,float y,float dw, float dh){ //在x,y出绘制字符ch

        AffineTransform trans=new AffineTransform();
        trans.translate(x,y);

        //平移
        trans.translate(Utils.rand_range(ofx_range),Utils.rand_range(ofy_range));

        //旋转
        trans.rotate(Math.toRadians(Utils.rand_range(rot_range)));

        //缩放
        trans.scale(Utils.rand_range(sc_range),Utils.rand_range(sc_range));

        g.drawImage(fdb.getCharRandom(ch,new Size(dw,dh)),trans,null);
    }

    public void loadPdf(String file) { //设置要绘制的文本
        pdf=new PdfDocument();
        pdf.loadFromFile(file);;
    }

    public void setFdb(FontDatabase fdb) { //设置字体库
        this.fdb = fdb;
    }

    public int getPageCount(){
        return pdf.getPages().getCount();
    }
}
