package com.tiagoalmeida.lottery.ui.games

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tiagoalmeida.lottery.data.model.UserGame
import com.tiagoalmeida.lottery.databinding.AdapterGamesBinding

class GamesAdapter(
    private val events: GamesAdapterEvents
) : RecyclerView.Adapter<GamesViewHolder>() {

    private val games: MutableList<UserGame> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterGamesBinding.inflate(inflater, parent, false)
        return GamesViewHolder(binding, events)
    }

    override fun getItemCount(): Int = games.size

    override fun onBindViewHolder(holder: GamesViewHolder, position: Int) {
        holder.bind(games[position])
    }

    fun addGames(toAdd: List<UserGame>) {
        games.clear()
        games.addAll(toAdd)

        notifyDataSetChanged()
    }
}
