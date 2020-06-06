package serverNet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPCrawler {
    public static String getLocalIP() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "Unknown";
        }
    }
    public static String getInternetIP() {
        try {
            URL url=new URL("https://www.baidu.com/s?wd=ip");
            String read;
            StringBuilder stringBuilder=new StringBuilder();
            URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty("Accept","Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            urlConnection.setRequestProperty("Accept-Encoding","utf-8");
            urlConnection.setRequestProperty("Accept-Language","zh-CN,zh;q=0.9,ja;q=0.8");
            urlConnection.setRequestProperty("Cache-Control","max-age=0");
            urlConnection.setRequestProperty("Connection","keep-alive");
            urlConnection.setRequestProperty("Cookie","BIDUPSID=E647B5FC488120747E952FD5A2FEBF22; PSTM=1543642793; BD_UPN=12314753; sug=3; ORIGIN=0; bdime=0; MCITY=-131%3A; BAIDUID=46A665D434FE4F2DC183664C5B09B4B1:FG=1; BD_HOME=1; BDRCVFR[feWj1Vr5u3D]=I67x6TjHwwYf0; delPer=0; BD_CK_SAM=1; PSINO=1; BDRCVFR[dG2JNJb_ajR]=mk3SLVN4HKm; BDRCVFR[-pGxjrCMryR]=mk3SLVN4HKm; ZD_ENTRY=bing; H_PS_PSSID=31626_1440_31669_21124_31069_31606_31270_31464_31714_30824_26350; BDORZ=B490B5EBF6F3CD402E515D22BCDA1598; COOKIE_SESSION=1015887_0_7_1_4_6_1_0_6_5_1_0_0_0_0_0_1586872005_0_1588105985%7C9%2314798794_10_1578304422%7C4; sugstore=0; H_PS_645EC=609bRtxrszLYUSDBPSt51hhb%2B%2BGchv9QLrXg5zznht880KDByaa%2FuZ6hmNI4%2FdM1Rf9A; BDSVRTM=92");
            urlConnection.setRequestProperty("Host","www.baidu.com");
            urlConnection.setRequestProperty("Sec-Fetch-Dest","document");
            urlConnection.setRequestProperty("Sec-Fetch-Mode","navigate");
            urlConnection.setRequestProperty("Sec-Fetch-Site","none");
            urlConnection.setRequestProperty("Sec-Fetch-User","?1");
            urlConnection.setRequestProperty("Upgrade-Insecure-Requests","1");
            urlConnection.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");
            urlConnection.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));
            while ((read = in.readLine()) != null)
                stringBuilder.append(read);
            Pattern p = Pattern.compile("IP:&nbsp;(.*?)</span>");
            Matcher m = p.matcher(stringBuilder.toString());
            if(m.find())
                return m.group(1);
            return "Unknown";
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown";
        }
    }
}
