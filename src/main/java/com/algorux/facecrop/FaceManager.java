package com.algorux.facecrop;

import org.bytedeco.javacv.FrameGrabber;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.w3c.dom.css.Rect;

@RestController
public class FaceManager {

    @PostMapping("/crop")
    String cropFace(@RequestBody Map<String,Object> videoparams) throws FrameGrabber.Exception {

        String source = videoparams.get("srcVideo").toString();
        int timestamp =(int) videoparams.get("timeStamp");
        int step = 10;
        int maxPhotos = 4;
        Map<String,String> result = new HashMap<>();
        if (videoparams.get("maxPhotos") != null)
            maxPhotos = (int)videoparams.get("maxPhotos");
        if (videoparams.get("step") != null)
            maxPhotos = (int)videoparams.get("step");
        File myObj = new File(source);
        FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(myObj.getAbsoluteFile());
        frameGrabber.start();
        Frame f = null;
        try {
            Java2DFrameConverter c = new Java2DFrameConverter();
            for (int i =0;i<timestamp;i++) {
                f = frameGrabber.grab();
                if (f==null)
                    return "{'Error' : 'Bad time stamp'}";
            }
            int outpCount  =0;
            int frameCount = 0;

            while(f!=null){
                f = frameGrabber.grab();
                if (frameCount == step) {
                    BufferedImage bi = c.convert(f);
                    String path = videoparams.get("destImg").toString() + "_"+outpCount++;
                    bi = cropImage(bi, 535,85, 780,430);
                    result.put("img_"+outpCount,path);
                    ImageIO.write(bi, "png", new File(path));
                    frameCount = 0;
                    if (outpCount >= maxPhotos)
                        break;
                }
                frameCount++;
            }

            frameGrabber.stop();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "ok";
    }

    private BufferedImage cropImage(BufferedImage src, int x1, int y1, int x2, int y2) {
        BufferedImage dest = src.getSubimage(x1, y1, x2, y2);
        return dest;
    }
    @GetMapping("/")
    String Home(){
        return "ok";
    }

}
