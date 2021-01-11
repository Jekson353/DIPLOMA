package com.samoylenko.kt12.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.samoylenko.kt12.R
import com.samoylenko.kt12.databinding.CardPostBinding
import com.samoylenko.kt12.dto.Post
import com.samoylenko.kt12.util.Calc


interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onLikes(post: Post) {}
    fun onDislike(post: Post) {}
    fun onIndexPage() {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun playVideo(post: Post) {}
    fun onClickPost(post: Post) {}
    fun onAuthor(post: Post){}
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
            imgAvatar.setImageResource(R.drawable.icon_app)
            author.text = post.author
            txtDate.text = post.published
            textData.text = post.content
            //visability.text = post.countVisability.toString()
            if (post.like<0){
                like.setTextColor(Color.parseColor("#FF0000"))
            }else if (post.like>0){
                like.setTextColor(Color.parseColor("#4CAF50"))
            }else{
                like.setTextColor(Color.parseColor("#9C9B9B"))
            }
            like.text = Calc.intToText(post.like)
            share.text = post.sharing.toString()
            like.isChecked = post.likedByMe
//            if (!post.video.equals("")) {
//                layoutVideo.visibility = View.VISIBLE
//                //videoViewPlay.text = post.video
//            }
            if (!post.video.equals("")){
                urlLink.visibility = View.VISIBLE
                urlLink.setText(post.video)
                //layoutVideo.visibility = View.VISIBLE

            }
            if (!post.image.equals("")) {
                layoutVideo.visibility = View.VISIBLE
                inputImagePost.setImageURI(post.image.toUri())
//                val imgFile = File(post.image)
//                if (imgFile.exists()) {
//                    val myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath())
//                    //val myImage: ImageView = findViewById(R.id.imageviewTest) as ImageView
//                    inputImagePost.setImageBitmap(myBitmap)
//                }else{
//                    author.text = "Картинка не найдена"
//                }
            }

            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }
            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }
            likes.setOnClickListener {
                onInteractionListener.onLikes(post)
            }
            dislike.setOnClickListener {
                onInteractionListener.onDislike(post)
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
                            R.id.viewPostAuthor -> {
                                onInteractionListener.onAuthor(post)
                                true
                            }
                            R.id.index_page -> {
                                onInteractionListener.onIndexPage()
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
            inputImagePost.setOnClickListener {
                onInteractionListener.playVideo(post)
            }

            binding.root.setOnClickListener {
                onInteractionListener.onClickPost(post)
            }
        }
    }
}