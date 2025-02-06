package com.lfssolutions.retialtouch.presentation.ui.common


import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material.Text
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.lfssolutions.retialtouch.domain.model.products.AnimatedProductCard
import com.lfssolutions.retialtouch.domain.model.products.CartItem
import com.lfssolutions.retialtouch.domain.model.products.Product
import com.lfssolutions.retialtouch.domain.model.products.Stock
import com.lfssolutions.retialtouch.presentation.ui.screens.CommonListRow
import com.lfssolutions.retialtouch.presentation.viewModels.SharedPosViewModel
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons
import com.lfssolutions.retialtouch.utils.LocalAppState
import com.lfssolutions.retialtouch.utils.capitalizeFirstChar
import com.outsidesource.oskitcompose.layout.spaceBetweenPadded
import org.jetbrains.compose.resources.painterResource
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.ic_star


@Composable
fun CategoryListItem(
    image: String="",
    name: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val appState = LocalAppState.current
    val textSty=if(appState.isPortrait)
        AppTheme.typography.captionBold()
    else
        AppTheme.typography.bodyBold()

    val boxColor= if (isSelected) AppTheme.colors.cardSelectedBgColor else AppTheme.colors.cardBgColor

    Card(modifier = Modifier.wrapContentWidth().wrapContentHeight()
        .clickable(interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick)
        .padding(horizontal = AppTheme.dimensions.padding2),
        colors = CardDefaults.cardColors(containerColor = boxColor),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = AppTheme.appShape.card) {
        Row(
            modifier = Modifier.wrapContentWidth().padding(horizontal = 5.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            /*SubcomposeAsyncImage(
                model = image,
                contentDescription = null,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(40.dp),
                contentScale = ContentScale.Crop,
                error = { ImagePlaceholder() }
            )*/

            Text(
                modifier = Modifier.defaultMinSize(100.dp).wrapContentWidth().padding(horizontal = 10.dp, vertical = 5.dp),
                text = name.capitalizeFirstChar(),
                style = textSty,
                color = if (isSelected) AppTheme.colors.appWhite else AppTheme.colors.primaryText,
            )
        }   
    }
}

@Composable
fun ProductListItem(
    product: Stock,
    viewModel: SharedPosViewModel,
    isList: Boolean = false,
    onClick: (AnimatedProductCard) -> Unit = {}
) {
    if (isList) {
        ListProductItem(
            product = product,
            viewModel=viewModel,
            onClick = onClick
        )
    } else {
        GridProductItem(
            product = product,
            viewModel=viewModel,
            onClick = onClick
        )
    }
}


@Composable
private fun GridProductItem(
    product: Stock,
    viewModel: SharedPosViewModel,
    onClick: (AnimatedProductCard) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val appState = LocalAppState.current
    val density = LocalDensity.current
    var card by remember(product) { mutableStateOf(AnimatedProductCard(product)) }

    val textStyle=if(appState.isPortrait)
        AppTheme.typography.captionMedium()
    else
        AppTheme.typography.bodyMedium()

    Column(
        modifier = modifier
            .heightIn(min = 180.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onClick(card) }
            )
            .onGloballyPositioned {
                with(density) {
                    card = card.copy(
                        width = it.size.width.toDp(),
                        height = it.size.height.toDp(),
                        xOffset = it.positionInWindow().x.toDp(),
                        yOffset = it.positionInWindow().y.toDp()
                    )
                }
            }
            .background(AppTheme.colors.cardBgColor)
            .padding(AppTheme.dimensions.padding5),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.padding5)
    ) {

        SubcomposeAsyncImage(
            modifier = Modifier
                .weight(0.7f)
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp)),
            model = "${product.imagePath}",
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            clipToBounds = true,
            error = { ImagePlaceholderWithUrl() }
        )

        Column(
            modifier = Modifier.weight(0.3f).padding(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically)
            {
                Text(
                    text = product.name.uppercase(),
                    modifier = Modifier
                        .weight(1.5f),
                    style = textStyle,
                    color = AppTheme.colors.textDarkGrey,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3
                )

                Text(
                    text = viewModel.formatPriceForUI(product.price?:0.0),
                    modifier = Modifier.wrapContentWidth(),
                    style = textStyle,
                    color = AppTheme.colors.textPrimary,
                    textAlign = TextAlign.End
                )
            }
            /*Text(
                text =  product.aliasName,
                modifier = Modifier.padding(horizontal = 10.dp),
                style = AppTheme.typography.captionMedium(),
                color = AppTheme.colors.primaryText,
            )*/
        }
    }
}

@Composable
private fun ListProductItem(
    product: Stock,
    viewModel: SharedPosViewModel,
    onClick: (AnimatedProductCard) -> Unit = {}
) {
    Card(modifier = Modifier.wrapContentWidth().wrapContentHeight(), elevation = CardDefaults.cardElevation(2.dp)){
        Row(
            modifier = Modifier
                .height(120.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { onClick(AnimatedProductCard(product)) }
                )
                .background(AppTheme.colors.cardBgColor)
                .padding(6.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .weight(0.2f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp)),
                model = "${product.imagePath}",
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                clipToBounds = true,
                error = { ImagePlaceholder() }
            )

            Row(
                modifier = Modifier.weight(0.8f), horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp)
                ) {
                    Text(
                        text = product.name.uppercase(),
                        modifier = Modifier,
                        style = AppTheme.typography.bodyBold(),
                        color = AppTheme.colors.textDarkGrey,
                    )
                    /*Text(
                        text = product.aliasName,
                        style = AppTheme.typography.captionMedium(),
                        color = AppTheme.colors.primaryText,
                    )*/
                }

                Text(
                    text = viewModel.formatPriceForUI(product.price?:0.0),
                    modifier = Modifier
                        .padding(end = 10.dp),
                    style = AppTheme.typography.bodyBold(),
                    color = AppTheme.colors.brandText,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
fun ProductItemAnimation(
    card: AnimatedProductCard,
    viewModel: SharedPosViewModel
) {
    var startAnimation by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(card) {
        startAnimation = true
    }

    val density = LocalDensity.current
    var screenWidth by remember {
        mutableStateOf(0.dp)
    }
    var screenHeight by remember {
        mutableStateOf(0.dp)
    }

    val animatedScale by animateFloatAsState(
        targetValue = if (startAnimation) 0.4f else 1f,
        animationSpec = tween(durationMillis = 50, easing = LinearEasing),
    )

    val xOffset by animateDpAsState(
        targetValue = if (startAnimation) screenWidth/4 else card.xOffset,
        animationSpec = tween(delayMillis = 50, durationMillis = 100, easing = LinearEasing)
    )

    val yOffset by animateDpAsState(
        targetValue = if (startAnimation) screenHeight else card.yOffset,
        animationSpec = tween(delayMillis = 50, durationMillis = 100, easing = LinearEasing)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                with(density) {
                    screenWidth = it.size.width.toDp()
                    screenHeight = it.size.height.toDp()
                }
            },
        contentAlignment = Alignment.TopStart
    ) {
        GridProductItem(
            product = card.product,
            viewModel=viewModel,
            modifier = Modifier
                .width(card.width)
                .height(card.height)
                .offset(
                    x = xOffset,
                    y = yOffset
                )
                .scale(animatedScale)

        )
    }
}



@Composable
fun CartListItem(
    index:Int,
    item: CartItem,
    isPortrait:Boolean,
    horizontalPadding: Dp,
    verticalPadding: Dp,
    posViewModel: SharedPosViewModel)
{
    val (primaryText, textTotalLabel) = when {
        item.discount > 0 || item.currentDiscount > 0 -> {
            AppTheme.colors.textError to  "  ${posViewModel.formatPriceForUI(item.getFinalPrice())} \n (${posViewModel.calculateDiscount(item)})"
        }
        else -> {
            AppTheme.colors.primaryText.copy(alpha = 0.8f) to posViewModel.formatPriceForUI(item.getFinalPrice())
        }
    }

    val (borderColor,rowBgColor)=when(index%2 == 0){
        true->  AppTheme.colors.borderColor to AppTheme.colors.listRowBgColor
        false ->AppTheme.colors.appWhite to AppTheme.colors.appWhite
    }

    val (buttonBgColor,textColor)=when(index%2 == 0){
        true->   AppTheme.colors.primaryColor to AppTheme.colors.appWhite
        false -> AppTheme.colors.listRowBgColor to AppTheme.colors.textPrimary
    }

    val textStyle = AppTheme.typography.captionMedium()

    Box(modifier = Modifier.fillMaxWidth().wrapContentHeight().background(AppTheme.colors.appWhite)){
        Column(modifier = Modifier.fillMaxWidth().wrapContentHeight().background(rowBgColor),
            verticalArrangement = Arrangement.spaceBetweenPadded(5.dp)) {
            AppHorizontalDivider(color = borderColor, modifier = Modifier.fillMaxWidth().padding(start = horizontalPadding))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {

                ListCenterText(
                    label = "${index+1}.",
                    textStyle = textStyle,
                    color = AppTheme.colors.textBlack,
                    modifier = Modifier.wrapContentWidth(),
                    arrangement = Arrangement.Start
                )


                Row(modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if(item.promotion!=null){
                        Image(
                            painter = painterResource(Res.drawable.ic_star),
                            contentDescription = null,
                            modifier = Modifier.size(AppTheme.dimensions.smallXIcon),
                            contentScale = ContentScale.Crop
                        )
                    }
                    //Items name
                    ListText(
                        label = "${item.stock.name} [${item.stock.inventoryCode}]",
                        textStyle =textStyle,
                        color = AppTheme.colors.textBlack,
                        modifier = Modifier.wrapContentWidth()
                    )
                }

                //modifier icons
                VectorIcons(icons = AppIcons.cancelIcon,
                    modifier = Modifier.wrapContentWidth(),
                    onClick = {
                        posViewModel.updateItemRemoveDialogState(true)
                        posViewModel.updateSelectedItem(item)
                    }
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Spacer(modifier = Modifier.weight(1f))

                //Price
                Row(modifier = Modifier.weight(1f),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                    GreyButtonWithElevation(
                        modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                        label = posViewModel.formatPriceForUI(item.price),
                        contentColor = textColor,
                        buttonBgdColor = buttonBgColor,
                        textStyle =textStyle,
                        onClick = {
                            posViewModel.updateItemDiscountDialogState(true)
                            posViewModel.onPriceItemClick(item,index)
                        }
                    )
                }

                //Qty
                Row(modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ){
                    QtyItemText(
                        label = "${item.qty}",
                        textStyle = textStyle,
                        color = AppTheme.colors.textPrimary,
                        modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                        isEven=index%2 == 0,
                        isPortrait=isPortrait,
                        onIncreaseClick = {
                            posViewModel.increaseQty(item)
                        },
                        onDecreaseClick = {
                            posViewModel.decreaseQty(item)
                        },
                        onClick = {
                            posViewModel.applyCustomQty(item)
                        }
                    )
                }
                Column(modifier = Modifier.weight(1f).wrapContentHeight(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    ListText(
                        modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                        label = posViewModel.formatPriceForUI(item.getFinalPrice()),
                        textStyle = AppTheme.typography.bodyBold(),
                        color = AppTheme.colors.textPrimary
                    )

                    if(item.discount > 0 || item.currentDiscount > 0){
                        ListText(
                            modifier = Modifier.wrapContentWidth().wrapContentHeight(),
                            label = "(${posViewModel.calculateDiscount(item)})",
                            textStyle = textStyle,
                            color = AppTheme.colors.textError
                        )
                    }
                }

            }
            AppHorizontalDivider(color = borderColor, modifier = Modifier.fillMaxWidth().padding(start = horizontalPadding))
        }
    }
}

@Composable
fun StokesListItem(position :Int, product: Product, currencySymbol: String, onClick: (Product) -> Unit) {
    val appState = LocalAppState.current
    val (borderColor,rowBgColor)=when(position%2 != 0){
        true->  AppTheme.colors.borderColor to AppTheme.colors.listRowBgColor
        false ->AppTheme.colors.appWhite to AppTheme.colors.appWhite
    }

    val horizontalPadding=if(appState.isPortrait)
        AppTheme.dimensions.padding10
    else
        AppTheme.dimensions.padding20

    if(appState.isPortrait){
        Box(modifier = Modifier.fillMaxWidth().wrapContentHeight().background(AppTheme.colors.appWhite)){
            Column(modifier = Modifier.fillMaxWidth().wrapContentHeight().background(rowBgColor).clickable{onClick(product)},
                verticalArrangement = Arrangement.spaceBetweenPadded(10.dp)) {
                AppHorizontalDivider(color = borderColor, modifier = Modifier.fillMaxWidth().padding(start = horizontalPadding))
                CommonListRow(product=product, currencySymbol=currencySymbol)
                ListText(
                    label = product.name?:"",
                    textStyle = AppTheme.typography.bodyMedium(),
                    color = AppTheme.colors.textBlack,
                    modifier = Modifier.wrapContentWidth().padding(start = horizontalPadding)
                )
                AppHorizontalDivider(color = borderColor, modifier = Modifier.fillMaxWidth().padding(start = horizontalPadding))
            }
        }
    }
    else{
        Box(modifier = Modifier.fillMaxWidth().wrapContentHeight().background(AppTheme.colors.appWhite)){
            Column(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(rowBgColor)
                .clickable{onClick(product)},
                verticalArrangement =Arrangement.spaceBetweenPadded(10.dp)
            ) {
                AppHorizontalDivider(color = borderColor, modifier = Modifier.fillMaxWidth().padding(start = horizontalPadding))
                CommonListRow(product=product,currencySymbol=currencySymbol)
                AppHorizontalDivider(color = borderColor, modifier = Modifier.fillMaxWidth().padding(start = horizontalPadding))
            }
        }
    }
}
