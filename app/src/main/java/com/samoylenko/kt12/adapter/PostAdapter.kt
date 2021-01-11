package com.samoylenko.kt12.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.samoylenko.kt12.R
import com.samoylenko.kt12.databinding.CardPostBinding
import com.samoylenko.kt12.dto.Post
import com.samoylenko.kt12.util.Calc

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun playVideo(post: Post) {}
    fun onClickPost(post: Post) {}
}

class PostAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            imgAvatar.setImageResource(R.drawable.ic_netology)
            author.text = post.author
            txtDate.text = post.published
            textData.text = post.content
            visability.text = post.countVisability.toString()
            like.text = Calc.intToText(post.like)
            share.text = post.sharing.toString()
            like.isChecked = post.likedByMe
            if (!post.video.equals("")) {
                layoutVideo.visibility = View.VISIBLE
                //videoViewPlay.text = post.video
            }

            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }
            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { itemView ->
                        when (itemView.itemId) {
                            R.id.deleteView -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.editView -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
            imageViewPlay.setOnClickListener {
                onInteractionListener.playVideo(post)
            }

            binding.root.setOnClickListener {
                onInteractionListener.onClickPost(post)
            }
        }
    }
}