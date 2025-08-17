package org.demon.vulmanage.plugins.docx;

import com.deepoove.poi.config.Configure;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

/**
 * docx模板服务
 *
 * @author max
 * @date 2023/12/20
 */
public interface TemplateDocxService {

    /**
     * 渲染docx模板
     *
     * @param templateDocxFile  模板docx文件路径
     * @param config            配置
     * @param tagBindingDataMap 标签绑定数据
     * @param outputStream      输出流
     * @throws IOException
     */
    void render(String templateDocxFile, Configure config, Map<String, ?> tagBindingDataMap, OutputStream outputStream) throws IOException;

    /**
     * 渲染docx模板
     *
     * @param templateDocxFile  模板docx文件路径
     * @param tagBindingDataMap 标签绑定数据
     * @param outputStream      输出流
     * @throws IOException
     */
    void render(String templateDocxFile, Map<String, ?> tagBindingDataMap, OutputStream outputStream) throws IOException;

}
