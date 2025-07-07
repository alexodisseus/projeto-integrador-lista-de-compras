/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.api.listadecompras.controllers;

/**
 *
 * @author mao
 */

import com.api.listadecompras.model.Listas;
import com.api.listadecompras.model.Usuario;
import com.api.listadecompras.repository.ListasRepository;
import com.api.listadecompras.repository.UsuarioRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/listas")
public class ListaController {

    @Autowired
    private ListasRepository listasRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/nova")
    public String criarLista(
            @RequestParam String nome,
            @RequestParam String descricao,
            HttpServletRequest request) {

        Long usuarioId = obterIdUsuarioDoCookie(request);
        if (usuarioId == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }

        Listas novaLista = new Listas();
        novaLista.setNome(nome);
        novaLista.setDescricao(descricao);
        novaLista.setData(LocalDate.now());
        novaLista.setUsuario(usuario);

        listasRepository.save(novaLista);
        return "redirect:/listas/dashboard";
    }

    private Long obterIdUsuarioDoCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("usuarioId".equals(cookie.getName())) {
                try {
                    return Long.valueOf(cookie.getValue());
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return null;
    }
    @GetMapping("/dashboard")
    public String mostrarDashboard(HttpServletRequest request, Model model) {
        Long usuarioId = obterIdUsuarioDoCookie(request);

        if (usuarioId == null) {
            return "redirect:/login";
        }

        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) {
            return "redirect:/login";
        }

        List<Listas> listas = listasRepository.findByUsuarioId(usuarioId);

        model.addAttribute("usuario", usuario);
        model.addAttribute("listas", listas);

        return "dashboard";
    }
    @GetMapping("/view")
    public String visualizarLista(@RequestParam Long id, HttpServletRequest request, Model model) {
        Long usuarioId = obterIdUsuarioDoCookie(request);
        if (usuarioId == null) {
            return "redirect:/login";
        }

        Listas lista = listasRepository.findById(id).orElse(null);

        // Verifica se a lista existe e pertence ao usuário logado
        if (lista == null || !lista.getUsuario().getId().equals(usuarioId)) {
            return "redirect:/dashboard";
        }

        model.addAttribute("lista", lista);
        return "viewLista";
    }


}
