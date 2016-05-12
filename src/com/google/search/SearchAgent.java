package com.google.search;

import jxl.Workbook;
import jxl.write.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
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
    private String fileOutputName;
    private String fileOutputNameXls;
    private int numberOfPages;
    private String qDuration;
    private String vDuration;
    private String googleLocation;
    private String attribute;
    private ArrayList<String> whiteList;
    private WritableWorkbook workbook;

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
        this.fileInputName = System.getProperty("user.dir") + "\\Input\\" + fileInputName;
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

    public void runProgram () throws IOException, InterruptedException {
        createAttribute();

        Date currentDate = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd_hh-mm-ss");
        fileOutputName = System.getProperty("user.dir") + "\\Output\\Results_" + format.format(currentDate) + ".txt";
        fileOutputNameXls = System.getProperty("user.dir") + "\\Output\\Results_" + format.format(currentDate) + ".xls";
        File file = new File(fileOutputName);
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

        try {
            file.createNewFile();
        } catch (IOException e) {
            anInterface.setStopped(true);
            anInterface.getStatus().setText(res.getString("cant.create.file"));
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

        /** For each request we create separate tab */
        for (String item : listOfRequests) {
            writer.write(String.format("========== Request: %s ==========", item));
            writer.write("\r\n");
            WritableSheet sheet = workbook.createSheet(item, 0);
            WritableSheet sheetYoutube = workbook.createSheet(item + " Youtube", 0);
            saveLinks(item, writer, sheet, sheetYoutube);
        }

        writer.close();
        try {
            workbook.write();
            workbook.close();
        } catch (WriteException e) {
            e.printStackTrace();
        }

        anInterface.getStatus().setText(res.getString("the.end"));
        anInterface.getStatus().setForeground(Color.GREEN);
    }

    public void saveLinks(String request, BufferedWriter writer, WritableSheet sheet, WritableSheet sheetYoutube) throws IOException, InterruptedException {
        int lineNumber = 1;
        int lineNumberYoutube = 1;

        for (int i = 0; i < numberOfPages; i++){
            int x = i + 1;
            jxl.write.Label cell = new jxl.write.Label(0, lineNumber, "Page #" + x);
            try {
                sheet.addCell(cell);
            } catch (WriteException e) {
                e.printStackTrace();
            }
            String pages = "&start=" + i * 10;

            try {
                writer.write(String.format("---------- Page #%s ----------", i + 1));
                writer.write("\r\n");
            } catch (IOException e) {
                anInterface.setStopped(true);
                anInterface.getStatus().setText(res.getString("cant.write.to.file"));
                anInterface.getStatus().setForeground(Color.RED);
            }

            Elements links = null;
            try {
                links = Jsoup.connect(String.format("%s%s%s%s", googleLocation, URLEncoder.encode(request, charset), attribute, pages)).userAgent(userAgent).get().select("a");
            } catch (IOException e) {

                /** Google ban handler. Should be finalized!!!!!!  */

                if (e.toString().contains("Status=503")){
                    anInterface.setStopped(true);
                    anInterface.getStatus().setText(res.getString("google.ban"));
                    anInterface.getStatus().setForeground(Color.RED);

                    /*String temp = e.toString().substring(e.toString().lastIndexOf("URL") + 4);
                    WebDriver driver = new FirefoxDriver();
                    driver.navigate().to(temp);
                    driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);

                    WebDriverWait wait = new WebDriverWait(driver, 15);
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sfdiv")));
                    if (driver.findElement(By.id("sfdiv")).isDisplayed()){
                        String link = driver.getCurrentUrl();
                        links = Jsoup.connect(link).userAgent(userAgent).get().select("a");
                    }*/
                }

            }
            int counterOfFoundRes = 0;

            for (Element link : links) {
                String title = link.text();
                String alternativeRequest = "";
                if (request.toLowerCase().contains("ё")) {
                    alternativeRequest = request.toLowerCase().replaceAll("ё", "е");
                }

                /** Check if link title matches the query */
                if (title.toLowerCase().contains(request.toLowerCase()) || (!alternativeRequest.isEmpty() && title.toLowerCase().contains(alternativeRequest))) {
                    String gUrl = link.absUrl("href"); // absUrl("href") - Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>".
                    String url = null;
                    try {
                        url = URLDecoder.decode(gUrl.substring(gUrl.indexOf('=') + 1, gUrl.indexOf('&')), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        anInterface.setStopped(true);
                        anInterface.getStatus().setText(res.getString("decoding.issue"));
                        anInterface.getStatus().setForeground(Color.RED);
                    }

                    if (gUrl.contains("youtube")) {
                        writeToXlsFile(sheetYoutube, gUrl, lineNumberYoutube, null);
                        lineNumberYoutube++;
                    } else if (whiteList != null) {
                        if (!checkPlayer(url)) {
                            try {
                                writeToTxtFile(writer, gUrl);
                            } catch (IOException e) {
                                anInterface.setStopped(true);
                                anInterface.getStatus().setText(res.getString("cant.write.to.file"));
                                anInterface.getStatus().setForeground(Color.RED);
                            }
                            writeToXlsFile(sheet, gUrl, lineNumber, null);
                            lineNumber++;
                        }
                    } else {
                        try {
                            writeToTxtFile(writer, gUrl);
                        } catch (IOException e) {
                            anInterface.setStopped(true);
                            anInterface.getStatus().setText(res.getString("cant.write.to.file"));
                            anInterface.getStatus().setForeground(Color.RED);
                        }
                        writeToXlsFile(sheet, gUrl, lineNumber, null);
                        lineNumber++;
                    }

                    counterOfFoundRes++;
                }
            }

            /** If on the page no matches, write message */
            if (counterOfFoundRes == 0) {
                try {
                    writer.write(res.getString("empty.result"));
                    writer.write("\r\n");
                } catch (IOException e) {
                    anInterface.setStopped(true);
                    anInterface.getStatus().setText(res.getString("cant.write.to.file"));
                    anInterface.getStatus().setForeground(Color.RED);
                }

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
    }

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

    public void writeToTxtFile(BufferedWriter writer, String gUrl) throws IOException {
        //writer.write(String.format("Title: %s  - Google URL: %s  - Content URL: %s", title, gUrl, url));   - old realization
        writer.write(gUrl);
        writer.write("\r\n");
    }

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