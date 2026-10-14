package uz.ttpu.framephases

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.ttpu.framephases.ui.theme.FramePhasesTheme

// One demo at a time. Each entry is shown from setContent { ... } exactly as the practice
// asks; the chips at the top only save you from editing setContent between experiments.
// Logcat filters: tag:Phases, tag:Parallax, tag:Derived, tag:Skip, tag:Cycle, tag:StackBelow
private class DemoEntry(val label: String, val content: @Composable () -> Unit)

private val demos = listOf(
    DemoEntry("Q4 LectureTree") { LectureTree() },
    DemoEntry("Q5 PaddingDemo") { PaddingDemo() },
    DemoEntry("Q6 OffsetDemo") { OffsetDemo() },
    DemoEntry("Q6 OffsetDemo (experiment)") { OffsetDemoExperiment() },
    DemoEntry("Q7 ColorDemo (Canvas)") { ColorDemo() },
    DemoEntry("Q7 ColorDemo (Box)") { ColorDemoBox() },
    DemoEntry("Q9 ParallaxSlow") { ParallaxSlow() },
    DemoEntry("Q9 ParallaxFast") { ParallaxFast() },
    DemoEntry("Q10 ScrollToTopNaive") { ScrollToTopNaive() },
    DemoEntry("Q10 ScrollToTopDerived") { ScrollToTopDerived() },
    DemoEntry("Q11 Parent (value)") { Parent() },
    DemoEntry("Q11 Parent (lambda)") { ParentWithLambda() },
    DemoEntry("Q13 CyclicDemo") { CyclicDemo() },
    DemoEntry("Q13 CyclicFixed") { CyclicFixed() },
    DemoEntry("Q14 StackBelow") { StackBelowDemo() },
    DemoEntry("Q14 StackBelow (3 children)") { StackBelowThreeChildren() },
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FramePhasesTheme {
                var selected by rememberSaveable { mutableIntStateOf(0) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(Modifier.padding(innerPadding)) {
                        DemoPicker(selected = selected, onSelect = { selected = it })
                        // key(...) throws the old demo's state away when you switch demos.
                        key(selected) {
                            Box(Modifier.weight(1f).fillMaxWidth()) {
                                demos[selected].content()
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DemoPicker(selected: Int, onSelect: (Int) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        itemsIndexed(demos) { index, demo ->
            FilterChip(
                selected = index == selected,
                onClick = { onSelect(index) },
                label = { Text(demo.label) },
            )
        }
    }
}
