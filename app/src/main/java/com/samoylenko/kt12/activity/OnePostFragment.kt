package com.samoylenko.kt12.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.samoylenko.kt12.R
import com.samoylenko.kt12.databinding.CardPostBinding
import com.samoylenko.kt12.dto.Post
import com.samoylenko.kt12.viewmodel.PostViewModel


class OnePostFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = { requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = CardPostBinding.inflate(inflater, container, false)
        val postId = arguments?.getLong("idPost")
        val onePost: Post? = postId?.let {
            Post(
                id = it,
                author = arguments?.getString("author")!!,
                content = arguments?.getString("content")!!,
                published = arguments?.getString("txtDate")!!,
                sharing = arguments?.getInt("share")!!,
                like = arguments?.getInt("like")!!,
                countVisability = arguments?.getInt("visability")!!,
                video = arguments?.getString("video")!!,
                image = arguments?.getString("image")!!,
                likedByMe = arguments?.getBoolean("likedByMe")!!
            )
        }

        binding.like.setOnClickListener {
            if (postId != null) {
                viewModel.likeById(postId)
            }
        }


        binding.menu.setOnClickListener {
            PopupMenu(it.context, it).apply {
                inflate(R.menu.options_post)
                setOnMenuItemClickListener { itemView ->
                    when (itemView.itemId) {
                        R.id.deleteView -> {
                            if (postId != null) {
                                viewModel.removeById(postId)
                                findNavController().navigateUp()
                            }
                            true
                        }
                        R.id.editView -> {
                            if (onePost != null) {
                                viewModel.edit(onePost)
                                val bundle = Bundle()
                                bundle.putString("textPost", onePost.content)
                                bundle.putString("urlVideo", onePost.video)
                                bundle.putString("image", onePost.image)
                                bundle.putString("owner", "onePost")
                                findNavController().navigate(
                                    R.id.action_onePostFragment_to_postFragment,
                                    bundle
                                )
                            }
                            true
                        }
                        else -> false
                    }
                }
            }.show()
        }

        binding.share.setOnClickListener {
            Intent(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_TEXT, onePost?.content)
                .setType("text/plain")
                .also {
                    if (it.resolveActivity(requireActivity().packageManager) == null) {
                        Toast.makeText(
                            requireActivity(),
                            "Нет приложений для отправки сообщений",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (postId != null) {
                            viewModel.shareById(postId)
                        }
                        startActivity(it)
                    }
                }
        }

        view?.isVisible = false

        binding.imgAvatar.setImageResource(R.drawable.ic_netology)
        binding.author.text = onePost!!.author
        binding.txtDate.text = onePost.published
        binding.textData.text = onePost.content
        binding.visability.text = onePost.countVisability.toString()
        binding.like.text = onePost.like.toString()
        binding.share.text = onePost.sharing.toString()
        binding.like.isChecked = onePost.likedByMe
//        if (!onePost.video.equals("")){
//            binding.layoutVideo.visibility = View.VISIBLE
//            //binding.videoViewPlay.text = onePost.video
//            binding.imageViewPlay.setImageURI(onePost.image.toUri())
//        }
        if (!onePost.image.equals("")){
            binding.layoutVideo.visibility = View.VISIBLE
            binding.inputImagePost.setImageURI(onePost.image.toUri())
        }

        return binding.root
    }

}