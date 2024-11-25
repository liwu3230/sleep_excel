package org.example.backend.web.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: liwu3
 * @Date: 2024/11/25 14:29
 */
@Data
public class ParseData {

    private String userName;
    private String userNumber;
    private List<Map<String, String>> data;

}
