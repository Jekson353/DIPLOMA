package com.samoylenko.kt12.activity

import android.content.Intent
import android.content.Intent.EXTRA_STREAM
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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


const val REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE = 21
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
                countVisability = arguments?.getInt("visability")!!,
                video = arguments?.getString("video")!!,
                image = arguments?.getString("image")!!,
                imageUri = arguments?.getString("imageUri")!!,
                likedByMe = arguments?.getBoolean("likedByMe")!!
            )
        }
        posts = onePost

        binding.like.setOnClickListener {
            if (postId != null) {
                viewModel.likeById(postId)
            }
        }

        binding.likes.setOnClickListener {
            if (postId != null) {
                viewModel.likesById(postId)
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
                            //if (postId != null) {
                            if (onePost != null) {
                                viewModel.viewByAuthor(onePost.author)
                            }
                            findNavController().navigateUp()
                            //}
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
            val file = File(onePost!!.image)
            val uri: Uri = file.toUri()
            val filename: String = file.name
            val intent = Intent(Intent.ACTION_SEND)

            val myPhotoFileUri = FileProvider.getUriForFile(
                requireActivity(),
                requireActivity().applicationContext.packageName + ".provider",
                file
            )
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.putExtra(EXTRA_STREAM, myPhotoFileUri)

            val text = onePost.author + "\n" + onePost.content
            intent.putExtra(Intent.EXTRA_TEXT, text)

            intent.type = "image/*"
            //intent.putExtra(Intent.EXTRA_STREAM, myPhotoFileUri)
            //requireContext().startActivity(intent)
            if (activity?.packageManager?.let { it1 -> intent.resolveActivity(it1) } != null) {
                startActivity(Intent.createChooser(intent, "Your title"))
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Нет приложений для отправки сообщений",
                    Toast.LENGTH_SHORT
                ).show()
            }


//            val permissionStatus =
//                ContextCompat.checkSelfPermission(
//                    requireActivity(),
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//                )
//
//            if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(
//                    requireActivity(),
//                    "есть разрешения",
//                    Toast.LENGTH_SHORT
//                ).show()
//                val uri: Uri = Uri.parse(posts?.imageUri)
//                val intent = Intent(Intent.ACTION_GET_CONTENT)
//                intent.type = "image/*"
//                intent.putExtra(Intent.EXTRA_STREAM, uri)
//                //requireContext().startActivity(intent)
//                if (activity?.packageManager?.let { it1 -> intent.resolveActivity(it1) } != null) {
//                    startActivity(intent)
//                } else {
//                    Toast.makeText(
//                        requireActivity(),
//                        "Нет приложений для отправки сообщений",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            } else {
//                Toast.makeText(
//                    requireActivity(),
//                    "запрашивается разрешение",
//                    Toast.LENGTH_SHORT
//                ).show()
//                ActivityCompat.requestPermissions(
//                    requireActivity(), arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                    REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE
//                )
//            }


            //val uri: Uri = Uri.parse(onePost?.imageUri)
//            val uri = FileProvider.getUriForFile(requireContext(),
//                BuildConfig.APPLICATION_ID + ".provider",onePost?.image)

            //val intent = Intent(Intent.ACTION_SEND)
//            intent.putExtra(Intent.EXTRA_STREAM, uri)
//            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//            intent.type = "image/*"
//            intent.putExtra(Intent.EXTRA_TEXT, onePost?.content.toString())
//            val title = onePost?.author
//            val chooser = Intent.createChooser(intent, title)
//            if (activity?.packageManager?.let { it1 -> intent.resolveActivity(it1) } != null) {
//                startActivity(chooser)
//            } else {
//                Toast.makeText(
//                    requireActivity(),
//                    "Нет приложений для отправки сообщений",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }

//            Intent(Intent.ACTION_SEND_MULTIPLE)
//                .putExtra(Intent.EXTRA_STREAM, uri)
//                .putExtra(Intent.EXTRA_TEXT, onePost?.content.toString())
//                .putExtra(Intent.EXTRA_TEXT, onePost?.author.toString())
//
//                .setType("*/*")
//                .also {
//                    if (it.resolveActivity(requireActivity().packageManager) == null) {
//                        Toast.makeText(
//                            requireActivity(),
//                            "Нет приложений для отправки сообщений",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    } else {
//                        if (postId != null) {
//                            viewModel.shareById(postId)
//                        }
//                        startActivity(it)
//                    }
//                }
        }

        view?.isVisible = false

        binding.imgAvatar.setImageResource(R.drawable.icon_app)
        binding.author.text = onePost!!.author
        binding.txtDate.text = onePost.published
        binding.textData.text = onePost.content
        //binding.visability.text = onePost.countVisability.toString()
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

    //попытка расшарить через разрешения
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // permission granted
                    val uri: Uri = Uri.parse(posts?.imageUri)
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "image/*"
                    intent.putExtra(EXTRA_STREAM, uri)
                    //requireContext().startActivity(intent)
                    if (activity?.packageManager?.let { it1 ->
                            intent.resolveActivity(
                                it1
                            )
                        } != null) {
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            requireActivity(),
                            "Нет приложений для отправки сообщений",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // permission denied
                    Toast.makeText(
                        requireActivity(),
                        "Доступ не разрешили",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
        }
    }
}