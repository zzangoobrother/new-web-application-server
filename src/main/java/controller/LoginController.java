package controller;

import db.DataBase;
import util.HttpSession;
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

        if (user.login(request.getParameter("password"))) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            response.sendRedirect("/index.html");
        } else {
            response.forward("/user/login_failed.html");
        }
    }
}
