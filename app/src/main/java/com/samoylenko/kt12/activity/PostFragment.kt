package com.samoylenko.kt12.activity

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.DialogFragment
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
    var pathImage = "" //полный путь к изображению
    private val REQUEST_CODE_DELETE_IMAGE = 50
    private val REQUEST_CODE_ADD_IMAGE = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPostBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val textPost = arguments?.getString("textPost")
        val urlLink = arguments?.getString("urlLink")
        val owner = arguments?.getString("owner")
        val image = arguments?.getString("image")

        binding.editTextPost.setText(textPost)
        binding.inputUrlLink.setText(urlLink)
        if (!image.equals("")) {
            if (image != null) {
                pathImage = image //при запуске - сохраняем путь в переменную
                binding.layoutImage.visibility = View.VISIBLE
                binding.inputImagePost.setImageURI(image.toUri())
            }
        }


        val callback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                if (owner.equals("onePost")) {
                    viewModel.noSave()
                    findNavController().popBackStack()
                    Toast.makeText(
                        requireActivity(),
                        "Отменено",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                } else {
                    viewModel.noSave()
                    findNavController().popBackStack()
                    Toast.makeText(
                        requireActivity(),
                        "noSave()",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        binding.addImageBtn.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, REQUEST_CODE_ADD_IMAGE)
        }

        binding.inputImagePost.setOnClickListener {
            val manager = this.parentFragmentManager
            val myDialogFragment = MyDialogFragment()

            myDialogFragment.setTargetFragment(this, REQUEST_CODE_DELETE_IMAGE)
            myDialogFragment.show(manager, getString(R.string.select_action))
        }

        binding.savePost.setOnClickListener {
            val content = binding.editTextPost.text.toString()
            val urlPost = binding.inputUrlLink.text.toString()


            if (content.isEmpty()) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.text_no_empty),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else if (content.length > 100) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.sybmol_has_more) + content.length,
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            viewModel.changeContent(content, urlPost, pathImage)
            viewModel.save()

            AndroidUtils.hideSoftKeyBoard(requireView())
            if (owner.equals("onePost")) {
                findNavController().navigate(R.id.action_postFragment_to_feedFragment)
            } else {
                findNavController().navigateUp()
            }

        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_DELETE_IMAGE -> {
                Toast.makeText(
                    activity, getString(R.string.delete_image),
                    Toast.LENGTH_LONG
                ).show()
                layoutImage?.visibility = View.GONE
                pathImage = ""
                inputImagePost?.setImageResource(0)
                addImageBtn?.visibility = View.VISIBLE
            }
            REQUEST_CODE_ADD_IMAGE -> {
                if (resultCode == RESULT_OK) {

                    //получаем имя файла
                    val uri: Uri? = data?.getData()
                    val cursor: Cursor? =
                        uri?.let {
                            context?.getContentResolver()?.query(it, null, null, null, null)
                        }
                    val nameIndex: Int = cursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    val filename2 = cursor.getString(nameIndex)
                    cursor.close()

                    inputImagePost.setImageURI(uri)
                    layoutImage.visibility = View.VISIBLE
                    addImageBtn.visibility = View.GONE

                    //копируем файл во внотреннюю структуру приложения
                    val originalFile: InputStream? = uri.let {
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
                    //при загрузке изображения, меняем путь в переменной, т.к. изображение могло измениться
                    pathImage = to.absolutePath
                    editTextPost.requestFocus()
                }
            }
        }
    }
}

class MyDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle(getString(R.string.confirmation))
                .setMessage(getString(R.string.sure_deleted))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    val intent = Intent()
                    targetFragment!!.onActivityResult(targetRequestCode, RESULT_OK, intent)
                }
                .setNegativeButton(getString(R.string.no)) { _, _ ->
                    Toast.makeText(
                        activity, getString(R.string.cancel_operation),
                        Toast.LENGTH_LONG
                    ).show()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}