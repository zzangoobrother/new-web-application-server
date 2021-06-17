package controller;

import db.DataBase;
import model.User;
import util.HttpRequest;
import util.HttpResponse;

public class LoginController extends AbstractController {

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        User user = DataBase.findUserById(request.getParameter("userId"));

        if (user == null) {
            response.forward("/user/login_failed.html");
            return;
        }

        if (user.getPassword().equals(request.getParameter("password"))) {
            response.addHeader("Set-Cookie", "logined=true");
            response.sendRedirect("/index.html");
        } else {
            response.forward("/user/login_failed.html");
        }
    }
}
