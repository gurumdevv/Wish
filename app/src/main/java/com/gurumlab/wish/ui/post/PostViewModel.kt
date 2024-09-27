package com.gurumlab.wish.ui.post

import android.net.Uri
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor() : ViewModel() {

    private var _projectTitle = mutableStateOf("")
    val projectTitle = _projectTitle
    private var _oneLineDescription = mutableStateOf("")
    val oneLineDescription = _oneLineDescription
    private var _simpleDescription = mutableStateOf("")
    val simpleDescription = _simpleDescription

    private var _projectDescription = mutableStateOf("")
    val projectDescription = _projectDescription

    private var _itemCount = mutableIntStateOf(1)
    val itemCount = _itemCount
    private val _featureTitles = mutableStateMapOf<Int, String>()
    val featureTitles = _featureTitles
    private val _featureDescriptions = mutableStateMapOf<Int, String>()
    val featureDescriptions = _featureDescriptions
    private val _selectedImageUris = mutableStateMapOf<Int, List<Uri>>()
    val selectedImageUris = _selectedImageUris

    fun setProjectTitle(title: String) {
        _projectTitle.value = title
    }

    fun setOneLineDescription(description: String) {
        _oneLineDescription.value = description
    }

    fun setSimpleDescription(description: String) {
        _simpleDescription.value = description
    }

    fun setProjectDescription(description: String) {
        _projectDescription.value = description
    }

    fun setItemCount(count: Int) {
        _itemCount.intValue = count
    }

    fun setFeatureTitles(index: Int, title: String) {
        _featureTitles[index] = title
    }

    fun setFeatureDescriptions(index: Int, description: String) {
        _featureDescriptions[index] = description
    }

    fun setSelectedImageUris(index: Int, uris: List<Uri>) {
        _selectedImageUris[index] = uris
    }
}