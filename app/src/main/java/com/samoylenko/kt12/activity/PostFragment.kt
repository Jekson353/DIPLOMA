package com.samoylenko.kt12.activity

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
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
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream


class PostFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = { requireActivity() })
    var pathUriImage = ""
    var imgUri = ""

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
                pathUriImage = image
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
            pathUriImage = ""
            inputImagePost.setImageResource(0)
        }

        binding.savePost.setOnClickListener {
            val content = binding.editTextPost.text.toString()
            val urlPost = binding.inputUrlVideo.text.toString()
            val pathImage = pathUriImage
            val uriImage = imgUri

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

            viewModel.changeContent(content, urlPost, pathImage, uriImage)
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

                    //получаем имя файла
                    val uri: Uri? = data?.getData()
                    val cursor: Cursor? =
                        uri?.let { context?.getContentResolver()?.query(it, null, null, null, null) }
                    val nameIndex: Int = cursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    val filename2 = cursor.getString(nameIndex)
                    cursor.close();


                    layoutVideo.visibility = View.VISIBLE
                    inputImagePost.setImageURI(uri)


                    //копируем файл во внотреннюю структуру приложения
                    val originalFile: InputStream? = uri?.let {
                        context?.getContentResolver()?.openInputStream(
                            it
                        )
                    }
                    val pathFile = context?.filesDir?.resolve(filename2)
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
                    pathUriImage = to.absolutePath
                    imgUri = uri.toString()

                    editTextPost.setText(filename2)

                }
            }
        }
    }

}