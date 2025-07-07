/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.api.listadecompras.controllers;

/**
 *
 * @author mao
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String login() {
        return "index"; // src/main/resources/templates/index.html
    }


    @GetMapping("/nova-lista")
    public String novaLista() {
        return "novaLista";
    }

    @GetMapping("/visualizar-lista")
    public String visualizarLista() {
        return "visualizarLista";
    }

    @GetMapping("/editar-item")
    public String editarItem() {
        return "editarItem";
    }


    @GetMapping("/logout")
    public String logout() {
        return "logout";
    }
}
