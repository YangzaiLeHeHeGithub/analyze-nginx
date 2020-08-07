package com.mobile.analyzenone.style;

import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import org.apache.poi.ss.usermodel.CellStyle;

public interface IExcelExportStyler {
    /**
     * 列表头样式
     * @param headerColor
     * @return
     */
    CellStyle getHeaderStyle(short headerColor);
    /**
     * 标题样式
     * @param color
     * @return
     */
    CellStyle getTitleStyle(short color);
    /**
     * 获取样式方法
     * @param Parity
     * @param entity
     * @return
     */
    CellStyle getStyles(boolean Parity, ExcelExportEntity entity);
}