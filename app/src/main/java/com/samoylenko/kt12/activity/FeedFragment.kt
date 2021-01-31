package com.samoylenko.kt12.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
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
                bundle.putInt("like", post.like)
                bundle.putInt("share", post.sharing)
                bundle.putString("image", post.image)
                bundle.putString("urlLink", post.urlLink)
                bundle.putString("imageUri", post.imageUri)
                findNavController().navigate(R.id.action_feedFragment_to_onePostFragment, bundle)
            }

            override fun playVideo(post: Post) {
                Intent(Intent.ACTION_VIEW, Uri.parse(post.urlLink))
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
                val intent = Intent(Intent.ACTION_SEND)
                if (!post.image.equals("")){
                    val file = File (post.image)
                    val myPhotoFileUri = FileProvider.getUriForFile(
                        requireActivity(),
                        requireActivity().applicationContext.packageName + ".provider",
                        file
                    )
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.putExtra(Intent.EXTRA_STREAM, myPhotoFileUri)
                    intent.setType("image/*")
                }else{
                    intent.setType("text/plain")
                }
                val textToShare = post.author + "\n" + post.content

                intent.putExtra(Intent.EXTRA_TEXT, textToShare)
                    .also {
                        if (it.resolveActivity(requireActivity().packageManager) == null) {
                            Toast.makeText(
                                requireActivity(),
                                "Нет приложений для отправки сообщений",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewModel.shareById(post.id)
                            startActivity(Intent.createChooser(intent, "Поделиться с помощью:"))
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
            bundle.putString("textPost", post.content)
            bundle.putString("urlLink", post.urlLink)
            bundle.putString("image", post.image)

            findNavController().navigate(R.id.action_feedFragment_to_postFragment, bundle)
        })


        return binding.root
    }
}
