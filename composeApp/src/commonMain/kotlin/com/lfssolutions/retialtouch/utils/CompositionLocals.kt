package com.lfssolutions.retialtouch.utils

import androidx.compose.runtime.compositionLocalOf
import com.lfssolutions.retialtouch.presentation.viewModels.SharedPosViewModel

// Define the CompositionLocal for SharedPosViewModel
val LocalSharedViewModel = compositionLocalOf<SharedPosViewModel> {
    error("SharedPosViewModel not provided")
}