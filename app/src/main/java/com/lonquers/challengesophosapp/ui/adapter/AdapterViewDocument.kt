package com.lonquers.challengesophosapp.ui.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lonquers.challengesophosapp.R
import com.lonquers.challengesophosapp.model.DocumentItems

class AdapterViewDocument(
    private val getDocsViewModel: List<DocumentItems>?,
    private val onItemSelected: (DocumentItems) -> Unit
) : RecyclerView.Adapter<AdapterViewDocumentViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewDocumentViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AdapterViewDocumentViewHolder(
            layoutInflater.inflate(
                R.layout.recycler_list_documents,
                parent,
                false
            )
        )
    }
    override fun onBindViewHolder(holder: AdapterViewDocumentViewHolder, position: Int) {
        val item = getDocsViewModel?.get(position)
        holder.render(item, onItemSelected)

    }

    override fun getItemCount(): Int {
        return getDocsViewModel?.size ?: 3
    }


}

