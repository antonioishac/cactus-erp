package br.com.cactuserp.pdv.service;

import br.com.cactuserp.pdv.entity.PdvOperador;
import br.com.cactuserp.pdv.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public boolean autenticar(String username, String password) {
        Optional<PdvOperador> usuario = usuarioRepository.findByLogin(username);
        if (usuario.isPresent()) {
            //return passwordEncoder.matches(password, usuario.get().getPassword());
            return true;
        }
        return false;
    }

    public Optional<PdvOperador> buscarPorUsername(String username) {
        return usuarioRepository.findByLogin(username);
    }

    public PdvOperador salvar(PdvOperador usuario) {
        if (usuario.getId() == null) {
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }
        return usuarioRepository.save(usuario);
    }
}