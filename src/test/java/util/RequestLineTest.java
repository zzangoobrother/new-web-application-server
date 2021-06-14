package util;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class RequestLineTest {

    @Test
    public void create_method() {
        RequestLine line = new RequestLine("GET /index.html HTTP/1.1");

        assertEquals(line.getMethod(), "GET");
        assertEquals(line.getPath(), "/index.html");

        line = new RequestLine("POST /index.html HTTP/1.1");
        assertEquals(line.getMethod(), "POST");
    }

    @Test
    public void create_path_and_params() {
        RequestLine line = new RequestLine("GET /user/create?userId=seonkang&password=password&name=SeonKang& HTTP/1.1");
        assertEquals(line.getMethod(), "GET");
        assertEquals(line.getPath(), "/user/create");
        Map<String, String> params = line.getParams();
        assertEquals(params.size(), 3);
    }
}