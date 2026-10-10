package com.example.model

enum class AiEngine(val title: String, val badge: String, val description: String) {
  GOOGLE_LYRIA(
    title = "Google Lyria",
    badge = "DeepMind Audio",
    description = "Next-gen harmonic synthesizer with crystal acoustic resonance and vocal fidelity."
  ),
  SUNO_AI(
    title = "Suno v3.5",
    badge = "Generative Audio",
    description = "Full-spectrum composition engine generating complete arrangements, verses, and beats."
  )
}

data class AiRecommendation(
  val song: Song,
  val matchPercentage: Int,
  val reason: String
)
