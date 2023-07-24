package ir.mehdiyari.krypt.ui.voice.record

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.mehdiyari.krypt.cryptography.api.KryptCryptographyHelper
import ir.mehdiyari.krypt.data.repositories.files.FilesRepository
import ir.mehdiyari.krypt.dispatchers.di.DispatchersQualifierType
import ir.mehdiyari.krypt.dispatchers.di.DispatchersType
import ir.mehdiyari.krypt.file.data.entity.FileEntity
import ir.mehdiyari.krypt.file.data.entity.FileTypeEnum
import ir.mehdiyari.krypt.ui.voice.recorder.SecondToTimerMapper
import ir.mehdiyari.krypt.ui.voice.recorder.VoiceRecorder
import ir.mehdiyari.krypt.ui.voice.recorder.meta.AudioMetaData
import ir.mehdiyari.krypt.ui.voice.recorder.meta.AudioMetaDataJsonParser
import ir.mehdiyari.krypt.utils.FilesUtilities
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class RecordVoiceViewModel @Inject constructor(
    private val voiceRecorder: VoiceRecorder,
    private val filesUtilities: FilesUtilities,
    private val kryptCryptographyHelper: KryptCryptographyHelper,
    @DispatchersType(DispatchersQualifierType.IO) private val ioDispatcher: CoroutineDispatcher,
    private val filesRepository: FilesRepository,
    private val audioMetaDataJsonAdapter: AudioMetaDataJsonParser,
    private val secondToTimerMapper: SecondToTimerMapper
) : ViewModel() {

    private val _recordTimer = MutableStateFlow("00:00:00")
    val recordTimer = _recordTimer.asStateFlow()
    private var timerJob: Job? = null
    private var timerAsSecond = 0L
        set(value) {
            field = value
            _recordTimer.value = secondToTimerMapper.map(value)
        }

    private val _recordVoiceViewState =
        MutableStateFlow<RecordVoiceViewState>(RecordVoiceViewState.Initialize)
    val recordVoiceViewState = _recordVoiceViewState.asStateFlow()

    private val _actionsButtonState = MutableStateFlow(
        RecordActionButtonsState(
            stop = true to ::onStopRecord,
            resume = false to ::onResumeRecord,
            save = true to ::onSaveRecord
        )
    )

    val actionsButtonState = _actionsButtonState.asStateFlow()

    fun startRecord() {
        if (!voiceRecorder.isInRecordingState()) {
            _recordVoiceViewState.value = RecordVoiceViewState.RecordStarted(isPaused = false)
            startTimer()

            viewModelScope.launch {
                voiceRecorder.startRecord(filesUtilities.getFilePathForVoiceRecord())
                voiceRecorder.setOnErrorListener { _, _, _ ->
                    _recordVoiceViewState.value = RecordVoiceViewState.MicError
                }
            }
        }
    }

    private fun onSaveRecord() {
        if (voiceRecorder.isInRecordingState()) {
            stopTimer()
            voiceRecorder.stopRecord()
        }

        viewModelScope.launch(ioDispatcher) {
            if (voiceRecorder.getRecordFilePath().isNotBlank()) {
                val encryptedDestination = filesUtilities.getRealFilePathForRecordedVoice()
                try {
                    if (kryptCryptographyHelper.encryptFile(
                            voiceRecorder.getRecordFilePath(),
                            encryptedDestination
                        ).isSuccess
                    ) {
                        saveAudioRecordInDataBase(
                            voiceRecorder.getRecordFilePath(),
                            encryptedDestination
                        )

                        if (!voiceRecorder.deleteAudioCacheFile()) {
                            filesUtilities.deleteCacheDir()
                        }

                        _recordVoiceViewState.value = RecordVoiceViewState.RecordSavedSuccessfully
                    } else {
                        _recordVoiceViewState.value = RecordVoiceViewState.RecordSavedFailed
                    }
                } catch (t: Throwable) {
                    _recordVoiceViewState.value = RecordVoiceViewState.RecordSavedFailed
                }
            } else {
                _recordVoiceViewState.value = RecordVoiceViewState.NavigateUp
            }
        }
    }

    private fun saveAudioRecordInDataBase(path: String, destinationPath: String) {
        val meta = audioMetaDataJsonAdapter.toJson(getRecordMetaData(path))
        viewModelScope.launch(ioDispatcher) {
            filesRepository.insertFiles(
                listOf(
                    FileEntity(
                        type = FileTypeEnum.Audio,
                        metaData = meta,
                        filePath = destinationPath,
                        accountName = ""
                    )
                )
            )
        }
    }

    private fun onResumeRecord() {
        if (!voiceRecorder.isInRecordingState()) {
            _recordVoiceViewState.value = RecordVoiceViewState.RecordStarted(isPaused = false)
            startTimer()
            voiceRecorder.resumeRecording()
            _actionsButtonState.value = _actionsButtonState.value.copy(
                stop = true to ::onStopRecord,
                resume = false to ::onResumeRecord,
            )
        }
    }

    private fun onStopRecord() {
        if (voiceRecorder.isInRecordingState()) {
            stopTimer()
            _recordVoiceViewState.value = RecordVoiceViewState.RecordStarted(isPaused = true)
            voiceRecorder.pauseRecord()
            _actionsButtonState.value = _actionsButtonState.value.copy(
                stop = false to ::onStopRecord,
                resume = true to ::onResumeRecord,
            )
        }
    }

    fun saveRecordRetry() {
        onSaveRecord()
    }

    private fun startTimer() {
        if (timerJob != null && timerJob!!.isActive) return
        timerJob = viewModelScope.launch(ioDispatcher) {
            for (i in 0..Long.MAX_VALUE) {
                ensureActive()
                delay(1000)
                timerAsSecond += 1
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob?.cancelChildren()
        timerJob = null
    }

    private fun getRecordMetaData(path: String): AudioMetaData =
        AudioMetaData(
            File(path).length(), timerAsSecond, System.currentTimeMillis()
        )

    override fun onCleared() {
        filesUtilities.deleteCacheDir()
        super.onCleared()
    }
}