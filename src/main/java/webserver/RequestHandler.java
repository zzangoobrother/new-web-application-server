package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
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
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            String line = br.readLine();
            log.debug("BufferedReader line : {}", line);

            if (line == null) {
                return;
            }

            String[] tokens = line.split(" ");
            int bodyLength = 0;
            while (!line.equals("")) {
                line = br.readLine();
                log.debug("request header : {}", line);
                if (line.startsWith("Content-Length")) {
                    bodyLength = getContentLength(line);
                }
            }

            String queryString = IOUtils.readData(br, bodyLength);
            Map<String, String> params = HttpRequestUtils.parseQueryString(queryString);

            String url = tokens[1];

            if (url.startsWith("/user/create")) {
                // get 방식
//                int index = url.indexOf("?");
//                String queryString = url.substring(index + 1);

                User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                DataBase.addUser(user);
                log.debug("user : {}", user);

                DataOutputStream dos = new DataOutputStream(out);
                response302Header(dos, "/index.html");
            } else if ("/user/login".startsWith(url)) {
                User user = DataBase.findUserById(params.get("userId"));

                if (user == null) {
                    responseResource(out, "/user/login_failed.html");
                    return;
                }

                if (user.getPassword().equals(params.get("password"))) {
                    DataOutputStream dos = new DataOutputStream(out);
                    respons302LoginSuccessHeader(dos);
                } else {
                    responseResource(out, "/user/login_failed.html");
                }
            } else {
                responseResource(out, url);
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private int getContentLength(String line) {
        return Integer.parseInt(line.split(":")[1].trim());
    }

    private void responseResource(OutputStream out, String url) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        response200Header(dos, body.length);
        responseBody(dos, body);
    }

    private void respons302LoginSuccessHeader(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Set-Cookie: logined=true \r\n");
            dos.writeBytes("Location: /index.html \r\n");
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

    private void response302Header(DataOutputStream dos, String url) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: " + url + " \r\n");
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
