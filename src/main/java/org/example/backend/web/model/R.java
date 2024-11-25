package org.example.backend.web.model;

import com.alibaba.fastjson.JSONObject;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.example.backend.web.util.T;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class R extends HashMap<String, Object> {
    public static final int CODE_OK = 0;
    public static final int CODE_ERROR_NEG1 = -1;
    public static final int CODE_ERROR_1 = 1;
    public static final int CODE_ERROR_500 = 500;
    public static final int CODE_WARN = -2;
    public static final String CODE = "code";
    public static final String MSG = "msg";
    public static final String DATA = "data";
    public static final String PAGE = "page";
    public static final String SUCCESS = "success";
    private static final long serialVersionUID = 1L;


    public R() {
        put(CODE, CODE_OK);
        put(MSG, "success");
        put(SUCCESS, true);
    }

    public static R error() {
        return error(CODE_ERROR_500, "未知异常，请联系管理员");
    }

    public static R errorNG1(String msg) {
        return error(CODE_ERROR_NEG1, msg);
    }

    public static R error(String msg) {
        return error(CODE_ERROR_500, msg);
    }

    public static R errorData(Object data) {
        return R.error().setData(data);
    }

    public static R error(int code, String msg) {
        R r = new R();
        r.put(CODE, code);
        r.put(SUCCESS, false);
        if (StringUtils.isBlank(msg)) {
            msg = "系统异常";
        }
        r.put(MSG, msg);
        return r;
    }

    public static R warn(String msg) {
        return error(CODE_WARN, msg);
    }

    public static R ok(String msg) {
        R r = new R();
        r.put(MSG, msg);
        return r;
    }

    public static R page(Page page) {
        R r = new R();
        r.put(PAGE, page);
        return r;
    }

    public static R data(Object data) {
        R r = new R();
        r.put(DATA, data);
        return r;
    }

    public static R error400() {
        return R.error(400, "参数错误");
    }

    public static R error400(String msg) {
        return R.error(400, "参数错误: " + msg);
    }

    public static R error401() {
        return R.error(401, "未认证");
    }

    public static R error403() {
        return R.error(403, "未授权");
    }

    public static R error404() {
        return R.error(404, "找不到URI");
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    public static R ok() {
        return new R();
    }

    public static R parse(String string) {
        try {
            R r = JSONObject.parseObject(string, R.class);
            if (r == null) {
                return R.error("parse msg error:" + T.subString(string, 100));
            }

            return r;
        } catch (Exception e) {
            return R.error("JSONObject.parseObject eror,string=" + string);
        }
    }

    public Integer getCode() {
        Object o = get(CODE);
        if (o == null) {
            return null;
        }

        if (!(o instanceof Number)) {
            return Integer.valueOf(o.toString());
        }

        if (o instanceof Integer) {
            return (Integer) o;
        }
        return ((Number) o).intValue();
    }

    public R setCode(int code) {
        this.put(CODE, code);
        return this;
    }

    public boolean isOk() {
        Integer code = getCode();
        return code != null && code == CODE_OK;
    }

    public boolean isNotOk() {
        return !isOk();
    }

    public void errorQuick(String msg) {
        put(CODE, CODE_ERROR_500);
        put(MSG, msg);
        put(SUCCESS, false);
    }

    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public R setMsg(String msg) {
        this.put(MSG, msg);
        return this;
    }

    public String getMsg() {
        return Objects.toString(this.get(MSG), null);
    }

    public Page getPage() {
        return (Page) this.get(PAGE);
    }

    public R setPage(Page page) {
        this.put(PAGE, page);
        return this;
    }

    public Boolean getSuccess() {
        return (Boolean) this.get(SUCCESS);
    }

    public R setSuccess(boolean success) {
        this.put(SUCCESS, success);
        return this;
    }

    public Page getPageForObject(Type type) {
        return JSONObject.parseObject(JSONObject.toJSONString(this.get(PAGE)), type);
    }

    public <T> Page<T> getPageForObject(Class<T> clazz) {
        Type type = new TypeToken<Page<T>>() {
        }
                .where(new TypeParameter<T>() {
                }, clazz).getType();
        return JSONObject.parseObject(JSONObject.toJSONString(this.get(PAGE)), type);
    }

    public <T> T getData() {
        return (T) get(DATA);
    }

    public R setData(Object data) {
        this.put(DATA, data);
        return this;
    }

    public <T> T getValue(String key) {
        return (T) get(key);
    }

    public <T> T getDataForObject(Class<T> clazz) {
        Object object = get(DATA);
        return JSONObject.parseObject(JSONObject.toJSONString(object), clazz);
    }

    public <T> List<T> getDataForList(Class<T> clazz) {
        Object object = get(DATA);
        return JSONObject.parseArray(JSONObject.toJSONString(object), clazz);
    }

    public R removeSuccessField() {
        this.remove(SUCCESS);
        return this;
    }

    public String toJSONString() {
        return JSONObject.toJSONString(this);
    }


}

