package ec.edu.uisek.githubclient.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RepoOwner (
    val id:Long,
    val login: String,
    @SerializedName("avatar_url")
    val avatarUrl: String,
): Serializable


