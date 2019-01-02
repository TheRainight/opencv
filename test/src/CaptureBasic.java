import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
public class CaptureBasic extends JPanel {
    private BufferedImage mImg;
    private BufferedImage mat2BI(Mat mat){                        //将图片转换为灰度图片
        int dataSize =mat.cols()*mat.rows()*(int)mat.elemSize();
        byte[] data=new byte[dataSize];
        mat.get(0, 0,data);
        int type=mat.channels()==1?
                BufferedImage.TYPE_BYTE_GRAY:BufferedImage.TYPE_3BYTE_BGR;//TYPE_3BYTE_BGR 表示一个具有 8 位 RGB 颜色分量的图像，
        //对应于 Windows 风格的 BGR 颜色模型，
        //具有用 3 字节存储的 Blue、Green 和 Red 三种颜色。
        //TYPE_BYTE_GRAY表示无符号 byte 灰度级图像（无索引）
        if(type==BufferedImage.TYPE_3BYTE_BGR){      //转换过程
            for(int i=0;i<dataSize;i+=3){
                byte blue=data[i+0];
                data[i+0]=data[i+2];
                data[i+2]=blue;
            }
        }
        BufferedImage image=new BufferedImage(mat.cols(),mat.rows(),type);
        image.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);
        return image;

    }

    public void paintComponent(Graphics g){//利用JPanel类的paintComponent()方法, 设置图片的高度和宽度
        if(mImg!=null){
            g.drawImage(mImg, 0, 0, mImg.getWidth(),mImg.getHeight(),this);
        }
    }
}
