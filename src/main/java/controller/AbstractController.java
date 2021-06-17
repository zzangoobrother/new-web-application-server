package controller;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpMethod;
import util.HttpRequest;
import util.HttpResponse;
import webserver.RequestHandler;

public class AbstractController implements Controller {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        HttpMethod method = request.getMethod();

        if (method.isPost()) {
            doPost(request, response);
        } else {
            doGet(request, response);
        }
    }

    public void doPost(HttpRequest request, HttpResponse response) {

    }

    public void doGet(HttpRequest request, HttpResponse response) {

    }
}
