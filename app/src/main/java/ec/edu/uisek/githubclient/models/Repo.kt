package ec.edu.uisek.githubclient.models

import java.io.Serializable

data class Repo (
    val id: Long,
    val name: String,
    val description: String?,
    val language: String?,
    val owner: RepoOwner
): Serializable // divide el objeto en bytes para asi poder guardar la informacion y poder cambiarla
data class RepoRequest(
    val name: String,
    val description: String,
)