package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            InputStreamReader reader = new InputStreamReader(in, "UTF-8");
            BufferedReader br = new BufferedReader(reader);
            String line = br.readLine();
            String url = HttpRequestUtils.getUrl(line);
            boolean login = false;
            int contentLen = 0;
            while (!"".equals(line)){
                if (line == null)
                    return;
                if (line.contains("Content-Length")){
                    contentLen = Integer.parseInt(HttpRequestUtils.getUrl(line));
                }
                if (line.contains("Cookie"))
                    login = isLogined(line);
                log.debug("header : {}", line);

                line = br.readLine();
            }
            //회원가입
            if (url.contains("/user/create")){
                String path = IOUtils.readData(br, contentLen);
                Map<String, String> params = HttpRequestUtils.parseQueryString(path);
                User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                log.debug("User : {}", user);
                DataBase.addUser(user);

                DataOutputStream dos = new DataOutputStream(out);
                response302Header(dos);
            }
            //로그인
            else if(url.equals("/user/login")){
                String path = IOUtils.readData(br, contentLen);
                Map<String, String> params = HttpRequestUtils.parseQueryString(path);
                log.debug("UserID : {}, UserPW : {}", params.get("userId"), params.get("password"));
                User user = DataBase.findUserById(params.get("userId"));
                if (user == null) {
                    log.debug("User Not Found!");
                    responseResource(out, "/user/login_failed.html");

                }
                if (user.getPassword().equals(params.get("password"))){
                    log.debug("Success!");
                    DataOutputStream dos = new DataOutputStream(out);
                    response302HeaderLoginSuccess(dos);
                }
                else {
                    log.debug("User Not Found");
                    responseResource(out, "/user/login_failed.html");
                }
            }
            //리스트 보여주기
            else if("/user/list".equals(url)){
                if (!login){
                    responseResource(out, "/user/login.html");
                    return;
                }
                Collection<User> users = DataBase.findAll();
                StringBuilder sb = new StringBuilder();
                sb.append("<table border='1'>");
                for (User user : users){
                    sb.append("<tr>");
                    sb.append("<td>" + user.getUserId() + "</td>");
                    sb.append("<td>" + user.getName() + "</td>");
                    sb.append("<td>" + user.getEmail() + "</td>");
                    sb.append("</tr>");
                }
                sb.append("</table>");
                byte[] body = sb.toString().getBytes();
                DataOutputStream dos = new DataOutputStream(out);
                response200Header(dos, body.length);
                responseBody(dos, body);
            }
            else if(url.endsWith(".css")){
                DataOutputStream dos = new DataOutputStream(out);
                byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
                response200CSSHeader(dos, body.length);
                responseBody(dos, body);
            }
            else{
                responseResource(out, url);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    private boolean isLogined(String line){
        String[] tokens = line.split(":");
        Map<String, String> cookies = HttpRequestUtils.parseCookies(tokens[1].trim());
        String value = cookies.get("logined");
        if (value == null)
            return false;
        return Boolean.parseBoolean(value);
    }
    private void response200CSSHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    private void responseResource(OutputStream out, String url) throws IOException{
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        response200Header(dos, body.length);
        responseBody(dos, body);
    }
    private void response302HeaderLoginSuccess(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: /index.html \r\n");
            dos.writeBytes("Set-Cookie: logined=true \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: /index.html\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
