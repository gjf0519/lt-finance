package com.lt.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

@Data
public class LoginUserEntity implements UserDetails {
    private String id;
    private String username;
    private String password;
    private String nickname;
    private String phone;
    private boolean isEnabled;
//    private List<UserApiEntity> userApis;

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new HashSet<>();
        Collection<GrantedAuthority> synchronizedCollection = Collections.synchronizedCollection(collection);
//        if (!CollectionUtils.isEmpty(userApis)) {
//            Map<String,List<UserApiEntity>> attributes = userApis.
//                    stream().collect(Collectors.groupingBy(UserApiEntity::getApiServer));
//            synchronizedCollection.add(new ApiGrantedAuthority(attributes));
//        }
        return collection;
    }
}
