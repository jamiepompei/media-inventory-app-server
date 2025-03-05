package com.inventory.app.server.mapper;

import com.inventory.app.server.entity.payload.request.UserRequest;
import com.inventory.app.server.entity.payload.response.UserResponse;
import com.inventory.app.server.entity.user.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "roles", target = "roles")
    UserResponse mapUserInfoToUserResponse(UserInfo userInfo);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "roles", target = "roles")
    UserInfo mapUserRequestToUserInfo(UserRequest userRequest);
}
