//package com.e3.utils;
//
//import java.io.FileNotFoundException;
//import java.io.IOException;
//
//import org.csource.common.MyException;
//import org.csource.fastdfs.ClientGlobal;
//import org.csource.fastdfs.StorageClient;
//import org.csource.fastdfs.StorageServer;
//import org.csource.fastdfs.TrackerClient;
//import org.csource.fastdfs.TrackerServer;
//import org.junit.Test;
//
//public class FastDfsUtils {
//	@Test
//	public void testFileUpload() throws FileNotFoundException, IOException, MyException{
//		//1. 加载配置文件，配置文件中的内容就是tracker服务的地址。
//		ClientGlobal.init("E:/Users/liujian/git/e3-manager-web/src/main/resources/conf/client.conf");
//		//2. 2、创建一个TrackerClient对象。直接new一个。
//		TrackerClient trackerClient = new TrackerClient();
//		//3、使用TrackerClient对象创建连接，获得一个TrackerServer对象。
//		TrackerServer trackerServer = trackerClient.getConnection();
//		//4、创建一个StorageServer的引用，值为null
//		StorageServer storageServer =null;
//		//5. 创建一个StorageClient对象，需要两个参数TrackerServer对象、StorageServer的引用
//		StorageClient storageClient = new StorageClient(trackerServer, storageServer);
//		//6、使用StorageClient对象上传图片。
//		String[] strings =storageClient.upload_file("E:/resource/9a80e2d06170b6bb01046233ede701b3.jpg", "jpg", null);
//		//7、返回数组。包含组名和图片的路径。
//		for (String string : strings) {
//			System.out.println(string);
//		}
//		
//		
//	}
//	
//	@Test
//	public void testFastutils() throws Exception{
//		 FastDFSClient fastDFSClient = new FastDFSClient("E:/Users/liujian/git/e3-manager-web/src/main/resources/client.conf");
//		 String string =fastDFSClient.uploadFile("E:/resource/9a80e2d06170b6bb01046233ede701b3.jpg", "jpg", null);
//		 System.out.println(string);
//		 
//		
//	}
//}
