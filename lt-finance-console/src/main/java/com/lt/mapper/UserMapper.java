package com.lt.mapper;

import com.lt.security.LoginUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select({"select id,username,password,is_enabled from lt_user where username = #{username}"})
    LoginUserEntity loadUserByUsername(@Param("username") String username);

    @Select({"select id,username,password,is_enabled from lt_user" +
            " where username = #{username} or email = #{username}"})
    LoginUserEntity loadUserByNameEmail(@Param("username") String username);

    LoginUserEntity loadUserByMobile(@Param("mobile") String mobile);
}
