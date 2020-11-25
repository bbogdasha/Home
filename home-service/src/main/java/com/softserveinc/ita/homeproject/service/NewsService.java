package com.softserveinc.ita.homeproject.service;

import com.softserveinc.ita.homeproject.service.dto.CreateOrUpdateNewsDto;
import com.softserveinc.ita.homeproject.service.dto.ReadNewsDto;

import java.util.Collection;

public interface NewsService {
    ReadNewsDto create(CreateOrUpdateNewsDto payload);
    ReadNewsDto update(Long id, CreateOrUpdateNewsDto payload);
    Collection<ReadNewsDto> getAll(Integer pageNumber, Integer pageSize);
    ReadNewsDto getById(Long id);
    void deleteById(Long id);
}
