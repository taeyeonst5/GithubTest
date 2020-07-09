package com.allen_chou.githubtest.ui.main

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.allen_chou.githubtest.R
import com.allen_chou.githubtest.api.User
import com.allen_chou.githubtest.extensions.logd
import com.allen_chou.githubtest.util.GlideApp
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.item_user.view.*

class UserAdapter : PagedListAdapter<User, UserAdapter.UserViewHolder>(UserDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        } ?: logd("getItem(position) null ")
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_user = itemView.tv_user
        val iv_avatar = itemView.iv_avatar
        val pb_loading = itemView.pb_loading
        val glideApp = GlideApp.with(itemView.context)

        fun bind(item: User) {
            logd("bind userName: ${item.login}")
            pb_loading.visibility = View.VISIBLE
            tv_user.text = item.login
            glideApp.load(item.avatar_url).listener(createListener()).diskCacheStrategy(
                DiskCacheStrategy.ALL
            ).override(50, 50)
                .into(iv_avatar)
        }

        private fun createListener(): RequestListener<Drawable> {
            return object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    hideLoading()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    hideLoading()
                    return false
                }

            }
        }

        private fun hideLoading() {
            pb_loading.visibility = View.GONE
        }
    }

    class UserDiffUtil : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

    }
}