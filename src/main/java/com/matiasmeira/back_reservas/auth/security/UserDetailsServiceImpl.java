package com.matiasmeira.back_reservas.auth.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.matiasmeira.back_reservas.auth.model.Usuario;
import com.matiasmeira.back_reservas.auth.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.List;

/**
 * Implementación de UserDetailsService que carga usuarios desde la base de datos.
 * Se utiliza en el proceso de autenticación de Spring Security.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    /**
     * Carga un usuario por su email (username).
     *
     * @param username El email del usuario
     * @return UserDetails del usuario encontrado
     * @throws UsernameNotFoundException Si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities(getAuthorities(usuario))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }

    /**
     * Obtiene las autoridades (roles) del usuario.
     *
     * @param usuario El usuario
     * @return Colección de autoridades
     */
    private Collection<GrantedAuthority> getAuthorities(Usuario usuario) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()));
    }
}
