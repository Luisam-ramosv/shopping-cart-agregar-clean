package isi.shoppingCart.usecases.ports;

public interface PaymentService {
    boolean procesarPago(int clienteId, double monto);
}
