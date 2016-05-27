package com.google.search;

import jxl.Workbook;
import jxl.write.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.Point;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.Dimension;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sergey.Chmihun on 03/04/2016.
 */
public class SearchAgent {
    private String charset = "UTF-8";
    private String userAgent = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";
    public static final String RESOURCE_PATH = "com.google.search.resources.";
    private static ResourceBundle res = ResourceBundle.getBundle(SearchAgent.RESOURCE_PATH + "common_en");

    private String fileInputName;
    private String fileOutputNameXls;
    private Date currentDate;
    private SimpleDateFormat format;
    private int numberOfPages;
    private String qDuration;
    private String vDuration;
    private String googleLocation;
    private String attribute;
    private ArrayList<String> whiteList;
    private WritableWorkbook workbook;
    private int counterOfFoundRes;
    private int lineNumber;
    private int lineNumberYoutube;

    public static SearchAgent prog;
    public static Interface anInterface;

    public static void main(String[] args) {
        prog = new SearchAgent();
        anInterface = new Interface();
    }

    public void setqDuration(String qDuration) {
        if (qDuration.equals("any")) this.qDuration = "";
        if (qDuration.equals("hour")) this.qDuration = "h";
        if (qDuration.equals("day")) this.qDuration = "d";
        if (qDuration.equals("week")) this.qDuration = "w";
        if (qDuration.equals("month")) this.qDuration = "m";
        if (qDuration.equals("year")) this.qDuration = "y";
    }

    public void setvDuration(String vDuration) {
        if (vDuration.equals("any")) this.vDuration = "";
        if (vDuration.equals("medium")) this.vDuration = "m";
        if (vDuration.equals("long")) this.vDuration = "l";
    }

    public void setGoogleLocation(String location) {
        if (location.equals("ua")) this.googleLocation = "http://www.google.com.ua/search?q=";
        if (location.equals("ru")) this.googleLocation = "http://www.google.ru/search?q=";
    }

    /** This method sets input and output file paths and automatically creates output file name with time stamp */
    public void setFileInputName(String fileInputName) {
        this.fileInputName = System.getProperty("user.dir") + "\\Input\\" + fileInputName;
        currentDate = new Date();
        format = new SimpleDateFormat("YYYY-MM-dd_hh-mm-ss");
        this.fileOutputNameXls = System.getProperty("user.dir") + "\\Output\\Results_" + format.format(currentDate) + ".xls";
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public void setWhiteList(ArrayList<String> whiteList) {
        this.whiteList = whiteList;
    }

    /** This method depending on the info that user specified creates attribute*/
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

    public void runProgram () throws IOException, InterruptedException {
        createAttribute();

        File excelFile = new File(fileOutputNameXls);

        /** Check of existence of file that we want to create for output data */
        while (true) {
            if (!excelFile.isFile()) {
                workbook = Workbook.createWorkbook(excelFile);
                break;
            } else {
                Thread.sleep(1000);
                excelFile = new File(System.getProperty("user.dir") + "\\Output\\Results_" + format.format(currentDate) + ".xls");
            }
        }

        BufferedReader r = new BufferedReader(new FileReader(fileInputName));
        ArrayList<String> listOfRequests = new ArrayList<>();

        while (r.ready()) {
            String request = r.readLine();
            listOfRequests.add(request);
        }

        r.close();

        /** For each request we create separate tab in XLS file and launch saveLinks method */
        for (String item : listOfRequests) {
            WritableSheet sheet = workbook.createSheet(item, 0);
            WritableSheet sheetYoutube = workbook.createSheet(item + " Youtube", 0);
            saveLinks(item, sheet, sheetYoutube);
        }

        try {
            workbook.write();
            workbook.close();
        } catch (WriteException e) {
            e.printStackTrace();
        }

        anInterface.getStatus().setText(res.getString("the.end"));
        anInterface.getStatus().setForeground(Color.GREEN);
    }

    /** This method saves links to the output XLS file, preliminarily checking them on the compliance with request and filtering allowed sources */
    public void saveLinks(String request, WritableSheet sheet, WritableSheet sheetYoutube) throws IOException, InterruptedException {
        lineNumber = 1;
        lineNumberYoutube = 1;

        /** We go through all pages for current request */
        for (int i = 0; i < numberOfPages; i++){
            counterOfFoundRes = 0;
            int x = i + 1;
            jxl.write.Label cell = new jxl.write.Label(0, lineNumber, "Page #" + x);
            try {
                sheet.addCell(cell);
            } catch (WriteException e) {
                e.printStackTrace();
            }
            String pages = "&start=" + i * 10;

            Elements links = null;
            try {
                links = Jsoup.connect(String.format("%s%s%s%s", googleLocation, URLEncoder.encode(request, charset), attribute, pages)).userAgent(userAgent).get().select("a");
            } catch (IOException e) {

                /** Google ban handler */
                if (e.toString().contains("Status=503")){
                    anInterface.setSuspended(true);
                    anInterface.getStatus().setText(res.getString("google.ban"));
                    anInterface.getStatus().setForeground(Color.RED);

                    String temp = e.toString().substring(e.toString().lastIndexOf("URL") + 4);
                    bannedParser(temp, request, sheet, sheetYoutube, x);
                    break;
                }
            }

            usualParser(links, request, sheet, sheetYoutube);
            emptyPage(sheet);
        }
    }

    /** This method parses HTML page in usual situation by using jsoup connection */
    public void usualParser (Elements links, String request, WritableSheet sheet, WritableSheet sheetYoutube) throws InterruptedException {
        for (Element link : links) {
            ArrayList<String> urls = checkMatchingRequest(link, request);

            if (urls == null) continue;

            String gUrl = urls.get(0);
            String url = urls.get(1);

            writeToFile(gUrl, url, sheet, sheetYoutube);

            counterOfFoundRes++;
        }
    }

    /** This method parses HTML page in situation when Google ban requests and asks to enter capture. In this case uses WebDriver. User need to enter capture once per requested title */
    public void bannedParser (String temp, String request, WritableSheet sheet, WritableSheet sheetYoutube, int x) throws IOException, InterruptedException {
        WebDriver driver = new FirefoxDriver();
        driver.manage().window().setPosition(new Point(0, 0));
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(600,350));
        driver.navigate().to(temp);
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

        WebDriverWait wait = new WebDriverWait(driver, 120);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sfdiv")));
        Elements links = null;

        if (driver.findElement(By.id("sfdiv")).isDisplayed()){
            anInterface.setSuspended(false);
            String link = driver.getCurrentUrl();
            links = Jsoup.connect(link).userAgent(userAgent).get().select("a");
        } else {
            anInterface.getStatus().setText(res.getString("time.out"));
            anInterface.getStatus().setForeground(Color.RED);
        }

        /** We go through all pages for current request */
        for (int i = --x; i < numberOfPages; i++) {
            counterOfFoundRes = 0;
            if (i != 0) {
                int y = i + 1;
                jxl.write.Label cell = new jxl.write.Label(0, lineNumber, "Page #" + y);
                try {
                    sheet.addCell(cell);
                } catch (WriteException e) {
                    e.printStackTrace();
                }

                /** Check whether next button exists and click */
                driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
                boolean exists = driver.findElements(By.xpath("//a[@id='pnnext']/span[2]")).size() != 0;
                driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

                if (exists) {
                    driver.findElement(By.xpath("//a[@id='pnnext']/span[2]")).click();
                    links = Jsoup.connect(driver.getCurrentUrl()).userAgent(userAgent).get().select("a");
                } else {
                    emptyPage(sheet);
                    continue;
                }
            }

            for (Element link : links) {
                ArrayList<String> urls = checkMatchingRequest(link, request);

                if (urls == null) continue;

                String gUrl = urls.get(0);
                String url = urls.get(1);

                writeToFile(gUrl, url, sheet, sheetYoutube);

                counterOfFoundRes++;
            }

            emptyPage(sheet);
        }

        driver.close();
    }

    /** This method returns element that matches to the request */
    public ArrayList<String> checkMatchingRequest (Element link, String request) {
        ArrayList<String> urls = new ArrayList<>();

        String title = link.text();
        String alternativeRequest = "";
        if (request.toLowerCase().contains("ё")) {
            alternativeRequest = request.toLowerCase().replaceAll("ё", "е");
        }

        if (title.toLowerCase().contains(request.toLowerCase()) || (!alternativeRequest.isEmpty() && title.toLowerCase().contains(alternativeRequest))) {
            urls.add(link.absUrl("href")); // absUrl("href") - Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
            try {
                urls.add(URLDecoder.decode(urls.get(0).substring(urls.get(0).indexOf('=') + 1, urls.get(0).indexOf('&')), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                anInterface.setStopped(true);
                anInterface.getStatus().setText(res.getString("decoding.issue"));
                anInterface.getStatus().setForeground(Color.RED);
            }
            return urls;
        } else {
            return null;
        }
    }

    public void writeToFile (String gUrl, String url, WritableSheet sheet, WritableSheet sheetYoutube) throws InterruptedException {
        if (gUrl.contains("youtube")) {
            writeToXlsFile(sheetYoutube, url, lineNumberYoutube, null);
            lineNumberYoutube++;
        } else if (whiteList != null) {
            if (!checkPlayer(url)) {
                writeToXlsFile(sheet, gUrl, lineNumber, null);
                lineNumber++;
            }
        } else {
            writeToXlsFile(sheet, gUrl, lineNumber, null);
            lineNumber++;
        }
    }

    /** This method checks existence of any matches and writes information message to the file */
    public void emptyPage (WritableSheet sheet) {
        if (counterOfFoundRes == 0) {
            WritableFont cellFont = new WritableFont(WritableFont.ARIAL, 10);
            try {
                cellFont.setColour(Colour.RED);
            } catch (WriteException e) {
                e.printStackTrace();
            }
            WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
            writeToXlsFile(sheet, res.getString("empty.result"), lineNumber, cellFormat);

            lineNumber++;
        }
    }

    /** This method checks whether link contain names of the sites that are specified in the white list and skip them */
    public boolean checkPlayer(String url) throws InterruptedException {
        for (String item : whiteList) {
            if (url.contains(item)) return true;
        }

        /** In this part program goes to every link and find all iframe elements and check if they equal to white list items. Don't work properly. I suppose there should be used multithreading. */
        /*Elements iframes = null;
        try {
            iframes = Jsoup.connect(url).userAgent(userAgent).get().select("iframe");
        } catch (IOException e) {
            anInterface.getStatus().setText(res.getString("connection.issue"));
            anInterface.getStatus().setForeground(Color.RED);
        }

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

    /** This method writes to output data to XLS file */
    public void writeToXlsFile(WritableSheet sheet, String gUrl, int lineNumber, WritableCellFormat cellFormat) {
        jxl.write.Label cell;
        if (cellFormat != null) {
            cell = new jxl.write.Label(1, lineNumber, gUrl, cellFormat);
        } else {
            cell = new jxl.write.Label(1, lineNumber, gUrl);
        }

        try {
            sheet.addCell(cell);
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }
}