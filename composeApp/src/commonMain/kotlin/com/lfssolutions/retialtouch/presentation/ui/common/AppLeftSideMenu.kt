package com.lfssolutions.retialtouch.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lfssolutions.retialtouch.navigation.NavigationItem
import com.lfssolutions.retialtouch.navigation.NavigationScreen
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons
import com.lfssolutions.retialtouch.utils.AppIcons.categoryIcon
import com.outsidesource.oskitcompose.systemui.SystemBarColorEffect
import com.outsidesource.oskitcompose.systemui.SystemBarIconColor
import org.jetbrains.compose.resources.vectorResource



@Composable
fun AppLeftSideMenu(
    modifier: Modifier = Modifier,
    contentBgColor: Color = AppTheme.colors.contentBgColor,
    navBgColor: Color = AppTheme.colors.backgroundNavbar,
    statusBarIconColor: SystemBarIconColor = SystemBarIconColor.Light,
    navIconColor: Color = AppTheme.colors.iconNavbar,
    statusBarColor: Color = AppTheme.colors.activeColor,
    onMenuItemClick: (NavigationScreen) -> Unit,
    content: @Composable() (BoxScope.(Dp) -> Unit),
    holdSaleContent: @Composable() (BoxScope.() -> Unit) = {},
) {
    val density = LocalDensity.current
    val defaultTableWidth = AppTheme.dimensions.tabletDefaultWidth
    // get local density from composable
    val localDensity = LocalDensity.current
    var heightIs by remember {
        mutableStateOf(0.dp)
    }

    SystemBarColorEffect(
        statusBarColor = statusBarColor,
        statusBarIconColor = statusBarIconColor,
        navigationBarColor = navBgColor
    )

    BoxWithConstraints(
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()

            ) {
                // Left Navigation Sidebar
                Box(modifier=Modifier.fillMaxHeight().fillMaxWidth(.2f).background(navBgColor)){// Use 20% of the device's width
                    Column(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                            .onGloballyPositioned { coordinates ->
                                heightIs = with(localDensity) { coordinates.size.height.toDp() } // Get the height
                            },
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // wifi Icon
                        IconButton(onClick = {  }) {
                            Icon(
                                imageVector = vectorResource(AppIcons.wifiIcon),
                                contentDescription = "",
                                tint = navIconColor,
                                modifier = modifier.size(24.dp).padding(horizontal = 5.dp)
                            )
                        }


                        // Category Icon
                        IconButton(onClick = {
                            onMenuItemClick.invoke(NavigationScreen.Category)
                        }) {
                            Icon(
                                imageVector = vectorResource(categoryIcon),
                                contentDescription = "Category",
                                tint = navIconColor,
                                modifier = modifier.size(24.dp).padding(horizontal = 5.dp)
                            )
                        }

                        // sync Icon
                        IconButton(onClick = {  }) {
                            Icon(
                                imageVector = vectorResource(AppIcons.syncIcon),
                                contentDescription = "sync",
                                tint = navIconColor,
                                modifier = modifier.size(24.dp).padding(horizontal = 5.dp)
                            )
                        }

                        // excel Icon
                        IconButton(onClick = {  }) {
                            Icon(
                                imageVector = vectorResource(AppIcons.excelIcon),
                                contentDescription = "receipt",
                                tint = navIconColor,
                                modifier = modifier.size(24.dp).padding(horizontal = 5.dp)
                            )
                        }

                        // excel Icon
                        IconButton(onClick = {  }) {
                            Icon(
                                imageVector = vectorResource(AppIcons.printerIcon),
                                contentDescription = "printer",
                                tint = navIconColor,
                                modifier = modifier.size(24.dp).padding(horizontal = 5.dp)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier.fillMaxSize().background(contentBgColor)
                ) {
                    val sizeM=24+16+20
                    val adjustedPadding = if (heightIs > sizeM.dp) heightIs - sizeM.dp else 0.dp
                    println("leftMenuHeight : ${heightIs.value}")
                    content(adjustedPadding)
                }
            }


            // Green Box Positioned Outside the Content Area
            //.offset(y = leftMenuHeight.toDp(density)) // Positioned below the sidebar
             val sizeM=24+16+20
            val adjustedPadding = if (heightIs > sizeM.dp) heightIs - sizeM.dp else 0.dp
            println("leftMenuHeight : ${heightIs.value}")
            // Green box to show collections
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = adjustedPadding, start = 40.dp)
                    .wrapContentWidth()
                    .wrapContentHeight()
            ) {
                holdSaleContent()
            }
        }

    }

}


fun Int.toDp(density: Density): Dp {
    return with(density) { this@toDp.toDp() }
}


