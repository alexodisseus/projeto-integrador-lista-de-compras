/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.api.listadecompras.repository;

/**
 *
 * @author mao
 */


import com.api.listadecompras.model.Listas;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ListasRepository extends JpaRepository<Listas, Long> {

    // Busca todas as listas de um determinado usuário
    List<Listas> findByUsuarioId(Long usuarioId);

    // Busca listas pelo nome (usado nos testes)
    List<Listas> findByNome(String nome);
}
