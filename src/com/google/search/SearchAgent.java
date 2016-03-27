package com.google.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by Sergey.Chmihun on 03/04/2016.
 */
public class SearchAgent {
    private static String google = "http://www.google.com/search?q=";
    private static String request = "Метод Фрейда 2";
    private static String charset = "UTF-8";
    private static String userAgent = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";
    private static String duration = "l";
    private static String time = "";
    private static String attribute;

    public static void main(String[] args) throws IOException, InvalidDataException {
        if (args.length != 0) {
            if (args[0].equals("h")) time = "h";
            if (args[0].equals("d")) time = "d";
            if (args[0].equals("w")) time = "w";
            if (args[0].equals("m")) time = "m";
            if (args[0].equals("y")) time = "y";
            else throw new InvalidDataException();
        }

        if (!time.isEmpty()) {
            attribute = "&tbs=qdr:" + time + ",dur:" + duration + "&tbm=vid";
        } else attribute = "&tbs=dur:" + duration + "&tbm=vid";

        System.out.println(String.format("%s%s%s", google, URLEncoder.encode(request, charset), attribute));

        Elements links = Jsoup.connect(String.format("%s%s%s", google, URLEncoder.encode(request, charset), attribute)).userAgent(userAgent).get().select("a");

        for (Element link : links) {
            String title = link.text();
            String url = link.absUrl("href"); // absUrl("href") - Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
            //url = URLDecoder.decode(url.substring(url.indexOf('=') + 1, url.indexOf('&')), "UTF-8");

            if (!url.startsWith("http") || !title.contains(request)) {
                continue; // Ads/news/etc.
            }

            System.out.println(String.format("Title: %s  - URL: %s", title, url));
        }
    }
}

// &start=0, 10, 20 - pages 1, 2, 3, 4