package com.forteach.server.controller;

import cn.binarywang.wx.miniapp.api.WxMaSecCheckService;
import com.alibaba.fastjson.JSONObject;
import com.forteach.server.config.WeChatMiniAppConfig;
import com.forteach.server.fastdfs.FastDFSClient;
import com.forteach.server.fastdfs.FastDFSFile;
import com.forteach.server.util.FileUtil;
import com.forteach.server.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static com.forteach.server.util.FileUtil.MB;

/**
 * @Auther: zhangyy
 * @Email: zhang10092009@hotmail.com
 * @Date: 18-12-4 21:10
 * @Version: 1.0
 * @Description:
 */
@RestController
@RequestMapping("/")
public class UploadController {
    private static Logger logger = LoggerFactory.getLogger(UploadController.class);


    @Value("${file.server.path}")
    public String filePath;

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile multipartFile, HttpServletRequest request) throws Exception {
        if (multipartFile.isEmpty()) {
            return "文件不存在请重新上传！";
        }
        try {
            // Get the file and save it somewhere
            String check = request.getParameter("check");
            if (StringUtil.isNotBlank(check) && "check".equals(check)) {
                long size = multipartFile.getSize();
                if (MB < size) {
                    return "您上传的文件大小为: " + FileUtil.getSize(size) + ",只能上传小于1MB的图片";
                }
                //调用微信信息内容校验是否合法
                final WxMaSecCheckService checkService = WeChatMiniAppConfig.getMaService().getSecCheckService();
                File file = FileUtil.convertMultiPartToFile(multipartFile);
                if (!checkService.checkImage(file)) {
                    return "图片不合法";
                }
            }
            String path = saveFile(multipartFile);
            Map<String, Object> map = new HashMap<>(2);
            map.put("fileName", multipartFile.getOriginalFilename());
            map.put("fileUrl", path);
            String sort = request.getParameter("sort");
            if (!StringUtils.isEmpty(sort)) {
                map.put("sort", Integer.parseInt(sort));
            }
            return JSONObject.toJSONString(map);
        } catch (Exception e) {
            logger.error("upload file failed", e);
        }
        return "";
    }

    @DeleteMapping(path = "/")
    public String deleteFile(HttpServletRequest httpServletRequest) throws Exception {
        String url = httpServletRequest.getParameter("url");
        if (StringUtil.isBlank(url)) {
            return "url不能是空白";
        }
        String name = url.substring(url.indexOf("group1"));
        String remoteFileName = name.substring(name.indexOf("/") + 1);
        FastDFSClient.deleteFile("group1", remoteFileName);
        return "success";
    }

    /**
     * @param multipartFile
     * @return
     * @throws IOException
     */
    private String saveFile(MultipartFile multipartFile) throws IOException {
        String[] fileAbsolutePath = {};
        String fileName = multipartFile.getOriginalFilename();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        byte[] file_buff = null;
        InputStream inputStream = multipartFile.getInputStream();
        if (inputStream != null) {
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
            logger.error("upload file Exception!", e);
        }
        if (fileAbsolutePath == null) {
            logger.error("upload file failed,please upload again!");
        }
        return filePath + fileAbsolutePath[0] + "/" + fileAbsolutePath[1];
    }
}