package isi.shoppingCart.usecases.services;

import isi.shoppingCart.entities.Compra;
import isi.shoppingCart.usecases.dto.OperationResult;

public class ConfirmarCompraResult extends OperationResult {
    private Compra compra;

    private ConfirmarCompraResult(boolean success, String errorMessage, Compra compra) {
        super(success, errorMessage);
        this.compra = compra;
    }

    public static ConfirmarCompraResult success(Compra compra) {
        return new ConfirmarCompraResult(true, null, compra);
    }

    public static ConfirmarCompraResult failure(String errorMessage) {
        return new ConfirmarCompraResult(false, errorMessage, null);
    }

    public Compra getCompra() {
        return compra;
    }
}
