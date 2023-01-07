package com.lonquers.challengesophosapp.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lonquers.challengesophosapp.databinding.RecyclerListDocumentsBinding
import com.lonquers.challengesophosapp.model.DocumentItems

class AdapterViewDocumentViewHolder (view: View) : RecyclerView.ViewHolder(view) {


    private val binding = RecyclerListDocumentsBinding.bind(view)

    fun render(getDocsViewModel: DocumentItems?, onItemSelected: (DocumentItems) -> Unit) {
        binding.tvDescription.text = getDocsViewModel?.TipoAdjunto
        binding.tvDate.text = getDocsViewModel?.Fecha
        binding.tvNameUser.text = getDocsViewModel?.Nombre
        binding.tvLastNameUser.text = getDocsViewModel?.Apellido

        binding.root.setOnClickListener {
            if (getDocsViewModel != null) {
                onItemSelected(getDocsViewModel)

            }
        }
    }
}