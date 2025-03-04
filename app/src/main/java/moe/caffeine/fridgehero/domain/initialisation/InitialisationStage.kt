package moe.caffeine.fridgehero.domain.initialisation

import kotlinx.coroutines.flow.MutableStateFlow

sealed class InitialisationStage(
  val name: String = "",
  val statusMessage: String = "",
  val progress: MutableStateFlow<Float> = MutableStateFlow(-1f),
  val state: InitialisationState = InitialisationState.INITIALISING
) {
  data object None : InitialisationStage(
    "",
    "",
    state = InitialisationState.NOT_STARTED
  )

  data object Started : InitialisationStage(
    "Started",
    "Fridge Hero is getting things ready for you..."
  )

  data object TaxonomyInitialisation : InitialisationStage(
    "Taxonomy Initialisation",
    "Loading Food Categories...",
    progress = MutableStateFlow(0f)
  )

  data object Error : InitialisationStage(
    "Error",
    statusMessage = "An error occurred during initialisation.",
    state = InitialisationState.ERROR
  )

  data object Finished : InitialisationStage(
    "Finished",
    "Loading Complete!",
    state = InitialisationState.READY
  )
}
