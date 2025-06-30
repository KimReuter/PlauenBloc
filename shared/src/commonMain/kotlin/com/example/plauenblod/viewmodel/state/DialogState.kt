package com.example.plauenblod.viewmodel.state

sealed class DialogState {
    object Hidden : DialogState()
    object ShowDeleteConfirm : DialogState()
    object ShowDeleteSuccess : DialogState()
    object ShowCreateSuccess : DialogState()
    object ShowEditSuccess : DialogState()
    object ShowCancelDialog : DialogState()
    data class Error(val message: String) : DialogState()
}