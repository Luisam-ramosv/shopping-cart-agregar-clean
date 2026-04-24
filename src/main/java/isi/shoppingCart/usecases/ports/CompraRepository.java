package isi.shoppingCart.usecases.ports;

import isi.shoppingCart.entities.Compra;

public interface CompraRepository {
    void guardar(Compra compra);
    int nextId();
}
