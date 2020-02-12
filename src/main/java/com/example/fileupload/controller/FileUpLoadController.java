package com.example.fileupload.controller;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * @author mkl
 * @date 2020/02/11
 **/
@Controller
public class FileUpLoadController {

    private static final Logger LOGGER = LoggerFactory.getLogger (FileUpLoadController.class);

    @GetMapping("/")
    public String index () {
        return "index";
    }

    @PostMapping("/fileupload01")
    public String fileupload01 (MultipartFile file, Model model) {
        System.out.println ("开始");
        if (file.isEmpty ()) {
            model.addAttribute ("Str", "上传失败，请选择文件");
            return "index";
        }
        // todo 选择了文件，开始上传操作
        try {
            // todo 构建上传目标路径，找到了项目的target的classes目录
            File destFile = new File (ResourceUtils.getURL ("classpath:").getPath ());
            if (!destFile.exists ()) {
                destFile.createNewFile ();
            }
            System.out.println ("destFile path:" + destFile.getAbsolutePath ());
            // todo 获取文件的完整名称,名称,扩展名(不带点)
            String oriFileName = file.getOriginalFilename ();
            String fileName = oriFileName.substring (0, oriFileName.lastIndexOf ("."));
            String fileExtension = oriFileName.substring (oriFileName.lastIndexOf (".") + 1);
            // todo 拼接子路径，文件扩展名 时间日期 UUID
            Date date = new Date ();
            File upload = new File (destFile.getAbsolutePath (), "static/" + fileExtension + "/" + new SimpleDateFormat ("yyyy-MM-dd/").format (date) + "/" + fileName + UUID.randomUUID () + "." + fileExtension);
            //若目标文件夹不存在，则创建
            if (!upload.exists ()) {
                upload.mkdirs ();
            }
            System.out.println ("完整的上传路径：" + upload.getAbsolutePath ());
            // 本质上还是使用了流 只不过是封装了步骤
            // todo 写入文件
            file.transferTo (upload);
            model.addAttribute ("Str", "上传成功");
        } catch (Exception e) {
            model.addAttribute ("Str", "上传失败");
            e.printStackTrace ();
        }
        return "index";
    }

    //ATTENTION!!!!!
    //Spring boot 默认使用 StandardServletMultipartResolver 解析 multipart 请求
    //控制层中接收的非原始request  而commons-fileupload又是对原始HttpServletRequest的解析  所以以下方法中解析不到数据
    @PostMapping("/fileupload02")
    public String fileupload02 (HttpServletRequest request, Model model) throws UnsupportedEncodingException {
        request.setCharacterEncoding ("UTF-8");
        /*
         ServletFileUpload类，主要用于解析form表单提交的数据、设置请求信息实体内容的最大允许字节数
         主要使用的方法见md文件
        */
        // todo 判断enctype属性是否是multipart/form-data
        boolean isMultipart = ServletFileUpload.isMultipartContent (request);
        /*
         FileItem接口，用于封装单个表单字段元素的数据，每一个表单字段都对应一个FileItem实例，
         在应用程序中使用的是其实现类DiskFileItem。
         主要使用的方法见md文件
        */
        /*
         FileItemFactory接口与其实现类DiskFileItemFactory
         创建ServletFileUpload实例需要依赖FileItemFactory工厂接口，DiskFileItemFactory是此接口的实现类
         主要使用的方法见md文件
        */
        try {
            if (isMultipart) {
                // todo 工厂实例
                FileItemFactory factory = new DiskFileItemFactory ();
                // todo ServletFileUpload实例依赖于FileItemFactory工厂
                ServletFileUpload upload = new ServletFileUpload (factory);
                // todo 解析表单字段，封装成一个FileItem实例的集合
                List<FileItem> itemList = upload.parseRequest (request);
                System.out.println ("itemList.size () = " + itemList.size ());
                if (null == itemList || itemList.size () == 0) {
                    model.addAttribute ("Str", "上传失败");
                    return "index";
                } else {
                    // todo 迭代器
                    Iterator<FileItem> iterator = itemList.iterator ();
                    while (iterator.hasNext ()) {
                        // todo 依次解析每一个FileItem实例，即表单字段
                        FileItem fileItem = iterator.next ();
                        if (fileItem.isFormField ()) {
                            //普通表单字段
                        } else {
                            //文件表单字段
                            // todo 用户上传的文件名
                            String fileUpName = fileItem.getName ();
                            System.out.println ("fileUpName = " + fileUpName);
                            // todo 要保存到的文件
                            File file = new File ("E:\\OneDrive\\IdeaProjects\\fileupload\\src\\main\\resources\\static\\" + fileUpName);
                            if (!file.exists ()) {
                                // todo 文件不存在 则先创建
                                file.createNewFile ();
                            }
                            // todo 写入，保存到目标文件
                            fileItem.write (file);
                        }
                    }
                    LOGGER.info ("上传成功");
                    model.addAttribute ("Str", "上传成功");
                }
            }
        } catch (Exception e) {
            model.addAttribute ("Str", "上传失败");
            LOGGER.error (e.toString (), e);
        }
        return "index";
    }
}
