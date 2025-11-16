package ec.edu.uisek.githubclient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ec.edu.uisek.githubclient.databinding.FragmentRepoitemBinding
import ec.edu.uisek.githubclient.models.Repo

// --- PASO 1: SIMPLIFICAR EL VIEWHOLDER ---
// El ViewHolder ya no necesita recibir las funciones directamente si se configuran en onBindViewHolder.
// O, para mantener el patrón, las recibe del onCreateViewHolder, pero limpiemos el código.
// Vamos a modificarlo para que reciba las funciones y no sean nulas.

class ReposViewHolder(
    private val binding: FragmentRepoitemBinding,
    private val onEdit: (Repo) -> Unit, // Las funciones ya no son opcionales (nullable)
    private val onDelete: (Repo) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(repo: Repo) {
        binding.repoName.text = repo.name
        binding.repoDescription.text = repo.description ?: "Sin descripcion"
        binding.repoLang.text = repo.language

        // Asignamos los listeners a los botones.
        // Ahora usamos las funciones 'onEdit' y 'onDelete' que no son nulas.
        binding.repoItemEditButton.setOnClickListener {
            onEdit(repo)
        }
        binding.repoItemDeleteButton.setOnClickListener {
            onDelete(repo)
        }

        Glide.with(binding.root.context)
            .load(repo.owner.avatarUrl)
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .circleCrop()
            .into(binding.repoOwnerImage)
    }
}


// --- PASO 2: LIMPIAR Y USAR CORRECTAMENTE EL ADAPTADOR ---

class ReposAdapter(
    // Recibimos las funciones de clic aquí. Este es el único lugar donde deben entrar.
    private val onEditClicked: (Repo) -> Unit,
    private val onDeleteClicked: (Repo) -> Unit
) : RecyclerView.Adapter<ReposViewHolder>() {

    private var repositories: List<Repo> = emptyList()

    // ELIMINADAS: Estas variables son redundantes y causaban el error.
    // var onEdit: ((Repo) -> Unit)? = null
    // var onDelete: ((Repo) -> Unit)? = null

    override fun getItemCount(): Int = repositories.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReposViewHolder {
        val binding = FragmentRepoitemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        // CORREGIDO: Pasamos las funciones del constructor al ViewHolder.
        // Antes le estabas pasando 'onEdit' y 'onDelete', que eran null.
        return ReposViewHolder(binding, onEditClicked, onDeleteClicked)
    }

    override fun onBindViewHolder(holder: ReposViewHolder, position: Int) {
        // Simplemente llamamos a bind. La lógica de los clics ya está en el ViewHolder.
        holder.bind(repositories[position])
    }

    fun updateRepositories(newRepositories: List<Repo>) {
        repositories = newRepositories
        notifyDataSetChanged()
    }
}