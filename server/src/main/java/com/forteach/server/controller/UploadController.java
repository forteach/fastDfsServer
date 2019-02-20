package com.forteach.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.forteach.server.fastdfs.FastDFSClient;
import com.forteach.server.fastdfs.FastDFSFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 18-12-4 21:10
 * @Version: 1.0
 * @Description:
 */
@Controller
@ResponseBody
@RequestMapping("/")
public class UploadController {
    private static Logger logger = LoggerFactory.getLogger(UploadController.class);

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file){
        if (file.isEmpty()) {
            return "文件不存在请重新上传！";
        }
        try {
            // Get the file and save it somewhere
            String path = saveFile(file);
            Map map = new HashMap<String, String>();
            map.put("fileName", file.getOriginalFilename());
            map.put("fileUrl", path);
            String jsonString = JSONObject.toJSONString(map);
            return jsonString;
        } catch (Exception e) {
            logger.error("upload file failed",e);
        }
        return null;
    }

    /**
     * @param multipartFile
     * @return
     * @throws IOException
     */
    public String saveFile(MultipartFile multipartFile) throws IOException {
        String[] fileAbsolutePath={};
        String fileName=multipartFile.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        byte[] file_buff = null;
        InputStream inputStream=multipartFile.getInputStream();
        if(inputStream!=null){
            int len1 = inputStream.available();
            file_buff = new byte[len1];
            inputStream.read(file_buff);
        }
        inputStream.close();
        FastDFSFile file = new FastDFSFile(fileName, file_buff, ext);
        try {
            //upload to fastdfs
            fileAbsolutePath = FastDFSClient.upload(file);
        } catch (Exception e) {
            logger.error("upload file Exception!",e);
        }
        if (fileAbsolutePath==null) {
            logger.error("upload file failed,please upload again!");
        }
        return FastDFSClient.getTrackerUrl()+fileAbsolutePath[0]+ "/"+fileAbsolutePath[1];
    }
}
