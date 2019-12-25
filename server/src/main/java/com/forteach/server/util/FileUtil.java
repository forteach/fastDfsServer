//package com.forteach.server.util;
//
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.text.DecimalFormat;
//
///**
// * @author: zhangyy
// * @email: zhang10092009@hotmail.com
// * @date: 2019/12/20 18:41
// * @version: 1.0
// * @description:
// */
//public class FileUtil {
//    /**
//     * 定义GB的计算常量
//     */
//    private static final int GB = 1024 * 1024 * 1024;
//    /**
//     * 定义MB的计算常量
//     */
//    public static final int MB = 1024 * 1024;
//    /**
//     * 定义KB的计算常量
//     */
//    private static final int KB = 1024;
//
//    /**
//     * 格式化小数
//     */
//    private static final DecimalFormat DF = new DecimalFormat("0.00");
//
//    public static File convertMultiPartToFile(MultipartFile multipartFile) throws IOException {
//        File file = new File(multipartFile.getOriginalFilename());
//        FileOutputStream fos = new FileOutputStream(file);
//        fos.write(multipartFile.getBytes());
//        fos.close();
//        return file;
//    }
//
//    /**
//     * 文件大小转换
//     *
//     * @param size
//     * @return
//     */
//    public static String getSize(long size) {
//        String resultSize = "";
//        if (size / GB >= 1) {
//            //如果当前Byte的值大于等于1GB
//            resultSize = DF.format(size / (float) GB) + "GB   ";
//        } else if (size / MB >= 1) {
//            //如果当前Byte的值大于等于1MB
//            resultSize = DF.format(size / (float) MB) + "MB   ";
//        } else if (size / KB >= 1) {
//            //如果当前Byte的值大于等于1KB
//            resultSize = DF.format(size / (float) KB) + "KB   ";
//        } else {
//            resultSize = size + "B   ";
//        }
//        return resultSize;
//    }
//}
