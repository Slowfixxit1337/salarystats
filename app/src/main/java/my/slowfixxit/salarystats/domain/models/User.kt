package my.slowfixxit.salarystats.domain.models

data class User(
    val id: String,
    val name: String,
    val email: String,
    val token: String
)