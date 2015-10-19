/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.model.ballot

import javax.validation.constraints.Min

data class Poll(
        var question: String = "",
        var options: List<String> = arrayListOf(),
        var correctAnswers: List<Int>? = arrayListOf(),
        /**
         * Provide a hint for the user & UI.
         */
        var multipleAnswers: Boolean = false
)

data class Vote(
        var referencePoll: String = "",
        @Min(value = 0) var answers: List<Int> = arrayListOf(),
        @Min(value = 0) var correctAnswers: List<Int>? = arrayListOf(),
        var correct: Boolean? = false
) {
    var user: String = "anonymous"

    companion object {
        fun asUser(referencePoll: String, answers: List<Int>, correctAnswers: List<Int>, user: String): Vote {
            val vote = Vote(referencePoll = referencePoll, answers = answers, correctAnswers = correctAnswers)
            vote.user = user

            return vote
        }
    }
}