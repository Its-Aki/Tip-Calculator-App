package com.appdev.tipcalculatorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appdev.tipcalculatorapp.components.InputField
import com.appdev.tipcalculatorapp.ui.theme.TipCalculatorAppTheme
import com.appdev.tipcalculatorapp.utils.calculateTotalPerPerson
import com.appdev.tipcalculatorapp.utils.calculateTotalTip
import com.appdev.tipcalculatorapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp{
                MainContent()
            }
        }
    }
}

@Composable
fun MyApp(content:@Composable ()->Unit) {
    TipCalculatorAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()

        }
    }
}
@Composable
fun TopHeader(totalPerPerson:Double=0.0){
    Surface(modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .clip(CircleShape.copy(all = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
    ) {
        Column(modifier = Modifier
            .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val total="%.2f".format(totalPerPerson)
            Text(
                text = "Total Per Person",
                color = Color.Black,
                style=MaterialTheme.typography.headlineLarge
                )
            Text(
                text = "$$total",
                fontWeight = FontWeight.ExtraBold,
                style=MaterialTheme.typography.headlineMedium,
                color = Color.Black
            )
        }
    }
}
@Preview(apiLevel = 34)
@Preview(showBackground = true)
@Composable
fun MainContent(){
    val splitByState= remember {
        mutableIntStateOf(1)
    }
    val tipAmountState= remember {
        mutableDoubleStateOf(0.0)
    }
    val totalPerPersonState= remember {
        mutableDoubleStateOf(0.0)
    }
    val range=IntRange(start = 1, endInclusive = 100)
    Column(modifier = Modifier.padding(12.dp)) {
        BillForm(splitByState=splitByState,
            tipAmountState = tipAmountState,
            totalPerPersonState = totalPerPersonState,
            range=range
        )
    }

}
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(
    modifier:Modifier=Modifier,
    onValChange:(String)->Unit={},
    range:IntRange=1..100,
    splitByState:MutableState<Int>,
    tipAmountState:MutableState<Double>,
    totalPerPersonState: MutableState<Double>
){
    val totalBillState=remember{
  mutableStateOf("")
    }
    val validState= remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController=LocalSoftwareKeyboardController.current
    val sliderPositionState= remember{
        mutableFloatStateOf(0f)
    }
    val sliderPercentage=(sliderPositionState.floatValue*100).toInt()

    TopHeader(totalPerPerson = totalPerPersonState.value)
    Spacer(modifier = Modifier.height(20.dp))
    Surface(modifier = modifier
        .padding(2.dp)
        .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(modifier=Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            InputField(
                valueState =totalBillState,
                labelId = "Enter Bill",
                isSingleLined =true ,
                enabled = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValChange(totalBillState.value)
                    keyboardController?.hide()
                }
            )
            if(validState){
                Row(modifier=Modifier
                    .padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Split",
                        modifier=Modifier.align(
                            alignment = Alignment.CenterVertically
                        )
                        )
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(
                        modifier=Modifier.padding(horizontal = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        RoundIconButton(imageVector = Icons.Rounded.KeyboardArrowLeft, onClick = {
                        splitByState.value=if((splitByState.value)>1 ) splitByState.value-1
                        else 1
                            totalPerPersonState.value= calculateTotalPerPerson(totalBill = (totalBillState.value).toDouble(), splitBy =splitByState.value,sliderPercentage=sliderPercentage )

                        })
                        Text(text = "${splitByState.value}")
                        RoundIconButton(imageVector = Icons.Rounded.KeyboardArrowRight, onClick = {
                            if((splitByState.value)<range.last ) {
                                splitByState.value=splitByState.value+1
                            }
                            totalPerPersonState.value= calculateTotalPerPerson(totalBill = (totalBillState.value).toDouble(), splitBy =splitByState.value,sliderPercentage=sliderPercentage )

                        })
                    }

                }
            Row(modifier=modifier.padding(horizontal = 3.dp, vertical = 12.dp)) {
                Text(modifier=Modifier.align(alignment = Alignment.CenterVertically),
                    text = "Tip"
                )
                   Spacer(modifier = Modifier.width(200.dp))
                Text(modifier=Modifier.align(alignment = Alignment.CenterVertically),
                    text = "$ ${tipAmountState.value}"
                )
               }
            Column(modifier=modifier.padding(3.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
                ) {
                Text(text = "$sliderPercentage %")
                Spacer(modifier = modifier.height(14.dp))
                Slider(
                    value = sliderPositionState.floatValue,
                    onValueChange ={newVal->
                    sliderPositionState.floatValue=newVal
                        tipAmountState.value=calculateTotalTip(totalBill = (totalBillState.value).toDouble(),sliderPercentage=sliderPercentage)
                        totalPerPersonState.value= calculateTotalPerPerson(totalBill = (totalBillState.value).toDouble(), splitBy =splitByState.value,sliderPercentage=sliderPercentage )
                     },
                    onValueChangeFinished = {},
                    steps = 0,
                    modifier=modifier.padding(start = 16.dp, end = 16.dp),

                )
            }
            }
            else{
                Box{}
            }

        }
    }
}