package com.hashmato.retailtouch.presentation.ui.members

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import com.hashmato.retailtouch.domain.model.members.Member
import com.hashmato.retailtouch.presentation.common.AppCheckBox
import com.hashmato.retailtouch.presentation.common.AppHorizontalDivider
import com.hashmato.retailtouch.presentation.common.BasicScreen
import com.hashmato.retailtouch.presentation.common.ListCenterText
import com.hashmato.retailtouch.presentation.common.ListText
import com.hashmato.retailtouch.presentation.common.SearchableTextWithGradientBg
import com.hashmato.retailtouch.theme.AppTheme
import com.hashmato.retailtouch.utils.AppIcons
import com.outsidesource.oskitcompose.layout.spaceBetweenPadded
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.koinInject
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.address
import retailtouch.composeapp.generated.resources.code
import retailtouch.composeapp.generated.resources.email
import retailtouch.composeapp.generated.resources.members
import retailtouch.composeapp.generated.resources.mobile_number
import retailtouch.composeapp.generated.resources.name
import retailtouch.composeapp.generated.resources.search_items


object MemberScreen : Screen {

    @Composable
    override fun Content() {
        MembersContent()
    }

    @Composable
    fun MembersContent(
        viewModel: MemberViewModel = koinInject()
    ){
        val appThemeContext = AppTheme.context
        val navigator=appThemeContext.getAppNavigator()
        val state by viewModel.uiState.collectAsStateWithLifecycle()

        BasicScreen(
            modifier = Modifier.systemBarsPadding(),
            gradientBg = appThemeContext.colors.screenGradientHorizontalBg,
            title = stringResource(Res.string.members),
            isTablet = appThemeContext.isTablet,
            contentMaxWidth = Int.MAX_VALUE.dp,
            onBackClick = {
                appThemeContext.navigateBack(navigator)
            }
        ){
            Column(modifier = Modifier.fillMaxWidth().fillMaxHeight()){

                SearchableTextWithGradientBg(
                    value = state.searchQuery,
                    leadingIcon= AppIcons.searchIcon,
                    placeholder = stringResource(Res.string.search_items),
                    label = stringResource(Res.string.search_items),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = AppTheme.dimensions.padding10, vertical = AppTheme.dimensions.padding10),
                    onValueChange = {
                        //viewModel.updateSearchQuery(it)
                    })

                val textStyle=if(appThemeContext.isPortrait)
                    AppTheme.typography.bodyBold()
                else
                    AppTheme.typography.titleBold()

                val horizontalPadding=if(appThemeContext.isPortrait)
                    AppTheme.dimensions.padding5
                else
                    AppTheme.dimensions.padding20

                AppHorizontalDivider(color = appThemeContext.colors.appWhite, modifier = Modifier.fillMaxWidth().padding(horizontal = horizontalPadding, vertical = AppTheme.dimensions.padding10))
                Row(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = horizontalPadding, vertical = AppTheme.dimensions.padding10),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(appThemeContext.dimensions.padding5)

                ){
                    //cancel button
                    if(state.selectedMembers.isNotEmpty()){
                        IconButton(onClick = {
                            viewModel.clearSelection()
                        }) {
                            Icon(imageVector = vectorResource(AppIcons.closeIcon), contentDescription = "Clear Selection", modifier = Modifier.size(appThemeContext.dimensions.icon16), tint = appThemeContext.colors.appWhite)
                        }
                    }

                    // Adjust the weight proportions
                    ListCenterText(
                        label = stringResource(Res.string.name),
                        color = AppTheme.colors.appWhite,
                        textStyle = textStyle,
                        modifier = Modifier.weight(1.5f))

                    ListCenterText(
                        label = stringResource(Res.string.code),
                        color = AppTheme.colors.appWhite,
                        textStyle = textStyle,
                        modifier = Modifier.weight(.5f)
                    )

                    ListCenterText(
                        label = stringResource(Res.string.email),
                        color = AppTheme.colors.appWhite,
                        textStyle = textStyle,
                        modifier = Modifier.weight(1f))
                    ListCenterText(
                        label = stringResource(Res.string.mobile_number),
                        color = AppTheme.colors.appWhite,
                        textStyle = textStyle,
                        modifier = Modifier.weight(1f))

                    if(!appThemeContext.isPortrait)
                    {
                        ListCenterText(
                            label = stringResource(Res.string.address),
                            color = AppTheme.colors.appWhite,
                            textStyle = textStyle,
                            modifier = Modifier.weight(1f))
                    }
                }
                AppHorizontalDivider(color = appThemeContext.colors.appWhite, modifier = Modifier.fillMaxWidth().padding(horizontal = horizontalPadding, vertical = AppTheme.dimensions.padding10))

                // Display filtered products in a LazyColumn
                LazyColumn(modifier = Modifier.fillMaxWidth().fillMaxHeight()){
                    // Filter the product tax list based on the search query
                    val filteredList = state.members.filter { it.matches(state.searchQuery) }.toMutableList()
                    itemsIndexed(filteredList){ index, member ->
                        MemberListItem(
                            position=index,
                            member=member,
                            isChecked = member.memberId in state.selectedMembers,
                            isTablet = appThemeContext.isTablet,
                            onCheckedChange = { product->
                                viewModel.toggleMemberSelection(product.memberId)
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MemberListItem(
    position: Int,
    member: Member,
    isTablet: Boolean,
    isChecked: Boolean=false,
    onCheckedChange : (Member) -> Unit
) {
    //val appState = LocalAppState.current
    val (borderColor,rowBgColor)=when(position%2 != 0){
        true->  AppTheme.colors.appWhite to Color.Transparent
        false ->AppTheme.colors.appOffWhite to AppTheme.colors.primaryColor
    }

    val horizontalPadding=if(isTablet)
        AppTheme.dimensions.padding5
    else
        AppTheme.dimensions.padding20

    val address="${member.address1}${member.address2} ${member.address3}"

    if(!isTablet){
        Column(modifier = Modifier.fillMaxWidth().wrapContentHeight().clickable{onCheckedChange (member)},
            verticalArrangement = Arrangement.Center) {
            CommonMemberListRow(member = member, checked = isChecked, onCheckedChange = {
                onCheckedChange.invoke(member)
            })

            if(address.trim().isNotEmpty()){
                ListText(
                    label = address,
                    textStyle = AppTheme.typography.bodyMedium(),
                    color = AppTheme.colors.appOffWhite,
                    modifier = Modifier.wrapContentWidth().padding(start = horizontalPadding)
                )
            }
            AppHorizontalDivider(color = borderColor, modifier = Modifier.fillMaxWidth().padding(horizontal = horizontalPadding, vertical = AppTheme.dimensions.padding10))
        }
    }
    else{
        Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()){
            Column(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(rowBgColor)
                .clickable{onCheckedChange.invoke(member)},
                verticalArrangement =Arrangement.spaceBetweenPadded(10.dp)
            ) {

                CommonMemberListRow(member = member, checked = isChecked, onCheckedChange = {
                    onCheckedChange.invoke(member)
                })
                AppHorizontalDivider(color = borderColor, modifier = Modifier.fillMaxWidth().padding(horizontal = horizontalPadding,vertical = AppTheme.dimensions.padding10))
            }
        }
    }
}

@Composable
fun CommonMemberListRow(member: Member,  checked: Boolean=false,
                  onCheckedChange : () -> Unit = {}){
    val address="${member.address1}${member.address2} ${member.address3}"
    val appState = AppTheme.context
    val textStyle=if(appState.isPortrait)
        AppTheme.typography.bodyMedium()
    else
        AppTheme.typography.titleMedium()

    val horizontalPadding=if(appState.isPortrait)
        AppTheme.dimensions.padding5
    else
        AppTheme.dimensions.padding20

    Row(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = horizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(appState.dimensions.padding5)

    ){
        //checkBox
        AppCheckBox(
            checked = checked,
            modifier = Modifier.size(appState.dimensions.standerIcon),
            checkedColor = AppTheme.colors.appWhite,
            uncheckedColor = AppTheme.colors.appOffWhite,
            checkmarkColor = AppTheme.colors.primaryColor,
            onCheckedChange = { onCheckedChange() }
        )

        //Name
        ListCenterText(
            label = member.name.uppercase(),
            textStyle = textStyle,
            color = AppTheme.colors.appWhite,
            modifier = Modifier.weight(1.5f)
        )

        //member code
        ListCenterText(
            label = member.memberCode,
            textStyle = textStyle,
            color = AppTheme.colors.appWhite.copy(alpha = .8f),
            modifier = Modifier.weight(.5f)
        )

        ListCenterText(
            label = member.email,
            textStyle = textStyle,
            color = AppTheme.colors.appWhite,
            modifier = Modifier.weight(1f)
        )


        ListCenterText(
            label = member.mobileNo,
            textStyle = textStyle,
            color = AppTheme.colors.appWhite,
            modifier = Modifier.weight(1f)
        )

        if(appState.isTablet){
            ListText(
                label = address,
                textStyle =textStyle,
                color = AppTheme.colors.appOffWhite,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


