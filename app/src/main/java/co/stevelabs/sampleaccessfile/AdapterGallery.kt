package co.stevelabs.sampleaccessfile

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.cell_gallery.view.*

class AdapterGallery(val mGalleryList: ArrayList<MediaFileData>, val onClickImageListener: (imageUri: Uri) -> Unit): RecyclerView.Adapter<AdapterGallery.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cell_gallery, parent, false))

        view.itemView.setOnClickListener {
            onClickImageListener.invoke(mGalleryList[view.adapterPosition].contentUri)
        }

        return view
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, mGalleryList[position])
    }

    override fun getItemCount(): Int {
        return mGalleryList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int, gallery: MediaFileData) {
            Glide.with(itemView.iv_image)
                .load(gallery.contentUri)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(itemView.iv_image)
        }
    }
}