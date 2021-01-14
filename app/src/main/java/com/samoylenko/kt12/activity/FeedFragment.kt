package com.samoylenko.kt12.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.samoylenko.kt12.R
import com.samoylenko.kt12.adapter.OnInteractionListener
import com.samoylenko.kt12.adapter.PostAdapter
import com.samoylenko.kt12.databinding.FragmentFeedBinding
import com.samoylenko.kt12.dto.Post
import com.samoylenko.kt12.viewmodel.PostViewModel


class FeedFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = { requireActivity() })
    private val binding by lazy {
        FragmentFeedBinding.inflate(layoutInflater)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_bar, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()

        if (id == R.id.index_page) {
            viewModel.onIndexPage()
        }
        if (id == R.id.about) {
            Toast.makeText(requireActivity(), "О приложении", Toast.LENGTH_LONG).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        val adapter = PostAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
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
                viewModel.viewByAuthor(post.author)
            }

            override fun onIndexPage() {
                viewModel.onIndexPage()
            }

            override fun onClickPost(post: Post) {
                val bundle = Bundle()
                bundle.putLong("idPost", post.id)
                bundle.putString("author", post.author)
                bundle.putString("txtDate", post.published)
                bundle.putString("content", post.content)
                bundle.putInt("visability", post.countVisability)
                bundle.putInt("like", post.like)
                bundle.putInt("share", post.sharing)
                bundle.putString("image", post.image)
                bundle.putString("video", post.video)
                bundle.putBoolean("likedByMe", post.likedByMe)
                bundle.putString("imageUri", post.imageUri)
                findNavController().navigate(R.id.action_feedFragment_to_onePostFragment, bundle)
            }

            override fun playVideo(post: Post) {
                Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                    .also {
                        if (it.resolveActivity(requireActivity().packageManager) == null) {
                            Toast.makeText(
                                requireActivity(),
                                "Нет приложений для просмотра видео",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            startActivity(it)
                        }
                    }
            }

            override fun onShare(post: Post) {
                Intent(Intent.ACTION_SEND)
                    .putExtra(Intent.EXTRA_TEXT, post.content)
                    .setType("text/plain")
                    .also {
                        if (it.resolveActivity(requireActivity().packageManager) == null) {
                            Toast.makeText(
                                requireActivity(),
                                "Нет приложений для отправки сообщений",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewModel.shareById(post.id)
                            startActivity(it)
                        }
                    }
            }
        })

        binding.listItem.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner, { posts ->
            adapter.submitList(posts)
        })

        binding.addPostButton.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_postFragment)
        }

        viewModel.edited.observe(viewLifecycleOwner, { post ->
            if (post.id == 0L) {
                return@observe
            }
            val bundle = Bundle()
            bundle.putString("textPost", post.content)
            bundle.putString("urlVideo", post.video)
            bundle.putString("image", post.image)

            findNavController().navigate(R.id.action_feedFragment_to_postFragment, bundle)
        })


        return binding.root
    }
}
