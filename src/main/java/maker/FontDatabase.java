package maker;

import com.google.common.collect.ArrayListMultimap;
import maker.utils.Size;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;


//字体库加载类
public class FontDatabase {

    HashMap<Character,File> font_map=new HashMap<Character,File>(); //字符对应的文件夹路径表
    ArrayListMultimap<Character,Image> font_img_map=ArrayListMultimap.create(); //字符对应的文字图片表，一个字符可以对应多个图片，后面随机选取
    public float dw,dh; //字体宽高
    Image im_default; //空白图片

    public FontDatabase(float dw,float dh){
        this.dw=dw;
        this.dh=dh;

        im_default= new BufferedImage((int)dw, (int)dh, BufferedImage.TYPE_INT_ARGB);
    }

    //加载字体库
    /*
        字体库结构
        root
          |___字
          |   |____1.png
          |   |____2.png
          |
          |___体
              |____1.png
              |____2.png
     */
    public void loadDatas(String path) throws IOException {
        font_map.clear();//清空字符路径表

        File dataroot=new File(path); //字体库根目录对象
        File[] font_dirs=dataroot.listFiles();
        for (File font_dir : font_dirs) { //遍历字体库中所有字符
            String name=font_dir.getName();
            font_map.put((char)Integer.parseInt(name),font_dir); //存入字符路径表
        }
    }

    //检查字符是否存在于字体库中，并检查字符是否载入缓存
    //缓存机制减少内存开销，不用加载全部字符
    public boolean checkChar(char ch){
        if(!font_map.containsKey(ch)) //库中没有这个字符
            return false;

        try {
            if(!font_img_map.containsKey(ch)) { //没有载入缓存 (字符图片表中没有这个字符作为key)
                File font_dir = font_map.get(ch); //获取字符文件夹目录
                File[] ch_ims = font_dir.listFiles();
                char name = (char) Integer.parseInt(font_dir.getName()); //获取字符(可以直接换成ch懒得动了)
                for (File ch_im : ch_ims) { //遍历加载字符对应的图片
                    //font_img_map.put(name, ImageIO.read(ch_im));
                    font_img_map.put(name, ImageIO.read(ch_im).getScaledInstance((int)dw,(int)dh,Image.SCALE_SMOOTH));
                    //font_map.put((char)Integer.parseInt(name), ImageIO.read(ch_im).getScaledInstance((int)dw,(int)dh,Image.SCALE_SMOOTH));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public Image getCharRandom_raw(char ch){ //随机取个字符对应的图片
        if(!checkChar(ch))
            return im_default;

        List<Image> list = font_img_map.get(ch);
        return list.get((int)(Math.random()*list.size()));
    }

    public Image getCharRandom(char ch){ //随机取个字符对应的图片
        return getCharRandom_raw(ch);
    }

    public Image getCharRandom(char ch, Size size){ //随机取个字符对应的图片
        return getCharRandom_raw(ch).getScaledInstance((int)size.w,(int)size.h,Image.SCALE_SMOOTH);
    }


}
