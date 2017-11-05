import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Main {

	public static void main(String[] args) {
		String url = "http://www.dudj.net/hongsejingdian/53/";
		try {
			getUrl(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getUrl(String url) throws Exception {
		Document doc = null;
		try {
			
			Response response = Jsoup.connect(url).execute();
			if(200 != response.statusCode()) {
				throw new Exception("訪問不了");			
			}
			doc = Jsoup.connect(url).cookies(response.cookies()).userAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; MALC)").get();
			System.out.println(doc);
			Pattern patternForUrl = Pattern.compile("<li><a href=\"/hongsejingdian/53/\\d{4}.html");
			Matcher matcherForUrl = patternForUrl.matcher(doc.toString());
			
			Map<String, String> map = new HashMap<>();
			while (matcherForUrl.find()) {
				String matchStr = matcherForUrl.group(0);
				//System.out.println(matchStr);
				String downloadUrl = matchStr.replace("<li><a href=\"", "http://www.dudj.net");
				
				Pattern patternForTitle = Pattern.compile(matchStr + "\" title=\".*? target");
				Matcher matcherForTitle = patternForTitle.matcher(doc.toString());
				if(matcherForTitle.find()) {
					String matchStr2 = matcherForTitle.group(0);
					//System.out.println(matchStr2);
					String title = matchStr2.replace(matchStr, "").replace("title=", "").replace("target", "").replace(" ", "").replace("\"", "");
					map.put(title, downloadUrl);
				} else {
					System.out.println(downloadUrl + " is not found");
				}
			}

			map.forEach((name, urlName)->{
				System.out.println(name + ": " + urlName);
				saveHtml(name, urlName);
			});
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void saveHtml(String name, String urlName) {
		try {
			File file = new File("D:\\用户目录\\我的文档\\E-Book\\毛选\\test\\" + name + ".html");
			if(file.exists()) {
				System.out.println("刪除" + file.getName());
				file.delete();
			} else {
				System.out.println("創建" + file.getName());
				file.createNewFile();
			}
			
			URL url = new URL(urlName);
			FileOutputStream fos = new FileOutputStream(file);
			InputStream is = url.openStream();
						
			BufferedInputStream bis = new BufferedInputStream(is);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			
			int length;
			byte[] bytes = new byte[1024*20];
			
			while((length = bis.read(bytes, 0, bytes.length)) != -1) {
				bos.write(bytes, 0, length);
			}
			
			bos.close();
			bis.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
