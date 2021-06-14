package util;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

public class HttpRequestTest {
    private String testDirectory = "./src/test/resources/";

    @Test
    public void request_GET() throws Exception {
        InputStream in = new FileInputStream(testDirectory + "Http_GET.txt");

        HttpRequest request = new HttpRequest(in);

        assertEquals(request.getMethod(), HttpMethod.GET);
        assertEquals(request.getPath(), "/user/create");
        assertEquals(request.getHeader("Connection"), "keep-alive");
        assertEquals(request.getParameter("userId"), "seonkang");
    }

    @Test
    public void request_POST() throws Exception {
        InputStream in = new FileInputStream(testDirectory + "Http_POST.txt");

        HttpRequest request = new HttpRequest(in);

        assertEquals(request.getMethod(), HttpMethod.POST);
        assertEquals(request.getPath(), "/user/create");
        assertEquals(request.getHeader("Connection"), "keep-alive");
        assertEquals(request.getParameter("userId"), "seonkang");
    }
}
