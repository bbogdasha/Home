package com.softserveinc.ita.homeproject.homeservice.service;

import com.softserveinc.ita.homeproject.homedata.entity.polls.templates.Poll;
import com.softserveinc.ita.homeproject.homeservice.dto.polls.templates.PollDto;

public interface PollService extends QueryableService<Poll, PollDto> {
    PollDto create(Long cooperationId, PollDto pollDto);

    PollDto update(Long cooperationId, Long id, PollDto pollDto);

    void deactivate(Long id);
}
