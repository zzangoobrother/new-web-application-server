package webserver;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequest;
import util.HttpRequestUtils;
import util.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;

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
            HttpRequest httpRequest = new HttpRequest(in);
            HttpResponse httpResponse = new HttpResponse(out);
            String path = getDefaultPath(httpRequest.getPath());

            if ("/user/create".equals(path)) {
                User user = new User(
                        httpRequest.getParameter("userId"),
                        httpRequest.getParameter("password"),
                        httpRequest.getParameter("name"),
                        httpRequest.getParameter("email")
                );
                DataBase.addUser(user);
                log.debug("user : {}", user);

                httpResponse.sendRedirect("/index.html");
            } else if ("/user/login".startsWith(path)) {
                User user = DataBase.findUserById(httpRequest.getParameter("userId"));

                if (user == null) {
                    httpResponse.forward("/user/login_failed.html");
                    return;
                }

                if (user.getPassword().equals(httpRequest.getParameter("password"))) {
                    httpResponse.addHeader("Set-Cookie", "logined=true");
                    httpResponse.sendRedirect("/index.html");
                } else {
                    httpResponse.forward("/user/login_failed.html");
                }
            } else if ("/user/list".equals(path)) {
                if (!isLogin(httpRequest.getHeader("Cookie"))) {
                    httpResponse.forward("/user/login.html");
                    return;
                }

                Collection<User> users = DataBase.findAll();
                StringBuilder sb = new StringBuilder();
                sb.append("<table border='1'>");

                for (User user : users) {
                    sb.append("<tr>");
                    sb.append("<td>" + user.getUserId() + "</td>");
                    sb.append("<td>" + user.getName() + "</td>");
                    sb.append("<td>" + user.getEmail() + "</td>");
                    sb.append("</tr>");
                }

                sb.append("</table>");

                httpResponse.forwardBody(sb.toString());
            } else {
                httpResponse.forward(path);
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private boolean isLogin(String cookieValue) {
        Map<String, String> cookie= HttpRequestUtils.parseCookies(cookieValue);
        String logined = cookie.get("logined");

        if (logined == null) {
            return false;
        }
        return Boolean.parseBoolean(logined);
    }

    private String getDefaultPath(String path) {
        if (path.equals("/")) {
            return "/index.html";
        }
        return path;
    }
}
