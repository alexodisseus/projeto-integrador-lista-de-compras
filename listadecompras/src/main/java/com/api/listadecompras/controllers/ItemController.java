/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.api.listadecompras.controllers;

/**
 *
 * @author mao
 */


import com.api.listadecompras.model.Item;
import com.api.listadecompras.model.Listas;
import com.api.listadecompras.repository.ItemRepository;
import com.api.listadecompras.repository.ListasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/itens")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ListasRepository listasRepository;

    // ✅ Adicionar novo item
    @PostMapping("/adicionar")
    public String adicionarItem(
            @RequestParam Long listaId,
            @RequestParam String nome,
            @RequestParam int quantidade,
            @RequestParam String unidade) {

        Listas lista = listasRepository.findById(listaId).orElse(null);
        if (lista == null) {
            return "redirect:/dashboard";
        }

        Item item = new Item();
        item.setNome(nome);
        item.setQuantidade(quantidade);
        item.setUnidade(unidade);
        item.setLista(lista);

        itemRepository.save(item);
        return "redirect:/listas/view?id=" + listaId;
    }

    // ✅ Exibir formulário de edição (GET)
    @GetMapping("/editar")
    public String mostrarFormularioEdicao(@RequestParam Long id, Model model) {
        Item item = itemRepository.findById(id).orElse(null);
        if (item == null) {
            return "redirect:/erro"; // ou "redirect:/dashboard"
        }

        model.addAttribute("item", item);
        return "editarItem"; // nome do template HTML
    }

    // ✅ Salvar alterações do item (POST)
    @PostMapping("/editar")
    public String editarItem(
            @RequestParam Long itemId,
            @RequestParam String nome,
            @RequestParam int quantidade,
            @RequestParam String unidade
    ) {
        Item item = itemRepository.findById(itemId).orElse(null);
        if (item == null) {
            return "redirect:/erro"; // ou "redirect:/dashboard"
        }

        item.setNome(nome);
        item.setQuantidade(quantidade);
        item.setUnidade(unidade);

        itemRepository.save(item);

        Long listaId = item.getLista().getId();
        return "redirect:/listas/view?id=" + listaId;
    }
}
