package kr.co.project.pokemoncompose.pokemonlist

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.co.project.pokemoncompose.data.model.PokedexListEntry
import kr.co.project.pokemoncompose.repository.PokemonRepository
import kr.co.project.pokemoncompose.utill.Constants
import kr.co.project.pokemoncompose.utill.Resource
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private var curPage = 0

    var pokemonList = mutableStateOf<List<PokedexListEntry>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    init {
        loadPokemonPaginated()
    }

    fun loadPokemonPaginated() {
        Log.d("test","loadPokemonPaginated")
        viewModelScope.launch {
            val result = repository.getPokemonList(Constants.PAGE_SIZE, curPage * Constants.PAGE_SIZE)

            when (result) {
                is Resource.Success -> {
                    endReached.value = curPage * Constants.PAGE_SIZE >= result.data!!.count

                    val pokedexEntries = result.data.results.mapIndexed { index, entry ->
                        val number = if (entry.url.endsWith("/")) {
                            entry.url.dropLast(1).takeLastWhile { it.isDigit() }
                        } else{
                            entry.url.takeLastWhile { it.isDigit() }
                        }

                        val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/" +
                                "pokemon/other/official-artwork/$number.png"
                        PokedexListEntry(entry.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }, url, number.toInt())
                    }
                    curPage++

                    loadError.value = ""
                    isLoading.value = false
                    pokemonList.value += pokedexEntries
                }

                is Resource.Error -> {
                    Log.d("test","Error")
                    Log.d("test","error_message = ${result.message!!}")

                    loadError.value = result.message!!
                    isLoading.value = false
                }

                else -> {

                }


            }
        }
    }


    fun calcDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

        Palette.from(bmp).generate { palatte ->
            palatte?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }
}