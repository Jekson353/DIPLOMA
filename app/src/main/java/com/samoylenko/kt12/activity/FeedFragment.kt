package com.samoylenko.kt12.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.samoylenko.kt12.R
import com.samoylenko.kt12.adapter.OnInteractionListener
import com.samoylenko.kt12.adapter.PostAdapter
import com.samoylenko.kt12.databinding.FragmentFeedBinding
import com.samoylenko.kt12.dto.Post
import com.samoylenko.kt12.viewmodel.PostViewModel
import java.io.File


class FeedFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = { requireActivity() })
    private val binding by lazy {
        FragmentFeedBinding.inflate(layoutInflater)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_bar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.demo_data -> viewModel.getDemoData(this.requireContext())
            R.id.about -> Toast.makeText(
                requireActivity(),
                getString(R.string.about_app),
                Toast.LENGTH_LONG
            ).show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val onAuthor = arguments?.getBoolean("onAuthor", false) ?: false
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        var doubleBackToExitPressedOnce = false
        val callback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                if (onAuthor) {
                    viewModel.onIndexPage()
                    findNavController().popBackStack()
                } else {
                    if (doubleBackToExitPressedOnce) {
                        finishAffinity(requireActivity())
                    }
                    doubleBackToExitPressedOnce = true
                    Toast.makeText(
                        requireActivity(),
                        getString(R.string.double_click_exit),
                        Toast.LENGTH_SHORT
                    ).show()
                    Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        val adapter = PostAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onLikes(post: Post) {
                viewModel.likesById(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onDislike(post: Post) {
                viewModel.dislikeById(post.id)
            }

            override fun onAuthor(post: Post) {
                val bundle = Bundle()
                bundle.putBoolean("onAuthor", true)
                viewModel.viewByAuthor(post.author)
                findNavController().navigate(R.id.feedFragment, bundle)
            }


            override fun onClickPost(post: Post) {
                val bundle = Bundle()
                bundle.putSerializable("post", post)
                findNavController().navigate(R.id.action_feedFragment_to_onePostFragment, bundle)
            }

            override fun goToUrl(post: Post) {
                var url = post.urlLink
                if (url != "") {
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        url = "http://$url"
                    }
                }

                Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    .also {
                        if (it.resolveActivity(requireActivity().packageManager) == null) {
                            Toast.makeText(
                                requireActivity(),
                                getString(R.string.no_app_url),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            startActivity(it)
                        }
                    }
            }

            override fun onShare(post: Post) {
                val intent = Intent(Intent.ACTION_SEND)
                if (post.image.isNotEmpty()) {
                    val file = File(post.image)
                    val myPhotoFileUri = FileProvider.getUriForFile(
                        requireActivity(),
                        requireActivity().applicationContext.packageName + ".provider",
                        file
                    )
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.putExtra(Intent.EXTRA_STREAM, myPhotoFileUri)
                    intent.type = "image/*"
                } else {
                    intent.type = "text/plain"
                }
                val textToShare = post.author + "\n" + post.content

                intent.putExtra(Intent.EXTRA_TEXT, textToShare)
                    .also {
                        if (it.resolveActivity(requireActivity().packageManager) == null) {
                            Toast.makeText(
                                requireActivity(),
                                R.string.no_app_share,
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            startActivity(
                                Intent.createChooser(
                                    intent,
                                    getString(R.string.share_from_help)
                                )
                            ).also {
                                viewModel.shareById(post.id)
                            }
                        }
                    }
            }
        })

        binding.listItem.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner, { posts ->
            adapter.submitList(posts.sortedByDescending { post -> post.like })
        })

        binding.addPostButton.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_postFragment)
        }

        viewModel.edited.observe(viewLifecycleOwner, { post ->
            if (post.id == 0L) {
                return@observe
            }
            val bundle = Bundle()
            bundle.putSerializable("toEditPost", post)

            findNavController().navigate(R.id.action_feedFragment_to_postFragment, bundle)
        })

        return binding.root
    }
}
