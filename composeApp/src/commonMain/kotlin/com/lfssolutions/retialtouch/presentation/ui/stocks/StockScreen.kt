package com.lfssolutions.retialtouch.presentation.ui.stocks

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import coil3.compose.AsyncImage
import com.lfssolutions.retialtouch.domain.model.products.POSProduct
import com.lfssolutions.retialtouch.presentation.ui.common.BasicScreen
import com.lfssolutions.retialtouch.presentation.ui.common.CommonListHeader
import com.lfssolutions.retialtouch.presentation.ui.common.SearchableTextWithBg
import com.lfssolutions.retialtouch.presentation.ui.common.StockProductListItem
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.search_items
import retailtouch.composeapp.generated.resources.stock

object StockScreen : Screen {

    @Composable
    override fun Content() {
        StockScreenContent()
    }
}

@Composable
fun StockScreenContent(
    viewModel: StockViewModel = koinInject()
){
    val state by viewModel.stockUiState.collectAsStateWithLifecycle()
    val currencySymbol by viewModel.currencySymbol.collectAsState()
    var lastKnownCount by remember { mutableStateOf(10) }
    val appThemeContext = AppTheme.context
    val navigator=appThemeContext.getAppNavigator()

    // Update last known count when products are loaded
   /* LaunchedEffect(state.products) {
        if (state.products.isNotEmpty()) {
            lastKnownCount = state.products.size
        }
     }

    val placeholderCount = if (state.products.isEmpty()) lastKnownCount else 0
*/
    BasicScreen(
        modifier = Modifier.systemBarsPadding(),
        title = stringResource(Res.string.stock),
        isTablet = appThemeContext.isTablet,
        contentMaxWidth = Int.MAX_VALUE.dp,
        onBackClick = {
            appThemeContext.navigateBack(navigator)
        }
    ){
        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight().background(AppTheme.colors.screenBackground)
        ){
            SearchableTextWithBg(
                value = state.searchQuery,
                leadingIcon= AppIcons.searchIcon,
                placeholder = stringResource(Res.string.search_items),
                label = stringResource(Res.string.search_items),
                modifier = Modifier.fillMaxWidth().padding(horizontal = AppTheme.dimensions.padding10, vertical = AppTheme.dimensions.padding10),
                onValueChange = {
                    viewModel.updateSearchQuery(it)
                })

            //List Content
            CommonListHeader(
                showCancel = state.selectedProducts.isNotEmpty(),
                onClearSelection={
                    viewModel.clearSelection()
                }
            )
            // Display filtered products in a LazyColumn
            LazyColumn(modifier = Modifier.fillMaxWidth().fillMaxHeight()){
                // Filter the product tax list based on the search query
                val filteredProducts = state.products.filter { it.matches(state.searchQuery) }.toMutableList()
                itemsIndexed(filteredProducts){ index, product ->
                    StockProductListItem(
                        position=index,
                        product=product,
                        isChecked = product.id in state.selectedProducts,
                        currencySymbol=currencySymbol,
                        isTablet = appThemeContext.isTablet,
                        onCheckedChange = { product->
                          viewModel.toggleProductSelection(product.id)
                        }
                    )
                }
            }
        }
    }

    /*Box(modifier = Modifier.fillMaxSize()) {
        if (state.products.isEmpty()) {
            LazyColumn {
                items(placeholderCount) {
                    ShimmerEffect()  // Show shimmer effect while loading
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

            }
        }
    }*/
}

@Composable
fun StockProductItem(product: POSProduct) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.image,
                contentDescription = "Product Image",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(text = product.name, fontWeight = FontWeight.Bold)
                Text(text = "Code: ${product.productCode}")
                Text(text = "Price: $${product.price}")
            }
        }
    }
}

@Composable
fun ShimmerEffect() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(8.dp)
            .shimmerEffect()
            .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
    )
}

// Extension function for shimmer effect
fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition()
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            tween(1000, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        )
    )
    this.background(Brush.linearGradient(listOf(Color.Gray.copy(alpha = alpha), Color.LightGray)))
}
