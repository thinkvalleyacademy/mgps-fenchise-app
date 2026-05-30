package com.mgps.user.service;

import com.mgps.user.entity.AppUser;
import com.mgps.user.service.RolePermissionService;
import com.mgps.user.repository.AppUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final RolePermissionService rolePermissionService;

    public AppUserDetailsService(AppUserRepository appUserRepository, RolePermissionService rolePermissionService) {
        this.appUserRepository = appUserRepository;
        this.rolePermissionService = rolePermissionService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = appUserRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new User(
            user.getEmail(),
            user.getPasswordHash(),
            user.getStatus() == com.mgps.user.entity.UserStatus.ACTIVE,
            true,
            true,
            user.getStatus() == com.mgps.user.entity.UserStatus.ACTIVE,
            buildAuthorities(user)
        );
    }

    private List<SimpleGrantedAuthority> buildAuthorities(AppUser user) {
        List<SimpleGrantedAuthority> authorities = rolePermissionService.getAuthorityNamesForRole(user.getRole())
            .stream()
            .map(SimpleGrantedAuthority::new)
            .toList();

        authorities = new java.util.ArrayList<>(authorities);
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        return List.copyOf(authorities);
    }
}
