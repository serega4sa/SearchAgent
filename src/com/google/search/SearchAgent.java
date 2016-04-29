package com.google.search;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
    private ArrayList<String> whiteList;

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

    public void setWhiteList(ArrayList<String> whiteList) {
        this.whiteList = whiteList;
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
            int counterOfFoundRes = 0;

            for (Element link : links) {
                String title = link.text();
                String alternativeRequest = "";
                if (request.toLowerCase().contains("ё")) {
                    alternativeRequest = request.toLowerCase().replaceAll("ё", "е");
                }

                if (title.toLowerCase().contains(request.toLowerCase()) || (!alternativeRequest.isEmpty() && title.toLowerCase().contains(alternativeRequest))) {
                    String gUrl = link.absUrl("href"); // absUrl("href") - Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
                    String url = URLDecoder.decode(gUrl.substring(gUrl.indexOf('=') + 1, gUrl.indexOf('&')), "UTF-8");

                    if (whiteList != null) {
                        if (!checkPlayer(url)) {
                            writer.write(String.format("Title: %s  - Google URL: %s  - Content URL: %s", title, gUrl, url));
                            writer.write("\r\n");
                            counterOfFoundRes++;
                        }
                    } else {
                        writer.write(String.format("Title: %s  - Google URL: %s  - Content URL: %s", title, gUrl, url));
                        writer.write("\r\n");
                        counterOfFoundRes++;
                    }
                }
            }

            if (counterOfFoundRes == 0) {
                writer.write("Videos that corresponds to the request wasn't found on this page.");
                writer.write("\r\n");
            }
        }
    }

    public boolean checkPlayer(String url) throws IOException {
        for (String item : whiteList) {
            if (url.contains(item)) return true;
        }

        //In this part program goes to every link and find all iframe elements and check if they equal to white list items. Don't work properly. I suppose there should be used multithreading.
        /*Elements iframes = Jsoup.connect(url).userAgent(userAgent).get().select("iframe");

        for (Element element : iframes) {
            if (element != null && element.attr("src").contains("http")) {
                String videoLink = element.attr("src");

                for (String item : whiteList) {
                    if (videoLink.contains(item)) return true;
                }
            }
        }*/

        return false;
    }
}