package com.menea.ecommerce.product_service.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class KeycloakRealmRoleConverter
        implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess == null) {
            return Collections.emptyList();
        }

        Object rolesObject = realmAccess.get("roles");

        if (!(rolesObject instanceof List<?> roles)) {
            return Collections.emptyList();
        }

        return roles.stream()
                .map(Object::toString)
                .map(role -> new SimpleGrantedAuthority(
                        "ROLE_" + role
                ))
                .map(GrantedAuthority.class::cast)
                .toList();
    }
}