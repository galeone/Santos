package it.galeone_dev.santos;

import org.slf4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jxls.area.XlsArea;
import org.jxls.common.AreaListener;
import org.jxls.common.CellRef;
import org.jxls.common.Context;
import org.jxls.transform.poi.PoiTransformer;
import org.slf4j.LoggerFactory;

public class JXLSCellBackground implements AreaListener {
    static Logger logger = LoggerFactory.getLogger(JXLSCellBackground.class);

    XlsArea area;
    PoiTransformer transformer;
    
    public JXLSCellBackground(XlsArea area) {
        this.area = area;
        transformer = (PoiTransformer) area.getTransformer();
    }
    
    @Override
    public void afterApplyAtCell(CellRef arg0, Context arg1) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void afterTransformCell(CellRef srcCell, CellRef targetCell, Context context) {
        Workbook workbook = transformer.getWorkbook();
        Sheet sheet = workbook.getSheet(targetCell.getSheetName());
        Row row = sheet.getRow(srcCell.getRow());
        if(row != null) {
            Cell cell = row.getCell(srcCell.getCol());
            logger.info(cell.getStringCellValue());
            highlightCell(targetCell);
        }
    }

    private void highlightCell(CellRef cellRef) {
        Workbook workbook = transformer.getWorkbook();
        Sheet sheet = workbook.getSheet(cellRef.getSheetName());
        Cell cell = sheet.getRow(cellRef.getRow()).getCell(cellRef.getCol());
        CellStyle cellStyle = cell.getCellStyle();
        CellStyle newCellStyle = workbook.createCellStyle();
        newCellStyle.setDataFormat( cellStyle.getDataFormat() );
        newCellStyle.setFont( workbook.getFontAt( cellStyle.getFontIndex() ));
        newCellStyle.setFillBackgroundColor( cellStyle.getFillBackgroundColor());
        newCellStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        newCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cell.setCellStyle(newCellStyle);
    }
    
    @Override
    public void beforeApplyAtCell(CellRef arg0, Context arg1) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void beforeTransformCell(CellRef arg0, CellRef arg1, Context arg2) {
        // TODO Auto-generated method stub
        
    }
    
}
