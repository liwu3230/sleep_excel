package org.example.backend.web.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.Pair;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.web.excel.ExcelAnalyzeUtils;
import org.example.backend.web.model.R;
import org.example.backend.web.model.dto.FileDto;
import org.example.backend.web.model.dto.ImportExcelDto;
import org.example.backend.web.util.T;
import org.mapstruct.Context;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.*;

@Slf4j
@Controller
public class CommonController {

    public static String UPLOAD_DIR = "/data/sleep-excel";

    @PostConstruct
    public void init() {
        String osName = System.getProperty("os.name");
        if (osName.toLowerCase().contains("window")) {
            UPLOAD_DIR = "G:\\data\\sleep-excel";
        }
    }

    @GetMapping({"/"})
    public String index(Model model) {
        return "index.html";
    }

    @GetMapping({"/login"})
    public String login(Model model) {
        return "login.html";
    }


    @RequestMapping({"/doLogin"})
    @ResponseBody
    public R doLogin(@RequestParam(required = false) String username, @RequestParam(required = false) String password) throws Exception {
        if (T.isBlank(username)) {
            return R.error("请输入账号");
        }
        if (T.isBlank(password)) {
            return R.error("请输入密码");
        }
        if ("admin".equals(username) && "admin123456".equals(password)) {
            StpUtil.login(Integer.valueOf(10001));
            return R.ok("登录成功");
        }
        return R.error("登录失败，请联系管理员!");
    }

    @RequestMapping({"isLogin"})
    @ResponseBody
    public R isLogin() {
        return R.ok("当前会话是否登录：" + StpUtil.isLogin());
    }

    @RequestMapping({"/upload"})
    @ResponseBody
    public R uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.error("请选择要上传的文件");
        }
        try {
            String filename = UPLOAD_DIR + File.separator + file.getOriginalFilename();
            File targetFile = new File(filename);
            if (targetFile.exists()) {
                targetFile.delete();
            }
            targetFile.mkdirs();
            file.transferTo(targetFile);

            Map<String, String> rtn = Maps.newHashMap();
            rtn.put("value", filename);
            return R.ok("文件上传成功").setData(rtn);
        } catch (Exception e) {
            log.error("upload file err", e);
            return R.error("文件上传失败: " + e.getMessage());
        }
    }

    @RequestMapping({"/queryFileList"})
    @ResponseBody
    public R queryFileList() {
        List<FileDto> files = new ArrayList<>();

        File directory = new File(UPLOAD_DIR);
        if (!directory.exists() || !directory.isDirectory()) {
            return R.data(files);
        }

        File[] listedFiles = directory.listFiles();
        if (T.isNotEmpty((Object[]) listedFiles)) {
            for (File file : listedFiles) {
                if (file.isFile()) {
                    FileDto fileDto = new FileDto();
                    fileDto.setUuid(T.UUID());
                    fileDto.setFileName(file.getName());
                    fileDto.setFolder(UPLOAD_DIR + File.separator + file.getName());
                    files.add(fileDto);
                }
            }
        }
        Map<String, Object> dataMap = Maps.newHashMap();
        dataMap.put("rows", files);
        return R.data(dataMap);
    }

    @RequestMapping({"/deleteFile"})
    @ResponseBody
    public R deleteFile(@RequestParam String folder) {
        if (T.isBlank(folder)) {
            return R.error("请选择要删除的文件!");
        }
        File file = new File(folder);
        if (!file.exists()) {
            return R.error("文件不存在!");
        }
        file.delete();

        return R.ok("删除成功");
    }

    @RequestMapping({"/downloadFile"})
    @ResponseBody
    public void downloadFile(@RequestParam String folder, @Context HttpServletResponse response) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            if (T.isBlank(folder)) {
                throw new IllegalArgumentException("请选择要下载的文件!");
            }
            File file = new File(folder);
            if (!file.exists()) {
                throw new IllegalArgumentException("文件不存在!");
            }
            String fileName = T.UUID() + "_" + file.getName();
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Access-Control-Expose-Headers", "Content-disposition");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);
            inputStream = Files.newInputStream(file.toPath());

            byte[] b = new byte[1024];
            outputStream = new BufferedOutputStream(response.getOutputStream());
            int len;
            while ((len = inputStream.read(b)) > 0) {
                outputStream.write(b, 0, len);
            }
            outputStream.flush();
        } catch (Exception e) {
            log.error("下载文件异常", e);
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(R.error("下载文件失败:" + e.getMessage()).toJSONString());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    @RequestMapping({"/uploadParseExcel"})
    @ResponseBody
    public R uploadParseExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return R.error("请选择要上传的文件");
        }
        File targetFile = null;
        try {
            String filename = UPLOAD_DIR + File.separator + "temp" + File.separator + T.UUID() + T.getFileType(file.getOriginalFilename());
            targetFile = new File(filename);
            if (targetFile.exists()) {
                targetFile.delete();
            }
            targetFile.mkdirs();
            file.transferTo(targetFile);

            List<Pair<String, List<List<String>>>> oriExcel = ExcelAnalyzeUtils.showExcel(filename);
            if (T.isEmpty(oriExcel)) {
                return R.error("没有解析到数据，请检查文件内容!");
            }

            List<List<String>> lists = oriExcel.get(0).getValue();
            List<String> heads = new ArrayList<>();
            int headIndex = -1;
            for (int i = 0; i < lists.size(); i++) {
                List<String> rows = lists.get(i);

                if (ExcelAnalyzeUtils.checkExcelHead(rows)) {
                    heads = rows;
                    headIndex = i;
                    break;
                }
            }
            if (headIndex < 0 || lists.size() <= headIndex + 1) {
                return R.error("没有解析到相关数据，请检查文件表头!");
            }

            List<Map<String, String>> excelMap = new ArrayList<>();
            for (int j = headIndex + 1; j < lists.size(); j++) {
                List<String> rows = lists.get(j);
                Map<String, String> rowMap = new HashMap<>();
                for (int k = 0; k < heads.size(); k++) {
                    String val = "";
                    if (k < rows.size()) {
                        val = rows.get(k);
                    }
                    rowMap.put(heads.get(k), val);
                }
                excelMap.add(rowMap);
            }
            return R.ok("文件解析成功").setData(excelMap);
        } catch (Exception e) {
            log.error("uploadParseExcel err", e);
            return R.error("文件解析失败: " + e.getMessage());
        } finally {
            if (targetFile != null && targetFile.exists()) {
                targetFile.delete();
            }
        }
    }

    @RequestMapping({"/convertTable"})
    @ResponseBody
    public R convertTable(@RequestBody ImportExcelDto dto) throws Exception {
        return R.data(dto.convertTable());
    }

    @RequestMapping({"/importExcel"})
    @ResponseBody
    public R importExcel(@RequestBody ImportExcelDto dto) throws Exception {
        if (T.isBlank(dto.getUserName())) {
            return R.error("请输入姓名!");
        }
        if (T.isBlank(dto.getUserNumber())) {
            return R.error("请输入编号!");
        }
        if (T.isBlank(dto.getRecordTime())) {
            return R.error("请输入日期!");
        }
        if (T.isEmpty(dto.getTableList())) {
            return R.error("解析excel内容为空!");
        }
        if (T.isBlank(dto.getImportFileFolder())) {
            return R.error("请选择导入的汇总文件");
        }
        File importFile = new File(dto.getImportFileFolder());
        String importFilePath = dto.getImportFileFolder();
        if (T.isBlank(importFilePath) || !importFile.exists()) {
            return R.error("你选择的汇总文件不存在");
        }

        String sheetName = getSheetNameByNumber(dto.getUserNumber());
        List<Map<String, String>> importList = dto.getParseExcel();
        Integer result = ExcelAnalyzeUtils.writeFile(dto, dto.getImportFileFolder(), sheetName, importList);

        StringBuilder mag = new StringBuilder();
        int total = importList.size();
        int success = result;
        int err = total - success;
        mag.append("导入完成，总共：").append(total).append("条，");
        if (success > 0) {
            mag.append("成功：").append(success).append("条，");
        }
        if (err > 0) {
            mag.append("失败：").append(err).append("条，");
        }
        mag.append("可在上面列表中下载对应的最新汇总文件！");
        return R.ok().setMsg(mag.toString());
    }

    public String getSheetNameByNumber(String userNumber) {
        if (T.isBlank(userNumber) || userNumber.length() < 8) {
            throw new RuntimeException("编号不符合规则");
        }
        String num = userNumber.substring(userNumber.length() - 2);
        if (Objects.equals(num, "01"))
            return "睡眠日记-基线";
        if (Objects.equals(num, "05"))
            return "睡眠日记-第4周";
        if (Objects.equals(num, "06"))
            return "睡眠日记-第三个月";
        if (Objects.equals(num, "08")) {
            return "睡眠日记-第十二个月";
        }
        throw new RuntimeException("编号找不到相应的sheet页");
    }

}
