package com.softserveinc.ita.homeproject.homeservice.dto.poll.results;

import com.softserveinc.ita.homeproject.homeservice.dto.BaseDto;
import com.softserveinc.ita.homeproject.homeservice.dto.poll.enums.PollQuestionTypeDto;
import com.softserveinc.ita.homeproject.homeservice.dto.poll.question.PollQuestionDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ResultQuestionDto extends BaseDto {

    private PollQuestionTypeDto type;

    private PollQuestionDto question;

    private int voteCount;
}
