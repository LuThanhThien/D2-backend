package com.dainam.D2.annotation.dto.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.Set;

public interface DtoMapper<D, E> {
    E toEntity(D dto);

    D toDto(E entity);

    List<E> toListEntity(List<D> listDto);

    List<D> toListDto(List<E> listEntity);

    Set<E> toSetEntity(Set<D> setDto);

    Set<D> toSetDto(Set<E> setEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(D dto, @MappingTarget E entity);

}
