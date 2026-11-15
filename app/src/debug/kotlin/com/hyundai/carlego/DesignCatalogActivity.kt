package com.hyundai.carlego

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.hyundai.carlego.designsystem.catalog.DesignCatalog
import com.hyundai.carlego.designsystem.theme.CarftTheme

class DesignCatalogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CarftTheme { DesignCatalog() } }
    }
}
