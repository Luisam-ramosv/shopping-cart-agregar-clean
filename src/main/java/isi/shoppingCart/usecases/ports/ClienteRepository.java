package isi.shoppingCart.usecases.ports;

import isi.shoppingCart.entities.Cliente;

public interface ClienteRepository {
    Cliente buscarPorId(int id);
    void guardar(Cliente cliente);
}
