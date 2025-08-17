package org.demon.vulmanage.plugins.docx;

import cn.hutool.core.util.ReUtil;
import com.deepoove.poi.data.ByteArrayPictureRenderData;
import com.deepoove.poi.data.PictureRenderData;
import com.deepoove.poi.data.PictureType;
import com.deepoove.poi.data.TextRenderData;
import com.deepoove.poi.data.style.PictureStyle;
import com.deepoove.poi.policy.AbstractRenderPolicy;
import com.deepoove.poi.policy.PictureRenderPolicy;
import com.deepoove.poi.policy.TextRenderPolicy;
import com.deepoove.poi.render.RenderContext;
import com.deepoove.poi.util.TableTools;
import com.deepoove.poi.xwpf.NiceXWPFDocument;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.demon.vulmanage.config.MinioConfig;
import org.demon.vulmanage.service.MinioService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.regex.Pattern;

@Component
public class HtmlRenderPolicy extends AbstractRenderPolicy {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    MinioService minioService;

    @Autowired
    MinioConfig minioConfig;

    @Override
    protected void afterRender(RenderContext context) {
        // 清空模板标签所在段落
        clearPlaceholder(context, true);
    }

    @Override
    public void doRender(RenderContext renderContext) throws Exception {
        if (renderContext.getData() == null || StringUtils.isBlank(renderContext.getData().toString())) {
            return;
        }

        //获得Apache POI增强类NiceXWPFDocument
        NiceXWPFDocument doc = renderContext.getXWPFDocument();


        String html = renderContext.getData().toString();
        html = html.replaceAll("&gt;", ">")
                .replaceAll("&lt;", "<")
                .replaceAll("&nbsp;", " ")
                .replaceAll("\\n", "");
        org.jsoup.nodes.Document htmlDoc = Jsoup.parse(html);
        Elements nodes = htmlDoc.body().children();
        XWPFParagraph whereParagraph = (XWPFParagraph) renderContext.getRun().getParent();
        XWPFParagraph xwpfParagraph = doc.insertNewParagraph(whereParagraph.getCTP().newCursor());

        for (Element e : nodes) {
            xwpfParagraph = parseHtmlToWord(e, doc, xwpfParagraph, true);
        }
    }

    /**
     * 转换整个html内容为word内容
     *
     * @param ele           ele
     * @param doc           doc
     * @param xwpfParagraph xwpfParagraph
     * @return {@link XWPFParagraph}
     */
    private XWPFParagraph parseHtmlToWord(Element ele, NiceXWPFDocument doc, XWPFParagraph xwpfParagraph
            , boolean isParent) throws Exception {

        //处理img图片
        if ("img".equals(ele.tagName())) {
            String url = ele.attr("src");
            String pattern = "(data:.*;base64,)(.*)";
            if (Pattern.matches(pattern, url)) {
                parseImgToWord(ele.attr("src"), xwpfParagraph);
                return xwpfParagraph;
            }
            url = url.toLowerCase();
            if (url.endsWith(".jpg") || url.endsWith(".png") || url.endsWith(".jpeg") || url.endsWith(".gif") || url.endsWith(".bmp")) {
                parseImgToWord(ele.attr("src"), xwpfParagraph);
                return xwpfParagraph;
            }
        }

        //处理table标签
        if ("table".equals(ele.tagName())) {
            xwpfParagraph = doc.insertNewParagraph(getPrevXWPFParagraph(doc, xwpfParagraph).getCTP().newCursor());
            parseTableToWord(doc, ele, xwpfParagraph.createRun());
            //有表格的话新建段落
            //xwpfParagraph = doc.createParagraph();
            return xwpfParagraph;
        }

        //处理其他文本标签
        String text = ele.ownText();
        boolean continueItr = true;
        //span标签默认全部为文字，不再继续迭代
        if ("span".equalsIgnoreCase(ele.tagName())) {
            text = ele.wholeText();
            continueItr = false;
        }

//        boolean enabledBreak = (isParent || StringUtils.isNotBlank(ele.text()))
//                && ReUtil.isMatch("(p|h[12345]|li|img)", ele.tagName());
        boolean enabledBreak = ReUtil.isMatch("(p|h[12345]|li|img|br|figure)", ele.tagName());
        if (enabledBreak) {
            XWPFRun run = xwpfParagraph.createRun();
            run.addBreak();
        }

        if (StringUtils.isNotBlank(text)) {
            XWPFRun run = xwpfParagraph.createRun();
            TextRenderPolicy.Helper.renderTextRun(run, new TextRenderData(text));
        }


        if (continueItr && !ele.children().isEmpty()) {
            for (Element me : ele.children()) {
                xwpfParagraph = parseHtmlToWord(me, doc, xwpfParagraph, false);
            }
        }

        return xwpfParagraph;
    }

    private XWPFParagraph getPrevXWPFParagraph(NiceXWPFDocument doc, XWPFParagraph xwpfParagraph) {
        List<XWPFParagraph> xwpfParagraphs = doc.getXWPFDocument().getParagraphs();
        for (int i = 0; i < xwpfParagraphs.size(); i++) {
            if (xwpfParagraphs.get(i).equals(xwpfParagraph)) {
                return xwpfParagraphs.get(i + 1);
            }
        }

        return xwpfParagraph;
    }


    /**
     * 转换图片为word内容
     *
     * @param imgUrl        imgUrl
     * @param xwpfParagraph xwpfParagraph
     * @author xuwangcheng
     * @date 2019/7/29 18:45
     */
    private void parseImgToWord(String imgUrl, XWPFParagraph xwpfParagraph) throws Exception {
        //获取图片本地路径
        BufferedImage img = getImgRealPath(imgUrl);

        // 如果获取图片失败，直接返回，不进行图片插入处理
        if (img == null) {
            return;
        }

        //获得图片的宽
        int width = img.getWidth();
        //获得图片的高
        int height = img.getHeight();
        if (width > 600) {
            //获取比例
            int rate = (width / 600) + 1;
            width = width / rate - 20;
            height = height / rate;
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", out);
            PictureRenderData pictureRenderData = new ByteArrayPictureRenderData(out.toByteArray(), PictureType.PNG);
            PictureStyle pictureStyle = new PictureStyle();
            pictureStyle.setWidth(width);
            pictureStyle.setHeight(height);
            pictureRenderData.setPictureStyle(pictureStyle);
            XWPFRun run = xwpfParagraph.createRun();
            PictureRenderPolicy.Helper.renderPicture(run, pictureRenderData);
        }
    }


    /**
     * 通过imgUrl获取图片BufferedImage对象
     *
     * @param imgUrl 图片URL，支持base64、http/https链接、相对路径
     * @return {@link BufferedImage} 获取失败时返回null
     * @author xuwangcheng
     * @date 2019/11/21 9:47
     */
    public BufferedImage getImgRealPath(String imgUrl) {
        try {
            // 处理base64格式图片
            String pattern = "(data:.*;base64,)(.*)";
            if (Pattern.matches(pattern, imgUrl)) {
                String base64Data = imgUrl;
                if (StringUtils.startsWith(imgUrl, "data:")) {
                    base64Data = StringUtils.split(imgUrl, ',')[1];
                }
                // base64转BufferedImage
                byte[] bytes = Base64.getDecoder().decode(base64Data);
                ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                return ImageIO.read(bais);
            }

            // 处理http/https链接
            if (imgUrl.startsWith("http://") || imgUrl.startsWith("https://")) {
                ResponseEntity<byte[]> response = restTemplate.exchange(imgUrl, HttpMethod.GET, new HttpEntity<>(null), byte[].class);
                if (response.getBody() != null) {
                    ByteArrayInputStream bais = new ByteArrayInputStream(response.getBody());
                    return ImageIO.read(bais);
                }
                return null;
            }

            // 处理相对路径，通过MinIO获取图片
            String trimmedUrl;
            String prefix = "/" + minioConfig.getBucketName();
            if (imgUrl.startsWith(prefix)) {
                // 从前缀长度的位置开始截取（移除前缀）
                trimmedUrl = imgUrl.substring(prefix.length());
            } else {
                // 若不包含前缀，返回原URL
                trimmedUrl = imgUrl;
            }
            byte[] imageBytes = minioService.getFileBytes(trimmedUrl);
            if (imageBytes != null) {
                ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
                return ImageIO.read(bais);
            }

            return null;

        } catch (Exception e) {
            // 发生异常时返回null，不抛出异常
            return null;
        }
    }


    /**
     * 转换表格为word内容
     *
     * @param doc doc
     * @param ele ele
     * @author xuwangcheng
     * @date 2019/7/29 18:45
     */
    private void parseTableToWord(NiceXWPFDocument doc, Element ele, XWPFRun run) throws Exception {
        //简化表格html
        org.jsoup.nodes.Document tableDoc = Jsoup.parse(simplifyTable(ele.outerHtml()));
        Elements trList = tableDoc.getElementsByTag("tr");
        Elements tdList = trList.get(0).getElementsByTag("td");

        //创建表格
        XWPFTable xwpfTable = doc.insertNewTbl(run.getCTR().newCursor());

        //设置样式
        //poi-tl 1.8.2 升级到1.9.1版本后，不提供TableRenderData.WIDTH_A4_FULL
        //该方法为设置table宽度，不设置则使用默认值。
        //TableTools.widthTable(xwpfTable, TableRenderData.WIDTH_A4_FULL, tdList.size());
        TableTools.borderTable(xwpfTable, 4);

        //写入表格行和列内容
        Map<String, Boolean>[][] array = new Map[trList.size()][tdList.size()];
        for (int row = 0; row < trList.size(); row++) {
            Element trElement = trList.get(row);
            Elements tds = trElement.getElementsByTag("td");
            for (int col = 0; col < tds.size(); col++) {
                Element colElement = tds.get(col);
                String colspan = colElement.attr("colspan");
                String rowspan = colElement.attr("rowspan");
                String style = colElement.attr("style");
                StringBuilder styleSB = new StringBuilder();
                if (!StringUtils.isEmpty(colspan)) {
                    int colCount = Integer.parseInt(colspan);
                    for (int i = 0; i < colCount - 1; i++) {
                        array[row][col + i + 1] = new HashMap<String, Boolean>();
                        array[row][col + i + 1].put("mergeCol", true);
                    }
                }
                if (!StringUtils.isEmpty(rowspan)) {
                    int rowCount = Integer.parseInt(rowspan);
                    for (int i = 0; i < rowCount - 1; i++) {
                        array[row + i + 1][col] = new HashMap<String, Boolean>();
                        array[row + i + 1][col].put("mergeRow", true);
                    }
                }
                XWPFTableCell tableCell = xwpfTable.getRow(row).getCell(col);
                if (StringUtils.isEmpty(colspan)) {
                    if (col == 0) {
                        if (tableCell.getCTTc().getTcPr() == null) {
                            tableCell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
                        } else {
                            if (tableCell.getCTTc().getTcPr().getHMerge() == null) {
                                tableCell.getCTTc().getTcPr().addNewHMerge().setVal(STMerge.RESTART);
                            } else {
                                tableCell.getCTTc().getTcPr().getHMerge().setVal(STMerge.RESTART);
                            }
                        }
                    } else {
                        if (array[row][col] != null && array[row][col].get("mergeCol") != null && array[row][col].get("mergeCol")) {
                            if (tableCell.getCTTc().getTcPr() == null) {
                                tableCell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
                            } else {
                                if (tableCell.getCTTc().getTcPr().getHMerge() == null) {
                                    tableCell.getCTTc().getTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
                                } else {
                                    tableCell.getCTTc().getTcPr().getHMerge().setVal(STMerge.CONTINUE);
                                }
                            }
                            continue;
                        } else {
                            if (tableCell.getCTTc().getTcPr() == null) {
                                tableCell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
                            } else {
                                if (tableCell.getCTTc().getTcPr().getHMerge() == null) {
                                    tableCell.getCTTc().getTcPr().addNewHMerge().setVal(STMerge.RESTART);
                                } else {
                                    tableCell.getCTTc().getTcPr().getHMerge().setVal(STMerge.RESTART);
                                }
                            }
                        }
                    }
                } else {
                    if (tableCell.getCTTc().getTcPr() == null) {
                        tableCell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
                    } else {
                        if (tableCell.getCTTc().getTcPr().getHMerge() == null) {
                            tableCell.getCTTc().getTcPr().addNewHMerge().setVal(STMerge.RESTART);
                        } else {
                            tableCell.getCTTc().getTcPr().getHMerge().setVal(STMerge.RESTART);
                        }
                    }
                }
                if (StringUtils.isEmpty(rowspan)) {
                    if (array[row][col] != null && array[row][col].get("mergeRow") != null && array[row][col].get("mergeRow")) {
                        if (tableCell.getCTTc().getTcPr() == null) {
                            tableCell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
                        } else {
                            if (tableCell.getCTTc().getTcPr().getVMerge() == null) {
                                tableCell.getCTTc().getTcPr().addNewVMerge().setVal(STMerge.CONTINUE);
                            } else {
                                tableCell.getCTTc().getTcPr().getVMerge().setVal(STMerge.CONTINUE);
                            }
                        }
                        continue;
                    } else {
                        if (tableCell.getCTTc().getTcPr() == null) {
                            tableCell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
                        } else {
                            if (tableCell.getCTTc().getTcPr().getVMerge() == null) {
                                tableCell.getCTTc().getTcPr().addNewVMerge().setVal(STMerge.RESTART);
                            } else {
                                tableCell.getCTTc().getTcPr().getVMerge().setVal(STMerge.RESTART);
                            }
                        }
                    }
                } else {
                    if (tableCell.getCTTc().getTcPr() == null) {
                        tableCell.getCTTc().addNewTcPr().addNewVMerge().setVal(STMerge.RESTART);
                    } else {
                        if (tableCell.getCTTc().getTcPr().getVMerge() == null) {
                            tableCell.getCTTc().getTcPr().addNewVMerge().setVal(STMerge.RESTART);
                        } else {
                            tableCell.getCTTc().getTcPr().getVMerge().setVal(STMerge.RESTART);
                        }
                    }
                }
                tableCell.removeParagraph(0);
                XWPFParagraph paragraph = tableCell.addParagraph();
                paragraph.setStyle(styleSB.toString());
                if (!StringUtils.isEmpty(style) && style.contains("text-align:center")) {
                    paragraph.setAlignment(ParagraphAlignment.CENTER);
                }

                parseHtmlToWord(colElement, doc, paragraph, true);
            }
        }
    }

    /**
     * 简化html中的表格dom
     *
     * @param tableContent tableContent
     * @return {@link String}
     * @author xuwangcheng
     * @date 2019/7/29 18:39
     */
    private static String simplifyTable(String tableContent) {
        if (StringUtils.isEmpty(tableContent)) {
            return null;
        }
        org.jsoup.nodes.Document tableDoc = Jsoup.parse(tableContent);
        Elements trElements = tableDoc.getElementsByTag("tr");
        if (trElements != null) {
            Iterator<Element> eleIterator = trElements.iterator();
            Integer rowNum = 0;
            // 针对于colspan操作
            while (eleIterator.hasNext()) {
                rowNum++;
                Element trElement = eleIterator.next();
                //去除所有样式
                trElement.removeAttr("class");
                Elements tdElements = trElement.getElementsByTag("td");
                List<Element> tdEleList = covertElements2List(tdElements);
                for (int i = 0; i < tdEleList.size(); i++) {
                    Element curTdElement = tdEleList.get(i);
                    //去除所有样式
                    curTdElement.removeAttr("class");
                    Element ele = curTdElement.clone();
                    String colspanValStr = curTdElement.attr("colspan");
                    if (!StringUtils.isEmpty(colspanValStr)) {
                        ele.removeAttr("colspan");
                        Integer colspanVal = Integer.parseInt(colspanValStr);
                        for (int k = 0; k < colspanVal - 1; k++) {
                            curTdElement.after(ele.outerHtml());
                        }
                    }
                }
            }
            // 针对于rowspan操作
            List<Element> trEleList = covertElements2List(trElements);
            Element firstTrEle = trElements.first();
            Elements tdElements = firstTrEle.getElementsByTag("td");
            Integer tdCount = tdElements.size();
            //获取该列下所有单元格
            for (int i = 0; i < tdElements.size(); i++) {
                for (Element trElement : trEleList) {
                    List<Element> tdElementList = covertElements2List(trElement.getElementsByTag("td"));
                    try {
                        tdElementList.get(i);
                    } catch (Exception e) {
                        continue;
                    }
                    Node curTdNode = tdElementList.get(i);
                    Node cNode = curTdNode.clone();
                    String rowspanValStr = curTdNode.attr("rowspan");
                    if (!StringUtils.isEmpty(rowspanValStr)) {
                        cNode.removeAttr("rowspan");
                        Element nextTrElement = trElement.nextElementSibling();
                        Integer rowspanVal = Integer.parseInt(rowspanValStr);
                        for (int j = 0; j < rowspanVal - 1; j++) {
                            Node tempNode = cNode.clone();
                            List<Node> nodeList = new ArrayList<Node>();
                            nodeList.add(tempNode);
                            if (j > 0) {
                                nextTrElement = nextTrElement.nextElementSibling();
                            }
                            Integer indexNum = i + 1;
                            if (i == 0) {
                                indexNum = 0;
                            }
                            if (indexNum.equals(tdCount)) {
                                nextTrElement.appendChild(tempNode);
                            } else {
                                nextTrElement.insertChildren(indexNum, nodeList);
                            }
                        }
                    }
                }
            }
        }
        Element tableEle = tableDoc.getElementsByTag("table").first();
        String tableHtml = tableEle.outerHtml();

        return tableHtml;
    }

    /**
     * 转换Elements为list
     *
     * @param curElements curElements
     * @return {@link List}
     * @author xuwangcheng
     * @date 2019/7/29 18:40
     */
    private static List<Element> covertElements2List(Elements curElements) {
        List<Element> elementList = new ArrayList<Element>();
        Iterator<Element> eleIterator = curElements.iterator();
        while (eleIterator.hasNext()) {
            Element curlement = eleIterator.next();
            elementList.add(curlement);
        }
        return elementList;
    }
}
