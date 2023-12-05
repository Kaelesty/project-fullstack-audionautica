package com.kaelesty.audionautica.presentation.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaelesty.audionautica.domain.entities.Playlist
import com.kaelesty.audionautica.domain.entities.Track
import com.kaelesty.audionautica.domain.usecases.AddToTracksQueueUseCase
import com.kaelesty.audionautica.domain.usecases.AddTrackToPlaylistUseCase
import com.kaelesty.audionautica.domain.usecases.CreatePlaylistUseCase
import com.kaelesty.audionautica.domain.usecases.DeleteTrackFromPlaylistUseCase
import com.kaelesty.audionautica.domain.usecases.GetAllPlaylistsUseCase
import com.kaelesty.audionautica.domain.usecases.GetPlaylistTracksUseCase
import com.kaelesty.audionautica.domain.usecases.SearchTracksUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MusicViewModel @Inject constructor(
	private val searchTracksUseCase: SearchTracksUseCase,
	private val addToTracksQueueUseCase: AddToTracksQueueUseCase,
	private val getAllPlaylistsUseCase: GetAllPlaylistsUseCase,
	private val addTrackToPlaylistUseCase: AddTrackToPlaylistUseCase,
	private val deleteTrackFromPlaylistUseCase: DeleteTrackFromPlaylistUseCase,
	private val getPlaylistTracksUseCase: GetPlaylistTracksUseCase,
	private val createPlaylistUseCase: CreatePlaylistUseCase
): ViewModel() {

	private val _tracksSearchResults = MutableLiveData<List<Track>>()
	val tracksSearchResults: LiveData<List<Track>> get() = _tracksSearchResults

	private val _playedTrackUri = MutableLiveData<Uri>()
	val playedTrackUri: LiveData<Uri> get() = _playedTrackUri

	private val _playlistTracksFlow = MutableSharedFlow<List<Track>>()
	val playlistTracksFlow: SharedFlow<List<Track>> get() = _playlistTracksFlow

	fun search(query: String) {
		viewModelScope.launch {
			val searchResults = searchTracksUseCase(query)
			_tracksSearchResults.postValue(searchResults)
		}
	}

	fun playTrack(track: Track) {
		viewModelScope.launch {
			addToTracksQueueUseCase(
				tracks = listOf(track),
				dropQueue = true,
			)
		}
	}

	fun addTrackToPlaylist(track: Track, playlistId: Int) {
		viewModelScope.launch(Dispatchers.IO) {
			addTrackToPlaylistUseCase(track, playlistId)
		}
	}

	fun getPlaylists(): LiveData<List<Playlist>> {
		return getAllPlaylistsUseCase()
	}

	fun pause() {

	}

	fun removeTrackFromPlaylist(track: Track, id: Int) {
		viewModelScope.launch(Dispatchers.IO) {
			deleteTrackFromPlaylistUseCase(track, id)
		}
	}

	fun getPlaylistTracks(id: Int): List<Track> {
		return getPlaylistTracksUseCase(id)
	}

	fun createPlaylist(playlist: Playlist) {
		viewModelScope.launch(Dispatchers.IO) {
			createPlaylistUseCase(playlist)
		}
	}
}