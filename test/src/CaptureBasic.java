import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class CaptureBasic extends JPanel {//JPanel 是 Java图形用户界面(GUI)工具包swing中的面板容器类
    static boolean  a=false;//设置结束拍照的标志
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

    public static void main(String[] args) {//开始实例化
        try{                                                                  //调用相机
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

            Mat capImg=new Mat();
            VideoCapture capture=new VideoCapture(0);
            int height = (int)capture.get(Videoio.CAP_PROP_FRAME_HEIGHT);//视频流帧的高.
            int width = (int)capture.get(Videoio.CAP_PROP_FRAME_WIDTH);
            if(height==0||width==0){                                         //如果相机未找到，抛出异常
                throw new Exception("相机设备未找到!");
            }

            JFrame frame=new JFrame("正在拍照");                      //相机窗体名称
            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);//调用任意已注册 WindowListener 的对象后自动隐藏并释放该窗体。
            CaptureBasic panel=new CaptureBasic();//实例化
            frame.setContentPane(panel);//设置窗体
            frame.setVisible(true);//设置可见性
            frame.setSize(width+frame.getInsets().left+frame.getInsets().right,
                    height+frame.getInsets().top+frame.getInsets().bottom);//设置窗体大小

            Mat temp=new Mat();
            while(frame.isShowing()){//当窗体打开时
                capture.read(capImg);
                Imgproc.cvtColor(capImg, temp, Imgproc.COLOR_RGB2GRAY);//利用RGB2GRAY将原图src转换为灰度图rgb2gray
                //
                panel.mImg=panel.mat2BI(detectFace(capImg));
                panel.repaint();
                //循环拍照
              if (a) {//拍照停止的条件 如果a==true
                frame.dispose();//关闭窗体

                //百度人脸对比API
                System.out.println("正在识别中...");
                List<String> list = FaceMatch.getFilesPath("G:\\opencv\\img1");
                int count = 1;
                for (String str : list) {
                    //  System.out.println("第" + count++ + "张");
                    FaceMatch.match(str, "G:\\opencv\\img\\0.png");
                    if (FaceMatch.s >= 85) {
                        String reg = "[^\u4e00-\u9fa5]";
                        System.out.println(str.replaceAll(reg, "") + "签到成功");
                        System.out.println("照片相似度为："+FaceMatch.s+"%");
                        break;
                    }
                    if (count > 36)//照片总数为36张
                        System.out.println("签到失败,未找到匹配的人脸信息");
                }
}
    }
        }catch(Exception e){
            System.out.println("出现错误：" + e);
        }finally{

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
