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

    // 2. RENOMBRAMOS 'addRepo' a 'createRepo' para mayor claridad y coherencia.
    @POST("user/repos")
    fun createRepo( // Nombre cambiado
        @Body repoRequest: RepoRequest
    ): Call<Repo>

    // 3. AÑADIMOS LA NUEVA FUNCIÓN PARA ACTUALIZAR (EDITAR)
    @PATCH("repos/{owner}/{repo}")
    fun updateRepo(
        @Path("owner") owner: String, // El dueño del repositorio
        @Path("repo") repoName: String, // El nombre original del repositorio a editar
        @Body repoRequest: RepoRequest // Los nuevos datos (nombre, descripción, etc.)
    ): Call<Repo>

    @DELETE("repos/{owner}/{repo}")
    fun deleteRepo(
        @Path("owner") owner: String,
        @Path("repo") repoName: String
    ): Call<Void>
}
