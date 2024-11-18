package com.lfssolutions.retialtouch.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.Rubik_Black
import retailtouch.composeapp.generated.resources.Rubik_Bold
import retailtouch.composeapp.generated.resources.Rubik_Medium
import retailtouch.composeapp.generated.resources.Rubik_Regular

object DesignTypography{

    @Composable
    fun amountLarge(): TextStyle = TextStyle(
        fontFamily = FontFamily(Font(Res.font.Rubik_Bold)),
        fontSize = 28.sp,
        lineHeight = 26.sp
    )

    @Composable
    fun header(): TextStyle = TextStyle(
        fontFamily = FontFamily(Font(Res.font.Rubik_Black)),
        fontSize = 24.sp,
        lineHeight = 20.sp
    )

    @Composable
    fun h1Normal(): TextStyle = TextStyle(
        fontFamily = FontFamily(Font(Res.font.Rubik_Bold)),
        fontSize = 20.sp,
        lineHeight = 20.sp
    )

    @Composable
    fun h1Medium(): TextStyle = TextStyle(
        fontFamily = FontFamily(Font(Res.font.Rubik_Medium)),
        fontSize = 20.sp,
        lineHeight = 20.sp
    )


    @Composable
    fun h1Bold(): TextStyle = TextStyle(
        fontFamily = FontFamily(Font(Res.font.Rubik_Bold)),
        fontSize = 20.sp,
        lineHeight = 20.sp
    )

    @Composable
    fun h1Black(): TextStyle = TextStyle(
        fontFamily = FontFamily(Font(Res.font.Rubik_Black)),
        fontSize = 20.sp,
        lineHeight = 20.sp
    )


    @Composable
    fun titleNormal(): TextStyle = TextStyle(
        fontFamily = FontFamily(Font(Res.font.Rubik_Regular)),
        fontSize = 16.sp,
        lineHeight = 16.sp
    )

    @Composable
    fun titleMedium(): TextStyle = TextStyle(
        fontFamily = FontFamily(Font(Res.font.Rubik_Medium)),
        fontSize = 16.sp,
        lineHeight = 16.sp
    )

    @Composable
    fun titleBold(): TextStyle = TextStyle(
        fontFamily = FontFamily(Font(Res.font.Rubik_Bold)),
        fontSize = 16.sp,
        lineHeight = 16.sp
    )


    @Composable
    fun bodyNormal(): TextStyle = TextStyle(
        fontFamily = FontFamily(Font(Res.font.Rubik_Regular)),
        fontSize = 15.sp,
        lineHeight = 16.sp
    )

    @Composable
    fun bodyMedium(): TextStyle = TextStyle(
        fontFamily = FontFamily(Font(Res.font.Rubik_Medium)),
        fontSize = 15.sp,
        lineHeight = 18.sp
    )

    @Composable
    fun bodyBold(): TextStyle = TextStyle(
        fontFamily = FontFamily(Font(Res.font.Rubik_Bold)),
        fontSize = 15.sp,
        lineHeight = 18.sp
    )

    @Composable
    fun bodyBlack(): TextStyle = TextStyle(
        fontFamily = FontFamily(Font(Res.font.Rubik_Black)),
        fontSize = 15.sp,
        lineHeight = 18.sp
    )

    @Composable
    fun captionNormal(): TextStyle = TextStyle(
        fontFamily = FontFamily(Font(Res.font.Rubik_Regular)),
        fontSize = 12.sp,
        lineHeight = 18.sp
    )

    @Composable
    fun captionMedium(): TextStyle = TextStyle(
        fontFamily = FontFamily(Font(Res.font.Rubik_Medium)),
        fontSize = 12.sp,
        lineHeight = 18.sp
    )

    @Composable
    fun captionBold(): TextStyle = TextStyle(
        fontFamily = FontFamily(Font(Res.font.Rubik_Bold)),
        fontSize = 12.sp,
        lineHeight = 18.sp
    )


    @Composable
    fun getMaterialTypography(): androidx.compose.material.Typography {
        return androidx.compose.material.Typography(
            defaultFontFamily = FontFamily(Font(Res.font.Rubik_Regular)),
        )
    }

    @Composable
    fun errorTitle(): TextStyle = TextStyle(
        fontFamily = FontFamily(Font(Res.font.Rubik_Bold)),
        fontSize = 16.sp,
        lineHeight = 16.sp,
        color = AppTheme.colors.textError
    )

    @Composable
    fun errorBody(): TextStyle = TextStyle(
        fontFamily = FontFamily(Font(Res.font.Rubik_Medium)),
        fontSize = 14.sp,
        lineHeight = 14.sp,
        color = AppTheme.colors.primaryText
    )

    @Composable
    fun timerHeader(): TextStyle = TextStyle(
        fontFamily = FontFamily(Font(Res.font.Rubik_Bold)),
        color = AppTheme.colors.appWhite,
        fontSize = 34.sp,
        lineHeight = 30.sp
    )

}