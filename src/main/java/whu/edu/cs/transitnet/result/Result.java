package whu.edu.cs.transitnet.result;

public class Result {
    //响应码
    private Integer code;
    private String message;
    private Object result;
    public Result(Integer code){
        this.code = code;
    }
    public int getCode(){
        return code;
    }
    public void setCode(Integer code){
        this.code = code;
    }

    public Result(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.result = data;
    }
}
