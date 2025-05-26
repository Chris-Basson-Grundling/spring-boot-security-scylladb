package com.cbg.sbss.mapper;

import com.cbg.sbss.dto.RefreshTokenDto;
import com.cbg.sbss.entity.RefreshToken;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RefreshTokenMapper {

  RefreshTokenDto toDto(RefreshToken refreshToken);

  RefreshToken toEntity(RefreshTokenDto RefreshTokenDto);
}