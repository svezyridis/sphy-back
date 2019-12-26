package sphy.subject.models;

import java.util.List;

public class RestResponse {
    private String status;
    private Object result;
    private String message;

    public RestResponse(String status,Object result,String message){
        this.status=status;
        this.result=result;
        this.message=message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
