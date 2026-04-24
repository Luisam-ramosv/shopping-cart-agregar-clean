package isi.shoppingCart.infrastructure.repositories;

import isi.shoppingCart.entities.Cliente;
import isi.shoppingCart.usecases.ports.ClienteRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryClienteRepository implements ClienteRepository {
    private List<Cliente> clientes;

    public InMemoryClienteRepository() {
        clientes = new ArrayList<Cliente>();
        clientes.add(new Cliente(1, "Cliente Demo"));
    }

    public Cliente buscarPorId(int id) {
        int i;
        for (i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getId() == id) {
                return clientes.get(i);
            }
        }
        return null;
    }

    public void guardar(Cliente cliente) {
        clientes.add(cliente);
    }
}
