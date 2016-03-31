package com.google.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Sergey.Chmihun on 03/04/2016.
 */
public class SearchAgent {
    private String charset = "UTF-8";
    private String userAgent = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";

    private String fileInputName;
    private String fileOutputName;
    private int numberOfPages;
    private String qDuration;
    private String vDuration;
    private String googleLocation;
    private String attribute;

    public static SearchAgent prog;
    public static Interface anInterface;

    public static void main(String[] args) {
        prog = new SearchAgent();
        anInterface = new Interface();
    }

    public String getqDuration() {
        return qDuration;
    }

    public void setqDuration(String qDuration) {
        if (qDuration.equals("any")) this.qDuration = "";
        if (qDuration.equals("hour")) this.qDuration = "h";
        if (qDuration.equals("day")) this.qDuration = "d";
        if (qDuration.equals("week")) this.qDuration = "w";
        if (qDuration.equals("month")) this.qDuration = "m";
        if (qDuration.equals("year")) this.qDuration = "y";
    }

    public String getvDuration() {
        return vDuration;
    }

    public void setvDuration(String vDuration) {
        if (vDuration.equals("any")) this.vDuration = "";
        if (vDuration.equals("medium")) this.vDuration = "m";
        if (vDuration.equals("long")) this.vDuration = "l";
    }

    public String getGoogleLocation() {
        return googleLocation;
    }

    public void setGoogleLocation(String location) {
        if (location.equals("ua")) this.googleLocation = "http://www.google.com.ua/search?q=";
        if (location.equals("ru")) this.googleLocation = "http://www.google.ru/search?q=";
    }

    public String getFileInputName() {
        return fileInputName;
    }

    public void setFileInputName(String fileInputName) {
        this.fileInputName = fileInputName;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public void createAttribute() {
        if (!qDuration.isEmpty()) {
            if (!vDuration.isEmpty()) {
                attribute = "&tbs=dur:" + vDuration + ",qdr:" + qDuration + "&tbm=vid";
            } else {
                attribute = "&tbs=qdr:" + qDuration + "&tbm=vid";
            }
        } else {
            if (!vDuration.isEmpty()) {
                attribute = "&tbs=dur:" + vDuration + "&tbm=vid";
            } else {
                attribute = "&tbm=vid";
            }
        }
    }

    public void runProgram () throws IOException {
        anInterface.getStatus().setText("processing...");
        anInterface.getStatus().setForeground(Color.BLUE);

        createAttribute();

        fileOutputName = fileInputName.substring(0, fileInputName.lastIndexOf("/") + 1) + "results.txt";
        File file = new File(fileOutputName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            anInterface.getStatus().setText("Can't create file. Please, do it manually and launch program again");
            anInterface.getStatus().setForeground(Color.RED);
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(fileOutputName));

        BufferedReader r = new BufferedReader(new FileReader(fileInputName));
        ArrayList<String> listOfRequests = new ArrayList<>();

        while (r.ready()) {
            String request = r.readLine();
            listOfRequests.add(request);
        }

        r.close();

        for (String item : listOfRequests) {
            writer.write(String.format("========== Request: %s ==========", item));
            writer.write("\r\n");
            saveLinks(numberOfPages, item, writer);
        }

        writer.close();
        anInterface.getStatus().setText("done");
        anInterface.getStatus().setForeground(Color.GREEN);
    }

    public void saveLinks(int numberOfPages, String request, BufferedWriter writer) throws IOException{
        for (int i = 0; i < numberOfPages; i++){
            String pages = "&start=" + i * 10;

            writer.write(String.format("---------- Page #%s ----------", i + 1));
            writer.write("\r\n");

            Elements links = Jsoup.connect(String.format("%s%s%s%s", googleLocation, URLEncoder.encode(request, charset), attribute, pages)).userAgent(userAgent).get().select("a");

            for (Element link : links) {
                String title = link.text();

                if (title.toLowerCase().contains(request.toLowerCase())) {
                    String gUrl = link.absUrl("href"); // absUrl("href") - Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
                    String url = URLDecoder.decode(gUrl.substring(gUrl.indexOf('=') + 1, gUrl.indexOf('&')), "UTF-8");

                    writer.write(String.format("Title: %s  - Google URL: %s  - Content URL: %s", title, gUrl, url));
                    writer.write("\r\n");
                }
                //System.out.println(String.format("Title: %s  - URL: %s", title, url));
            }
        }
    }
}