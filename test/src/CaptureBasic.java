import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.imgcodecs.Imgcodecs;
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
/**
     * opencv实现人脸识别
     * @param img
     */
    public static Mat detectFace(Mat img)
    {
        System.out.println("正在扫描脸部信息... ");
        // 从配置文件lbpcascade_frontalface.xml中创建一个人脸识别器，该文件位于opencv安装目录中
        CascadeClassifier faceDetector = new CascadeClassifier("G:\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt.xml");


        // 在图片中检测人脸
        MatOfRect faceDetections = new MatOfRect();

        faceDetector.detectMultiScale(img, faceDetections);

        //System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));//扫描到的脸部的数量

        Rect[] rects = faceDetections.toArray();//将人脸转换为数组形式

        if(rects != null && rects.length >= 1){
            for(int i = 0 ; i < rects.length ; i++){
                Rect rect = rects[i];
                Imgproc.rectangle(img, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                        new Scalar(0, 0, 255), 2);//扫描脸部时的框的设置（颜色及宽度）
                Mat sub = img.submat(rects[i]);//Mat sub = new Mat(image,rect);
                Mat mat = new Mat();
                Size size = new Size(300, 300);//人脸框的标准大小
                System.out.println("已检测到人脸，正在截取中...");
                a=true;
                Imgproc.resize(sub, mat, size);//将人脸进行截图并保存 
                Imgcodecs.imwrite("G:\\opencv\\img\\0.png", mat);//将扫描到的脸部截取存储
                System.out.println("人脸截取成功，正在匹配中...");

            }
        }
        return img;
    }
}
}
