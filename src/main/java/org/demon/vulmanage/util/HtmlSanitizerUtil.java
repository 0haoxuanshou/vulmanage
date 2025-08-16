package org.demon.vulmanage.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

/**
 * HTML内容清理工具类
 * 使用jsoup库防护XSS攻击，清理用户输入的HTML内容
 */
@Component
public class HtmlSanitizerUtil {

    /**
     * 富文本编辑器安全白名单
     * 允许常用的富文本标签和属性
     */
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

    /**
     * 基础文本安全白名单
     * 只允许基本的文本格式化标签
     */
    private static final Safelist BASIC_TEXT_SAFELIST = Safelist.basic();

    /**
     * 清理富文本内容
     * 适用于富文本编辑器输入的内容，使用jsoup安全白名单过滤危险内容
     *
     * @param html 原始HTML内容
     * @return 清理后的安全HTML内容
     */
    public String sanitizeRichText(String html) {
        if (html == null || html.trim().isEmpty()) {
            return html;
        }

        // 使用jsoup的安全白名单清理HTML内容
        return Jsoup.clean(html, RICH_TEXT_SAFELIST);
    }

    /**
     * 清理基础文本内容
     * 适用于普通文本输入，使用jsoup基础安全白名单过滤
     *
     * @param html 原始HTML内容
     * @return 清理后的安全HTML内容
     */
    public String sanitizeBasicText(String html) {
        if (html == null || html.trim().isEmpty()) {
            return html;
        }

        // 使用jsoup的基础安全白名单清理HTML内容
        return Jsoup.clean(html, BASIC_TEXT_SAFELIST);
    }

    /**
     * 完全移除HTML标签
     * 将HTML内容转换为纯文本
     *
     * @param html 原始HTML内容
     * @return 纯文本内容
     */
    public String stripHtml(String html) {
        if (html == null || html.trim().isEmpty()) {
            return html;
        }

        // 使用jsoup移除所有HTML标签，只保留文本内容
        return Jsoup.clean(html, Safelist.none());
    }

    /**
     * 检查HTML内容是否包含潜在的XSS攻击
     * 通过比较原始内容和清理后的内容来判断
     *
     * @param html 原始HTML内容
     * @return 如果内容被修改（包含潜在攻击），返回true
     */
    public boolean containsPotentialXss(String html) {
        if (html == null || html.trim().isEmpty()) {
            return false;
        }

        // 使用富文本安全白名单清理内容
        String sanitized = sanitizeRichText(html);
        return !html.equals(sanitized);
    }

    /**
     * 转义HTML特殊字符
     * 提供向后兼容的HTML转义功能
     *
     * @param input 原始字符串
     * @return 转义后的字符串
     */
    public String escapeHtml(String input) {
        if (input == null) {
            return null;
        }

        // 使用jsoup进行HTML转义
        return Jsoup.clean(input, Safelist.none()).replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}