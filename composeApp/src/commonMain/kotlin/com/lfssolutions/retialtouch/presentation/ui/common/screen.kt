package com.lfssolutions.retialtouch.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons
import com.outsidesource.oskitcompose.layout.FlexRowLayoutScope.weight
import com.outsidesource.oskitcompose.systemui.SystemBarColorEffect
import com.outsidesource.oskitcompose.systemui.SystemBarIconColor
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.general_back



@Composable
fun GradientBackgroundScreen(
    modifier: Modifier = Modifier,
    blur : Float = .1f,
    screenBackground: Brush = AppTheme.colors.screenGradientHorizontalBg,
    navBgColor: Color = AppTheme.colors.backgroundNavbar,
    statusBarIconColor: SystemBarIconColor = SystemBarIconColor.Light,
    navIconColor: Color = AppTheme.colors.iconNavbar,
    statusBarColor: Color = AppTheme.colors.activeColor,
    isBlur: Boolean = true,
    content: @Composable (Dp) -> Unit = {},
){
    SystemBarColorEffect(
        statusBarColor = statusBarColor,
        statusBarIconColor = statusBarIconColor,
        navigationBarColor = navBgColor
    )

    BoxWithConstraints(
        modifier = modifier
            .background(screenBackground)
            .windowInsetsPadding(WindowInsets.systemBars)
            .alpha(if(isBlur) blur else 1f)
    ) {
        content(maxHeight)
    }
}


@Composable
fun CashierBasicScreen(
    modifier: Modifier = Modifier,
    appToolbarContent: @Composable () -> Unit = {},
    statusBarColor: Color = AppTheme.colors.activeColor,
    statusBarIconColor: SystemBarIconColor = SystemBarIconColor.Light,
    navigationBarColor: Color = Color.Transparent,
    screenBackground: Color = AppTheme.colors.screenBackground,
    contentMaxWidth: Dp = AppTheme.dimensions.screenDefaultMaxWidth,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 0.dp,
        vertical = 0.dp,
    ),
    contentAlignment: Alignment = Alignment.Center,
    isScrollable: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {
    val density = LocalDensity.current
    val defaultTableWidth = AppTheme.dimensions.tabletDefaultWidth

    SystemBarColorEffect(
        statusBarColor = statusBarColor,
        navigationBarColor = navigationBarColor,
        statusBarIconColor = statusBarIconColor,
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(screenBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        appToolbarContent()
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .widthIn(max = contentMaxWidth)
                .fillMaxWidth(),
        ) {
            val screenHeight = maxHeight - contentPadding.verticalPadding()

            CompositionLocalProvider(
                LocalScreenHeight provides screenHeight,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (isScrollable)
                                Modifier.verticalScroll(state = rememberScrollState())
                            else
                                Modifier
                        )
                        .padding(contentPadding),
                    contentAlignment = contentAlignment,
                    content = content,
                )
            }
        }
    }
}

@Composable
fun BackgroundScreen(
    modifier: Modifier = Modifier,
    appToolbarContent: @Composable () -> Unit = {},
    statusBarColor: Color = AppTheme.colors.primaryDarkColor,
    statusBarIconColor: SystemBarIconColor = SystemBarIconColor.Light,
    navigationBarColor: Color = Color.Transparent,
    screenBackground: Color = AppTheme.colors.appWhite,
    contentMaxWidth: Dp = AppTheme.dimensions.screenDefaultMaxWidth,
    contentPadding: PaddingValues = PaddingValues(
        horizontal = 0.dp,
        vertical = 0.dp,
    ),
    contentAlignment: Alignment = Alignment.Center,
    isScrollable: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) {

    SystemBarColorEffect(
        statusBarColor = statusBarColor,
        navigationBarColor = navigationBarColor,
        statusBarIconColor = statusBarIconColor,
    )
    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(screenBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        appToolbarContent()
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .widthIn(max = contentMaxWidth)
                .fillMaxWidth(),
        ) {
           // val statusBarInsets = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
            val screenHeight = maxHeight - contentPadding.verticalPadding()

            CompositionLocalProvider(
                LocalScreenHeight provides screenHeight,
            ) {
                AppScreenPadding(
                    content = { horizontalPadding, verticalPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .then(
                                    if (isScrollable)
                                        Modifier.verticalScroll(state = rememberScrollState())
                                    else
                                        Modifier
                                )
                                .padding(PaddingValues(
                                    horizontal = horizontalPadding,
                                    vertical = verticalPadding + navigationBarPadding,
                                )),
                            contentAlignment = contentAlignment,
                            content = content,
                        )
                    }
                )}
            }
        }
    }


@Composable
fun BasicScreen(
    modifier: Modifier = Modifier,
    title: String,
    statusBarColor: Color = AppTheme.colors.primaryDarkColor,
    statusBarIconColor: SystemBarIconColor = SystemBarIconColor.Light,
    navigationBarColor: Color = Color.Transparent,
    screenBackground: Color = AppTheme.colors.secondaryBg,
    contentMaxWidth: Dp = AppTheme.dimensions.screenDefaultMaxWidth,
    contentColor: Color = AppTheme.colors.appBarContent,
    contentAlignment: Alignment = Alignment.Center,
    showBackButton: Boolean = true,
    isTablet: Boolean = false,
    isScrollable: Boolean = true,
    onBackClick: () -> Unit = { },
    content: @Composable BoxScope.() -> Unit,
) {
    SystemBarColorEffect(
        statusBarColor = statusBarColor,
        navigationBarColor = navigationBarColor,
        statusBarIconColor = statusBarIconColor,
    )
    var showMenu by remember { mutableStateOf(false) }

    val textStyle = if(isTablet)
        AppTheme.typography.titleBold().copy(fontSize = 20.sp)
    else
        AppTheme.typography.bodyBold()

        Scaffold(topBar = {
        TopAppBar(
            modifier = modifier.shadow(elevation = 8.dp),
            backgroundColor=AppTheme.colors.primaryDarkColor,
            contentColor=AppTheme.colors.appWhite,
            title = { Text(text = title , color = contentColor, style = textStyle) },
            /*actions = {
                IconButton(onClick = { }) {
                    Icon(imageVector = Icons.Filled.Share, contentDescription = "Share")
                }
                IconButton(onClick = { }) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = "Save")
                }
                // over flow menu
                *//*IconButton(onClick = { showMenu = !showMenu }) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(text = { Text(text = "Menu Item") }, onClick = { })
                }*//*
            },*/
            navigationIcon = {
                IconButton(onClick = {
                    onBackClick.invoke()
                }) {
                    Icon(imageVector = vectorResource(AppIcons.backIcon), contentDescription = "Back")
                }
            }
        )
    }) { innerPadding ->
        // screen content.
            ResponsiveBox(
                modifier = Modifier
                    .weight(1f)
                    .widthIn(max = contentMaxWidth)
                    .fillMaxWidth()
                    .padding(innerPadding),
                bgColor = screenBackground
            ){
                content()
            }

        /*BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .widthIn(max = contentMaxWidth)
                .fillMaxWidth()
                .padding(innerPadding)
                .background(screenBackground),
        ){
            AppScreenPadding(
                content = { horizontalPadding, verticalPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(PaddingValues(
                                horizontal = horizontalPadding,
                                vertical = verticalPadding,
                            )),
                        contentAlignment = contentAlignment,
                        content = content,
                    )
                }
            )
        }*/
    }
}


@Composable
fun TopAppBarContent(
    modifier: Modifier = Modifier,
    title: String,
    leftContent: (@Composable RowScope.() -> Unit)? = null,
    rightContent: (@Composable RowScope.() -> Unit)? = null,
    background: Color = AppTheme.colors.primaryColor,
    contentColor: Color = AppTheme.colors.appBarContent,
    textStyle: TextStyle = AppTheme.typography.titleMedium(),
    showBackButton: Boolean = true,
    isTablet: Boolean = false,
    onBackClick: () -> Unit = { },
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = if (isTablet) 65.dp else 50.dp)
            .background(background)
            .padding(horizontal = if (isTablet) 80.dp else 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        when {
            leftContent != null -> leftContent()
            showBackButton -> {
                TopAppBarButton(
                    icon = AppIcons.backIcon,
                    label = stringResource(Res.string.general_back),
                    textStyle = if (!isTablet) textStyle else textStyle.copy(fontSize = 18.sp),
                    onClick = onBackClick,
                )
            }
            else -> Box(modifier = Modifier.widthIn(min = 50.dp))
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = title,
                style = if (!isTablet) textStyle else textStyle.copy(fontSize = 18.sp),
                color = contentColor,
                textAlign = TextAlign.Center,
            )
        }

        if (rightContent != null) {
            rightContent()
        } else {
            Box(modifier = Modifier.widthIn(min = 70.dp))
        }
    }
}


@Composable
fun TopAppBarButton(
    icon: DrawableResource? = null,
    label: String? = null,
    color: Color = AppTheme.colors.appBarContent,
    textStyle: TextStyle = AppTheme.typography.bodyMedium(),
    isTablet: Boolean = false,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .widthIn(min = 30.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                onClick = onClick,
                role = Role.Button,
            )
            .padding(5.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon?.let {
            Icon(
                painter = painterResource(it),
                contentDescription = null,
                tint = color,
            )

            if (label != null) Spacer(modifier = Modifier.width(6.dp))
        }

        label?.let {
            Text(
                text = it,
                style = if (!isTablet) textStyle else textStyle.copy(fontSize = 18.sp),
                color = color,
            )
        }
    }
}

private val LocalScreenHeight = staticCompositionLocalOf { Dp.Unspecified }

/**
 * Sets the screen's to the minimum height to be the full height. This is useful
 * for allowing screen scrolling while also have buttons anchored to the bottom.
 */
fun Modifier.fillScreenHeight() = composed {
    val backButtonSpace = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    //heightIn(min = LocalScreenHeight.current - backButtonSpace)
    heightIn(min = LocalScreenHeight.current)
}

fun PaddingValues.verticalPadding(): Dp {
    return calculateTopPadding() + calculateBottomPadding()
}