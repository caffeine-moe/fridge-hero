package moe.caffeine.fridgehero.user.profile

import kotlinx.serialization.Serializable
import moe.caffeine.fridgehero.user.config.ProfileConfig
import java.util.*

@Serializable
data class ProfileImpl(
    override val id : String = "",
    override val config : ProfileConfig,
    override val fridge : List<String> = listOf(),
) : Profile() {

    companion object Builder {
        private fun generateId() : String = UUID.randomUUID().toString()

        fun config(config : ProfileConfig.() -> Unit) : ProfileConfig = ProfileConfig().also(config)

        fun build(config : Builder.() -> ProfileConfig) : ProfileImpl {
            return ProfileImpl(generateId(), config.invoke(this))
        }
    }
}