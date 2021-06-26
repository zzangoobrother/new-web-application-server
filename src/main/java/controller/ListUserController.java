package controller;

import db.DataBase;
import util.HttpSession;
import model.User;
import util.HttpRequest;
import util.HttpResponse;

import java.util.Collection;

public class ListUserController extends AbstractController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        if (!isLogin(request.getSession())) {
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

    public boolean isLogin(HttpSession session) {
        Object user = session.getAttribute("user");

        if (user == null) {
            return false;
        }
        return true;
    }
}
