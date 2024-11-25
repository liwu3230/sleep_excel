package org.example.backend.web.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.example.backend.web.aspect.DateTime;

import javax.validation.constraints.Min;
import java.io.Serializable;

@Data
public class BaseParam implements Serializable {

    @DateTime(message = "格式错误，正确格式为：yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间:yyyy-MM-dd HH:mm:ss")
    private String startTime;

    @DateTime(message = "格式错误，正确格式为：yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间:yyyy-MM-dd HH:mm:ss")
    private String endTime;

    @ApiModelProperty(value = "当前页，默认是1")
    @Min(value = 1)
    private int page = 1;

    @ApiModelProperty(value = "每页最大行数,默认是10")
    @Min(value = 1)
    private int limit = 10;

    public int getOffset() {
        if (limit < 1 || page < 1) {
            return 0;
        } else {
            return limit * (page - 1);
        }
    }

}