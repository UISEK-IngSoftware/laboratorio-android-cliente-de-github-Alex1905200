package ec.edu.uisek.githubclient.models

import java.io.Serializable

data class Repo (
    val id: Long,
    val name: String,
    val description: String?,
    val language: String?,
    val owner: RepoOwner
): Serializable
data class RepoRequest(
    val name: String,
    val description: String,
)