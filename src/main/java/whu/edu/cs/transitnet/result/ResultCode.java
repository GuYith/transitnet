package whu.edu.cs.transitnet.result;

public enum ResultCode {
    SUCCESS(200),
    FAIL(400),
    UNAUTHORIZED(401),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500),
    WRONG_PASSWORD(300);

    public Integer code;
    ResultCode(int code) {
        this.code = code;
    }
}
