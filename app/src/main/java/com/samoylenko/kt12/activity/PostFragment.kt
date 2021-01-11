package com.samoylenko.kt12.activity

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.samoylenko.kt12.R
import com.samoylenko.kt12.databinding.FragmentPostBinding
import com.samoylenko.kt12.util.AndroidUtils
import com.samoylenko.kt12.viewmodel.PostViewModel
import kotlinx.android.synthetic.main.fragment_post.*
import java.io.*


class PostFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = { requireActivity() })
    var tempUriImage = "";

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentPostBinding.inflate(inflater, container, false)

        val textPost = arguments?.getString("textPost")
        val urlVideo = arguments?.getString("urlVideo")
        val owner = arguments?.getString("owner")
        val image = arguments?.getString("image")

        binding.editTextPost.setText(textPost)
        binding.inputUrlVideo.setText(urlVideo)
        if (!image.equals("")) {
            if (image != null) {
                tempUriImage = image
            }
        }
        if (!image.equals("")) {
            binding.layoutVideo.visibility = View.VISIBLE
            binding.inputImagePost.setImageURI(image?.toUri())
        }
        binding.editTextPost.requestFocus()


        binding.addImageBtn.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, 1)
        }

        binding.inputImagePost.setOnClickListener {
            layoutVideo.visibility = View.GONE
            tempUriImage = ""
            inputImagePost.setImageResource(0)
        }

        binding.savePost.setOnClickListener {
            val content = binding.editTextPost.text.toString()
            val urlPost = binding.inputUrlVideo.text.toString()
            val uriImage = tempUriImage
            if (content.isEmpty()){
                Toast.makeText(
                    requireActivity(),
                    "Текст не может быть пустым",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }else if (content.length > 100){
                Toast.makeText(
                    requireActivity(),
                    "Количество символов не более 100. Сейчас их ${content.length}",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            viewModel.changeContent(content, urlPost, uriImage)
            viewModel.save()

            AndroidUtils.hideSoftKeyBoard(requireView())
            if(owner.equals("onePost")){
                findNavController().navigate(R.id.action_postFragment_to_feedFragment)
            }else{
                findNavController().navigateUp()
            }

        }



        return binding.root
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                if (resultCode == RESULT_OK) {
                    val chosenImageUri: Uri? = data?.data
                    layoutVideo.visibility = View.VISIBLE
                    inputImagePost.setImageURI(chosenImageUri)

// video is some file in internal storage
                    val real_path: String? = chosenImageUri?.getPath()
                    val auxFile: File = File(real_path.toString())
                    val filename = auxFile.getName()


                    val originalFile: InputStream? = chosenImageUri?.let {
                        context?.getContentResolver()?.openInputStream(
                            it
                        )
                    }
                    val pathFile = context?.filesDir?.resolve(filename)
                    val to: File = pathFile as File


                    val out: OutputStream = FileOutputStream(to)
                    val buf = ByteArray(1024)
                    var len: Int
                    if (originalFile != null) {
                        while (originalFile.read(buf).also { len = it } > 0) {
                            out.write(buf, 0, len)
                        }
                    }
                    out.close()
                    if (originalFile != null) {
                        originalFile.close()
                    }
                    tempUriImage = to.absolutePath

                    addImageBtn.setText(tempUriImage)


                }
            }
        }
    }

}