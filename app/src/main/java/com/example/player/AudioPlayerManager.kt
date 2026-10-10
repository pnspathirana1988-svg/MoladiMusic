package com.example.player

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import com.example.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

data class PlaybackState(
  val currentSong: Song? = null,
  val isPlaying: Boolean = false,
  val isBuffering: Boolean = false,
  val currentPositionMs: Long = 0L,
  val totalDurationMs: Long = 0L,
  val isShuffleEnabled: Boolean = false,
  val repeatMode: RepeatMode = RepeatMode.OFF,
  val queue: List<Song> = emptyList(),
  val queueIndex: Int = -1,
  val volume: Float = 1.0f
) {
  val progressFraction: Float
    get() = if (totalDurationMs > 0) (currentPositionMs.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f) else 0f

  val currentPositionFormatted: String
    get() = formatTime(currentPositionMs / 1000)

  val totalDurationFormatted: String
    get() = formatTime(totalDurationMs / 1000)

  private fun formatTime(seconds: Long): String {
    val m = seconds / 60
    val s = seconds % 60
    return "%02d:%02d".format(m, s)
  }
}

enum class RepeatMode {
  OFF,
  ALL,
  ONE
}

class AudioPlayerManager(private val context: Context) {
  private val scope = CoroutineScope(Dispatchers.Main + Job())
  private var mediaPlayer: MediaPlayer? = null
  private var progressJob: Job? = null
  private var synthJob: Job? = null
  private var audioTrack: AudioTrack? = null

  private val _playbackState = MutableStateFlow(PlaybackState())
  val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

  // Track listening history callback
  var onSongPlayed: ((Song) -> Unit)? = null

  fun setQueue(songs: List<Song>, startIndex: Int = 0) {
    if (songs.isEmpty()) return
    val index = startIndex.coerceIn(0, songs.size - 1)
    _playbackState.value = _playbackState.value.copy(
      queue = songs,
      queueIndex = index
    )
    playSong(songs[index])
  }

  fun playSong(song: Song) {
    stopCurrentPlayback()
    val durationMs = (song.durationSeconds * 1000).toLong()

    _playbackState.value = _playbackState.value.copy(
      currentSong = song,
      isPlaying = true,
      isBuffering = true,
      currentPositionMs = 0L,
      totalDurationMs = durationMs
    )

    onSongPlayed?.invoke(song)

    try {
      val mp = MediaPlayer().apply {
        setAudioAttributes(
          AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()
        )
        setDataSource(context, Uri.parse(song.streamUrl))
        setOnPreparedListener { player ->
          _playbackState.value = _playbackState.value.copy(
            isBuffering = false,
            totalDurationMs = if (player.duration > 0) player.duration.toLong() else durationMs
          )
          player.start()
          startProgressTracking()
        }
        setOnCompletionListener {
          handleSongCompletion()
        }
        setOnErrorListener { _, what, extra ->
          Log.w("AudioPlayerManager", "MediaPlayer error $what, $extra. Switching to audio synthesizer fallback.")
          fallbackToSynthMelody(song)
          true
        }
        prepareAsync()
      }
      mediaPlayer = mp
    } catch (e: Exception) {
      Log.e("AudioPlayerManager", "Exception preparing media player: ${e.message}. Using melodic synth fallback.")
      fallbackToSynthMelody(song)
    }
  }

  private fun fallbackToSynthMelody(song: Song) {
    _playbackState.value = _playbackState.value.copy(isBuffering = false, isPlaying = true)
    startProgressTracking()

    // Start pleasant ambient synth generator on background thread
    synthJob?.cancel()
    synthJob = CoroutineScope(Dispatchers.Default).launch {
      val sampleRate = 44100
      val minBufSize = AudioTrack.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT
      )

      try {
        val track = AudioTrack(
          AudioManager.STREAM_MUSIC,
          sampleRate,
          AudioFormat.CHANNEL_OUT_MONO,
          AudioFormat.ENCODING_PCM_16BIT,
          minBufSize,
          AudioTrack.MODE_STREAM
        )
        audioTrack = track
        track.play()

        // Generate harmonic chord progression (C - Am - F - G)
        val chordFreqs = listOf(
          listOf(261.63, 329.63, 392.0),
          listOf(220.0, 261.63, 329.63),
          listOf(174.61, 220.0, 261.63),
          listOf(196.0, 246.94, 293.66)
        )
        var chordIdx = 0

        val buffer = ShortArray(1024)
        var sampleIdx = 0

        while (isActive && _playbackState.value.isPlaying) {
          val activeChord = chordFreqs[chordIdx % chordFreqs.size]
          for (i in buffer.indices) {
            val t = sampleIdx.toDouble() / sampleRate
            var sampleVal = 0.0
            for (f in activeChord) {
              sampleVal += sin(2.0 * Math.PI * f * t) * 0.25
            }
            buffer[i] = (sampleVal * Short.MAX_VALUE * 0.4).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            sampleIdx++
          }
          track.write(buffer, 0, buffer.size)

          if (sampleIdx % (sampleRate * 2) == 0) {
            chordIdx++
          }
        }
        track.stop()
        track.release()
      } catch (e: Exception) {
        Log.e("AudioPlayerManager", "Synth error: ${e.message}")
      }
    }
  }

  fun togglePlayPause() {
    val state = _playbackState.value
    if (state.currentSong == null) {
      if (state.queue.isNotEmpty()) {
        playSong(state.queue.first())
      }
      return
    }

    if (state.isPlaying) {
      pause()
    } else {
      resume()
    }
  }

  fun pause() {
    mediaPlayer?.let {
      if (it.isPlaying) it.pause()
    }
    progressJob?.cancel()
    synthJob?.cancel()
    _playbackState.value = _playbackState.value.copy(isPlaying = false)
  }

  fun resume() {
    val mp = mediaPlayer
    if (mp != null) {
      try {
        mp.start()
        _playbackState.value = _playbackState.value.copy(isPlaying = true)
        startProgressTracking()
      } catch (e: Exception) {
        _playbackState.value?.currentSong?.let { fallbackToSynthMelody(it) }
      }
    } else {
      _playbackState.value?.currentSong?.let { fallbackToSynthMelody(it) }
    }
  }

  fun seekTo(positionMs: Long) {
    val boundedPos = positionMs.coerceIn(0L, _playbackState.value.totalDurationMs)
    mediaPlayer?.let {
      try {
        it.seekTo(boundedPos.toInt())
      } catch (_: Exception) {}
    }
    _playbackState.value = _playbackState.value.copy(currentPositionMs = boundedPos)
  }

  fun skipForward10s() {
    val newPos = _playbackState.value.currentPositionMs + 10000L
    seekTo(newPos)
  }

  fun rewind10s() {
    val newPos = (_playbackState.value.currentPositionMs - 10000L).coerceAtLeast(0L)
    seekTo(newPos)
  }

  fun playNext() {
    val state = _playbackState.value
    if (state.queue.isEmpty()) return

    val nextIdx = if (state.isShuffleEnabled) {
      (state.queue.indices).random()
    } else {
      (state.queueIndex + 1) % state.queue.size
    }

    _playbackState.value = state.copy(queueIndex = nextIdx)
    playSong(state.queue[nextIdx])
  }

  fun playPrevious() {
    val state = _playbackState.value
    if (state.queue.isEmpty()) return

    if (state.currentPositionMs > 3000L) {
      seekTo(0L)
      return
    }

    val prevIdx = if (state.queueIndex > 0) state.queueIndex - 1 else state.queue.size - 1
    _playbackState.value = state.copy(queueIndex = prevIdx)
    playSong(state.queue[prevIdx])
  }

  fun toggleShuffle() {
    _playbackState.value = _playbackState.value.copy(
      isShuffleEnabled = !_playbackState.value.isShuffleEnabled
    )
  }

  fun cycleRepeatMode() {
    val nextMode = when (_playbackState.value.repeatMode) {
      RepeatMode.OFF -> RepeatMode.ALL
      RepeatMode.ALL -> RepeatMode.ONE
      RepeatMode.ONE -> RepeatMode.OFF
    }
    _playbackState.value = _playbackState.value.copy(repeatMode = nextMode)
  }

  private fun handleSongCompletion() {
    when (_playbackState.value.repeatMode) {
      RepeatMode.ONE -> {
        seekTo(0L)
        resume()
      }
      RepeatMode.ALL -> playNext()
      RepeatMode.OFF -> {
        if (_playbackState.value.queueIndex < _playbackState.value.queue.size - 1) {
          playNext()
        } else {
          pause()
          seekTo(0L)
        }
      }
    }
  }

  private fun startProgressTracking() {
    progressJob?.cancel()
    progressJob = scope.launch {
      while (isActive && _playbackState.value.isPlaying) {
        val mp = mediaPlayer
        if (mp != null && mp.isPlaying) {
          try {
            _playbackState.value = _playbackState.value.copy(
              currentPositionMs = mp.currentPosition.toLong(),
              totalDurationMs = if (mp.duration > 0) mp.duration.toLong() else _playbackState.value.totalDurationMs
            )
          } catch (_: Exception) {}
        } else {
          // Synthetic position advancing
          val cur = _playbackState.value.currentPositionMs + 250L
          if (cur >= _playbackState.value.totalDurationMs && _playbackState.value.totalDurationMs > 0) {
            handleSongCompletion()
          } else {
            _playbackState.value = _playbackState.value.copy(currentPositionMs = cur)
          }
        }
        delay(250)
      }
    }
  }

  private fun stopCurrentPlayback() {
    progressJob?.cancel()
    synthJob?.cancel()
    try {
      audioTrack?.stop()
      audioTrack?.release()
    } catch (_: Exception) {}
    audioTrack = null

    mediaPlayer?.let {
      try {
        if (it.isPlaying) it.stop()
        it.reset()
        it.release()
      } catch (_: Exception) {}
    }
    mediaPlayer = null
  }

  fun release() {
    stopCurrentPlayback()
  }
}
