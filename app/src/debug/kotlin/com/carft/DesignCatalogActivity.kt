package com.carft

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.carft.designsystem.catalog.DesignCatalog
import com.carft.designsystem.theme.CarftTheme

class DesignCatalogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CarftTheme { DesignCatalog() } }
    }
}
