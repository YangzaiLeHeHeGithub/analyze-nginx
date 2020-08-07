package com.mobile.analyzenone.util;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.mobile.analyzenone.bean.AnalyzeDataVo;
import com.mobile.analyzenone.style.impl.ExcelExportStylerColorImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.util.List;

/**
 * ExcelWriter
 *
 * @author xuyang
 * @version 1.0
 * @description 输出Excel文件
 * @date 2020/8/6 13:04
 */
@Slf4j
public class ExcelWriterCopy {

    /**
     * 写入excel
     * @param data
     * @param filePath
     */
    public static void writeExcel(List<AnalyzeDataVo> data, String filePath) {
        // 写入数据到工作簿对象内

        //获取导出参数
        ExportParams exportParams = new ExportParams("移动阅卷应用成效", "数据展示", ExcelType.XSSF);
        //设置导出样式
        exportParams.setStyle(ExcelExportStylerColorImpl.class);
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams,
                AnalyzeDataVo .class, data);
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
        } catch (Exception e) {

        }

    }
}
 