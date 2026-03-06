package de.asp.vicoreproducer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.shape.rounded
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.decoration.HorizontalLine
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.Position
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import de.asp.vicoreproducer.composables.StepperRow

@Composable
fun ChartScreen(
    viewModel: ChartScreenViewModel
) {
    val state by viewModel.state.collectAsState()

    ChartScreenContent(
        state = state,
        onIntent = viewModel::onIntent
    )
}

@Composable
private fun ChartScreenContent(
    state: ChartScreenState,
    onIntent: (ChartScreenIntent) -> Unit
) {
    val modelProducer = remember {
        CartesianChartModelProducer()
    }

    LaunchedEffect(state.dataSeries) {
        modelProducer.runTransaction {
            lineSeries {
                series(state.dataSeries)
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            StepperRow(
                label = "Horizontal Line Y-value",
                value = state.horizontalLineY.toString(),
                onValueChange = { onIntent(ChartScreenIntent.IncreaseHorizontalY) },
                onIncrement = { onIntent(ChartScreenIntent.IncreaseHorizontalY) },
                onDecrement = { onIntent(ChartScreenIntent.DecreaseHorizontalY) },
            )

            AudioLevelChart(
                modelProducer = modelProducer,
                thresholdPercent = state.horizontalLineY,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(top = 24.dp),
            )
        }
    }
}

@Composable
private fun AudioLevelChart(
    modelProducer: CartesianChartModelProducer,
    thresholdPercent: Int,
    modifier: Modifier = Modifier,
) {
    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                rangeProvider = CartesianLayerRangeProvider.fixed(
                    minX = 0.0,
                    maxX = 100.0,
                    minY = 0.0,
                    maxY = 100.0,
                )
            ),
            startAxis = VerticalAxis.rememberStart(
                itemPlacer = remember {
                    VerticalAxis.ItemPlacer.step(step = { 10.0 })
                },
            ),
            bottomAxis = HorizontalAxis.rememberBottom(label = null),
            decorations = listOf(
                getHorizontalLine(
                    thresholdPercent.toDouble(),
                    MaterialTheme.colorScheme.error.toArgb(),
                    MaterialTheme.colorScheme.onError.toArgb(),

                    )
            ),
        ),
        modelProducer = modelProducer,
        animationSpec = null,
        animateIn = false,
        scrollState = rememberVicoScrollState(scrollEnabled = false),
        modifier = modifier,
    )
}

private fun getHorizontalLine(
    thresholdValue: Double,
    lineColor: Int,
    textColor: Int,
): HorizontalLine {
    val fill = Fill(lineColor)
    return HorizontalLine(
        y = { thresholdValue },
        line = LineComponent(fill = fill, thicknessDp = 1f),
        labelComponent = TextComponent(
            color = textColor,
            margins = Insets(startDp = 6f),
            padding = Insets(startDp = 8f, endDp = 8f, bottomDp = 2f),
            background = ShapeComponent(
                fill,
                CorneredShape.rounded(bottomLeft = 4.dp, bottomRight = 4.dp)
            ),
        ),
        label = { "Threshold" },
        verticalLabelPosition = Position.Vertical.Bottom,
    )
}