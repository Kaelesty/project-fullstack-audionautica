package com.kaelesty.audionautica.presentation.composables.music

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kaelesty.audionautica.R
import com.kaelesty.audionautica.domain.entities.Track
import com.kaelesty.audionautica.presentation.composables.access.GradientCard
import com.kaelesty.audionautica.presentation.ui.fonts.SpaceGrotesk
import com.kaelesty.audionautica.presentation.ui.theme.AudionauticaTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

@Preview
@Composable
fun MusicScreenPreview() {
	AudionauticaTheme {
		MusicScreen(
			onPlay = {},
			onPause = { },
			onResume = { },
			onSearch = {},
			onAddTrackToPlaylist = {},
			tracksSearchResults = MutableLiveData(),
			playingFlow = MutableSharedFlow(),
			trackFlow = MutableSharedFlow(),
		)
	}
}

@Composable
fun MusicScreen(
	//viewModel: MusicViewModel,
	onPlay: (Track) -> Unit,
	onPause: () -> Unit,
	onResume: () -> Unit,
	onSearch: (String) -> Unit,
	onAddTrackToPlaylist: (Track) -> Unit,
	tracksSearchResults: LiveData<List<Track>>,
	playingFlow: SharedFlow<Boolean>,
	trackFlow: SharedFlow<Track>
) {

	val mode = rememberSaveable {
		mutableStateOf(MusicScreenMode.SEARCH)
	}


	Scaffold(
		modifier = Modifier
			.fillMaxSize(),
		bottomBar = {
			Column {
				PlayerBar(
					onResume = { onResume() },
					onPause = { onPause() },
					playingFlow = playingFlow,
					trackFlow = trackFlow
				)
				MusicNavigationBar(mode)
			}
		},
	) {
		it
		Image(
			painter = painterResource(
				id = R.drawable.space_background
			),
			contentDescription = "Space Background",
			modifier = Modifier
				.fillMaxSize(),
			contentScale = ContentScale.Crop
		)
		when (mode.value) {
			MusicScreenMode.SEARCH -> Search(
				onSearch = onSearch,
				tracksSearchResults = tracksSearchResults,
				onPlay = onPlay,
				onPause = onPause,
				onAddTrack = onAddTrackToPlaylist,
			)

			MusicScreenMode.PLAYLISTS -> Playlists()
			MusicScreenMode.MY_TRACKS -> MyTracks()
		}
	}
}

@Composable
fun Playlists(
) {
}

@Composable
fun MyTracks() {
//	val tracks = viewModel.tracksSearchResults.observeAsState(mutableListOf())
//
//	Button(onClick = { onAddTrack() }) {
//		Text("Add track")
//	}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Search(
	onSearch: (String) -> Unit,
	tracksSearchResults: LiveData<List<Track>>,
	onPlay: (Track) -> Unit,
	onPause: () -> Unit,
	onAddTrack: (Track) -> Unit,
) {
	val tracks by tracksSearchResults.observeAsState(listOf())
	LazyColumn(
		modifier = Modifier
			.fillMaxSize(),
		content = {
			stickyHeader { MusicSearchBar(onSearch) }
			items(tracks, key = { it.id }) {
				TrackSearchResult(
					track = it,
					onClick = {
						onPlay(it)
					},
					onAdd = {
						onAddTrack(it)
					}
				)
			}
			item {
				Spacer(modifier = Modifier.height(100.dp))
			}
		},

		)
}

@Composable
fun PlayerBar(
	onResume: () -> Unit,
	onPause: () -> Unit,
	playingFlow: SharedFlow<Boolean>,
	trackFlow: SharedFlow<Track>
) {

	val playing by playingFlow.collectAsState(initial = false)
	val track by trackFlow.collectAsState(initial = Track(-1, "", "", listOf()))
	GradientCard {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(horizontal = 6.dp, vertical = 4.dp)
		) {
			Text(
				text = track.title + " - " + track.artist,
				fontFamily = SpaceGrotesk,
				fontWeight = FontWeight.Normal,
				fontSize = 16.sp,
				modifier = Modifier
					.weight(1f)
			)
			Icon(
				imageVector = if (!playing) Icons.Outlined.PlayArrow else Icons.Outlined.Done,
				contentDescription = null,
				modifier = Modifier
					.clickable {
						if (playing) {
							onPause()
						} else {
							onResume()
						}
					}
			)
		}
	}
}

@Composable
fun MusicSearchBar(
	onSearch: (String) -> Unit,
) {

	var query by rememberSaveable {
		mutableStateOf("")
	}

	GradientCard {
		OutlinedTextField(
			value = query,
			onValueChange = {
				query = it
				onSearch(it)
			},
			leadingIcon = {
				Icon(
					Icons.Outlined.Search,
					contentDescription = "Search Icon",
					modifier = Modifier
						.padding(8.dp)

				)
			},
			modifier = Modifier.fillMaxWidth(),
			singleLine = true
		)
	}
}

@Composable
fun MusicNavigationBar(mode: MutableState<MusicScreenMode>) {
	var selectedItem by rememberSaveable {
		mutableStateOf(0)
	}
	val items = listOf("Search", "Playlists", "My Tracks")

	NavigationBar(
		containerColor = Color.Transparent,
		modifier = Modifier.height(100.dp)
	) {
		GradientCard {
			Row(verticalAlignment = Alignment.CenterVertically) {
				items.forEachIndexed { index, item ->
					NavigationBarItem(
						selected = selectedItem == index,
						onClick = {
							selectedItem = index
							when (index) {
								0 -> {
									mode.value = MusicScreenMode.SEARCH
								}

								1 -> {
									mode.value = MusicScreenMode.PLAYLISTS
								}

								2 -> {
									mode.value = MusicScreenMode.MY_TRACKS
								}
							}
						},
						icon = {
							Icon(
								when (item) {
									"Search" -> Icons.Outlined.Search
									"Playlists" -> Icons.Outlined.List
									"My Tracks" -> Icons.Outlined.FavoriteBorder
									else -> Icons.Outlined.Warning
								},
								"Search Icon"
							)
						},
						modifier = Modifier.height(80.dp)
					)
				}
			}
		}
	}
}