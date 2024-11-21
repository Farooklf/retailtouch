package com.lfssolutions.retialtouch.presentation.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lfssolutions.retialtouch.domain.model.members.MemberItem
import com.lfssolutions.retialtouch.domain.model.products.PosUIState
import com.lfssolutions.retialtouch.presentation.viewModels.SharedPosViewModel
import com.lfssolutions.retialtouch.theme.AppTheme
import com.lfssolutions.retialtouch.utils.AppIcons
import com.outsidesource.oskitcompose.layout.spaceBetweenPadded
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import retailtouch.composeapp.generated.resources.Res
import retailtouch.composeapp.generated.resources.address
import retailtouch.composeapp.generated.resources.apply
import retailtouch.composeapp.generated.resources.cancel
import retailtouch.composeapp.generated.resources.clear
import retailtouch.composeapp.generated.resources.create
import retailtouch.composeapp.generated.resources.create_new_member
import retailtouch.composeapp.generated.resources.email
import retailtouch.composeapp.generated.resources.member_code
import retailtouch.composeapp.generated.resources.mobile_number
import retailtouch.composeapp.generated.resources.name
import retailtouch.composeapp.generated.resources.zip_code


@Composable
fun MemberList(
    posUIState : PosUIState,
    posViewModel: SharedPosViewModel,
    onMemberCreate: () -> Unit
){
    Column(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start
    ){

        SearchableTextWithBg(
            value = posUIState.searchMember,
            onValueChange = {
                posViewModel.onSearchMember(it)
            }
        )


        //Cancel button
        RowButtonWithIcons(
            label = stringResource(Res.string.clear),
            icons = AppIcons.closeIcon,
            iconColor = AppTheme.colors.textError,
            modifier = Modifier.wrapContentWidth().wrapContentHeight(),
            onClick = {
              posViewModel.updateMemberDialogState(false)
            }
        )

        //Create Member
        RowButtonWithIcons(
            label = stringResource(Res.string.create),
            icons = AppIcons.plusIcon,
            iconColor = AppTheme.colors.appGreen,
            modifier = Modifier.wrapContentWidth().wrapContentHeight(),
            onClick = {
                //Create Member
                onMemberCreate()
            }
        )

        //Members List
        LazyColumn(modifier = Modifier.fillMaxWidth()){

            val searchQuery= posUIState.searchMember

            val filteredProducts = posUIState.memberList.filter { member ->
                (searchQuery.isEmpty() || member.name.contains(searchQuery, ignoreCase = true)  ||
                        member.memberCode?.contains(searchQuery, ignoreCase = true) == true)
            }.sortedBy { it.name }

            items(filteredProducts){ member ->
                MemberListItem(member, onClick = { selectedItem->
                    posViewModel.onSelectedMember(selectedItem)
                })
            }
        }

    }
}

@Composable
fun MemberListItem(member: MemberItem, onClick: (MemberItem) -> Unit) {

    Column(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(10.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(modifier = Modifier
            .wrapContentWidth().wrapContentHeight().clickable{onClick(member)},
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spaceBetweenPadded(10.dp)
        ){
            Icon(
                modifier = Modifier.size(AppTheme.dimensions.smallIcon),
                imageVector = vectorResource(AppIcons.empRoleIcon),
                tint = AppTheme.colors.primaryText,
                contentDescription = ""
            )

            ListText(
                label = "${member.name} \n${member.memberCode}",
                textStyle = AppTheme.typography.bodyMedium(),
                color = AppTheme.colors.primaryText,
                modifier = Modifier.wrapContentWidth(),
                singleLine = false
            )

        }

    }
}


@Composable
fun CreateMemberForm(
    posUIState : PosUIState,
    posViewModel: SharedPosViewModel
){

    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(horizontal = 10.dp, vertical = 10.dp)
        .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ){

        ScreenHeaderText(
            label = stringResource(Res.string.create_new_member)
        )


        AppOutlinedDropDown(
            selectedValue = posUIState.selectedMemberGroup,
            options = posUIState.memberGroupList,
            label = "",
            labelExtractor = { it.name ?: "" },  // Extract name from MemberGroupItem
            modifier = Modifier.fillMaxWidth(),
            onValueChangedEvent = {selectedValue ->
                posViewModel.onSelectedMemberGroup(selectedValue)
            }
        )

        //member group
        AppOutlinedTextField(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            value = posUIState.memberCode,
            onValueChange = { memberCode ->
                posViewModel.updateMemberCode(memberCode)
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType= KeyboardType.Number

            ),
            label = stringResource(Res.string.member_code),
            placeholder = "",
            singleLine = true,
            focusedBorderColor=AppTheme.colors.primaryColor,
            unfocusedBorderColor =AppTheme.colors.secondaryColor,
            error = posUIState.memberCodeError,
            enabled = !posUIState.isLoading
        )

        AppOutlinedTextField(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            value = posUIState.memberName,
            onValueChange = { memberName ->
                posViewModel.updateMemberName(memberName)
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            label = stringResource(Res.string.name),
            placeholder = "",
            singleLine = true,
            focusedBorderColor=AppTheme.colors.primaryColor,
            unfocusedBorderColor =AppTheme.colors.secondaryColor,
            error = posUIState.memberNameError,
            enabled = !posUIState.isLoading
        )

        AppOutlinedTextField(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            value = posUIState.email,
            onValueChange = { memberName ->
                posViewModel.updateEmail(memberName)
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType= KeyboardType.Email
            ),
            label = stringResource(Res.string.email),
            placeholder = "",
            singleLine = true,
            focusedBorderColor=AppTheme.colors.primaryColor,
            unfocusedBorderColor =AppTheme.colors.secondaryColor,
            error = null,
            enabled = !posUIState.isLoading
        )

        AppOutlinedTextField(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            value = posUIState.mobileNo,
            onValueChange = { memberName ->
                posViewModel.updateMobileNo(memberName)
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType= KeyboardType.Phone
            ),
            label = stringResource(Res.string.mobile_number),
            placeholder = "",
            singleLine = true,
            focusedBorderColor=AppTheme.colors.primaryColor,
            unfocusedBorderColor =AppTheme.colors.secondaryColor,
            error = null,
            enabled = !posUIState.isLoading
        )

        AppOutlinedTextField(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            value = posUIState.address,
            onValueChange = { memberName ->
                posViewModel.updateAddress(memberName)
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType= KeyboardType.Text
            ),
            label = stringResource(Res.string.address),
            placeholder = "",
            singleLine = true,
            focusedBorderColor=AppTheme.colors.primaryColor,
            unfocusedBorderColor =AppTheme.colors.secondaryColor,
            error = null,
            enabled = !posUIState.isLoading
        )

        AppOutlinedTextField(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            value = posUIState.zipCode,
            onValueChange = { memberName ->
                posViewModel.updateZipCode(memberName)
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
                keyboardType= KeyboardType.Number
            ),
            label = stringResource(Res.string.zip_code),
            placeholder = "",
            singleLine = true,
            focusedBorderColor=AppTheme.colors.primaryColor,
            unfocusedBorderColor =AppTheme.colors.secondaryColor,
            error = null,
            enabled = !posUIState.isLoading
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ){

            ButtonCard(
                modifier = Modifier.wrapContentHeight().wrapContentWidth().padding(horizontal = 10.dp),
                label = stringResource(Res.string.apply),
                icons = AppIcons.applyIcon,
                backgroundColor = AppTheme.colors.appGreen,
                innerPaddingValues = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
                onClick = {

                }
            )

            ButtonCard(
                modifier = Modifier.wrapContentHeight().wrapContentWidth().padding(horizontal = 10.dp),
                label = stringResource(Res.string.cancel),
                icons = AppIcons.closeIcon,
                backgroundColor = AppTheme.colors.textError,
                innerPaddingValues = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
                onClick = {
                    posViewModel.updateCreateMemberDialogState(false)
                }
            )
        }

    }


}