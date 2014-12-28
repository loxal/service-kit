/*
 * Copyright 2014 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.model.ballot

import javax.validation.constraints.NotNull
import javax.validation.constraints.Min

data class Poll(NotNull var question: String = "", NotNull var answers: List<String> = arrayListOf())

data class Vote(NotNull var referencePoll: String = "", NotNull Min(value = 0) var answerOptionIndex: Int = 0)