package org.RRA.tax_appeal_system.DTOS.responses;

<<<<<<< HEAD
public record GenericResponse(
        int statusCode,
        String message
) {
}
=======
public class GenericResponse<T> {
    private int status;
    private String message;
    private T data;

    public GenericResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Getters and setters
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
>>>>>>> 56846b21f2875e2f142bc77ece649612375f8f27
