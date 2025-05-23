package com.tiagoalmeida.lottery.ui.games

import androidx.recyclerview.widget.RecyclerView
import com.tiagoalmeida.lottery.R
import com.tiagoalmeida.lottery.data.model.UserGame
import com.tiagoalmeida.lottery.databinding.AdapterGamesBinding

class GamesViewHolder(
    private val binding: AdapterGamesBinding,
    private val events: GamesAdapterEvents
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(userGame: UserGame) {
        val type = userGame.type

        with(binding) {
            colorId = type.primaryColor
            textViewContest.text = type.toString()
            textViewContestNumber.text = if (userGame.isSingleGame()) {
                userGame.startContestNumber
            } else {
                formatContestNumber(
                    userGame.startContestNumber,
                    userGame.endContestNumber
                )
            }

            root.setOnClickListener { events.onGameClicked(userGame, binding.card) }
            root.setOnLongClickListener { events.onGameLongClicked(userGame) }
        }

    }

    private fun formatContestNumber(startContest: String, endContest: String): String {
        val endNumber =  if (endContest.isEmpty()) "∞" else endContest
        return String.format(binding.root.context.getString(R.string.contest_start_to_end), startContest, endNumber)
    }
}
