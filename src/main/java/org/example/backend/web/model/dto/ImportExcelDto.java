package org.example.backend.web.model.dto;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import org.example.backend.web.excel.ExcelAnalyzeUtils;
import org.example.backend.web.util.DateUtil;
import org.example.backend.web.util.T;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class ImportExcelDto {

    private String uuid;
    private Date createTime;
    private String userName;
    private String userNumber;
    private String recordTime;
    private String importFileFolder;
    private List<Map<String, String>> excel;
    private List<Table> tableList;

    public List<Map<String, String>> getParseExcel() throws Exception {
        List<Map<String, String>> parseList = new ArrayList<>();
        if (T.isEmpty(this.tableList)) {
            return parseList;
        }
        Date beginDate = DateUtil.parseDate(this.recordTime);
        for (int i = 0; i < this.tableList.size(); i++) {
            if (i > 0) {
                beginDate = DateUtil.getNextDate(beginDate);
            }
            Table table = this.tableList.get(i);
            Map<String, String> map = new LinkedHashMap<>();
            map.put("姓名", this.userName);
            map.put("编号", this.userNumber);
            map.put("日期", DateUtil.format(beginDate, "yyyy.MM.dd"));
            for (Map.Entry<String, String> entry : ExcelAnalyzeUtils.HEAD_MAP.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                Field field = Table.class.getDeclaredField(v);
                field.setAccessible(true);
                String value = (String) field.get(table);
                map.put(k, value);
            }
            parseList.add(map);
        }
        return parseList;
    }

    public ParseData getOldParseExcel() {
        ParseData parseData = new ParseData();
        if (T.isEmpty(this.excel)) {
            return parseData;
        }
        List<Map<String, String>> parseList = Lists.newArrayList();
        Date beginDate = DateUtil.parseDate(this.recordTime);
        for (int i = 0; i < this.excel.size(); i++) {
            Map<String, String> oriMap = this.excel.get(i);
            Map<String, String> map = Maps.newLinkedHashMap();
            if (i > 0) {
                beginDate = DateUtil.getNextDate(beginDate);
            }

            String fixStr = "加黑的请于晨起后填写";
            boolean needFiltered = false;
            for (Map.Entry<String, String> entry : oriMap.entrySet()) {
                String v = entry.getValue();
                if (T.isNotBlank(v) && v.contains(fixStr)) {
                    needFiltered = true;

                    int index = v.indexOf("姓名");
                    if (index != -1) {
                        String result = v.substring(index);
                        String[] delArr = {"姓名", "编号", "：", ":", "首次", ".", "/", "课题一", "课题二", "课题三", "课题四", "课题五", "课题六", "课题七", "课题八", "课题九", "课题十", "课题1", "课题2", "课题3", "课题4", "课题5", "课题6", "课题7", "课题8", "课题9", "课题10", "基线", "第1周", "第2周", "第3周", "第4周", "第5周", "第6周", "第7周", "第8周", "第一周", "第二周", "第三周", "第四周", "第五周", "第六周", "第七周", "第八周", "第三个月", "第3个月", "第3月", "第三月"};


                        for (String str : delArr) {
                            result = result.replace(str, "");
                        }
                        result = ExcelAnalyzeUtils.formatStr(result, true, true, true);
                        String digitRegex = "\\d+";
                        String nonDigitRegex = "\\D+";
                        String userName = extractSubstring(result, nonDigitRegex);
                        String userNumber = extractSubstring(result, digitRegex);
                        parseData.setUserName(userName);
                        parseData.setUserNumber(userNumber);
                    }
                    break;
                }
            }
            if (!needFiltered) {
                for (String head : ExcelAnalyzeUtils.HEAD) {
                    for (Map.Entry<String, String> entry : oriMap.entrySet()) {
                        String k = entry.getKey();
                        String v = entry.getValue();
                        if (ExcelAnalyzeUtils.similar(head, k)) {
                            map.put(head, v);
                            map.put("姓名", this.userName);
                            map.put("编号", this.userNumber);
                            map.put("日期", DateUtil.format(beginDate, "yyyy.MM.dd"));
                            break;
                        }
                    }
                }
                parseList.add(map);
            }
        }
        parseData.setData(parseList);
        return parseData;
    }

    public Map<String, Object> convertTable() throws Exception {
        Map<String, Object> dataMap = new HashMap<>();

        ParseData parseData = getOldParseExcel();
        if (T.isBlank(this.userName) && T.isNotBlank(parseData.getUserName())) {
            dataMap.put("userName", parseData.getUserName());
        }
        if (T.isBlank(this.userNumber) && T.isNotBlank(parseData.getUserNumber())) {
            dataMap.put("userNumber", parseData.getUserNumber());
        }
        List<Map<String, String>> rowList = parseData.getData();
        List<Table> tableList = new ArrayList<>();
        dataMap.put("tableList", tableList);
        if (T.isEmpty(rowList)) {
            return dataMap;
        }
        int id = 1;
        for (Map<String, String> map : rowList) {
            Table table = new Table();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                String fieldName = ExcelAnalyzeUtils.HEAD_MAP.get(k);
                if (T.isBlank(fieldName)) {
                    continue;
                }
                Field field = Table.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(table, v);
            }
            table.setId(id);
            table.setH11("0");
            table.setH12("0");
            table.setH13("无");

            tableList.add(table);
            id++;
        }
        return dataMap;
    }

    private static String extractSubstring(String originalString, String regex) {
        StringBuilder stringBuilder = new StringBuilder();
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(originalString);
        while (matcher.find()) {
            String substring = matcher.group();
            stringBuilder.append(substring);
        }
        return stringBuilder.toString();
    }

}


