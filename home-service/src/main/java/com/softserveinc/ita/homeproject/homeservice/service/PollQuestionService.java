package com.softserveinc.ita.homeproject.homeservice.service;

import com.softserveinc.ita.homeproject.homedata.entity.polls.templates.PollQuestion;
import com.softserveinc.ita.homeproject.homeservice.dto.polls.templates.PollQuestionDto;

public interface PollQuestionService extends QueryableService<PollQuestion, PollQuestionDto> {

    PollQuestionDto createPollQuestion(PollQuestionDto pollQuestionDto);

    PollQuestionDto updatePollQuestion(PollQuestionDto updatePollQuestion);

    void deactivatePollQuestion(Long pollId, Long pollQuestionId);
}
