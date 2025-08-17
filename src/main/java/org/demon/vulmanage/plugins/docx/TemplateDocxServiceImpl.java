package org.demon.vulmanage.plugins.docx;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * docx模板服务实现类
 *
 * @author max
 * @date 2023/12/20
 */
@Service
public class TemplateDocxServiceImpl implements TemplateDocxService {
    @Autowired
    HtmlRenderPolicy htmlRenderPolicy;

    /**
     * 渲染docx模板
     *
     * @param templateDocxFile  模板docx文件路径
     * @param tagBindingDataMap 标签绑定数据
     * @param outputStream      输出流
     * @throws IOException
     */
    @Override
    public void render(String templateDocxFile, Map<String, ?> tagBindingDataMap, OutputStream outputStream) throws IOException {
        Configure config = Configure.builder().build();
        config.plugin('$', htmlRenderPolicy);
        XWPFTemplate template = XWPFTemplate.compile(templateDocxFile, config).render(tagBindingDataMap);
        template.write(outputStream);
        template.close();
    }

    /**
     * 渲染docx模板
     *
     * @param templateDocxFile  模板docx文件路径
     * @param config            配置
     * @param tagBindingDataMap 标签绑定数据
     * @param outputStream      输出流
     * @throws IOException
     */
    @Override
    public void render(String templateDocxFile, Configure config, Map<String, ?> tagBindingDataMap, OutputStream outputStream) throws IOException {
        config.plugin('$', htmlRenderPolicy);
        XWPFTemplate template = XWPFTemplate.compile(templateDocxFile, config).render(tagBindingDataMap);
        template.write(outputStream);
        template.close();
    }

}
