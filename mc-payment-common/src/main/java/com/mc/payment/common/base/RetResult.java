package com.mc.payment.common.base;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 对请求接口返回 Json 格式数据的简易封装。
 *
 * <p>
 * 所有预留字段：<br>
 * code = 状态码 <br>
 * msg  = 描述信息 <br>
 * data = 携带对象 <br>
 * </p>
 *
 * @author conor
 * @since 2024/1/25 18:45
 */
public class RetResult<T> implements Serializable {

    // 序列化版本号
    private static final long serialVersionUID = 1L;

    // 预定的状态码
    public static final int CODE_SUCCESS = 200;
    public static final int CODE_ERROR = 500;

    private int code;
    private String msg;
    private T data;
    private Map<String, Object> extraFieldMap;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Map<String, Object> getExtraFieldMap() {
        return extraFieldMap;
    }

    public void setExtraFieldMap(Map<String, Object> extraFieldMap) {
        this.extraFieldMap = extraFieldMap;
    }

    public void addExtraField(String key, Object value) {
        if (extraFieldMap == null) {
            extraFieldMap = new HashMap<>();
        }
        extraFieldMap.put(key, value);
    }

    public Object getExtraField(String key) {
        return extraFieldMap.get(key);
    }

    /**
     * 构建
     */
    public RetResult() {
    }

    /**
     * 构建
     *
     * @param code 状态码
     * @param msg  信息
     * @param data 数据
     */
    public RetResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // ============================  静态方法快速构建  ==================================

    // 构建成功
    public static <T> RetResult<T> ok() {
        return new RetResult<>(CODE_SUCCESS, "ok", null);
    }

    public static <T> RetResult<T> ok(String msg) {
        return new RetResult<>(CODE_SUCCESS, msg, null);
    }

    public static <T> RetResult<T> ok(String msg, T data) {
        return new RetResult<>(CODE_SUCCESS, msg, data);
    }

    public static <T> RetResult<T> code(int code) {
        return new RetResult<>(code, null, null);
    }

    public static <T> RetResult<T> code(int code, String msg) {
        return new RetResult<>(code, msg, null);
    }

    public static <T> RetResult<T> data(T data) {
        return new RetResult<>(CODE_SUCCESS, "ok", data);
    }

    // 构建失败
    public static <T> RetResult<T> error() {
        return new RetResult<>(CODE_ERROR, "error", null);
    }

    public static <T> RetResult<T> error(String msg) {
        return new RetResult<>(CODE_ERROR, msg, null);
    }
    
    public static <T> RetResult<T> error(String msg, T data) {
        return new RetResult<>(CODE_ERROR, msg, data);
    }

    public static <T> RetResult<T> error(Throwable t) {
        String message = t.getMessage();
        if (message != null && message.length() > 100) {
            message = message.substring(0, 100);
        }
        return new RetResult<>(CODE_ERROR, message, null);
    }

    // 构建指定状态码
    public static <T> RetResult<T> get(int code, String msg, T data) {
        return new RetResult<>(code, msg, data);
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "{"
                + "\"code\": " + this.getCode()
                + ", \"msg\": " + transValue(this.getMsg())
                + ", \"data\": " + transValue(this.getData())
                + ", \"extraFieldMap\": " + transValue(this.getExtraFieldMap())
                + "}";
    }

    /**
     * 转换 value 值：
     * 如果 value 值属于 String 类型，则在前后补上引号
     * 如果 value 值属于其它类型，则原样返回
     *
     * @param value 具体要操作的值
     * @return 转换后的值
     */
    private String transValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return "\"" + value + "\"";
        }
        return String.valueOf(value);
    }

    /**
     * 将指定值转化为指定类型
     *
     * @param <T> 泛型
     * @param obj 值
     * @param cs  类型
     * @return 转换后的值
     */
    @SuppressWarnings("unchecked")
    private static <T> T getValueByType(Object obj, Class<T> cs) {
        // 如果 obj 为 null 或者本来就是 cs 类型
        if (obj == null || obj.getClass().equals(cs)) {
            return (T) obj;
        }
        // 开始转换
        String obj2 = String.valueOf(obj);
        Object obj3;
        if (cs.equals(String.class)) {
            obj3 = obj2;
        } else if (cs.equals(int.class) || cs.equals(Integer.class)) {
            obj3 = Integer.valueOf(obj2);
        } else if (cs.equals(long.class) || cs.equals(Long.class)) {
            obj3 = Long.valueOf(obj2);
        } else if (cs.equals(short.class) || cs.equals(Short.class)) {
            obj3 = Short.valueOf(obj2);
        } else if (cs.equals(byte.class) || cs.equals(Byte.class)) {
            obj3 = Byte.valueOf(obj2);
        } else if (cs.equals(float.class) || cs.equals(Float.class)) {
            obj3 = Float.valueOf(obj2);
        } else if (cs.equals(double.class) || cs.equals(Double.class)) {
            obj3 = Double.valueOf(obj2);
        } else if (cs.equals(boolean.class) || cs.equals(Boolean.class)) {
            obj3 = Boolean.valueOf(obj2);
        } else if (cs.equals(char.class) || cs.equals(Character.class)) {
            obj3 = obj2.charAt(0);
        } else {
            obj3 = obj;
        }
        return (T) obj3;
    }

    public boolean isSuccess() {
        return CODE_SUCCESS == this.getCode();
    }
}
