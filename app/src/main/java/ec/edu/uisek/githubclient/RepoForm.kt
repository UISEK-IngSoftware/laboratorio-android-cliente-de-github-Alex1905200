package ec.edu.uisek.githubclient

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityRepoFormBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.RepoRequest
import ec.edu.uisek.githubclient.services.GithubApiService
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepoForm : AppCompatActivity() {
    private lateinit var binding: ActivityRepoFormBinding
    private val apiService: GithubApiService = RetrofitClient.getApiService()
    private var repoToEdit: Repo? = null // Variable para guardar el repo si estamos editando

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepoFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //RECUPERAR EL OBJETO 'Repo' COMPLETO
        // MainActivity envía "EXTRA_REPO", así que lo leemos aquí.
        repoToEdit = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("EXTRA_REPO", Repo::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("EXTRA_REPO") as? Repo
        }

        //CONFIGURAR VISTA Y BOTONES SEGÚN EL MODO ---
        if (repoToEdit != null) {
            // Si recibimos un repo, estamos en MODO EDICIÓN.
            prepareEditMode(repoToEdit!!)
        } else {
            // Si no, estamos en MODO CREACIÓN.
            prepareCreateMode()
        }
        //sino si se aplasta el boton de cancelar se acaba
        binding.cancelButton.setOnClickListener { finish() }
    }


    private fun prepareEditMode(repo: Repo) {
        supportActionBar?.title = "Editar Repositorio"
        binding.saveButton.text = "Actualizar"

        // Llenamos el formulario con los datos del repo.
        binding.repoNameInput.setText(repo.name)
        binding.repoDescriptionInput.setText(repo.description)
        //Se quita la edicion del nombre del repositorio
        binding.repoNameInput.isEnabled = false
        // Opcional: Cambiamos el color para que se vea claramente que está deshabilitado
        binding.repoNameInput.alpha = 0.5f
        // El botón de guardar AHORA llama a la función de actualizar.
        binding.saveButton.setOnClickListener { handleUpdate(repo) }
    }

    private fun prepareCreateMode() {
        supportActionBar?.title = "Nuevo Repositorio"
        binding.saveButton.text = "Crear"
        binding.repoNameInput.isEnabled = true
        binding.repoNameInput.alpha = 1.0f
        // El botón de guardar llama a la función de crear.
        binding.saveButton.setOnClickListener { handleCreate() }
    }

    private fun handleCreate() {
        if (!validateForm()) return

        val repoName = binding.repoNameInput.text.toString().trim()
        val repoDescription = binding.repoDescriptionInput.text.toString().trim()
        val repoRequest = RepoRequest(repoName, repoDescription)

        // Llamamos a la API para crear el repositorio.
        apiService.createRepo(repoRequest).enqueue(object : Callback<Repo> {
            override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio creado exitosamente")
                    finish()
                } else {
                    showMessage("Error al crear: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<Repo>, t: Throwable) {
                showMessage("Fallo en la conexión: ${t.message}")
            }
        })
    }

    private fun handleUpdate(originalRepo: Repo) {
        if (!validateForm()) return

        val newDescription = binding.repoDescriptionInput.text.toString().trim()
        val repoRequest = RepoRequest(name = originalRepo.name, description = newDescription)


        // Llamamos a la API para actualizar, usando el nombre del dueño y el nombre original.
        apiService.updateRepo(originalRepo.owner.login,originalRepo.name, repoRequest)
            .enqueue(object : Callback<Repo> {
                override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                    if (response.isSuccessful) {
                        showMessage("Repositorio actualizado exitosamente")
                        finish()
                    } else {
                        // Un error 403 o 404 aquí puede ser por falta de permisos en el token.
                        showMessage("Error al actualizar: ${response.code()}")
                        Log.e("RepoForm", "Error body: ${response.errorBody()?.string()}")
                    }
                }
                override fun onFailure(call: Call<Repo>, t: Throwable) {
                    showMessage("Fallo de conexión al actualizar: ${t.message}")
                }
            })
    }


    //asegurarse de que el nombre del repositorio es válido antes de intentar crearlo o actualizarlo
    private fun validateForm(): Boolean {
        val repoName = binding.repoNameInput.text.toString()
        // Limpiamos errores previos.
        binding.repoNameInput.error = null

        if (repoName.isBlank()) {
            binding.repoNameInput.error = "El nombre del repositorio es requerido"
            return false
        }
        if (repoName.contains(" ")) {
            binding.repoNameInput.error = "El nombre no puede contener espacios"
            return false
        }
        return true
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
