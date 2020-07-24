package maker;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Main {

    public static void main(String[] args){
        /*GeneraterPDF gpdf=new GeneraterPDF();
        gpdf.loadPdf("./text.pdf");
        gpdf.geneOnePage(0);*/
        if(args.length>0 && args[0].equals("pdf"))
            make_pdf();
        else
            make_txt();
        /*for(String str:args)
            System.out.println(str);*/
    }

    public static void make_pdf(){
        File out_dir=new File("./output");
        if (!out_dir.exists())
            out_dir.mkdirs();

        //加载配置文件
        Properties properties = new Properties();
        try {
            FileInputStream inputStream = new FileInputStream("./pdf.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        float dw=50,dh=50;
        String stmp=properties.getProperty("char_size",null);
        if(stmp!=null){
            String[] tmp=stmp.split(",");
            dw=Float.parseFloat(tmp[0]);
            dh=Float.parseFloat(tmp[1]);
        }

        //加载字体库
        System.out.println("load font");
        FontDatabase fbd=new FontDatabase(dw,dh);
        try {
            fbd.loadDatas(properties.getProperty("font_path","./tea"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        GeneraterPDF generater=new GeneraterPDF();
        try {
            generater.loadProperties(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
        generater.setFdb(fbd);
        generater.loadPdf(properties.getProperty("pdf_path","./text.pdf"));

        int pcount=generater.getPageCount();

        for(int i=0;i<pcount;i++){
            System.out.println("generate page"+i);
            BufferedImage bi = generater.geneOnePage(i);
            try {
                ImageIO.write(bi, "png", new File("./output/pdf_page" + i + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void make_txt(){
        File out_dir=new File("./output");
        if (!out_dir.exists())
            out_dir.mkdirs();

        //加载配置文件
        Properties properties = new Properties();
        try {
            FileInputStream inputStream = new FileInputStream("./range.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        float dw=50,dh=50;
        String stmp=properties.getProperty("char_size",null);
        if(stmp!=null){
            String[] tmp=stmp.split(",");
            dw=Float.parseFloat(tmp[0]);
            dh=Float.parseFloat(tmp[1]);
        }

        //加载字体库
        System.out.println("load font");
        FontDatabase fbd=new FontDatabase(dw,dh);
        try {
            fbd.loadDatas(properties.getProperty("font_path","./tea"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Generater generater=new Generater();
        try {
            generater.loadProperties(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
        generater.setFdb(fbd);
        generater.setText(Utils.readFile(properties.getProperty("text_path","./test.txt")));
        int page=1;

        //循环生成图像，直到结束(可以进一步封装进Generater)
        while (!generater.isOver()) {
            System.out.println("generate page"+page);
            BufferedImage bi = generater.geneOnePage();
            try {
                ImageIO.write(bi, "png", new File("./output/page" + page + ".png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            page++;
        }
    }
}
