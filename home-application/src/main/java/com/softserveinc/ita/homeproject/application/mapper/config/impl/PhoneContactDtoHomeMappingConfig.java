package com.softserveinc.ita.homeproject.application.mapper.config.impl;

import com.softserveinc.ita.homeproject.application.mapper.HomeMapper;
import com.softserveinc.ita.homeproject.application.mapper.config.HomeMappingConfig;
import com.softserveinc.ita.homeproject.homeservice.dto.general.contact.ContactDto;
import com.softserveinc.ita.homeproject.homeservice.dto.general.contact.PhoneContactDto;
import com.softserveinc.ita.homeproject.model.CreatePhoneContact;
import lombok.RequiredArgsConstructor;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PhoneContactDtoHomeMappingConfig implements HomeMappingConfig<CreatePhoneContact, ContactDto> {

    @Lazy
    private final HomeMapper homeMapper;

    @Override
    public void addMappings(TypeMap<CreatePhoneContact, ContactDto> typeMap) {
        typeMap.setProvider(request -> homeMapper.convert(request.getSource(), PhoneContactDto.class));
    }
}
