package com.e3.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.e3.utils.FastDFSClient;
import com.e3.utils.JsonUtils;

@Controller
public class PictureController {
	
	@Value("${IMAGE_SERVER_URL}")
	private String IMAGE_SERVER_URL;
	
	@RequestMapping("/pic/upload")
	@ResponseBody
	public String uploadPicture(MultipartFile uploadFile){
		try {
			//1.获取图片的扩展名
			String fileName =uploadFile.getOriginalFilename();
			String extName =fileName.substring(fileName.lastIndexOf(".")+1);
			//2. 把图片上传到图片服务器。使用封装的工具类实现。需要取文件的内容和扩展名
			FastDFSClient fastDFSClient = new FastDFSClient("classpath:conf/client.conf");
			//3.图片服务器返回图片的url
			String path =fastDFSClient.uploadFile(uploadFile.getBytes(), extName);
			//4.将图片的url补充完整，返回一个完整的url。
			String url = IMAGE_SERVER_URL+path;
			//5. 返回map
			Map result = new HashMap();
			result.put("error", 0);
			result.put("url", url);
			//6. 把java对象转换成字符串, 解决浏览器兼容问题
			String json = JsonUtils.objectToJson(result);
			return json;
			
		} catch (Exception e) {
			Map result = new HashMap();
			result.put("error", 0);
			result.put("message", "错误信息");
			String json = JsonUtils.objectToJson(result);
			return json;
		}
		
	}
}
