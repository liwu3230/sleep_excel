package org.example.backend.web.excel;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.backend.web.model.dto.ImportExcelDto;
import org.example.backend.web.util.T;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
public class ExcelAnalyzeUtils {

    public static final String[] HEAD = new String[]{"你昨晚几点熄灯准备睡觉?", "你躺了多久的时间睡着?", "你半夜醒了几次?一共多长时间?", "你早上醒来时间?", "你是几点从床上起来的？", "睡眠质量1-2-3-4-5很好-很差", "白天嗜睡程度1-2-3-4-5很清醒—很困", "午睡时间", "你白天打几次瞌睡?共睡了多久?", "饮食时间", "(咖啡、茶、可乐、奶茶)注明类别时间及饮用量", "饮酒量及时间", "是否服用药物？请注明药名及剂量", "摘下腕表时间（原因）", "备注"};

    public static final Map<String, String> HEAD_MAP = new ConcurrentHashMap<>();

    private static final Map<String, Function<Map<String, String>, String>> headMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {
        List<Pair<String, List<List<String>>>> list = showExcel("C:\\Users\\yyadmin\\Desktop\\郑雅文(1).xlsx");
        List<List<String>> lists = list.get(0).getValue();
        List<String> heads = new ArrayList<>();
        int headIndex = -1;
        for (int i = 0; i < lists.size(); i++) {
            List<String> rows = lists.get(i);
            if (checkExcelHead(rows)) {
                heads = rows;
                headIndex = i;
                break;
            }
        }
        List<Map<String, String>> excelMap = new ArrayList<>();
        for (int j = headIndex + 1; j < lists.size(); j++) {
            List<String> rows = lists.get(j);
            Map<String, String> rowMap = new LinkedHashMap<>();
            for (int k = 0; k < heads.size(); k++) {
                String val = "";
                if (k < rows.size()) {
                    val = rows.get(k);
                }
                rowMap.put(heads.get(k), val);
            }
            excelMap.add(rowMap);
        }
        System.out.println(excelMap);
    }


    public static String formatStr(String str1, boolean newline, boolean space, boolean underline) {
        if (T.isBlank(str1)) {
            return "";
        }
        if (newline) {
            str1 = str1.replace("\n", "");
        }
        if (space) {
            str1 = str1.trim().replaceAll("\\s+", "");
        }
        if (underline) {
            str1 = str1.replace("_", "");
        }
        return str1;
    }

    public static String convertMinute(String str) {
        if (T.isBlank(str)) {
            return "";
        }
        int sIndex = str.indexOf("时");
        int eIndex = str.indexOf("分");
        if (sIndex >= 0 && eIndex >= 0 && sIndex < eIndex) {
            String hour = str.substring(0, sIndex);
            String minute = str.substring(sIndex + 1, eIndex);
            hour = formatStr(hour, true, true, true);
            minute = formatStr(minute, true, true, true);

            int hourNum;
            int minuteNum;
            try {
                hourNum = Integer.parseInt(hour);
            } catch (NumberFormatException e) {
                return str;
            }
            try {
                minuteNum = Integer.parseInt(minute);
            } catch (NumberFormatException e) {
                return str;
            }
            return String.valueOf(minuteNum + 60 * hourNum);
        }
        return str;
    }

    public static boolean isPositiveInteger(String str) {
        try {
            int number = Integer.parseInt(str);
            return (number >= 0);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean similar(String str1, String str2) {
        str1 = str1.replace("\n", "");
        str2 = str2.replace("\n", "");
        double similarity = StrUtil.similar(str1, str2);
        return (similarity >= 0.7D);
    }

    public static List<Pair<String, List<List<String>>>> showExcel(String path) throws Exception {
        List<Pair<String, List<List<String>>>> cellsList = Collections.emptyList();

        if (StringUtils.isNotBlank(path) && is2007Excel(path))
            return showHighExcel(path);
        if (StringUtils.isNotBlank(path) && is2003Excel(path)) {
            return showLowExcel(path);
        }
        return cellsList;
    }

    public static List<List<String>> queryRowList(Sheet sheet) throws Exception {
        List<List<String>> rowList = new ArrayList<>();
        if (sheet == null) {
            return rowList;
        }
        for (int j = 0; j < sheet.getLastRowNum() + 1; j++) {
            List<String> cellList = new ArrayList<>();
            Row row = sheet.getRow(j);
            if (row != null) {
                for (int k = 0; k < row.getLastCellNum(); k++) {
                    Cell cell = row.getCell(k);
                    if (cell != null) {
                        if (isMergedCell(sheet, cell)) {
                            cell = getMergedCell(sheet, cell);
                        }
                        String cellValue = "";
                        switch (cell.getCellType()) {
                            case NUMERIC:
                                cellValue = convertToDecimal(cell.getNumericCellValue() + "");
                                break;
                            case STRING:
                                cellValue = (cell.getStringCellValue() == null) ? "" : cell.getStringCellValue().trim();
                                break;
                            case BOOLEAN:
                                cellValue = cell.getBooleanCellValue() + "";
                                break;
                            case FORMULA:
                                try {
                                    cellValue = String.valueOf(cell.getNumericCellValue());
                                } catch (Exception e) {
                                    try {
                                        cellValue = String.valueOf(cell.getRichStringCellValue());
                                    } catch (Exception ex) {
                                        try {
                                            cellValue = String.valueOf(cell.getCellFormula());
                                        } catch (Exception exc) {
                                            try {
                                                cellValue = String.valueOf(cell.getBooleanCellValue());
                                            } catch (Exception exc2) {
                                                try {
                                                    cellValue = String.valueOf(cell.getStringCellValue());
                                                } catch (Exception exc3) {
                                                    exc.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                            case BLANK:
                                cellValue = "";
                                break;
                            case ERROR:
                                cellValue = "非法字符";
                                break;
                            default:
                                cellValue = "未知类型";
                                break;
                        }
                        cellList.add(cellValue);
                    }
                }
                rowList.add(cellList);
            }
        }
        return rowList;
    }


    public static List<Pair<String, List<List<String>>>> showHighExcel(String path) throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            throw new Exception("the file may not exist :" + file.getPath());
        }
        FileInputStream fis = new FileInputStream(file);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        List<Pair<String, List<List<String>>>> sheetList = new ArrayList<>();

        try {
            String cellValue = "";
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                XSSFSheet sheet = wb.getSheetAt(i);
                if (sheet != null) {
                    String sheetName = wb.getSheetName(i);
                    List<List<String>> rowList = new ArrayList<>();
                    for (int j = 0; j < sheet.getLastRowNum() + 1; j++) {
                        List<String> cellList = new ArrayList<>();
                        XSSFRow row = sheet.getRow(j);
                        if (row != null) {
                            for (int k = 0; k < row.getLastCellNum(); k++) {
                                XSSFCell cell = row.getCell(k);
                                if (cell != null) {
                                    if (isMergedCell(sheet, cell)) {
                                        cell = (XSSFCell) getMergedCell(sheet, cell);
                                    }
                                    switch (cell.getCellType()) {
                                        case NUMERIC:
                                            cellValue = convertToDecimal(cell.getNumericCellValue() + "");
                                            break;
                                        case STRING:
                                            cellValue = (cell.getStringCellValue() == null) ? "" : cell.getStringCellValue().trim();
                                            break;
                                        case BOOLEAN:
                                            cellValue = cell.getBooleanCellValue() + "";
                                            break;
                                        case FORMULA:
                                            try {
                                                cellValue = String.valueOf(cell.getNumericCellValue());
                                            } catch (Exception e) {
                                                try {
                                                    cellValue = String.valueOf(cell.getRichStringCellValue());
                                                } catch (Exception ex) {
                                                    try {
                                                        cellValue = String.valueOf(cell.getCellFormula());
                                                    } catch (Exception exc) {
                                                        try {
                                                            cellValue = String.valueOf(cell.getBooleanCellValue());
                                                        } catch (Exception exc2) {
                                                            try {
                                                                cellValue = String.valueOf(cell.getStringCellValue());
                                                            } catch (Exception exc3) {
                                                                exc.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            break;
                                        case BLANK:
                                            cellValue = "";
                                            break;
                                        case ERROR:
                                            cellValue = "非法字符";
                                            break;
                                        default:
                                            cellValue = "未知类型";
                                            break;
                                    }
                                    cellList.add(cellValue);
                                }
                            }
                            rowList.add(cellList);
                        }
                    }
                    sheetList.add(new Pair<>(sheetName, rowList));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            wb.close();
            fis.close();
        }
        return sheetList;
    }

    public static List<Pair<String, List<List<String>>>> showLowExcel(String path) throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            throw new Exception("the file may not exist :" + file.getPath());
        }
        FileInputStream fis = new FileInputStream(path);
        HSSFWorkbook wb = new HSSFWorkbook(fis);
        List<Pair<String, List<List<String>>>> sheetList = new ArrayList<>();
        try {
            String cellValue = "";
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                HSSFSheet sheet = wb.getSheetAt(i);
                if (sheet != null) {
                    String sheetName = wb.getSheetName(i);
                    List<List<String>> rowList = new ArrayList<>();
                    for (int j = 0; j < sheet.getLastRowNum() + 1; j++) {
                        List<String> cellList = new ArrayList<>();
                        HSSFRow row = sheet.getRow(j);
                        if (row != null) {
                            for (int k = 0; k < row.getLastCellNum(); k++) {
                                HSSFCell cell = row.getCell(k);
                                if (cell != null) {
                                    if (isMergedCell(sheet, cell)) {
                                        cell = (HSSFCell) getMergedCell(sheet, cell);
                                    }
                                    switch (cell.getCellType()) {
                                        case NUMERIC:
                                            cellValue = convertToDecimal(cell.getNumericCellValue() + "");
                                            break;
                                        case STRING:
                                            cellValue = (cell.getStringCellValue() == null) ? "" : cell.getStringCellValue().trim();
                                            break;
                                        case BOOLEAN:
                                            cellValue = cell.getBooleanCellValue() + "";
                                            break;
                                        case FORMULA:
                                            try {
                                                cellValue = String.valueOf(cell.getNumericCellValue());
                                            } catch (Exception e) {
                                                try {
                                                    cellValue = String.valueOf(cell.getRichStringCellValue());
                                                } catch (Exception ex) {
                                                    try {
                                                        cellValue = String.valueOf(cell.getCellFormula());
                                                    } catch (Exception exc) {
                                                        try {
                                                            cellValue = String.valueOf(cell.getBooleanCellValue());
                                                        } catch (Exception exc2) {
                                                            try {
                                                                cellValue = String.valueOf(cell.getStringCellValue());
                                                            } catch (Exception exc3) {
                                                                exc.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            break;
                                        case BLANK:
                                            cellValue = "";
                                            break;
                                        case ERROR:
                                            cellValue = "非法字符";
                                            break;
                                        default:
                                            cellValue = "未知类型";
                                            break;
                                    }
                                    cellList.add(cellValue);
                                }
                            }
                            rowList.add(cellList);
                        }
                    }
                    sheetList.add(new Pair<>(sheetName, rowList));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            wb.close();
            fis.close();
        }
        return sheetList;
    }

    public static XSSFCell getNextCellByHighExcel(XSSFWorkbook wb, int sheetNum, int rowNum, int cellNum) {
        XSSFCell xssfCell = null;
        XSSFSheet sheet = wb.getSheetAt(sheetNum);
        if (sheet == null) {
            return xssfCell;
        }
        XSSFRow row = sheet.getRow(rowNum);
        if (row == null) {
            return xssfCell;
        }
        XSSFCell cell = row.getCell(cellNum + 1);
        if (cell == null) {
            return xssfCell;
        }
        return cell;
    }

    public static XSSFCell getNextRowCellByHighExcel(XSSFWorkbook wb, int sheetNum, int rowNum, int cellNum) {
        XSSFCell xssfCell = null;
        XSSFSheet sheet = wb.getSheetAt(sheetNum);
        if (sheet == null) {
            return xssfCell;
        }
        XSSFRow row = sheet.getRow(rowNum + 1);
        if (row == null) {
            return xssfCell;
        }
        XSSFCell cell = row.getCell(cellNum);
        if (cell == null) {
            return xssfCell;
        }
        return cell;
    }

    private static boolean isMergedCell(Sheet sheet, Cell cell) {
        for (CellRangeAddress mergedRegion : sheet.getMergedRegions()) {
            if (mergedRegion.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
                return true;
            }
        }
        return false;
    }

    private static Cell getMergedCell(Sheet sheet, Cell cell) {
        for (CellRangeAddress mergedRegion : sheet.getMergedRegions()) {
            if (mergedRegion.isInRange(cell.getRowIndex(), cell.getColumnIndex())) {
                Row firstRow = sheet.getRow(mergedRegion.getFirstRow());
                return firstRow.getCell(mergedRegion.getFirstColumn());
            }
        }
        return cell;
    }

    public static HSSFCell getNextCellByLowExcel(HSSFWorkbook wb, int sheetNum, int rowNum, int cellNum) {
        HSSFCell hssfCell = null;
        HSSFSheet sheet = wb.getSheetAt(sheetNum);
        if (sheet == null) {
            return hssfCell;
        }
        HSSFRow row = sheet.getRow(rowNum);
        if (row == null) {
            return hssfCell;
        }
        HSSFCell cell = row.getCell(cellNum + 1);
        if (cell == null) {
            return hssfCell;
        }
        return cell;
    }

    public static HSSFCell getNextRowCellByLowExcel(HSSFWorkbook wb, int sheetNum, int rowNum, int cellNum) {
        HSSFCell hssfCell = null;
        HSSFSheet sheet = wb.getSheetAt(sheetNum);
        if (sheet == null) {
            return hssfCell;
        }
        HSSFRow row = sheet.getRow(rowNum + 1);
        if (row == null) {
            return hssfCell;
        }
        HSSFCell cell = row.getCell(cellNum);
        if (cell == null) {
            return hssfCell;
        }
        return cell;
    }

    public static boolean is2003Excel(String filePath) {
        return filePath.matches("^.+\\.(?i)(xls)$");
    }

    public static boolean is2007Excel(String filePath) {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }

    public static boolean isNumber(String str) {
        String reg = "^[0-9]+(.[0-9]+)?$";
        return str.matches(reg);
    }

    public static String convertToDecimal(String str) {
        if (str.matches("-?[\\d.]+(?:E-?\\d+)?")) {
            Double scientificDouble = Double.parseDouble(str);
            NumberFormat nf = new DecimalFormat("################################################.###########################################");
            return nf.format(scientificDouble);
        }
        return str;
    }

    public static String stringConvertNumber(String str) {
        double number = 0.0D;
        try {
            number = Double.parseDouble(str);
        } catch (NumberFormatException e) {
            int index = str.indexOf("/");
            if (index != -1) {
                String tmp = str.substring(0, index);
                try {
                    number = Double.parseDouble(tmp);
                } catch (NumberFormatException e1) {
                    return "";
                }
            }
        }

        BigDecimal bigNum = (new BigDecimal(number)).setScale(0, RoundingMode.HALF_UP);
        return bigNum.toString();
    }

    public static boolean checkExcelHead(List<String> rowsList) {
        if (T.isEmpty(rowsList)) {
            return false;
        }
        int similarCount = 0;
        for (String str : rowsList) {
            for (String h : HEAD) {
                if (similar(str, h)) {
                    similarCount++;
                    break;
                }
            }
        }
        if (similarCount > HEAD.length / 2) {
            return true;
        }
        return false;
    }

    public static Integer writeFile(ImportExcelDto dto, String importFilePath, String targetSheetName, List<Map<String, String>> importList) throws Exception {
        boolean isHigh;
        int successCount = 0;

        if (is2007Excel(importFilePath)) {
            isHigh = true;
        } else if (is2003Excel(importFilePath)) {
            isHigh = false;
        } else {
            throw new RuntimeException("不支持的格式!");
        }

        List<Pair<String, List<List<String>>>> oriExcel = showExcel(importFilePath);
        int existedSheetIndex = -1;
        List<List<String>> existedRowList = new ArrayList<>();

        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            HSSFWorkbook hSSFWorkbook = null;
            fis = new FileInputStream(importFilePath);
            for (int i = 0; i < oriExcel.size(); i++) {
                Pair<String, List<List<String>>> pair = oriExcel.get(i);
                String sheetName = pair.getKey();
                List<List<String>> rowList = pair.getValue();
                if (T.isNotBlank(sheetName) && Objects.equals(sheetName.trim(), targetSheetName)) {
                    existedSheetIndex = i;
                    existedRowList = rowList;

                    break;
                }
            }
            if (isHigh) {
                XSSFWorkbook xSSFWorkbook = new XSSFWorkbook(fis);
            } else {
                hSSFWorkbook = new HSSFWorkbook(fis);
            }

            CellStyle cellStyle = hSSFWorkbook.createCellStyle();
            cellStyle.setBorderTop(BorderStyle.THIN);
            cellStyle.setTopBorderColor((short) 0);
            cellStyle.setBorderBottom(BorderStyle.THIN);
            cellStyle.setBottomBorderColor((short) 0);
            cellStyle.setBorderLeft(BorderStyle.THIN);
            cellStyle.setLeftBorderColor((short) 0);
            cellStyle.setBorderRight(BorderStyle.THIN);
            cellStyle.setRightBorderColor((short) 0);
            cellStyle.setWrapText(true);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            if (existedSheetIndex < 0 && T.isEmpty(existedRowList)) {
                Sheet sheet = hSSFWorkbook.createSheet(targetSheetName);

                int lastRowNum = 0;
                Row rowHead = sheet.createRow(lastRowNum);
                List<String> keyList = new ArrayList<>(headMap.keySet());
                for (int j = 0; j < keyList.size(); j++) {
                    Cell cell = rowHead.createCell(j);
                    cell.setCellValue((keyList.get(j) == null) ? "" : keyList.get(j));
                    cell.setCellStyle(cellStyle);

                    int columnWidth = (keyList.get(j).getBytes()).length;
                    if (columnWidth > 30) {
                        columnWidth = 30;
                    }
                    sheet.setColumnWidth(0, columnWidth * 256);
                }
                lastRowNum++;

                for (int k = 0; k < importList.size(); k++) {
                    Map<String, String> dataMap = importList.get(k);
                    List<String> cellList = new ArrayList<>();
                    headMap.forEach((key, val) -> cellList.add(val.apply(dataMap)));
                    Row row = sheet.createRow(lastRowNum + k);
                    successCount++;
                    for (int m = 0; m < cellList.size(); m++) {
                        Cell cell = row.createCell(m);
                        cell.setCellValue((cellList.get(m) == null) ? "" : cellList.get(m));
                        cell.setCellStyle(cellStyle);
                    }
                }
                fos = new FileOutputStream(importFilePath);
                hSSFWorkbook.write(fos);
                hSSFWorkbook.close();
            } else {
                for (Map<String, String> stringStringMap : importList) {
                    Row row;
                    Sheet sheet = hSSFWorkbook.getSheetAt(existedSheetIndex);
                    String userName = stringStringMap.getOrDefault("姓名", "");
                    String userNumber = stringStringMap.getOrDefault("编号", "");
                    String dateTime = stringStringMap.getOrDefault("日期", "");

                    List<String> cellList = new ArrayList<>();
                    headMap.forEach((k, v) -> cellList.add(v.apply(stringStringMap)));

                    List<List<String>> oldRowList = queryRowList(sheet);
                    Map<String, Integer> eqMap = new LinkedHashMap<>();
                    Map<String, Integer> firstNumMap = new LinkedHashMap<>();
                    for (int inx = 1; inx < oldRowList.size(); inx++) {
                        List<String> oldRow = oldRowList.get(inx);
                        if (T.isNotEmpty(oldRow) && oldRow.size() >= 3) {
                            String oldUserName = (oldRow.get(0) == null) ? "" : oldRow.get(0);
                            String oldUserNumber = (oldRow.get(1) == null) ? "" : oldRow.get(1);
                            String oldDateTime = (oldRow.get(2) == null) ? "" : oldRow.get(2);
                            if (T.isNotBlank(userName) && T.isNotBlank(oldUserName) && Objects.equals(userName.trim(), oldUserName.trim()) &&
                                    T.isNotBlank(userNumber) && T.isNotBlank(oldUserNumber) && Objects.equals(userNumber.trim(), oldUserNumber.trim()) &&
                                    T.isNotBlank(oldDateTime)) {
                                eqMap.put(oldDateTime, inx);
                            }
                            if (T.isNotBlank(oldUserNumber)) {
                                firstNumMap.putIfAbsent(oldUserNumber, inx);
                            }
                        }
                    }

                    if (eqMap.containsKey(dateTime)) {
                        int index = eqMap.get(dateTime);
                        row = sheet.getRow(index);
                        successCount++;
                    } else if (T.isEmpty(eqMap)) {
                        List<String> keys = new ArrayList<>(firstNumMap.keySet());
                        keys.sort(String::compareTo);
                        int rowIndex = -1;
                        for (String key : keys) {
                            if (userNumber.compareTo(key) <= 0) {
                                rowIndex = firstNumMap.get(key);
                                break;
                            }
                        }
                        if (rowIndex >= 0) {
                            sheet.shiftRows(rowIndex, sheet.getLastRowNum(), 1, true, false);
                            row = sheet.createRow(rowIndex);
                            successCount++;
                        } else {
                            row = sheet.createRow(sheet.getLastRowNum() + 1);
                            successCount++;
                        }
                    } else {
                        List<String> keys = new ArrayList<>(eqMap.keySet());
                        keys.sort(String::compareTo);
                        int d = -1;
                        for (int m = 0; m < keys.size(); m++) {
                            String key = keys.get(m);
                            if (dateTime.compareTo(key) < 0) {
                                d = m;
                                break;
                            }
                        }
                        if (d >= 0 && d < keys.size() - 1) {
                            int rowIndex = eqMap.get(keys.get(d));
                            sheet.shiftRows(rowIndex, sheet.getLastRowNum(), 1, true, false);
                            row = sheet.createRow(rowIndex);
                            successCount++;
                        } else {
                            int rowIndex = eqMap.get(keys.get(keys.size() - 1));
                            if (rowIndex < sheet.getLastRowNum()) {
                                sheet.shiftRows(rowIndex + 1, sheet.getLastRowNum(), 1, true, false);
                            }
                            row = sheet.createRow(rowIndex + 1);
                            successCount++;
                        }
                    }

                    for (int k = 0; k < cellList.size(); k++) {
                        Cell cell = row.createCell(k);
                        cell.setCellValue((cellList.get(k) == null) ? "" : cellList.get(k));
                        cell.setCellStyle(cellStyle);
                    }

                    fos = new FileOutputStream(importFilePath);
                    hSSFWorkbook.write(fos);
                    closeOutputStream(fos);
                }
                hSSFWorkbook.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            closeInputStream(fis);
            closeOutputStream(fos);
        }
        return successCount;
    }

    public static void closeOutputStream(OutputStream out) {
        if (out == null) {
            return;
        }
        try {
            out.flush();
            out.close();
        } catch (Exception exception) {
        }
    }


    public static void closeInputStream(InputStream in) {
        if (in == null) {
            return;
        }
        try {
            in.close();
        } catch (Exception exception) {
        }
    }

}
