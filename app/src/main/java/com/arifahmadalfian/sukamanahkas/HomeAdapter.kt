package com.arifahmadalfian.sukamanahkas

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.arifahmadalfian.sukamanahkas.data.model.Kas
import com.arifahmadalfian.sukamanahkas.databinding.ListPemasukanBinding
import com.arifahmadalfian.sukamanahkas.utils.HDMY
import com.arifahmadalfian.sukamanahkas.utils.HHDMY
import com.arifahmadalfian.sukamanahkas.utils.epochToDateTime
import com.arifahmadalfian.sukamanahkas.utils.toCapitalize

class HomeAdapter(
    private var clickListener: IOnKasItemsClickListener
): RecyclerView.Adapter<HomeAdapter.UserViewHolder>() {

    private var kas = ArrayList<Kas>()

    @SuppressLint("NotifyDataSetChanged")
    fun setUser(user: ArrayList<Kas>){
        kas.clear()
        kas.addAll(user)
        notifyDataSetChanged()
    }

    inner class UserViewHolder(private var binding: ListPemasukanBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(kas: Kas, action: IOnKasItemsClickListener){
            with(binding) {
                ivProfile.load(kas.profile) {
                    placeholder(R.mipmap.ic_launcher)
                    error(R.mipmap.ic_launcher)
                    crossfade(true)
                    crossfade(200)
                    transformations(RoundedCornersTransformation(30f))
                }
                tvName.text = kas.name.toCapitalize()
                tvCreateAt.text = "by ${kas.createBy.toLowerCase()}"
                tvInclusion.text = kas.inclusion
                tvCreateBy.text = kas.createAt.toLong().epochToDateTime(HHDMY)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = ListPemasukanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(kas[position], clickListener)
    }

    override fun getItemCount(): Int {
        return kas.size
    }
}

interface IOnKasItemsClickListener {
    fun onKasItemClickListener(kas: Kas, position: Int)
}

