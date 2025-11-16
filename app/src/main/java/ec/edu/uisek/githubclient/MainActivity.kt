package ec.edu.uisek.githubclient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityMainBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.services.GithubApiService
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var reposAdapter: ReposAdapter
    // Renombramos la variable para mayor claridad
    private val apiService: GithubApiService = RetrofitClient.gitHubApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupFab()
        // Eliminamos la llamada a fetchRepositories() y loadRepositories() de aquí
    }

    override fun onResume() {
        super.onResume()
        // Cada vez que la actividad vuelve a estar en primer plano, recargamos los repositorios.
        fetchRepositories()
    }

    private fun setupRecyclerView() {
        // 1. ADAPTACIÓN: Implementamos los listeners 'onEdit' y 'onDelete' al crear el adaptador.
        reposAdapter = ReposAdapter(
            onEditClicked = { repo ->
                // Lógica para editar: Llama a la función que abre el formulario de edición.
                displayEditRepoForm(repo)
            },
            onDeleteClicked = { repo ->
                // Lógica para eliminar: Muestra un diálogo de confirmación.
                showDeleteConfirmationDialog(repo)
            }
        )
        binding.reposRecyclerView.adapter = reposAdapter
    }

    // 2. NUEVA FUNCIÓN: Muestra un diálogo para confirmar la eliminación.
    private fun showDeleteConfirmationDialog(repo: Repo) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar eliminación")
            .setMessage("¿Estás seguro de que quieres eliminar '${repo.name}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                // Si el usuario confirma, llamamos a la función que elimina el repo de la API.
                deleteRepository(repo)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // 3. NUEVA FUNCIÓN: Llama a la API para eliminar el repositorio.
    private fun deleteRepository(repo: Repo) {
        // Asumimos que tu API necesita el nombre del dueño y del repositorio para eliminar.
        // Si tu modelo 'Repo' no tiene 'owner', necesitarás ajustarlo.
        val call = apiService.deleteRepo(repo.owner.login, repo.name)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio '${repo.name}' eliminado correctamente")
                    fetchRepositories() // Recarga la lista para reflejar el cambio.
                } else {
                    showMessage("Error al eliminar el repositorio: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showMessage("Fallo en la conexión: ${t.message}")
                Log.e("MainActivity", "Error al eliminar", t)
            }
        })
    }

    private fun fetchRepositories() {
        // Usamos la variable de clase 'apiService'
        val call = apiService.getRepos()

        call.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                if (response.isSuccessful) {
                    val repos = response.body()
                    if (repos != null) { // Simplificamos la comprobación
                        reposAdapter.updateRepositories(repos)
                    } else {
                        showMessage("No se encontraron repositorios")
                    }
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "No Autorizado"
                        403 -> "Prohibido"
                        404 -> "No Encontrado"
                        else -> "Error ${response.code()}"
                    }
                    showMessage("Error: $errorMessage")
                }
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                showMessage("No se pudieron cargar los repositorios")
                Log.e("MainActivity", "Error al cargar repositorios", t)
            }
        })
    }

    // 4. FUNCIÓN MODIFICADA: Ahora acepta un objeto Repo para pasarlo al formulario.
    private fun displayEditRepoForm(repo: Repo) {
        // Inicia RepoForm y le pasa el objeto 'repo' completo.
        // Asegúrate de que tu clase 'Repo' implemente 'Serializable'.
        val intent = Intent(this, RepoForm::class.java).apply {
            putExtra("EXTRA_REPO", repo)
        }
        startActivity(intent)
    }

    private fun setupFab() {
        // Esta función ahora solo maneja la creación de un nuevo repo.
        binding.repoFab.setOnClickListener {
            // Llama a displayNewRepoForm que no pasa ningún dato extra.
            displayNewRepoForm()
        }
    }

    private fun displayNewRepoForm() {
        // Inicia el formulario para crear un nuevo repositorio (sin extras).
        val intent = Intent(this, RepoForm::class.java)
        startActivity(intent)
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}