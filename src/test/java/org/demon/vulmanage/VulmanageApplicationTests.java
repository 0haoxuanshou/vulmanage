package org.demon.vulmanage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VulmanageApplicationTests {

    @Test
    void contextLoads() {
    }

    private static final Safelist RICH_TEXT_SAFELIST = Safelist.basic()
            .addTags("h1", "h2", "h3", "h4", "h5", "h6")
            .addAttributes("a", "href", "title")
            .addAttributes("img", "src", "alt", "title", "width", "height")
            .addAttributes("blockquote", "cite")
            .addAttributes("table", "border", "cellpadding", "cellspacing")
            .addAttributes("td", "colspan", "rowspan")
            .addAttributes("th", "colspan", "rowspan")
            .addProtocols("a", "href", "http", "https", "mailto")
            .addProtocols("blockquote", "cite", "http", "https");

    public static void main(String[] args) {
        String html = "<html><body>" +
                "<img src=\"image1.jpg\" alt=\"图片1\">" +
                "<img src=\"/images/image2.png\" alt=\"图片2\">" +
                "<img src=\"https://example.com/image3.gif\" alt=\"图片3\">" +
                "</body></html>";



        // 输出处理后的HTML
        System.out.println(Jsoup.clean(html, RICH_TEXT_SAFELIST));
    }


}
