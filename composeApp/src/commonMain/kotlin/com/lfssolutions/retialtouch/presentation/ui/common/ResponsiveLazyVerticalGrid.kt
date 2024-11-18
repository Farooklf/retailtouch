package com.lfssolutions.retialtouch.presentation.ui.common


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lfssolutions.retialtouch.domain.model.AppState
import com.lfssolutions.retialtouch.domain.model.home.HomeScreenItem
import com.lfssolutions.retialtouch.domain.model.home.HomeUIState
import com.lfssolutions.retialtouch.domain.model.paymentType.PaymentMethod
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons
import com.lfssolutions.retialtouch.utils.LocalAppState
import com.outsidesource.oskitcompose.layout.FlexRowLayoutScope.weight
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.cashier
import retailtouch.composeapp.generated.resources.drawer
import retailtouch.composeapp.generated.resources.logout
import retailtouch.composeapp.generated.resources.members
import retailtouch.composeapp.generated.resources.payout
import retailtouch.composeapp.generated.resources.printer
import retailtouch.composeapp.generated.resources.receipt
import retailtouch.composeapp.generated.resources.settings
import retailtouch.composeapp.generated.resources.settlement
import retailtouch.composeapp.generated.resources.stock
import retailtouch.composeapp.generated.resources.sync



fun LazyGridScope.ListGridItems(
    homeItemList: List<HomeScreenItem>,
    syncInProgress: Boolean,
    onClick: (Int) -> Unit
) {
    items(homeItemList.size) { index ->
        val item = homeItemList[index]
        val itemName = stringResource(item.labelResId)
        HomeItem(syncInProgress,itemName,item.icon,item.homeItemId,
            onClick = {
                onClick.invoke(item.homeItemId)
            }
        )
    }
}


@Composable
fun HomeItem(
    syncInProgress:Boolean=false,
    title:String,
    icon: DrawableResource,
    id:Int=0,
    onClick: () -> Unit,
) {
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(syncInProgress) {
        if (syncInProgress) {
            rotation.animateTo(
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            rotation.snapTo(0f)
        }
    }

    Column(
        modifier = Modifier.wrapContentWidth()
            .wrapContentHeight()
            .padding(vertical = 20.dp)
            .clickable{onClick.invoke()},
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        Icon( imageVector = vectorResource(icon),
            tint = AppTheme.colors.appWhite,
            contentDescription = title,
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.CenterHorizontally)
                .padding(8.dp)
                .rotate(if(id==5)rotation.value else 0f))

        Text(
            text = title.uppercase(),
            color = AppTheme.colors.appWhite,
            style = AppTheme.typography.titleNormal(),
            textAlign = TextAlign.Center
        )
    }

}


@Composable
fun HomeGridSection() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center

        ) {

            HomeItem(
                title = stringResource(Res.string.cashier),
                icon = AppIcons.categoryIcon,
                onClick = {}
            )

            HomeItem(
                title = stringResource(Res.string.members),
                icon = AppIcons.membershipIcon,
                onClick = {}
            )

            HomeItem(
                title = stringResource(Res.string.stock),
                icon = AppIcons.membershipIcon,
                onClick = {}
            )

            HomeItem(
                title = stringResource(Res.string.receipt),
                icon = AppIcons.settingIcon,
                onClick = {}
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center

        ) {

            HomeItem(
                title = stringResource(Res.string.sync),
                icon = AppIcons.syncIcon,
                onClick = {}
            )

            HomeItem(
                title = stringResource(Res.string.settlement),
                icon = AppIcons.membershipIcon,
                onClick = {}
            )

            HomeItem(
                title = stringResource(Res.string.payout),
                icon = AppIcons.membershipIcon,
                onClick = {}
            )

            HomeItem(
                title = stringResource(Res.string.printer),
                icon = AppIcons.settingIcon,
                onClick = {}
            )
        }

        //3rd
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center

        ) {

            HomeItem(
                title = stringResource(Res.string.drawer),
                icon = AppIcons.categoryIcon,
                onClick = {}
            )

            HomeItem(
                title = stringResource(Res.string.settings),
                icon = AppIcons.settingIcon,
                onClick = {}
            )

            HomeItem(
                title = stringResource(Res.string.logout),
                icon = AppIcons.logoutIcon,
                onClick = {}
            )

        }
    }

}


@Composable
fun <T> LazyVerticalGridG(
    items: List<T>,
    onClick: (T) -> Unit,
    modifier:Modifier=Modifier.fillMaxSize()
) {
    val appState = LocalAppState.current
    val gridCell=getGridCell(appState)


    LazyVerticalGrid(
        columns = GridCells.Fixed(gridCell), // Adjust the column count as needed
        modifier = modifier
    ){
        items(items.size) { index ->
            val item = items[index]

            LazyVerticalItem(
                 item=item,
                isTab=appState.isTablet,
                 onClick = {
                     onClick(item)
                 }
            )
        }
    }
}

@Composable
fun <T> LazyVerticalItem(item: T,isTab:Boolean, onClick: () -> Unit) {
    item as PaymentMethod
    val cardColor = if (item.isSelected) {
        CardDefaults.cardColors(containerColor = AppTheme.colors.listItemSelectedCardColor)
    } else {
        CardDefaults.cardColors(containerColor = AppTheme.colors.listItemCardColor)
        }

    Card(
        modifier = Modifier.wrapContentHeight().weight(1f).padding(vertical = if(isTab) AppTheme.dimensions.tabListVerPadding else AppTheme.dimensions.phoneListVerPadding, horizontal = AppTheme.dimensions.listHorPadding)
            .clickable{  onClick.invoke() },
        colors = cardColor,
        elevation = CardDefaults.cardElevation(AppTheme.dimensions.cardElevation),
        shape = AppTheme.appShape.card
    ){
      Row(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = if(isTab) AppTheme.dimensions.tabHorPadding else AppTheme.dimensions.phoneHorPadding , vertical = if(isTab) AppTheme.dimensions.tabVerPadding else AppTheme.dimensions.phoneVerPadding).horizontalScroll(
          rememberScrollState()
      ),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {

          KamelImage(
              resource = asyncPainterResource("https://image.shutterstock.com/image-photo/grilled-chicken-drumsticks-chips-vegetables-260nw-279137960.jpg"),
              contentDescription = null,
              modifier = Modifier
                  .size(AppTheme.dimensions.mediumIcon),
              contentScale = ContentScale.Crop,
              onFailure = { ImagePlaceholder() },
              onLoading = { ImagePlaceholder() }
          )
          Text(
              text = item.name?.trim()?:"",
              style = AppTheme.typography.captionMedium(),
              color = AppTheme.colors.appWhite,
              minLines=1,
              maxLines = 1,
              softWrap = true,
              overflow = TextOverflow.Ellipsis
          )


          if(item.isShowPaidAmount){
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                  text = item.paidAmount,
                  style = AppTheme.typography.bodyMedium(),
                  color = AppTheme.colors.appWhite,
                  minLines=1,
                  maxLines = 1,
                  softWrap = true,
                  overflow = TextOverflow.Ellipsis
              )

              VectorIcons(icons = AppIcons.calculatorIcon,
                  modifier = Modifier.width(AppTheme.dimensions.smallIcon),
                  iconColor = AppTheme.colors.appWhite,
                  onClick = {
                      //onIconClick.invoke()
                  }
              )
          }


      }

    }
}

fun getGridCell(appState: AppState): Int {
    return when (appState.isPortrait) {
        true -> 2
        false-> 4

        /*DeviceType.SMALL_PHONE -> {
            2
        }

        DeviceType.LARGE_PHONE -> {
            2
        }

        DeviceType.SMALL_TABLET -> {
            3
        }

        DeviceType.LARGE_TABLET -> {
            4
        }*/
    }
}
