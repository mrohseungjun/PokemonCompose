package kr.co.project.pokemoncompose.repository

import android.util.Log
import dagger.hilt.android.scopes.ActivityScoped
import kr.co.project.pokemoncompose.data.remote.PokeApi
import kr.co.project.pokemoncompose.data.remote.response.Pokemon
import kr.co.project.pokemoncompose.data.remote.response.PokemonList
import kr.co.project.pokemoncompose.utill.Resource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepository @Inject constructor(
    private val api: PokeApi
) {
    suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList> {
        val response = try {
            api.getPokemonList(limit, offset)
        } catch (e: Exception) {
            Log.d("test","Exception = ${e.message}")
            return Resource.Error("An unknown error occured.")
        }
        return Resource.Success(response)
    }

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        val response = try {
            api.getPokemonInfo(pokemonName)
        } catch (e: Exception) {
            return Resource.Error("An unknown error occured.")
        }
        return Resource.Success(response)
    }
}