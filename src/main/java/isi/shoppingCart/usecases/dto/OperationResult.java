package isi.shoppingCart.usecases.dto;

public class OperationResult {
    private boolean success;
    private String errorMessage;

    protected OperationResult(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static OperationResult ok() {
        return new OperationResult(true, null);
    }

    public static OperationResult failure(String errorMessage) {
        return new OperationResult(false, errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
