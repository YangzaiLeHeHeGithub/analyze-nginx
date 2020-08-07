package com.mobile.analyzenone.style.impl;

import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import com.mobile.analyzenone.style.IExcelExportStyler;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * ExcelExportStylerBorderImpl
 *
 * @author xuyang
 * @version 1.0
 * @description 导出Excel边框样式实现类
 * @date 2020/8/6 13:14
 */
public class ExcelExportStylerBorderImpl implements IExcelExportStyler {

    @Override
    public CellStyle getHeaderStyle(short headerColor) {
        return null;
    }

    @Override
    public CellStyle getTitleStyle(short color) {
        return null;
    }

    @Override
    public CellStyle getStyles(boolean Parity, ExcelExportEntity entity) {
        return null;
    }
}
 