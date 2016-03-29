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
    private static String google = "http://www.google.com/search?q=";
    //private static String fileInputName;
    private static String fileOutputName;
    private static String charset = "UTF-8";
    private static String userAgent = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";
    private static String duration = "l";
    private static String time = "";
    private static String attribute;

    public static SearchAgent prog;

    public static void main(String[] args) throws IOException, InvalidDataException{
        prog = new SearchAgent();
        gui gui = new gui();

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


    }

    public void runProgram (String fileInputName, int numberOfPages) throws IOException {

        fileOutputName = fileInputName.substring(0, fileInputName.lastIndexOf("/") + 1) + "results.txt";
        File file = new File(fileOutputName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            gui.AlarmPrint alarmPrint = new gui.AlarmPrint();
            alarmPrint.print("Can't create file. Please, do it manually and launch program again", Color.red);
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
    }

    public static void saveLinks(int numberOfPages, String request, BufferedWriter writer) throws IOException{
        for (int i = 0; i < numberOfPages; i++){
            String pages = "&start=" + i * 10;

            writer.write(String.format("---------- Page #%s ----------", i + 1));
            writer.write("\r\n");

            Elements links = Jsoup.connect(String.format("%s%s%s%s", google, URLEncoder.encode(request, charset), attribute, pages)).userAgent(userAgent).get().select("a");

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