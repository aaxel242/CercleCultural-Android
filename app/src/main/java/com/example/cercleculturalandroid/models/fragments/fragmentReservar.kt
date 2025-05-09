import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.badlogic.gdx.backends.android.AndroidFragmentApplication
import com.example.cercleculturalandroid.R
import com.example.cercleculturalandroid.models.fragments.FragmentGdx

class FragmentReservar : Fragment() {
    private var gdxFragment: FragmentGdx? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
                             ): View? {
        return inflater.inflate(R.layout.fragment_reservar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar fragmento LibGDX
        gdxFragment = FragmentGdx()
        childFragmentManager.beginTransaction()
            .replace(R.id.gdx_container, gdxFragment!!)
            .commit()
    }
}