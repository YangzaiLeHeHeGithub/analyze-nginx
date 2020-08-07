package com.mobile.analyzenone.util;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.mobile.analyzenone.bean.AnalyzeDataVo;
import com.mobile.analyzenone.style.impl.ExcelExportStylerColorImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
public class ExcelWriter {
    private static List<String> CELL_HEADS; //列头
    static {
        // 类装载时就载入指定好的列头信息，如有需要，可以考虑做成动态生成的列头
        CELL_HEADS = new ArrayList<>();
        CELL_HEADS.add("请求链接");
        CELL_HEADS.add("请求方式");
        CELL_HEADS.add("功能说明");
        CELL_HEADS.add("调用次数");
        CELL_HEADS.add("百分比");
    }
    /**
     * 设置第一行列头的样式
     *
     * @param workbook 工作簿对象
     * @return 单元格样式对象
     */
    private static CellStyle buildHeadCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        //对齐方式设置
        style.setAlignment(HorizontalAlignment.CENTER);
        //边框颜色和宽度设置
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex()); // 下边框
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex()); // 左边框
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex()); // 右边框
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex()); // 上边框
        //设置背景颜色
        style.setFillForegroundColor(IndexedColors.BLUE_GREY.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //粗体字设置
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
    /**
     * 生成sheet表，并写入第一行数据（列头）
     *
     * @param workbook 工作簿对象
     * @return 已经写入列头的Sheet
     */
    private static Sheet buildDataSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet();
        // 设置列头宽度
        for (int i = 0; i < CELL_HEADS.size(); i++) {
            sheet.setColumnWidth(i, 8000);
        }
        // 设置默认行高
        sheet.setDefaultRowHeight((short) 400);
        // 构建头单元格样式
        CellStyle cellStyle = buildHeadCellStyle(sheet.getWorkbook());
        // 写入第一行各列的数据
        Row head = sheet.createRow(0);
        for (int i = 0; i < CELL_HEADS.size(); i++) {
            Cell cell = head.createCell(i);
            cell.setCellValue(CELL_HEADS.get(i));
            cell.setCellStyle(cellStyle);
        }
        return sheet;
    }
    /**
     * 将数据转换成行
     *
     * @param data 源数据
     * @param row  行对象
     * @return
     */
    private static void convertDataToRow(AnalyzeDataVo data, Row row) {
        int cellNum = 0;
        Cell cell;
        // 请求链接
        cell = row.createCell(cellNum++);
        cell.setCellValue(null == data.getApi() ? "" : data.getApi());
        // 请求方式
        cell = row.createCell(cellNum++);
        cell.setCellValue(data.getMethod());
        // 功能说明
        cell = row.createCell(cellNum++);
        cell.setCellValue(data.getDesc());
        // 调用次数
        cell = row.createCell(cellNum++);
        cell.setCellValue(data.getCount());
        // 百分比
        cell = row.createCell(cellNum++);
        cell.setCellValue(data.getPercent());
    }
    /**
     * 生成Excel并写入数据信息
     *
     * @param dataList 数据列表
     * @return 写入数据后的工作簿对象
     */
    public static Workbook exportData(List<AnalyzeDataVo> dataList) {
        // 生成xlsx的Excel
        Workbook workbook = new SXSSFWorkbook();

        // 如需生成xls的Excel，请使用下面的工作簿对象，注意后续输出时文件后缀名也需更改为xls
        //Workbook workbook = new HSSFWorkbook();

        // 生成Sheet表，写入第一行的列头
        Sheet sheet = buildDataSheet(workbook);
        //构建每行的数据内容
        int rowNum = 1;
        for (Iterator<AnalyzeDataVo> it = dataList.iterator(); it.hasNext(); ) {
            AnalyzeDataVo data = it.next();
            if (data == null) {
                continue;
            }
            //输出行数据
            Row row = sheet.createRow(rowNum++);
            convertDataToRow(data, row);
        }
        return workbook;
    }
    /**
     * 写入excel
     * @param data
     * @param filePath
     */
    public static void writeExcel(List<AnalyzeDataVo> data, String filePath) {
        // 写入数据到工作簿对象内
        Workbook workbook = ExcelWriter.exportData(data);
        // 以文件的形式输出工作簿对象
        FileOutputStream fileOut = null;
        try {
            File exportFile = new File(filePath);
            if (!exportFile.exists()) {
                exportFile.createNewFile();
            }

            fileOut = new FileOutputStream(filePath);
            workbook.write(fileOut);
            fileOut.flush();
        } catch (Exception e) {
            log.warn("输出Excel时发生错误，错误原因：" + e.getMessage());
        } finally {
            try {
                if (null != fileOut) {
                    fileOut.close();
                }
                if (null != workbook) {
                    workbook.close();
                }
            } catch (IOException e) {
                log.warn("关闭输出流时发生错误，错误原因：" + e.getMessage());
            }
        }

    }
}
 