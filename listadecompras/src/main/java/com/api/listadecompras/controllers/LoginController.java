/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.api.listadecompras.controllers;

/**
 *
 * @author mao
 */

import com.api.listadecompras.model.Usuario;
import com.api.listadecompras.repository.UsuarioRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Exibe a página de login (index.html)
    @GetMapping("/login")
    public String mostrarLogin() {
        return "index"; // carrega src/main/resources/templates/index.html
    }
    @PostMapping("/login")
    public String processarLogin(@ModelAttribute Usuario usuario, HttpServletResponse response) {
        return usuarioRepository.findByLoginAndSenha(usuario.getLogin(), usuario.getSenha())
                .map(u -> {
                    Cookie cookie = new Cookie("usuarioId", u.getId().toString());
                    cookie.setPath("/");
                    cookie.setHttpOnly(true);
                    cookie.setMaxAge(60 * 60 * 2); // 2 horas
                    response.addCookie(cookie);
                    return "redirect:/listas/dashboard";
                })
                .orElse("redirect:/login?erro=true");
    }

}
