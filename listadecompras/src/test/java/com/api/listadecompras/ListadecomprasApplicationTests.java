package com.api.listadecompras;

import com.api.listadecompras.model.Item;
import com.api.listadecompras.model.Listas;
import com.api.listadecompras.model.Usuario;
import com.api.listadecompras.repository.ItemRepository;
import com.api.listadecompras.repository.ListasRepository;
import com.api.listadecompras.repository.UsuarioRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ListadecomprasApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ListasRepository listasRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Usuario usuario;
    private Listas lista;

    @BeforeEach
    public void setup() {
        // Limpa bancos para testes isolados
        itemRepository.deleteAll();
        listasRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Cria usuário para testes
        usuario = new Usuario();
        usuario.setLogin("testuser");
        usuario.setSenha("testpass");
        usuarioRepository.save(usuario);

        // Cria lista para testes
        lista = new Listas();
        lista.setNome("Lista de Teste");
        lista.setDescricao("Descrição teste");
        lista.setData(LocalDate.now());
        lista.setUsuario(usuario);
        listasRepository.save(lista);
    }

    // 1. Login de usuário existente
    @Test
    public void testLoginValido() throws Exception {
        mockMvc.perform(post("/login")
                .param("login", "testuser")
                .param("senha", "testpass")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/listas/dashboard"));
    }

    @Test
    public void testLoginInvalido() throws Exception {
        mockMvc.perform(post("/login")
            .param("login", "usuarioInvalido")
            .param("senha", "senhaErrada"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login?erro=true"));
    }

    // 2. Criação de nova lista de compras
    @Test
    public void testCriarNovaLista() throws Exception {
        mockMvc.perform(post("/listas/nova")
                .param("nome", "Nova Lista")
                .param("descricao", "Descrição nova lista")
                .cookie(new Cookie("usuarioId", usuario.getId().toString()))
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/listas/dashboard"));

        Listas listaCriada = listasRepository.findByNome("Nova Lista").stream().findFirst().orElse(null);
        assert listaCriada != null;
        assert listaCriada.getDescricao().equals("Descrição nova lista");
        assert listaCriada.getUsuario().getId().equals(usuario.getId());
    }

    // 3. Adicionar item a uma lista existente
    @Test
    public void testAdicionarItem() throws Exception {
        mockMvc.perform(post("/itens/adicionar")
                .param("listaId", lista.getId().toString())
                .param("nome", "Arroz")
                .param("quantidade", "2")
                .param("unidade", "kg")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/listas/view?id=" + lista.getId()));

        Item item = itemRepository.findAll().stream()
                .filter(i -> i.getNome().equals("Arroz"))
                .findFirst()
                .orElse(null);

        assert item != null;
        assert item.getQuantidade() == 2;
        assert item.getUnidade().equals("kg");
        assert item.getLista().getId().equals(lista.getId());
    }

    // 4. Editar item existente na lista
    @Test
    public void testEditarItem() throws Exception {
        Item item = new Item();
        item.setNome("Feijão");
        item.setQuantidade(1);
        item.setUnidade("kg");
        item.setLista(lista);
        itemRepository.save(item);

        mockMvc.perform(post("/itens/editar")
                .param("itemId", item.getId().toString())
                .param("nome", "Feijão Preto")
                .param("quantidade", "3")
                .param("unidade", "pacotes")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/listas/view?id=" + lista.getId()));

        Item itemAtualizado = itemRepository.findById(item.getId()).orElse(null);
        assert itemAtualizado != null;
        assert itemAtualizado.getNome().equals("Feijão Preto");
        assert itemAtualizado.getQuantidade() == 3;
        assert itemAtualizado.getUnidade().equals("pacotes");
    }
}
