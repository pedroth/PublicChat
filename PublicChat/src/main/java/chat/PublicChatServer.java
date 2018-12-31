package chat;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import utils.*;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class PublicChatServer {
    private final static String HOME_ADDRESS = "resources/";
    private final static String DATA_ADDRESS = HOME_ADDRESS + "data/";
    // time in seconds
    private final static double TIMEOUT = 10;
    private final int serverPort;
    private List<UnitLog> logList = new ArrayList<>();
    private Map<String, Double> uID2TimeMap = new ConcurrentHashMap<>();
    private StopWatch stopWatch;

    PublicChatServer(int serverPort) {
        this.serverPort = serverPort;
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            final String regex = "[0-9]*";
            final Pattern pattern = Pattern.compile(regex);
            final Matcher matcher = pattern.matcher(args[0]);
            PublicChatServer publicChatServer = new PublicChatServer(matcher.find() ? Integer.valueOf(args[0]) : 8080);
            publicChatServer.start();
        } else {
            PublicChatServer publicChatServer = new PublicChatServer(8080);
            publicChatServer.start();
        }
    }

    void start() {
        this.stopWatch = new StopWatch();

        //check for dead clients
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
            double dt = stopWatch.getEleapsedTime();
            stopWatch.resetTime();
            for (String id : uID2TimeMap.keySet()) {
                Double t = uID2TimeMap.get(id);
                if (t > TIMEOUT) {
                    uID2TimeMap.remove(id);
                } else {
                    uID2TimeMap.put(id, t + dt);
                }
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        try {
            InetSocketAddress inetSocketAddress = new InetSocketAddress(this.serverPort);
            log.info("Start public chat server at : http://" + InetAddress.getLocalHost().getHostAddress() + ":" + this.serverPort + "/PublicChat");

            HttpServer httpServer = HttpServer.create(inetSocketAddress, 0);
            createServices(httpServer);
            httpServer.setExecutor(Executors.newCachedThreadPool());
            httpServer.start();
        } catch (IOException e) {
            log.debug("Exception Caught:", e);
        }
    }

    private void createServices(HttpServer httpServer) {
        httpServer.createContext("/PublicChat", httpExchange -> {
            try {
                printClientData(httpExchange);
                String file = parseGetInput(httpExchange.getRequestURI().toString());
                if ("/PublicChat".equals(file)) {
                    file = "PublicChat.html";
                    JServerUtils.respondWithTextFile(httpExchange, HOME_ADDRESS + file);
                } else {
                    JServerUtils.respondWithBytes(httpExchange, HOME_ADDRESS + file);
                }
            } catch (IOException e) {
                JServerUtils.respondWithText(httpExchange, "<p>" + getTraceError(e) + "<p>");
            }
        });

        httpServer.createContext("/chat", httpExchange -> {
            try {
                printClientData(httpExchange);
                TextIO textIO = new TextIO();
                textIO.read(httpExchange.getRequestBody());
                String text = textIO.getText();
                if ("".equals(text)) {
                    throw new RuntimeException("Empty String");
                }
                Map<String, String> stringMap = JServerUtils.parsePostMessage(text);
                for (Map.Entry<String, String> entry : stringMap.entrySet()) {
                    log.info(entry.getKey() + " : " + entry.getValue());
                }
                String id = stringMap.get("id");
                putClientInMap(id);
                String jsonAns = getLogInfo(Integer.valueOf(stringMap.get("index").replaceAll("\n", "")));
                log.info(jsonAns);
                JServerUtils.respondWithText(httpExchange, jsonAns);
            } catch (Exception e) {
                JServerUtils.respondWithText(httpExchange, "<p>" + getTraceError(e) + "<p>");
            }
        });

        httpServer.createContext("/putText", httpExchange -> {
            try {
                printClientData(httpExchange);

                TextIO textIO = new TextIO();
                textIO.read(httpExchange.getRequestBody());
                Map<String, String> stringMap = JServerUtils.parsePostMessageUnformatted(textIO.getText());
                for (Map.Entry<String, String> entry : stringMap.entrySet()) {
                    log.info(entry.getKey() + " : " + entry.getValue());
                }
                String id = stringMap.get("id");
                putClientInMap(id);
                this.logList.add(new UnitLog(id, stringMap.get("log")));
                JServerUtils.respondWithText(httpExchange, "OK");
            } catch (Exception e) {
                JServerUtils.respondWithText(httpExchange, "<p>" + getTraceError(e) + "<p>");
            }
        });

        httpServer.createContext("/clear", httpExchange -> {
            try {
                printClientData(httpExchange);
                TextIO textIO = new TextIO();
                textIO.read(httpExchange.getRequestBody());
                Map<String, String> stringMap = JServerUtils.parsePostMessage(textIO.getText());
                for (Map.Entry<String, String> entry : stringMap.entrySet()) {
                    log.info(entry.getKey() + " : " + entry.getValue());
                }
                this.logList.removeAll(this.logList);
                removeData();
                JServerUtils.respondWithText(httpExchange, "OK");
            } catch (Exception e) {
                JServerUtils.respondWithText(httpExchange, "<p>" + getTraceError(e) + "<p>");
            }
        });

        httpServer.createContext("/upload", httpExchange -> {
            try {
                printClientData(httpExchange);
                String fileName = uploadFile(httpExchange);
                JServerUtils.respondWithText(httpExchange, fileName);
            } catch (Exception e) {
                JServerUtils.respondWithText(httpExchange, "<p>" + getTraceError(e) + "<p>");
            }
        });
    }

    private void removeData() {
        FilesCrawler.applyFiles(DATA_ADDRESS, File::delete);
    }

    private String uploadFile(HttpExchange he) throws IOException {
        int readByte;
        final InputStream requestBody = he.getRequestBody();
        SMUpload smUpload = new SMUpload();
        while ((readByte = requestBody.read()) != -1) {
            smUpload.next(readByte);
        }
        String fileName = smUpload.getFileName();

        if (fileName == null) throw new RuntimeException("No file name");

        FilesCrawler.createDirs(DATA_ADDRESS);
        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(DATA_ADDRESS + fileName))) {
            StopWatch stopWatch = new StopWatch();
            smUpload.getData().forEach(ConsumerWithException.wrap(outputStream::write));
            log.info("<<<<<  " + stopWatch.getEleapsedTime() + "  >>>>>");
        }
        return fileName;
    }

    private String getLogInfo(int index) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("{ \"users\": [");
        String[] uids = this.uID2TimeMap.keySet().toArray(new String[this.uID2TimeMap.size()]);
        for (int i = 0; i < uids.length; i++) {
            stringBuilder.append("\"" + uids[i] + "\"" + ((i == uids.length - 1) ? "" : ","));
        }

        stringBuilder.append("],");
        stringBuilder.append("\"log\": [");
        for (int i = index + 1; i < this.logList.size(); i++) {
            UnitLog unitLog = this.logList.get(i);
            stringBuilder.append("{\"id\": \"" + unitLog.getId() + "\", \"text\":\"" + unitLog.getText() + "\"}" + ((i == this.logList.size() - 1) ? "" : ","));
        }

        stringBuilder.append("],");
        stringBuilder.append("\"needClean\": ");
        stringBuilder.append("" + (index >= this.logList.size()) + "");
        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    private String parseGetInput(String request) throws UnsupportedEncodingException {
        return URLDecoder.decode(request, StandardCharsets.UTF_8.toString()).replace("/PublicChat/", "");
    }


    private String getTraceError(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        e.printStackTrace();
        return sw.toString();
    }

    private void printClientData(HttpExchange httpExchange) {
        URI requestURI = httpExchange.getRequestURI();
        log.info(httpExchange.getRequestMethod() + " " + requestURI);
        log.info("address : " + httpExchange.getRemoteAddress());
    }

    private void putClientInMap(String id) {
        this.uID2TimeMap.put(id, 0.0);
    }
}