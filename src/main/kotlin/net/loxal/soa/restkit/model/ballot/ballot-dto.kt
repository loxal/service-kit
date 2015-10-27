/*
 * Copyright 2015 Alexander Orlov <alexander.orlov@loxal.net>. All rights reserved.
 */

package net.loxal.soa.restkit.model.ballot

import javax.validation.constraints.Min

data class Poll(
        var question: String = "",
        var options: List<String> = arrayListOf(),
        var correctAnswers: List<Int>? = arrayListOf(), // TODO make them invisible for non-admins
        /**
         * Provide a hint for the user & UI.
         */
        var multipleAnswers: Boolean = false
)

data class ReviewedVote(
        var referencePoll: String = "",
        @Min(value = 0) var answers: List<Int> = arrayListOf()
) {
    var user: String = "anonymous"
    var correct: Boolean? = false
}

data class Vote(
        var referencePoll: String = "",
        @Min(value = 0) var answers: List<Int> = arrayListOf()
) {
    var user: String = "anonymous"
    var correct: Boolean? = false

    companion object {
        fun asUser(referencePoll: String, answers: List<Int>, user: String): Vote {
            val vote = Vote(referencePoll = referencePoll, answers = answers)
            vote.user = user

            return vote
        }
    }
}