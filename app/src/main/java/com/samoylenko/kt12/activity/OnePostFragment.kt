package com.samoylenko.kt12.activity

import android.content.Intent
import android.content.Intent.EXTRA_STREAM
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
    var post: Post? = null

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_one_post, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteView -> post?.id?.let {
                viewModel.removeById(it)
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.deleted),
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            }
            R.id.editView -> post?.let {
                viewModel.edit(it)
                val bundle = Bundle()
                bundle.putSerializable("toEditPost", it)
                bundle.putString("owner", "onePost") //для определения логики навигации

                findNavController().navigate(
                    R.id.action_onePostFragment_to_postFragment,
                    bundle
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        setHasOptionsMenu(true)

        val binding = CardPostBinding.inflate(inflater, container, false)
        val onePost: Post? = arguments?.getSerializable("post") as? Post

        post = onePost
        var likesInt = onePost?.like
        var sharingInt = onePost?.sharing

        binding.likes.setOnClickListener {
            onePost?.id?.let {
                viewModel.likesById(it)
                likesInt = likesInt?.plus(1)
                binding.like.text = likesInt.toString()
            }
        }
        binding.dislike.setOnClickListener {
            onePost?.id?.let {
                viewModel.dislikeById(it)
                likesInt = likesInt?.minus(1)
                binding.like.text = likesInt.toString()
            }
        }

        binding.menu.visibility = View.GONE

        binding.share.setOnClickListener {
            onePost.let {
                val intent = Intent(Intent.ACTION_SEND)
                it?.image?.let { its ->
                    if (its.isNotEmpty()) {
                        val file = File(its)
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
                } ?: run {
                    intent.type = "text/plain"
                }

                val textToShare = it?.author + "\n" + it?.content
                intent.putExtra(Intent.EXTRA_TEXT, textToShare)

                activity?.packageManager?.let { it1 ->
                    intent.resolveActivity(it1)
                    onePost?.id?.let { it2 -> viewModel.shareById(it2) }
                    sharingInt = sharingInt?.plus(1)
                    binding.share.text = sharingInt.toString()
                    startActivity(Intent.createChooser(intent, getString(R.string.share_from_help)))
                } ?: run {
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
        binding.author.text = onePost?.author
        binding.txtDate.text = onePost?.published
        binding.textData.text = onePost?.content
        binding.like.text = onePost?.like.toString()
        binding.share.text = onePost?.sharing.toString()
        onePost?.let {
            if (it.image.isNotEmpty()) {
                binding.layoutLink.visibility = View.VISIBLE
                binding.inputImagePost.setImageURI(it.image.toUri())
            }
        }

        return binding.root
    }
}