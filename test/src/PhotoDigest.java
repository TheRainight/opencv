import javax.imageio.*;
import java.awt.image.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PhotoDigest {

    public static ArrayList<String> getFilesPath(String path) throws Exception {//遍历目录中的图片
//目标集合fileList
        ArrayList<String> fileList = new ArrayList<String>();
        File file = new File(path);
        if(file.isDirectory()){
            File []files = file.listFiles();
            for(File fileIndex:files){
//如果这个文件是目录，则进行递归搜索
                fileList.add(fileIndex.getPath());
            }
        }
        return fileList;
    }
            public static void main(String[] args) throws Exception {
                List<String> list = PhotoDigest.getFilesPath("G:\\opencv\\img1");//人脸目录
                int i = 1;
                System.out.println("正在识别中...");
                for (String str : list) {

                    System.out.println("第" + i++ + "张");
                    float percent = compare(getData("G:\\opencv\\img\\0.png"),//拍的照片
                            getData(str));
                    System.out.println("两张图片的相似度为：" + percent + "%");
                    if (percent>=75) {
                        System.out.println("签到成功");
                        break;
                    }
                    if(i>37){
                        System.out.println("签到失败");
                    }
                }
            }

            public static int[] getData(String name) {
                try {
                    BufferedImage img = ImageIO.read(new File(name));//将图片转换为灰度直方图
                    BufferedImage slt = new BufferedImage(100, 100,
                            BufferedImage.TYPE_INT_RGB);
                    slt.getGraphics().drawImage(img, 0, 0, 100, 100, null);

                    int[] data = new int[256];
                    for (int x = 0; x < slt.getWidth(); x++) {
                        for (int y = 0; y < slt.getHeight(); y++) {
                            int rgb = slt.getRGB(x, y);//转为RGB直方图
                            Color myColor = new Color(rgb);
                            int r = myColor.getRed();
                            int g = myColor.getGreen();
                            int b = myColor.getBlue();
                            data[(r + g + b) / 3]++;
                        }
                    }
                    // data 就是所谓图形学当中的直方图的概念
                    return data;
                } catch (Exception exception) {
                    System.out.println("有文件没有找到,请检查文件是否存在或路径是否正确");
                    return null;
                }
            }
         
            public static float compare(int[] s, int[] t) {//具体图片相似度计算
                try {
                    float result = 0F;
                    for (int i = 0; i < 256; i++) {
                        int abs = Math.abs(s[i] - t[i]);
                        int max = Math.max(s[i], t[i]);
                        result += (1 - ((float) abs / (max == 0 ? 1 : max)));
                    }
                    return (result / 256) * 100;
                } catch (Exception exception) {
                    return 0;
                }
            }
        }
