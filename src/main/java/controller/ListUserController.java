package controller;

import db.DataBase;
import model.User;
import util.HttpRequest;
import util.HttpRequestUtils;
import util.HttpResponse;

import java.util.Collection;
import java.util.Map;

public class ListUserController extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        if (!isLogin(request.getHeader("Cookie"))) {
            response.forward("/user/login.html");
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

        response.forwardBody(sb.toString());
    }

    public boolean isLogin(String cookieValue) {
        Map<String, String> cookie= HttpRequestUtils.parseCookies(cookieValue);
        String logined = cookie.get("logined");

        if (logined == null) {
            return false;
        }
        return Boolean.parseBoolean(logined);
    }
}
