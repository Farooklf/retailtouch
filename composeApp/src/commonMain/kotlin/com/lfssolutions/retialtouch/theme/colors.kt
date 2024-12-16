package com.lfssolutions.retialtouch.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode

val BrandRed = Color(0xFFED1C24)
val BrandGrey = Color(0xFF3D312E)
val BrandDarkBlue= Color(0xFF0e2e3b)
val APPGradientColor1 = Color(0xFF35a3d6) //0xFF35a3d6
val APPGradientColor2 = Color(0xFF226888)
val APPGradientColor3 = Color(0xFF0e2e3b)
val APPGradientColor4 = Color(0xFF0e2e3b)
val TextWhite = Color(0xFFFFFFFF)
val ProgressPrimary = Color(0xFFED1C24)

interface SystemDesignColors {
    val backgroundWindow: Color
    val backgroundDialog: Color
    val backgroundMenu: Color
    val backgroundNavbar: Color
    val screenBackground: Color
    val secondaryBg: Color
    val iconNavbar: Color
    val textPrimary: Color
    val primaryColor: Color
    val secondaryColor: Color
    val secondaryText: Color
    val textCaption: Color
    val textError: Color
    val primaryText: Color
    val appWhite: Color
    val appOffWhite: Color
    val progressPrimary: Color
    val progressBlue: Color
    val primaryButtonBg: Color
    val primaryButtonContent: Color
    val closeButtonBg: Color
    val closeButtonContent: Color
    val textFieldIcon: Color
    val headerTextColor: Color
    val loaderBgColor: Color
    val activeColor: Color
    val inactiveColor: Color
    val searchBoxColor: Color
    val listBorderColor: Color
    val listDivider: Color
    val greyButtonBg: Color
    val greyDarkButtonBg: Color
    val appGreen: Color
    val listItemCardColor: Color
    val cartItemBg: Color
    val listItemSelectedCardColor: Color
    val buttonPrimaryBgColor: Color
    val buttonPrimaryDisableBgColor: Color
    val buttonGreenBgColor: Color
    val buttonGreenDisableBgColor: Color
    val appBarBg: Color
    val appBarContent: Color
    val primaryCardBg: Color
    val secondaryCardBg: Color
    val primaryCardBorder: Color
    val brandText: Color
    val brand: Color
    val textLightGrey: Color
    val textBlack: Color
    val textDarkGrey: Color
    val listRowBgColor: Color
    val listRowBorderColor: Color
    val mintGreenColor: Color
    val switchCheckedThumbColor: Color
    val switchUncheckedThumbColor: Color
    val primaryDarkColor: Color
    val appRed: Color
    val cardBorder: Color


    val displayMasterBackground: Brush
    val screenGradientVerticalBg: Brush
    val screenGradientHorizontalBg: Brush

}

fun DesignColorsLight() : SystemDesignColors = object : SystemDesignColors{
    override val backgroundMenu: Color = BrandGrey
    override val backgroundNavbar: Color = BrandDarkBlue
    override val backgroundWindow: Color = Color(0xFFFFFFFF)
    override val secondaryBg = Color(0xFFFFFFFF)
    override val backgroundDialog: Color = Color(0xFFFFFFFF)
    override val closeButtonBg: Color = Color(0xFFED1C24)
    override val closeButtonContent: Color = Color(0xFFFFFFFF)
    override val textPrimary: Color = Color(0xFF005ce5)
    override val progressPrimary: Color = ProgressPrimary
    override val primaryButtonBg: Color = Color(0xFFED1C24)
    override val primaryButtonContent: Color = Color(0xFFFFFFFF)
    override val textFieldIcon: Color = Color(0xFFED1C24)
    override val headerTextColor: Color = Color(0xFF000000)
    override val loaderBgColor: Color = Color(0xFFFFFFFF)
    override val iconNavbar: Color = Color(0xFFFFFFFF)
    override val activeColor: Color = Color(0xFF1954a0)//35a3d6
    override val inactiveColor: Color = Color(0xFF35a3d6)
    override val screenBackground = Color(0xFFF1F1F1)
    override val searchBoxColor: Color = Color(0xFF438afe)
    override val listBorderColor: Color = Color(0xFF000000)
    override val listDivider: Color = Color(0xFF000000)
    override val greyButtonBg: Color = Color(0xFFc8d0d3)
    override val appGreen = Color(0xFF15ae5c)
    override val appRed = Color(0xFFfe0002)
    override val greyDarkButtonBg= Color(0xFF626465)
    override val progressBlue= Color(0xFF226888)
    override val primaryColor= Color(0xFF005ce5)
    override val primaryDarkColor= Color(0xFF1954a0)
    override val secondaryColor= Color(0xFFbcdffb)
    override val listItemCardColor= Color(0xFF607d8b)
    override val cartItemBg = Color(0xFFF8F8F8)
    override val listItemSelectedCardColor= Color(0xFF8bc34a)
    override val appBarBg= Color(0xFF2596be)
    override val appBarContent= Color(0xFFFFFFFF)
    override val primaryCardBg = Color(0xFFFFFFFF)
    override val secondaryCardBg = Color(0xFFF8F8F8)
    override val primaryCardBorder = Color(0xFFD9D9D9)
    override val primaryText: Color = Color(0xFF000000)
    override val secondaryText = Color(0xFF494949)
    override val textLightGrey = Color(0xFFf5f5f5)
    override val textBlack = Color(0xFF010101)
    override val textDarkGrey = Color(0xFF4c4c4c)
    override val textCaption: Color = Color(0xFF000000)
    override val textError: Color = Color(0xFFb8131a)
    override val appWhite: Color = TextWhite
    override val appOffWhite: Color = Color(0xFFe7e8e6)
    override val brandText = Color(0xFF2596be)
    override val brand = Color(0xFF005ce5)
    override val listRowBgColor = Color(0xFFf4f4f4)
    override val listRowBorderColor = Color(0xFFe1e1e1)
    override val mintGreenColor = Color(0xFF76c29c)
    override val cardBorder = Color(0xFFD7D7D7)


    override val switchCheckedThumbColor: Color
        get() = Color(0xFF2596be)

    override val switchUncheckedThumbColor: Color
        get() = Color(0xFFbcdffb)

    override val buttonPrimaryBgColor: Color
        get() = Color(0xFF2596be)
    override val buttonPrimaryDisableBgColor: Color
        get() = Color(0xFF86d8df)
    override val buttonGreenBgColor: Color
        get() = Color(0xFF4cb050)
    override val buttonGreenDisableBgColor: Color
        get() = Color(0xFF95feae)

    val gradientColors = listOf(
        APPGradientColor1,
        APPGradientColor2,
        APPGradientColor3
    )

    override val displayMasterBackground: Brush = Brush.linearGradient(
        colors = gradientColors,
        start = Offset(0f, 0f),
        end = Offset(1000f, 1000f)
    )

    override val screenGradientVerticalBg= Brush.linearGradient(
        colors = gradientColors,
        start = Offset(0f,0f),
        end = Offset(0f, Float.POSITIVE_INFINITY),
        tileMode = TileMode.Clamp
    )

    override val screenGradientHorizontalBg = Brush.linearGradient(
        colors = gradientColors,
        start = Offset(0f,0f),
        end = Offset(Float.POSITIVE_INFINITY, 0f),
        tileMode = TileMode.Clamp
    )
}

fun DesignColorsDark() : SystemDesignColors = object : SystemDesignColors{
    override val backgroundMenu: Color = BrandGrey
    override val backgroundNavbar: Color = BrandDarkBlue
    override val backgroundWindow: Color = Color(0xFFFFFFFF)
    override val secondaryBg = Color(0xFFFFFFFF)
    override val backgroundDialog: Color = Color(0xFFFFFFFF)
    override val closeButtonBg: Color = Color(0xFFED1C24)
    override val greyButtonBg: Color = Color(0xFFc8d0d3)
    override val closeButtonContent: Color = Color(0xFFFFFFFF)
    override val textPrimary: Color = Color(0xFF005ce5)
    override val progressPrimary: Color = ProgressPrimary
    override val primaryButtonBg: Color = Color(0xFFED1C24)
    override val primaryButtonContent: Color = Color(0xFFFFFFFF)
    override val textFieldIcon: Color = Color(0xFFED1C24)
    override val headerTextColor: Color = Color(0xFF000000)
    override val loaderBgColor: Color = Color(0xFFFFFFFF)
    override val iconNavbar: Color = Color(0xFFFFFFFF)
    override val activeColor: Color = Color(0xFF1954a0)
    override val inactiveColor: Color = Color(0xFF35a3d6)
    override val screenBackground: Color = Color(0xFFF1F1F1)
    override val searchBoxColor: Color = Color(0xFF438afe)
    override val listBorderColor: Color = Color(0xFF000000)
    override val listDivider: Color = Color(0xFF000000)
    override val appGreen = Color(0xFF15ae5c)
    override val appRed = Color(0xFFfe0002)
    override val greyDarkButtonBg= Color(0xFF626465)
    override val progressBlue= Color(0xFF226888)
    override val primaryColor= Color(0xFF005ce5)
    override val primaryDarkColor= Color(0xFF1954a0)
    override val secondaryColor= Color(0xFFbcdffb)
    override val listItemCardColor= Color(0xFF607d8b)
    override val cartItemBg = Color(0xFF383A3D)
    override val listItemSelectedCardColor= Color(0xFF8bc34a)
    override val appBarBg= Color(0xFF2596be)
    override val appBarContent= Color(0xFFFFFFFF)
    override val primaryCardBg = Color(0xFFFFFFFF)
    override val secondaryCardBg = Color(0xFFF8F8F8)
    override val primaryCardBorder = Color(0xFFD9D9D9)
    override val primaryText: Color = Color(0xFF000000)
    override val secondaryText = Color(0xFF494949)
    override val textBlack = Color(0xFF000000)
    override val textLightGrey = Color(0xFFE2E2E2)
    override val textDarkGrey = Color(0xFF4c4c4c)
    override val textCaption: Color = Color(0xFF000000)
    override val textError: Color = Color(0xFFb8131a)
    override val appWhite: Color = Color(0xFFFFFFFF)
    override val appOffWhite: Color = Color(0xFFe7e8e6)
    override val brandText = Color(0xFF2596be)
    override val brand = Color(0xFF005ce5)
    override val listRowBgColor = Color(0xFFf4f4f4)
    override val listRowBorderColor = Color(0xFFe1e1e1)
    override val mintGreenColor = Color(0xFF76c29c)
    override val cardBorder = Color(0xFFD7D7D7)

    override val buttonPrimaryBgColor: Color
        get() = Color(0xFF2596be)
    override val buttonPrimaryDisableBgColor: Color
        get() = Color(0xFF86d8df)
    override val buttonGreenBgColor: Color
        get() = Color(0xFF4cb050)
    override val buttonGreenDisableBgColor: Color
        get() = Color(0xFF95feae)

    override val switchCheckedThumbColor: Color
        get() = Color(0xFF2596be)

    override val switchUncheckedThumbColor: Color
        get() = Color(0xFFbcdffb)

    val gradientColors = listOf(
        APPGradientColor1,
        APPGradientColor2,
        APPGradientColor3,
        APPGradientColor4
    )

    override val displayMasterBackground: Brush = Brush.linearGradient(
        colors = gradientColors,
        start = Offset(0f, 0f),
        end = Offset(1000f, 1000f)
    )

    override val screenGradientVerticalBg= Brush.linearGradient(
        colors = gradientColors,
        start = Offset(0f,0f),
        end = Offset(0f, Float.POSITIVE_INFINITY),
        tileMode = TileMode.Clamp
    )

    override val screenGradientHorizontalBg = Brush.linearGradient(
        colors = gradientColors,
        start = Offset(0f,0f),
        end = Offset(Float.POSITIVE_INFINITY, 0f),
        tileMode = TileMode.Clamp
    )


}



fun createGradientBrush(
    colors: List<Color>,
    isVertical: Boolean = true
): Brush {

    val endOffset = if (isVertical) {
        Offset(0f, Float.POSITIVE_INFINITY)
    } else {
        Offset(Float.POSITIVE_INFINITY, 0f)
    }

    return Brush.linearGradient(
        colors = colors,
        start = Offset(0f, 0f),
        end = endOffset,
        tileMode = TileMode.Clamp
    )
}


