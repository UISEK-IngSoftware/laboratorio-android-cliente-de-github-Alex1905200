package ec.edu.uisek.githubclient.services

import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.RepoRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH // 1. IMPORTAMOS LA ANOTACIÓN PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface GithubApiService {
    @GET("user/repos")
    fun getRepos(
        @Query("sort") sort: String = "created",
        @Query("direction") direction: String = "desc"
    ): Call<List<Repo>>

    // cambie el nombre de la funcion de addRepo a createRepo
    @POST("user/repos")
    fun createRepo( // Nombre cambiado
        @Body repoRequest: RepoRequest
    ): Call<Repo>

    // la nueva funcion para actualizar(editar) el repositorio
    @PATCH("repos/{owner}/{repo}")
    fun updateRepo(
        @Path("owner") owner: String, // El dueño del repositorio
        @Path("repo") repoName: String, // El nombre original del repositorio a editar
        @Body repoRequest: RepoRequest // Los nuevos datos (nombre, descripción, etc.)
    ): Call<Repo>
    // la funcion para eliminar el repositorio
    @DELETE("repos/{owner}/{repo}")
    fun deleteRepo(
        @Path("owner") owner: String,
        @Path("repo") repoName: String
    ): Call<Void>
}
