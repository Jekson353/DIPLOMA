package com.samoylenko.kt12.activity

import android.content.Intent
import android.content.Intent.EXTRA_STREAM
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.samoylenko.kt12.R
import com.samoylenko.kt12.databinding.CardPostBinding
import com.samoylenko.kt12.dto.Post
import com.samoylenko.kt12.viewmodel.PostViewModel
import java.io.File

class OnePostFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = { requireActivity() })
    var posts: Post? = null
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
                urlLink = arguments?.getString("urlLink")!!,
                image = arguments?.getString("image")!!,
            )
        }
        posts = onePost
        var likesInt = onePost?.like
        var sharingInt = onePost?.sharing

        binding.likes.setOnClickListener {
            if (postId != null) {
                viewModel.likesById(postId)
                likesInt = likesInt?.plus(1)
                binding.like.text = likesInt.toString()
            }
        }
        binding.dislike.setOnClickListener {
            if (postId != null) {
                viewModel.dislikeById(postId)
                likesInt = likesInt?.minus(1)
                binding.like.text = likesInt.toString()
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
                        R.id.viewPostAuthor -> {
                            if (onePost != null) {
                                viewModel.viewByAuthor(onePost.author)
                            }
                            findNavController().navigateUp()
                            true
                        }
                        R.id.editView -> {
                            if (onePost != null) {
                                viewModel.edit(onePost)
                                val bundle = Bundle()
                                bundle.putString("textPost", onePost.content)
                                bundle.putString("urlLink", onePost.urlLink)
                                bundle.putString("image", onePost.image)
                                bundle.putString(
                                    "owner",
                                    "onePost"
                                ) //для определения логики навигации
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
            if (onePost != null) {
                val intent = Intent(Intent.ACTION_SEND)
                if (!onePost.image.equals("")) {
                    val img = onePost.image
                    val file = File(img)
                    val myPhotoFileUri = FileProvider.getUriForFile(
                        requireActivity(),
                        requireActivity().applicationContext.packageName + ".provider",
                        file
                    )
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.putExtra(EXTRA_STREAM, myPhotoFileUri)
                    intent.type = "image/*"
                } else {
                    intent.type = "text/plain"
                }

                val textToShare = onePost.author + "\n" + onePost.content
                intent.putExtra(Intent.EXTRA_TEXT, textToShare)

                if (activity?.packageManager?.let { it1 -> intent.resolveActivity(it1) } != null) {
                    viewModel.shareById(postId)
                    sharingInt = sharingInt?.plus(1)
                    binding.share.text = sharingInt.toString()
                    startActivity(Intent.createChooser(intent, getString(R.string.share_from_help)))
                } else {
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.no_app_share),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        view?.isVisible = false

        binding.imgAvatar.setImageResource(R.drawable.icon_app)
        binding.author.text = onePost!!.author
        binding.txtDate.text = onePost.published
        binding.textData.text = onePost.content
        binding.like.text = onePost.like.toString()
        binding.share.text = onePost.sharing.toString()
        if (!onePost.image.equals("")) {
            binding.layoutLink.visibility = View.VISIBLE
            binding.inputImagePost.setImageURI(onePost.image.toUri())
        }

        return binding.root
    }
}